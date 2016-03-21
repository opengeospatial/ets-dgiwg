package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_CAPABILITIES;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests if the capabilities contains a valid value for AccessConstraint.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesAccessConstraintTest extends AbstractBaseGetCapabilitiesFixture {

    private static final List<String> EXPECTED_ACCESS_CONSTRAINTS = Arrays.asList( "unclassified", "restricted",
                                                                                 "confidential", "secret", "topSecret" );

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 10")
    public void wmsCapabilitiesAccessConstraintsExists() {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:AccessConstraints != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 10", dependsOnMethods = "wmsCapabilitiesAccessConstraintsExists")
    public
                    void wmsCapabilitiesAccessConstraintsContainsValueFromDMF()
                                    throws XPathFactoryConfigurationException, XPathExpressionException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        String accessConstraints = parseAccessConstraints( rsp );
        assertTrue( EXPECTED_ACCESS_CONSTRAINTS.contains( accessConstraints ),
                    "AccessConstraints are not valid, must be one of " + EXPECTED_ACCESS_CONSTRAINTS + " but was "
                                    + accessConstraints );
    }

    private String parseAccessConstraints( ClientResponse rsp )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        String xPathAccessConstraints = "//wms:WMS_Capabilities/wms:Service/wms:AccessConstraints";
        XPathFactory factory = XPathFactory.newInstance( XPathConstants.DOM_OBJECT_MODEL );
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext( NS_BINDINGS );
        return (String) xpath.evaluate( xPathAccessConstraints, rsp.getEntity( Document.class ), XPathConstants.STRING );
    }

}