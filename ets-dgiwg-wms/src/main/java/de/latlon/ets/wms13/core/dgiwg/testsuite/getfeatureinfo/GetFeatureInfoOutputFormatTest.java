package de.latlon.ets.wms13.core.dgiwg.testsuite.getfeatureinfo;

import static de.latlon.ets.core.assertion.ETSAssert.assertContentType;
import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_FEATURE_INFO;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.INFO_FORMAT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.TEXT_HTML;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.TEXT_XML;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.xml.soap.SOAPException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests if the expected formats for GetFeatureInfo requests (text/xml and text/html) are supported.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetFeatureInfoOutputFormatTest extends BaseGetFeatureInfoFixture {

    @BeforeMethod
    public void clearRequest() {
        this.reqEntity.removeKvp( INFO_FORMAT_PARAM );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3")
    public void wmsCapabilitiesOutputFormatTextXmlSupported()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetFeatureInfo/wms:Format/text() = 'text/xml'";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3")
    public void wmsCapabilitiesOutputFormatTextHtmlSupported()
                    throws SOAPException {
        String xPathHtml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetFeatureInfo/wms:Format/text() = 'text/html'";
        assertXPath( xPathHtml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3", dependsOnMethods = "wmsCapabilitiesOutputFormatTextXmlSupported")
    public
                    void wmsGetFeatureInfoOutputFormatTextXmlSupported()
                                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_FEATURE_INFO,
                                                                  ProtocolBinding.GET );
        this.reqEntity.addKvp( INFO_FORMAT_PARAM, TEXT_XML );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertContentType( rsp.getHeaders(), TEXT_XML );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3", dependsOnMethods = "wmsCapabilitiesOutputFormatTextHtmlSupported")
    public
                    void wmsGetFeatureInfoOutputFormatTextHtmlSupported()
                                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_FEATURE_INFO,
                                                                  ProtocolBinding.GET );
        this.reqEntity.addKvp( INFO_FORMAT_PARAM, TEXT_HTML );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertContentType( rsp.getHeaders(), TEXT_HTML );
    }

}