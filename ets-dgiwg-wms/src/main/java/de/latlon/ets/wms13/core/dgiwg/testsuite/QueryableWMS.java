package de.latlon.ets.wms13.core.dgiwg.testsuite;

import static org.testng.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import de.latlon.ets.core.util.XMLUtils;
import de.latlon.ets.wms13.core.TestRunArg;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;

/**
 * The DGIWG WMS profile requires that a conforming implementation satisfies the
 * requirements of the <strong>Queryable WMS</strong> conformance class in
 * accord with the base WMS standard. See Table 1, cl. 6.2: Requirement 1.
 * 
 * <p>
 * The OGC test suite can be invoked using a RESTful API provided by a test
 * execution service (TES). The default TES is the OGC beta installation
 * (located at http://cite.opengeospatial.org/te2/), but this can be overridden
 * using the "tes" test run argument.
 * </p>
 * 
 * @see <a target="_blank" href=
 *      "https://portal.dgiwg.org/files/?artifact_id=11514&format=pdf">DGIWG -
 *      Web Map Service 1.3 Profile - Revision (DGIWG-112, Ed. 2.1)</a>
 * @see <a target="_blank" href=
 *      "http://portal.opengeospatial.org/files/?artifact_id=14416">OpenGIS&#174;
 *      Web Map Server Implementation Specification, Version 1.3.0</a>
 */
public class QueryableWMS {

    private static final Logger LOGR = Logger.getLogger(QueryableWMS.class.getPackage().getName());
    private Level logLevel = Level.INFO;
    /** Location of default test execution service (OGC beta installation). */
    private static final URI DEFAULT_TES = URI.create("http://cite.opengeospatial.org/te2/");
    /** HTML namespace name. */
    private static final String HTML_NS = "http://www.w3.org/1999/xhtml";
    /** Test run parameter: Location of capabilities document (URI). */
    private static final String CAPABILITIES_URL = "capabilities-url";
    /** Test run parameter: Run tests for 'Queryable WMS' (boolean). */
    private static final String QUERYABLE = "queryable";
    /** JAX-RS Client component. */
    private Client httpClient;
    /** Endpoint of base WMS test run controller. */
    private URI baseTestRunController;
    /** URI that refers to the WMS capabilities document for the IUT. */
    private String wmsCapabilitiesRef;

    /**
     * This <code>BeforeTest</code> configuration method initializes the JAX-RS
     * client component that is used to interact with the TES.
     * 
     * @param testContext
     *            Information about the test run.
     */
    @BeforeTest
    public void initTestFixture(ITestContext testContext) {
        ClientConfig config = new DefaultClientConfig();
        this.httpClient = Client.create(config);
        this.wmsCapabilitiesRef = testContext.getSuite().getParameter(TestRunArg.WMS.toString());
    }

    /**
     * This <code>BeforeClass</code> configuration method discovers the location
     * of the test run controller for the base OGC test suite. If it cannot be
     * found, the base tests are skipped.
     * 
     * @param testContext
     *            Information about the test run.
     */
    @BeforeClass
    public void discoverBaseControllerEndpoint(ITestContext testContext) {
        String tes = testContext.getSuite().getParameter(TestRunArg.TES.toString());
        if (null != tes && !tes.isEmpty()) {
            try {
                URI altTestExecService = new URI(tes);
                this.baseTestRunController = discoverTestRunController(altTestExecService);
            } catch (URISyntaxException e1) {
                LOGR.info(String.format("Value of test run argument '%s' is not a valid URI: %s",
                        TestRunArg.TES.toString(), tes));
            } catch (RuntimeException e2) {
                LOGR.info(e2.getMessage());
            }
        }
        if (null == this.baseTestRunController) {
            try {
                this.baseTestRunController = discoverTestRunController(DEFAULT_TES);
            } catch (RuntimeException e) {
                throw new SkipException(e.getMessage());
            }
        }
        LOGR.log(logLevel, this.baseTestRunController.toString());
    }

