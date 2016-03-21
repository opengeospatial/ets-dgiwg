package de.latlon.ets.wms13.core.dgiwg.testsuite.getmap;

import static de.latlon.ets.core.assertion.ETSAssert.assertContentType;
import static de.latlon.ets.core.assertion.ETSAssert.assertStatusCode;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.BBOX_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.BGCOLOR_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.CRS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.ELEVATION_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.EXCEPTIONS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.FORMAT_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_MAP;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.LAYERS_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.TIME_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.TRANSPARENT_PARAM;
import static de.latlon.ets.wms13.core.domain.ProtocolBinding.GET;
import static de.latlon.ets.wms13.core.util.ServiceMetadataUtils.getOperationEndpoint;
import static de.latlon.ets.wms13.core.util.request.WmsRequestBuilder.findBoundingBox;
import static de.latlon.ets.wms13.core.util.request.WmsRequestBuilder.getSupportedTransparentFormat;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import javax.xml.soap.SOAPException;

import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.collections.Pair;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.wms13.core.domain.BoundingBox;
import de.latlon.ets.wms13.core.domain.Dimension;
import de.latlon.ets.wms13.core.domain.LayerInfo;

/**
 * Tests if the multi-dimensional data are supported.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetMapMultiDimensionalDataTest extends BaseGetMapFixture {

    @BeforeMethod
    public void clearRequest() {
        this.reqEntity.removeKvp( FORMAT_PARAM );
        this.reqEntity.removeKvp( LAYERS_PARAM );
        this.reqEntity.removeKvp( CRS_PARAM );
        this.reqEntity.removeKvp( BBOX_PARAM );
        this.reqEntity.removeKvp( FORMAT_PARAM );
        this.reqEntity.removeKvp( TRANSPARENT_PARAM );
        this.reqEntity.removeKvp( BGCOLOR_PARAM );
        this.reqEntity.removeKvp( EXCEPTIONS_PARAM );
        this.reqEntity.removeKvp( ELEVATION_PARAM );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.3., S.20, Requirement 27")
    public void wmsGetMapMultiDimensionalDataSupported_Elevation( ITestContext testContext )
                    throws SOAPException {
        URI endpoint = getOperationEndpoint( this.wmsCapabilities, GET_MAP, GET );

        String requestFormat = getSupportedTransparentFormat( wmsCapabilities, GET_MAP );
        Pair<LayerInfo, Dimension> layerInfoAndDimension = findLayerinfoWithElevationDimension( layerInfo );
        LayerInfo layerInfoWithElevationDimension = layerInfoAndDimension.first();

        BoundingBox bbox = findBoundingBox( layerInfoWithElevationDimension );
        String elevationParameterValue = findParameterValue( layerInfoAndDimension );

        this.reqEntity.addKvp( FORMAT_PARAM, requestFormat );
        this.reqEntity.addKvp( LAYERS_PARAM, layerInfoWithElevationDimension.getLayerName() );
        this.reqEntity.addKvp( CRS_PARAM, bbox.getCrs() );
        this.reqEntity.addKvp( BBOX_PARAM, bbox.getBboxAsString() );
        this.reqEntity.addKvp( TRANSPARENT_PARAM, "TRUE" );
        this.reqEntity.addKvp( BGCOLOR_PARAM, "0xFFFFFF" );
        this.reqEntity.addKvp( EXCEPTIONS_PARAM, "XML" );
        this.reqEntity.addKvp( ELEVATION_PARAM, elevationParameterValue );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        storeResponseImage( rsp, "Requirement27", "multiDimensionalData_elevation", requestFormat );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertStatusCode( rsp.getStatus(), 200 );
        assertContentType( rsp.getHeaders(), requestFormat );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.3., S.20, Requirement 27")
    public void wmsGetMapMultiDimensionalDataSupported_Time( ITestContext testContext )
                    throws SOAPException {
        URI endpoint = getOperationEndpoint( this.wmsCapabilities, GET_MAP, GET );

        String requestFormat = getSupportedTransparentFormat( wmsCapabilities, GET_MAP );
        Pair<LayerInfo, Dimension> layerInfoAndDimension = findLayerinfoWithTimeDimension( layerInfo );
        LayerInfo layerInfoWithTimeDimension = layerInfoAndDimension.first();

        BoundingBox bbox = findBoundingBox( layerInfoWithTimeDimension );
        String timeParameterValue = findParameterValue( layerInfoAndDimension );

        this.reqEntity.addKvp( FORMAT_PARAM, requestFormat );
        this.reqEntity.addKvp( LAYERS_PARAM, layerInfoWithTimeDimension.getLayerName() );
        this.reqEntity.addKvp( CRS_PARAM, bbox.getCrs() );
        this.reqEntity.addKvp( BBOX_PARAM, bbox.getBboxAsString() );
        this.reqEntity.addKvp( TRANSPARENT_PARAM, "TRUE" );
        this.reqEntity.addKvp( BGCOLOR_PARAM, "0xFFFFFF" );
        this.reqEntity.addKvp( EXCEPTIONS_PARAM, "XML" );
        this.reqEntity.addKvp( TIME_PARAM, timeParameterValue );

        ClientResponse rsp = wmsClient.submitRequest( this.reqEntity, endpoint );

        storeResponseImage( rsp, "Requirement27", "multiDimensionalData_time", requestFormat );

        assertTrue( rsp.hasEntity(), ErrorMessage.get( ErrorMessageKey.MISSING_XML_ENTITY ) );
        assertStatusCode( rsp.getStatus(), 200 );
        assertContentType( rsp.getHeaders(), requestFormat );
    }

    private Pair<LayerInfo, Dimension> findLayerinfoWithElevationDimension( List<LayerInfo> layerInfos ) {
        return findLayerWithDimension( layerInfos, "elevation" );
    }

    private Pair<LayerInfo, Dimension> findLayerinfoWithTimeDimension( List<LayerInfo> layerInfos ) {
        return findLayerWithDimension( layerInfos, "time" );
    }

    private Pair<LayerInfo, Dimension> findLayerWithDimension( List<LayerInfo> layerInfos, String dimensionName ) {
        for ( LayerInfo layerInfo : layerInfos ) {
            List<Dimension> dimensions = layerInfo.getDimensions();
            for ( Dimension dimension : dimensions ) {
                if ( dimensionName.equalsIgnoreCase( dimension.getName() ) )
                    return new Pair<LayerInfo, Dimension>( layerInfo, dimension );
            }
        }
        throw new SkipException( "No layer with dimension '" + dimensionName + "' supported!" );

    }

    private String findParameterValue( Pair<LayerInfo, Dimension> layerInfoAndDimension ) {
        return layerInfoAndDimension.second().getDimensionValue().getRequestableDimension().retrieveRequestableValue();
    }

}