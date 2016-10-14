package de.latlon.ets.wms13.core.dgiwg.testsuite;

import static de.latlon.ets.wms13.core.assertion.WmsAssertion.assertSimpleWMSCapabilities;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.latlon.ets.wms13.core.client.WmsClient;
import de.latlon.ets.wms13.core.domain.DGIWGWMS;
import de.latlon.ets.wms13.core.domain.SuiteAttribute;
import de.latlon.ets.wms13.core.domain.WmsNamespaces;

/**
 * Confirms the readiness of the SUT to undergo testing. If any of these
 * configuration methods fail then all remaining tests in the suite are skipped.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class Prerequisites {

    /**
     * Verifies that the service capabilities description is a WMS Capabilities
     * document.
     * 
     * @param testContext
     *            the test run context, never <code>null</code>
     */
    @Test
    public void verifyServiceDescription(ITestContext testContext) {
        Document wmsMetadata = (Document) testContext.getSuite().getAttribute(SuiteAttribute.TEST_SUBJECT.getName());
        assertSimpleWMSCapabilities(wmsMetadata);
    }

    /**
     * Confirms that the SUT is available and produces a service description in
     * response to a basic GetCapabilities request. The document element is
     * expected to have the following infoset properties:
     * <ul>
     * <li>[local name] = "WMS_Capabilities"</li>
     * <li>[namespace name] = "http://www.opengis.net/wms"</li>
     * </ul>
     */
    @Test(dependsOnMethods = { "verifyServiceDescription" })
    public void serviceIsAvailable(ITestContext testContext) {
        Document wmsMetadata = (Document) testContext.getSuite().getAttribute(SuiteAttribute.TEST_SUBJECT.getName());
        WmsClient wmsClient = new WmsClient(wmsMetadata);
        Document capabilities = wmsClient.getCapabilities();
        assertNotNull(capabilities, "No GetCapabilities response from SUT.");
        Element docElement = capabilities.getDocumentElement();
        assertEquals(docElement.getLocalName(), DGIWGWMS.WMS_CAPABILITIES,
                "Capabilities document element has unexpected [local name].");
        assertEquals(docElement.getNamespaceURI(), WmsNamespaces.WMS,
                "Capabilities document element has unexpected [namespace name].");
    }

}