    /**
     * Runs the OGC WMS test suite by submitting a (GET) request to the test run
     * controller. The following test run arguments are set:
     * <ul>
     * <li>capabilities-url : A URI that refers to a WMS capabilities
     * document</li>
     * <li>queryable: "queryable"</li>
     * </ul>
     * 
     * @throws IOException
     *             If an I/O error occurs while trying to read or write the test
     *             run results.
     */
    @Test(description = "See DGIWG-112: Table 1, Requirement 1")
    public void invokeBaseTestSuite() throws IOException {
        LOGR.log(logLevel, "Starting base test run at {0}", this.baseTestRunController);
        MultivaluedMap<String, String> args = new MultivaluedMapImpl();
        args.putSingle(CAPABILITIES_URL, this.wmsCapabilitiesRef);
        args.putSingle(QUERYABLE, QUERYABLE);
        WebResource resource = this.httpClient.resource(this.baseTestRunController).queryParams(args);
        ClientResponse rsp = resource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        assertEquals(rsp.getStatus(), ClientResponse.Status.OK.getStatusCode(), "Unexpected status code.");
        LOGR.log(logLevel, "Test run was completed.");
        assertTrue(rsp.hasEntity(), "No entity in response from test run controller.");
        File results = saveEntityToFile(rsp);
        LOGR.log(logLevel, "Test run results were saved to {0}", results);
        // examine result of main test
        XdmNode mainTestNode;
        try {
            XdmValue xdmVal = XMLUtils.evaluateXPath2(new StreamSource(results), "/execution/log[1]", null);
            mainTestNode = (XdmNode) xdmVal.itemAt(0);
        } catch (SaxonApiException e) {
            throw new AssertionError(e.getMessage());
        }
        XdmNode startTest = (XdmNode) mainTestNode.axisIterator(Axis.CHILD, new QName("starttest")).next();
        LOGR.log(logLevel, "Base test run identifier: {0}", startTest.getAttributeValue(new QName("path")));
        XdmNode endTest = (XdmNode) mainTestNode.axisIterator(Axis.CHILD, new QName("endtest")).next();
        int statusCode = Integer.parseInt(endTest.getAttributeValue(new QName("result")));
        TestVerdict mainVerdict = TestVerdict.values()[statusCode - 1];
        assertEquals(mainVerdict, TestVerdict.PASS, "Unexpected verdict for main test.");
    }

    /**
     * Writes the content of the response entity to a temporary file.
     * 
     * @param rsp
     *            An HTTP response message.
     * @return A File containing the entity body.
     * @throws IOException
     *             If an I/O error occurs.
     */
    File saveEntityToFile(ClientResponse rsp) throws IOException {
        File destFile = File.createTempFile("wmsResults-", ".xml");
        try (InputStream in = rsp.getEntityInputStream(); OutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return destFile;
    }

    /**
     * Interacts with the test execution service (TES) in order to discover the
     * URI for the base WMS test run controller. This controller executes the
     * standard OGC test suite. The end-point is given by the URI template shown
     * below, where <code>{tes}</code> is the root URI of the test execution
     * service and <code>{version}</code> is the version of the WMS test suite.
     * 
     * <pre>
     * {tes}/rest/suites/wms/{version}/run
     * </pre>
     * 
     * @param tesEndpoint
     *            An 'http' URI that refers to the test execution service.
     * @return The URI for the standard WMS test run controller.
     */
    URI discoverTestRunController(URI tesEndpoint) {
        // fetch list of available test suites from TES
        URI targetURI = UriBuilder.fromUri(tesEndpoint).path("rest/suites/").build();
        WebResource resource = this.httpClient.resource(targetURI);
        ClientResponse rsp = resource.get(ClientResponse.class);
        if (!rsp.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            throw new RuntimeException(String.format("Test execution service not available at %s", tesEndpoint));
        }
        // find WMS test run controller
        String xpath = "//html:li/html:a[starts-with(@id,'wms')]/@href";
        XdmValue xdmVal;
        Source entity = new StreamSource(rsp.getEntityInputStream(), targetURI.toString());
        try {
            xdmVal = XMLUtils.evaluateXPath2(entity, xpath, Collections.singletonMap(HTML_NS, "html"));
        } catch (SaxonApiException e) {
            throw new RuntimeException(
                    String.format("Failed to access list of test suites at %s. Reason: %s", targetURI, e.getMessage()));
        }
        if (xdmVal.size() == 0) {
            throw new RuntimeException(String.format("WMS test suite is not listed at %s", targetURI));
        }
        URI controllerUri = null;
        try {
            String etsPath = xdmVal.getUnderlyingValue().getStringValue();
            controllerUri = UriBuilder.fromUri(targetURI).path(etsPath).path("run").build();
        } catch (XPathException e) {
            // not possible with atomic value
        }
        return controllerUri;
    }

    /**
     * Test verdicts assigned in CTL test suites. For each verdict, its
     * corresponding status code is given by ordinal() + 1. Conversely, to look
     * up a verdict by status code, evaluate TestVerdict.values()[statusCode -
     * 1].
     */
    enum TestVerdict {
        PASS, NOT_TESTED, SKIP, WARNING, INHERITED_FAILURE, FAIL;
    }
}
