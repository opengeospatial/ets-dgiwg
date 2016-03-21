package de.latlon.ets.wms13.core.dgiwg.testsuite.getfeatureinfo;

import static de.latlon.ets.wms13.core.domain.DGIWGWMS.FEATURE_COUNT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_FEATURE_INFO;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests feature count functionality of GetFeatureInfo.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 */
public class GetFeatureInfoFeatureCountTest extends BaseGetFeatureInfoFixture {

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.5.6, S.22, Requirement 29")
    public void wmsGetFeatureInfoFeatureCountWithValueOfOne()
                    throws SOAPException, XPathExpressionException, XPathFactoryConfigurationException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_FEATURE_INFO,
                                                                  ProtocolBinding.GET );
        this.reqEntity.addKvp( FEATURE_COUNT_PARAM, "1" );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        Document entity = rsp.getEntity( Document.class );
        NodeList featureMemberNodes = parseFeatureMemberNodes( entity );

        assertTrue( featureMemberNodes.getLength() == 1,
                    "FEATURE_COUNT is set to 1, but the returned number of feature members is not exactly one!" );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.5.6, S.22, Requirement 29")
    public void wmsGetFeatureInfoFeatureCountWithValueOfTen()
                    throws SOAPException, XPathExpressionException, XPathFactoryConfigurationException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_FEATURE_INFO,
                                                                  ProtocolBinding.GET );
        this.reqEntity.addKvp( FEATURE_COUNT_PARAM, "10" );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );

        Document entity = rsp.getEntity( Document.class );
        NodeList featureMemberNodes = parseFeatureMemberNodes( entity );

        assertTrue( featureMemberNodes.getLength() >= 1,
                    "FEATURE_COUNT is set to 10, but less than one feature member is returned!" );
        assertTrue( featureMemberNodes.getLength() <= 10,
                    "FEATURE_COUNT is set to 10, but more than ten feature members are returned!" );
    }

}
