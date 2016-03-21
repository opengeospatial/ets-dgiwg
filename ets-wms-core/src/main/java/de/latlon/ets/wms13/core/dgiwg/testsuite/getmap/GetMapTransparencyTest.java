package de.latlon.ets.wms13.core.dgiwg.testsuite.getmap;

import static de.latlon.ets.core.assertion.ETSAssert.assertStatusCode;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.BGCOLOR_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.FORMAT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_MAP;
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
 * Tests if the transparency is supported.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetMapTransparencyTest extends BaseGetMapFixture {

    @BeforeMethod
    public void clearRequest() {
        this.reqEntity.removeKvp( FORMAT_PARAM );
        this.reqEntity.removeKvp( TRANSPARENT_PARAM );
        this.reqEntity.removeKvp( BGCOLOR_PARAM );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.3., S.19, Requirement 24")
    public void wmsGetMapTransparencySupported_True( ITestContext testContext )
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_MAP, ProtocolBinding.GET );
        String requestFormat = findRequiredImageFormatWithTransparencySupport();

        this.reqEntity.addKvp( FORMAT_PARAM, requestFormat );
        this.reqEntity.addKvp( TRANSPARENT_PARAM, "TRUE" );
        this.reqEntity.addKvp( BGCOLOR_PARAM, "0x000000" );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        storeResponseImage( rsp, "Requirement24", "transparentTrue_transparentBackground", requestFormat );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertStatusCode( rsp.getStatus(), 200 );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.3., S.19, Requirement 24")
    public void wmsGetMapTransparencySupported_False( ITestContext testContext )
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_MAP, ProtocolBinding.GET );
        String requestFormat = findRequiredImageFormatWithTransparencySupport();

        this.reqEntity.addKvp( FORMAT_PARAM, requestFormat );
        this.reqEntity.addKvp( TRANSPARENT_PARAM, "FALSE" );
        this.reqEntity.addKvp( BGCOLOR_PARAM, "0x000000" );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        storeResponseImage( rsp, "Requirement24", "transparentFalse_blackBackground", requestFormat );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertStatusCode( rsp.getStatus(), 200 );
    }

}