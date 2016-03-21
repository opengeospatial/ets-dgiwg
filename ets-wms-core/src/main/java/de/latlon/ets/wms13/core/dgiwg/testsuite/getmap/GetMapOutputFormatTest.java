package de.latlon.ets.wms13.core.dgiwg.testsuite.getmap;

import static de.latlon.ets.core.assertion.ETSAssert.assertContentType;
import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.FORMAT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_MAP;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.IMAGE_GIF;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.IMAGE_JPEG;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.IMAGE_PNG;
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
 * Tests if the expected formats for GetMap requests (image/png, image/gif and image/jpeg) are supported.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetMapOutputFormatTest extends BaseGetMapFixture {

    @BeforeMethod
    public void clearRequest() {
        this.reqEntity.removeKvp( FORMAT_PARAM );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.2., S.9, Requirement 5")
    public void wmsCapabilitiesOutputFormatImagePngSupported()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetMap/wms:Format/text() = 'image/png'";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.2., S.9, Requirement 5")
    public void wmsCapabilitiesOutputFormatImageGifSupported()
                    throws SOAPException {
        String xPathHtml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetMap/wms:Format/text() = 'image/gif'";
        assertXPath( xPathHtml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.2., S.9, Requirement 5")
    public void wmsCapabilitiesOutputFormatImageJpegSupported()
                    throws SOAPException {
        String xPathHtml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetMap/wms:Format/text() = 'image/jpeg'";
        assertXPath( xPathHtml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.2., S.9, Requirement 5", dependsOnMethods = "wmsCapabilitiesOutputFormatImagePngSupported")
    public
                    void wmsGetMapOutputFormatImagePngSupported()
                                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_MAP, ProtocolBinding.GET );
        this.reqEntity.addKvp( FORMAT_PARAM, IMAGE_PNG );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertContentType( rsp.getHeaders(), IMAGE_PNG );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.2., S.9, Requirement 5", dependsOnMethods = "wmsCapabilitiesOutputFormatImageGifSupported")
    public
                    void wmsGetMapOutputFormatImageGifSupported()
                                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_MAP, ProtocolBinding.GET );
        this.reqEntity.addKvp( FORMAT_PARAM, IMAGE_GIF );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertContentType( rsp.getHeaders(), IMAGE_GIF );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.2., S.9, Requirement 5", dependsOnMethods = "wmsCapabilitiesOutputFormatImageJpegSupported")
    public
                    void wmsGetMapOutputFormatImageJpegSupported()
                                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_MAP, ProtocolBinding.GET );
        this.reqEntity.addKvp( FORMAT_PARAM, IMAGE_JPEG );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertContentType( rsp.getHeaders(), IMAGE_JPEG );
    }

}