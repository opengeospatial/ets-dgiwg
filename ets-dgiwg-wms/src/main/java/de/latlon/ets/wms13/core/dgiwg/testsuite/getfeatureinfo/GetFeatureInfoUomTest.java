package de.latlon.ets.wms13.core.dgiwg.testsuite.getfeatureinfo;

import static de.latlon.ets.wms13.core.domain.DGIWGWMS.BBOX_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.CRS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_FEATURE_INFO;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.LAYERS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.QUERY_LAYERS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.STYLES_PARAM;
import static de.latlon.ets.wms13.core.domain.ProtocolBinding.GET;
import static de.latlon.ets.wms13.core.util.request.WmsRequestBuilder.findBoundingBox;
import static javax.xml.xpath.XPathConstants.BOOLEAN;
import static javax.xml.xpath.XPathConstants.STRING;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.domain.BoundingBox;
import de.latlon.ets.wms13.core.domain.Dimension;
import de.latlon.ets.wms13.core.domain.LayerInfo;
import de.latlon.ets.wms13.core.uom.UomMatcher;
import de.latlon.ets.wms13.core.uom.UomMatcherFromFile;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * Tests units of measure for dimensional values returned in a GetFeatureInfo response.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetFeatureInfoUomTest extends BaseGetFeatureInfoFixture {

    private final UomMatcher uomMatcher = new UomMatcherFromFile();

    @DataProvider(name = "layerInfoWithDimension")
    public Object[][] provideLayerInfoWithDimension( ITestContext testContext ) {
        initBaseFixture( testContext );

        List<LayerInfo> layerInfoWithDimension = new ArrayList<LayerInfo>();
        for ( LayerInfo layerInfo : this.layerInfo ) {
            if ( !layerInfo.getDimensions().isEmpty() && layerInfo.isQueryable() )
                layerInfoWithDimension.add( layerInfo );
        }
        Object[][] resultingLayerInfo = new Object[layerInfoWithDimension.size()][];
        for ( LayerInfo layerInfo : layerInfoWithDimension ) {
            resultingLayerInfo[layerInfoWithDimension.indexOf( layerInfo )] = new Object[] { layerInfo };
        }
        return resultingLayerInfo;

    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.6, S.22, Requirement 32", dataProvider = "layerInfoWithDimension")
    public
                    void wmsGetFeatureInfoInfoUnitOfMeasureOfDimensionalData( LayerInfo layerInfoWithDimension )
                                    throws SOAPException, XPathExpressionException, XPathFactoryConfigurationException {
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, GET_FEATURE_INFO, GET );

        BoundingBox bbox = findBoundingBox( layerInfoWithDimension );

        String layerName = layerInfoWithDimension.getLayerName();
        reqEntity.addKvp( LAYERS_PARAM, layerName );
        reqEntity.addKvp( STYLES_PARAM, "" );
        reqEntity.addKvp( CRS_PARAM, bbox.getCrs() );
        reqEntity.addKvp( BBOX_PARAM, bbox.getBboxAsString() );
        reqEntity.addKvp( QUERY_LAYERS_PARAM, layerName );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );
        this.rspEntity = rsp.getEntity( Document.class );
        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        NodeList featureMemberNodes = parseFeatureMembers( layerName );

        assertResponseContainsUnitOfMeasure( layerInfoWithDimension, featureMemberNodes );
    }

    private NodeList parseFeatureMembers( String layerName )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        NodeList featureMemberNodes = parseFeatureMemberNodes( this.rspEntity );
        if ( featureMemberNodes.getLength() == 0 )
            throw new SkipException( "Layer " + layerName
                                     + " does not contain any results in response of GetFeatureInfo." );
        return featureMemberNodes;
    }

    private void assertResponseContainsUnitOfMeasure( LayerInfo layerInfoWithDimension, NodeList featureMemberNodes )
                    throws XPathExpressionException, XPathFactoryConfigurationException {
        for ( Dimension dimension : layerInfoWithDimension.getDimensions() ) {
            String dimensionName = dimension.getName();
            for ( int memberIndex = 0; memberIndex < featureMemberNodes.getLength(); memberIndex++ ) {
                Node featureMember = featureMemberNodes.item( memberIndex );
                if ( containsDimension( dimensionName, featureMember ) ) {
                    assertUom( dimensionName, featureMember );
                }
            }

        }
    }

    private void assertUom( String dimensionName, Node featureMember )
                    throws XPathExpressionException, XPathFactoryConfigurationException, AssertionError {
        String expression = String.format( "//*[local-name() = '%s_uom']", dimensionName );
        String returnedUom = (String) createXPath().evaluate( expression, featureMember, STRING );
        if ( returnedUom == null || returnedUom.equals( "" ) ) {
            throw new AssertionError( "Missing UoM for dimension " + dimensionName + "!" );
        } else {
            assertTrue( uomMatcher.isExpectedUoM( returnedUom ), "UoM '" + returnedUom + "' of dimension "
                                                                 + dimensionName + " is not expected!" );
        }
    }

    private boolean containsDimension( String dimensionName, Node featureMember )
                    throws XPathExpressionException, XPathFactoryConfigurationException {
        String expression = String.format( "//*[local-name() = '%s']", dimensionName );
        return (boolean) createXPath().evaluate( expression, featureMember, BOOLEAN );
    }

}