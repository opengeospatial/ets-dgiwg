package de.latlon.ets.wms13.core.dgiwg.testsuite.getmap;

import static de.latlon.ets.core.assertion.ETSAssert.assertContentType;
import static de.latlon.ets.core.assertion.ETSAssert.assertStatusCode;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.FORMAT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_MAP;
import static de.latlon.ets.wms13.core.util.request.WmsRequestBuilder.getSupportedFormat;
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
 * Tests if the all GetMap request parameters are supported.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetMapRequestParametersTest extends BaseGetMapFixture {

    @BeforeMethod
    public void clearRequest() {
        this.reqEntity.removeKvp( FORMAT_PARAM );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.3., S.19, Requirement 23")
    public void wmsGetMapRequestParametersSupported( ITestContext testContext )
                    throws SOAPException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_MAP, ProtocolBinding.GET );
        String requestFormat = getSupportedFormat( wmsCapabilities, GET_MAP );
        this.reqEntity.addKvp( FORMAT_PARAM, requestFormat );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        storeResponseImage( rsp, "Requirement23", "simple", requestFormat );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertStatusCode( rsp.getStatus(), 200 );
        assertContentType( rsp.getHeaders(), requestFormat );
    }

}