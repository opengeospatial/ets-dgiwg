package de.latlon.ets.wms13.core.dgiwg.testsuite.getfeatureinfo;

import static de.latlon.ets.core.assertion.ETSAssert.assertContentType;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.EXCEPTIONS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_FEATURE_INFO;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.QUERY_LAYERS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.TEXT_HTML;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.TEXT_XML;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.annotations.Test;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests exceptions of GetFeatureInfo request.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 */
public class GetFeatureInfoExceptionsTest extends BaseGetFeatureInfoFixture {

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.5.8, S.22, Requirement 30")
    public void wmsGetFeatureInfoExceptionsWithValueOfXml()
                    throws SOAPException, XPathExpressionException, XPathFactoryConfigurationException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_FEATURE_INFO,
                                                                  ProtocolBinding.GET );
        this.reqEntity.removeKvp( QUERY_LAYERS_PARAM );
        this.reqEntity.addKvp( EXCEPTIONS_PARAM, TEXT_XML );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertContentType( rsp.getHeaders(), TEXT_XML );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.5.8, S.22, Requirement 30")
    public void wmsGetFeatureInfoExceptionsWithValueOfHtml()
                    throws SOAPException, XPathExpressionException, XPathFactoryConfigurationException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_FEATURE_INFO,
                                                                  ProtocolBinding.GET );
        this.reqEntity.removeKvp( QUERY_LAYERS_PARAM );
        this.reqEntity.addKvp( EXCEPTIONS_PARAM, TEXT_HTML );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertContentType( rsp.getHeaders(), TEXT_HTML );
    }

}