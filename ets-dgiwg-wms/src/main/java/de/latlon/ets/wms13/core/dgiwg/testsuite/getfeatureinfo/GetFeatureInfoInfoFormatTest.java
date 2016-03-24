package de.latlon.ets.wms13.core.dgiwg.testsuite.getfeatureinfo;

import static de.latlon.ets.core.assertion.ETSAssert.assertContentType;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_FEATURE_INFO;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.INFO_FORMAT_PARAM;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests INFO_FORMAT parameter of GetFeatureInfo request.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 */
public class GetFeatureInfoInfoFormatTest extends BaseGetFeatureInfoFixture {

    @DataProvider(name = "supportedFormats")
    public Object[][] parseSupportedFormats( ITestContext testContext )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        List<String> formats = ServiceMetadataUtils.parseSupportedFormats( wmsCapabilities, GET_FEATURE_INFO );
        Object[][] supportedFormats = new Object[formats.size()][];
        for ( int indexOfFormat = 0; indexOfFormat < supportedFormats.length; indexOfFormat++ ) {
            supportedFormats[indexOfFormat] = new Object[] { formats.get( indexOfFormat ) };
        }
        return supportedFormats;
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.6, S.22, Requirement 31", dataProvider = "supportedFormats")
    public
                    void wmsGetFeatureInfoInfoFormatWithAllValuesOfCapabilities( String supportedFormat )
                                    throws SOAPException, XPathExpressionException, XPathFactoryConfigurationException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_FEATURE_INFO,
                                                                  ProtocolBinding.GET );
        this.reqEntity.addKvp( INFO_FORMAT_PARAM, supportedFormat );
        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertContentType( rsp.getHeaders(), supportedFormat );
    }

}