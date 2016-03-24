package de.latlon.ets.wms13.core.dgiwg.testsuite;

import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.w3c.dom.Document;

import de.latlon.ets.core.util.NamespaceBindings;
import de.latlon.ets.core.util.TestSuiteLogger;
import de.latlon.ets.core.util.XMLUtils;
import de.latlon.ets.wms13.core.client.WmsClient;
import de.latlon.ets.wms13.core.client.WmsKvpRequest;
import de.latlon.ets.wms13.core.domain.LayerInfo;
import de.latlon.ets.wms13.core.domain.WmsNamespaces;
import de.latlon.ets.wms13.core.domain.SuiteAttribute;

/**
 * A supporting base class that provides common configuration methods and data providers. The configuration methods are
 * invoked before any that may be defined in a subclass.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public abstract class AbstractBaseGetFixture {

    /** Maximum length of response (string) added as result attribute. */
    private static final int MAX_RSP_ATTR_LENGTH = 1536;
    
    protected static final NamespaceBindings NS_BINDINGS = WmsNamespaces.withStandardBindings();

    protected Document wmsCapabilities;

    protected WmsClient wmsClient;

    protected DocumentBuilder docBuilder;

    protected WmsKvpRequest reqEntity;

    protected Document rspEntity;

    protected List<LayerInfo> layerInfo;

    public void setWmsClient( WmsClient wmsClient ) {
        this.wmsClient = wmsClient;
    }

    /**
     * Sets up the base fixture. The service metadata document is obtained from the ISuite context. The suite attribute
     * {@link SuiteAttribute#TEST_SUBJECT testSubject} should yield a DOM Document node having
     * {http://www.opengis.net/wms}WMS_Capabilities as the document element.
     * 
     * @param testContext
     *            the test (set) context, never <code>null</code>
     */
    @SuppressWarnings("unchecked")
    @BeforeClass(alwaysRun = true)
    public void initBaseFixture( ITestContext testContext ) {
        if ( this.wmsCapabilities != null )
            return;
        this.wmsCapabilities = (Document) testContext.getSuite().getAttribute( SuiteAttribute.TEST_SUBJECT.getName() );
        this.wmsClient = new WmsClient( this.wmsCapabilities );
        this.layerInfo = (List<LayerInfo>) testContext.getSuite().getAttribute( SuiteAttribute.LAYER_INFO.getName() );
    }

    /**
     * Initializes the (namespace-aware) DOM parser.
     */
    @BeforeClass
    public void initParser() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        try {
            this.docBuilder = factory.newDocumentBuilder();
        } catch ( ParserConfigurationException e ) {
            TestSuiteLogger.log( Level.WARNING, "Failed to create DOM parser", e );
        }
    }

    /**
     * Augments the test result with supplementary attributes in the event that a test method failed. The "request"
     * attribute contains a String representing the query component (GET method). The "response" attribute contains the
     * content of the response entity.
     */
    @AfterMethod
    public void addAttributesOnTestFailure( ITestResult result ) {
        if ( result.getStatus() != ITestResult.FAILURE ) {
            return;
        }
        if ( this.reqEntity != null ) {
            String request = this.reqEntity.asQueryString();
            result.setAttribute( "request", request );
        }
        if ( this.rspEntity != null ) {
            StringBuilder response = new StringBuilder( XMLUtils.writeNodeToString( this.rspEntity ) );
            if ( response.length() > MAX_RSP_ATTR_LENGTH ) {
                response.delete( MAX_RSP_ATTR_LENGTH, response.length() );
            }
            result.setAttribute( "response", response.toString() );
        }
    }

    /**
     * Augments the test result with supplementary attributes in the event that a test method succeeded. The "request"
     * attribute contains a String representing the query component (GET method).
     */
    @AfterMethod
    public void addAttributesOnTestSuccess( ITestResult result ) {
        if ( result.getStatus() != ITestResult.SUCCESS ) {
            return;
        }
        if ( this.reqEntity != null ) {
            String request = this.reqEntity.asQueryString();
            result.setAttribute( "request", request );
        }
    }

}