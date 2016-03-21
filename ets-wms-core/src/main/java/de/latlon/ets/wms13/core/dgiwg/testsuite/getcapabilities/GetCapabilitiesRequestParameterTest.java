package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertContentType;
import static de.latlon.ets.wms13.core.assertion.WmsAssertion.assertSimpleWMSCapabilities;
import static de.latlon.ets.wms13.core.assertion.WmsAssertion.assertVersion130;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.FORMAT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_CAPABILITIES;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.TEXT_XML;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.UPDATE_SEQUENCE_PARAM;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;
import java.util.Random;

import javax.xml.soap.SOAPException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests if the request parameters for GetCapabilites requests are supported.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesRequestParameterTest extends AbstractBaseGetCapabilitiesFixture {

    private static final Random RANDOM = new Random();

    @BeforeMethod
    public void clearRequest() {
        this.reqEntity.removeKvp( FORMAT_PARAM );
        this.reqEntity.removeKvp( UPDATE_SEQUENCE_PARAM );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.1., S.12, Requirement 8")
    public void wmsCapabilitiesOutputFormatParameterSupported()
                    throws SOAPException {
        List<String> supportedFormats = ServiceMetadataUtils.parseSupportedFormats( wmsCapabilities, GET_CAPABILITIES );
        if ( supportedFormats.size() > 0 ) {
            String format = retrieveRandomFormat( supportedFormats );

            this.reqEntity.addKvp( FORMAT_PARAM, format );

            URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_CAPABILITIES,
                                                                      ProtocolBinding.GET );
            ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

            assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
            assertContentType( rsp.getHeaders(), format );

            if ( TEXT_XML.equals( format ) ) {
                Document reqEntity = rsp.getEntity( Document.class );
                assertSimpleWMSCapabilities( reqEntity );
                assertVersion130( reqEntity );
            }
        }
    }

    private String retrieveRandomFormat( List<String> supportedFormats ) {
        int index = RANDOM.nextInt( supportedFormats.size() );
        return supportedFormats.get( index );
    }

}