package de.latlon.ets.wms13.core.dgiwg.testsuite.getmap;

import static de.latlon.ets.core.assertion.ETSAssert.assertContentType;
import static de.latlon.ets.core.assertion.ETSAssert.assertStatusCode;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.BGCOLOR_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.EXCEPTIONS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.FORMAT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_MAP;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.LAYERS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.TRANSPARENT_PARAM;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.xml.soap.SOAPException;

import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests if BLANK Exceptions are supported.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetMapBlankExceptionsTest extends BaseGetMapFixture {

    @BeforeMethod
    public void clearRequest() {
        this.reqEntity.removeKvp( FORMAT_PARAM );
        this.reqEntity.removeKvp( LAYERS_PARAM );
        this.reqEntity.removeKvp( TRANSPARENT_PARAM );
        this.reqEntity.removeKvp( BGCOLOR_PARAM );
        this.reqEntity.removeKvp( EXCEPTIONS_PARAM );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.3., S.19, Requirement 26")
    public void wmsGetMapBlankExceptionsSupported_TransparentTrue( ITestContext testContext )
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_MAP, ProtocolBinding.GET );
        String requestFormat = findRequiredImageFormatWithTransparencySupport();

        this.reqEntity.addKvp( FORMAT_PARAM, requestFormat );
        this.reqEntity.addKvp( TRANSPARENT_PARAM, "TRUE" );
        this.reqEntity.addKvp( EXCEPTIONS_PARAM, "BLANK" );
        this.reqEntity.addKvp( BGCOLOR_PARAM, "0x000000" );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        storeResponseImage( rsp, "Requirement26", "blankExceptionExpected_transparentBackground", requestFormat );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertStatusCode( rsp.getStatus(), 200 );
        assertContentType( rsp.getHeaders(), requestFormat );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.3., S.19, Requirement 26")
    public void wmsGetMapBlankExceptionsSupported_TransparentFalse( ITestContext testContext )
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_MAP, ProtocolBinding.GET );
        String requestFormat = findRequiredImageFormatWithTransparencySupport();

        this.reqEntity.addKvp( FORMAT_PARAM, requestFormat );
        this.reqEntity.addKvp( TRANSPARENT_PARAM, "FALSE" );
        this.reqEntity.addKvp( EXCEPTIONS_PARAM, "BLANK" );
        this.reqEntity.addKvp( BGCOLOR_PARAM, "0x000000" );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        storeResponseImage( rsp, "Requirement26", "blankExceptionExpected_blackBackground", requestFormat );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertStatusCode( rsp.getStatus(), 200 );
        assertContentType( rsp.getHeaders(), requestFormat );
    }

}