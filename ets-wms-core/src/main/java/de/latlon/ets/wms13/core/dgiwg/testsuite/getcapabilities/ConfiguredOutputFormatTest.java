package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;
import static de.latlon.ets.wms13.core.assertion.WmsAssertion.assertSimpleWMSCapabilities;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_CAPABILITIES;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.xml.soap.SOAPException;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests if the capabilities document contains the expected formats:
 * 
 * <ul>
 * <li>GetCapabilities
 * <ul>
 * <li>text/xml</li>
 * <li>text/htmlml</li>
 * </ul>
 * </li>
 * <li>GetFeatureInfo
 * <ul>
 * <li>text/xml</li>
 * <li>text/htmlml</li>
 * </ul>
 * </li>
 * <li>GetMap
 * <ul>
 * <li>image/png</li>
 * <li>image/jpeg</li>
 * <li>image/gif</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class ConfiguredOutputFormatTest extends AbstractBaseGetCapabilitiesFixture {

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3")
    public void wmsCapabilitiesOutputFormatGetCapabilitiesTextXmlSupported()
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        Document entity = rsp.getEntity( Document.class );
        assertSimpleWMSCapabilities( entity );

        String xPathXml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetCapabilities/wms:Format/text() = 'text/xml'";
        assertXPath( xPathXml, entity, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3")
    public void wmsCapabilitiesOutputFormatGetCapabilitiesTextHtmlSupported()
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        Document entity = rsp.getEntity( Document.class );
        assertSimpleWMSCapabilities( entity );

        String xPathXml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetCapabilities/wms:Format/text() = 'text/html'";
        assertXPath( xPathXml, entity, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3")
    public void wmsCapabilitiesOutputFormatGetFeatureInfoTextXmlSupported()
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        Document entity = rsp.getEntity( Document.class );
        assertSimpleWMSCapabilities( entity );

        String xPathXml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetFeatureInfo/wms:Format/text() = 'text/xml'";
        assertXPath( xPathXml, entity, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 3")
    public void wmsCapabilitiesOutputFormatGetFeatureInfoTextHtmlSupported()
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        Document entity = rsp.getEntity( Document.class );
        assertSimpleWMSCapabilities( entity );

        String xPathXml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetFeatureInfo/wms:Format/text() = 'text/html'";
        assertXPath( xPathXml, entity, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.2., S.9, Requirement 5")
    public void wmsCapabilitiesOutputFormatGetMapImagePngSupported()
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        Document entity = rsp.getEntity( Document.class );
        assertSimpleWMSCapabilities( entity );

        String xPathXml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetMap/wms:Format/text() = 'image/png'";
        assertXPath( xPathXml, entity, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.2., S.9, Requirement 5")
    public void wmsCapabilitiesOutputFormatGetMapImageGifSupported()
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        Document entity = rsp.getEntity( Document.class );
        assertSimpleWMSCapabilities( entity );

        String xPathXml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetMap/wms:Format/text() = 'image/gif'";
        assertXPath( xPathXml, entity, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.2., S.9, Requirement 5")
    public void wmsCapabilitiesOutputFormatGetMapImageJpegSupported()
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        Document entity = rsp.getEntity( Document.class );
        assertSimpleWMSCapabilities( entity );

        String xPathXml = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetMap/wms:Format/text() = 'image/jpeg'";
        assertXPath( xPathXml, entity, NS_BINDINGS );
    }

}