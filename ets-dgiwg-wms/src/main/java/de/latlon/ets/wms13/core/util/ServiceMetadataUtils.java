package de.latlon.ets.wms13.core.util;

import java.net.URI;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.latlon.ets.core.util.NamespaceBindings;
import de.latlon.ets.core.util.TestSuiteLogger;
import de.latlon.ets.wms13.core.domain.BoundingBox;
import de.latlon.ets.wms13.core.domain.Dimension;
import de.latlon.ets.wms13.core.domain.LayerInfo;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.domain.WmsNamespaces;
import de.latlon.ets.wms13.core.domain.dimension.DimensionUnitValue;
import de.latlon.ets.wms13.core.domain.dimension.RequestableDimension;
import de.latlon.ets.wms13.core.domain.dimension.RequestableDimensionList;
import de.latlon.ets.wms13.core.domain.dimension.date.DateTimeDimensionInterval;
import de.latlon.ets.wms13.core.domain.dimension.date.DateTimeRequestableDimension;
import de.latlon.ets.wms13.core.domain.dimension.number.NumberDimensionInterval;
import de.latlon.ets.wms13.core.domain.dimension.number.NumberRequestableDimension;

/**
 * Provides various utility methods for accessing service metadata.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public final class ServiceMetadataUtils {

    private static final NamespaceBindings NS_BINDINGS = WmsNamespaces.withStandardBindings();

    private ServiceMetadataUtils() {
    }

    /**
     * Extracts a request endpoint from a WMS capabilities document. If the request URI contains a query component it is
     * removed (but not from the source document).
     * 
     * @param wmsMetadata
     *            the document node containing service metadata (OGC capabilities document).
     * @param opName
     *            the operation (request) name
     * @param binding
     *            the message binding to use (if {@code null} any supported binding will be used)
     * @return the URI referring to a request endpoint, <code>null</code> if no matching endpoint is found
     */
    public static URI getOperationEndpoint( final Document wmsMetadata, String opName, ProtocolBinding binding ) {
        if ( null == binding || binding.equals( ProtocolBinding.ANY ) ) {
            binding = getOperationBindings( wmsMetadata, opName ).iterator().next();
        }
        if ( binding == null )
            return null;

        String expr = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:%s/wms:DCPType/wms:HTTP/wms:%s/wms:OnlineResource/@xlink:href";
        String xPathExpr = String.format( expr, opName, binding.getElementName() );

        String href = null;
        try {
            XPath xPath = createXPath();
            href = xPath.evaluate( xPathExpr, wmsMetadata );
        } catch ( XPathExpressionException ex ) {
            TestSuiteLogger.log( Level.INFO, ex.getMessage() );
        }

        return createEndpoint( href );
    }

    /**
     * Determines which protocol bindings are supported for a given operation.
     * 
     * @param wmsMetadata
     *            the capabilities document (wms:WMS_Capabilities), never <code>null</code>
     * @param opName
     *            the name of the WMS operation
     * @return A Set of protocol bindings supported for the operation. May be empty but never <code>null</code>.
     */
    public static Set<ProtocolBinding> getOperationBindings( final Document wmsMetadata, String opName ) {
        Set<ProtocolBinding> protoBindings = new HashSet<>();

        if ( isOperationBindingSupported( wmsMetadata, opName, ProtocolBinding.GET ) )
            protoBindings.add( ProtocolBinding.GET );
        if ( isOperationBindingSupported( wmsMetadata, opName, ProtocolBinding.POST ) )
            protoBindings.add( ProtocolBinding.POST );

        return protoBindings;
    }

    /**
     * Parses the configured formats for the given operation.
     * 
     * @param wmsCapabilities
     *            the capabilities document (wms:WMS_Capabilities), never <code>null</code>
     * @param opName
     *            the name of the WMS operation
     * @return a list of the supported formats by the operation, never <code>null</code>
     */
    public static List<String> parseSupportedFormats( Document wmsCapabilities, String opName ) {
        ArrayList<String> supportedFormats = new ArrayList<>();

        String expr = "//wms:WMS_Capabilities/wms:Capability/wms:Request/wms:%s/wms:Format";
        String xPathExpr = String.format( expr, opName );

        try {
            XPath xPath = createXPath();
            NodeList formatNodes = (NodeList) xPath.evaluate( xPathExpr, wmsCapabilities, XPathConstants.NODESET );
            for ( int formatNodeIndex = 0; formatNodeIndex < formatNodes.getLength(); formatNodeIndex++ ) {
                Node formatNode = formatNodes.item( formatNodeIndex );
                String format = formatNode.getTextContent();
                if ( format != null && !format.isEmpty() )
                    supportedFormats.add( format );
            }
        } catch ( XPathExpressionException ex ) {
            TestSuiteLogger.log( Level.INFO, ex.getMessage() );
        }

        return supportedFormats;
    }

    /**
     * Parses all named layers from the capabilities document.
     * 
     * @param wmsCapabilities
     *            the capabilities document (wms:WMS_Capabilities), never <code>null</code>
     * @return a list of {@link LayerInfo}s supported by the WMS, never <code>null</code>
     */
    public static List<LayerInfo> parseLayerInfo( Document wmsCapabilities ) {
        ArrayList<LayerInfo> layerInfos = new ArrayList<>();
        try {
            String layerExpr = "//wms:Layer[wms:Name/text() != '']";

            XPath xPath = createXPath();
            NodeList layerNodes = (NodeList) xPath.evaluate( layerExpr, wmsCapabilities, XPathConstants.NODESET );

            for ( int layerNodeIndex = 0; layerNodeIndex < layerNodes.getLength(); layerNodeIndex++ ) {
                Node layerNode = layerNodes.item( layerNodeIndex );
                LayerInfo layerInfo = parseLayerInfo( xPath, layerNode );
                layerInfos.add( layerInfo );
            }

        } catch ( XPathExpressionException xpe ) {
            throw new RuntimeException( "Error evaluating XPath expression against capabilities doc. ", xpe );
        } catch ( ParseException e ) {
            throw new RuntimeException( "Error parsing layer infos from doc. ", e );
        }
        return layerInfos;
    }

    /**
     * Parses the updateSequence value from the capabilities document.
     * 
     * @param wmsCapabilities
     *            the capabilities document (wms:WMS_Capabilities), never <code>null</code>
     * @return the value of the {@link ServiceMetadataUtils} attribute, <code>null</code> if the attribute is missing or
     *         the value empty
     */
    public static String parseUpdateSequence( Document wmsCapabilities ) {
        try {
            String layerExpr = "//wms:WMS_Capabilities/@updateSequence ";
            XPath xPath = createXPath();
            String updateSequence = (String) xPath.evaluate( layerExpr, wmsCapabilities, XPathConstants.STRING );
            return updateSequence == null || updateSequence.isEmpty() ? null : updateSequence;
        } catch ( XPathExpressionException xpe ) {
            throw new RuntimeException( "Error evaluating XPath expression against capabilities doc. ", xpe );
        }
    }

    /**
     * Parses the EX_GeographicBoundingBox from the layer.
     * 
     * @param layerNode
     *            node of the layer, never <code>null</code>
     * @return the {@link BoundingBox} - crs is CRS:84, <code>null</code> if missing
     */
    public static BoundingBox parseGeographicBoundingBox( Node layerNode ) {
        XPath xPath = createXPath();
        String bboxesExpr = "ancestor-or-self::wms:Layer/wms:EX_GeographicBoundingBox";
        try {
            NodeList bboxNodes = (NodeList) xPath.evaluate( bboxesExpr, layerNode, XPathConstants.NODESET );
            Node bboxNode = bboxNodes.item( bboxNodes.getLength() - 1 );
            double minX = asDouble( bboxNode, "wms:westBoundLongitude", xPath );
            double minY = asDouble( bboxNode, "wms:southBoundLatitude", xPath );
            double maxX = asDouble( bboxNode, "wms:eastBoundLongitude", xPath );
            double maxY = asDouble( bboxNode, "wms:northBoundLatitude", xPath );
            return new BoundingBox( "CRS:84", minX, minY, maxX, maxY );
        } catch ( XPathExpressionException xpe ) {
            throw new RuntimeException(
                            "Error evaluating XPath expression against capabilities doc while parsing geographic BBOX of layer. ",
                            xpe );
        }
    }

    /**
     * Parses all Layer elements of a capabilities document.
     * 
     * @param wmsCapabilities
     *            capabilities document, never <code>null</code>
     * @return node list containing all layer elements of the capabilities document
     * @throws XPathFactoryConfigurationException
     * @throws XPathExpressionException
     */
    public static NodeList parseAllLayerNodes( Document wmsCapabilities )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        String xPathAbstract = "//wms:Layer";
        return createNodeList( wmsCapabilities, xPathAbstract );
    }

    /**
     * Parses all requestable Layer elements of a capabilities document. Requestable layers are identified by the
     * existence of a Name sub element.
     * 
     * @param wmsCapabilities
     *            capabilities document, never <code>null</code>
     * @return node list containing all requestable layer elements of the capabilities document
     * @throws XPathFactoryConfigurationException
     * @throws XPathExpressionException
     */
    public static NodeList parseRequestableLayerNodes( Document wmsCapabilities )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        String xPathAbstract = "//wms:Layer[wms:Name]";
        return createNodeList( wmsCapabilities, xPathAbstract );
    }

    static RequestableDimension parseRequestableDimension( String units, String value )
                    throws ParseException {
        if ( value.contains( "," ) )
            return parseListRequestableDimension( units, value );
        return parseSingleRequestableDimension( units, value );
    }

    private static NodeList createNodeList( Document wmsCapabilities, String xPathAbstract )
                    throws XPathExpressionException {
        XPath xpath = createXPath();
        return (NodeList) xpath.evaluate( xPathAbstract, wmsCapabilities, XPathConstants.NODESET );
    }

    private static RequestableDimension parseListRequestableDimension( String units, String value )
                    throws ParseException {
        List<RequestableDimension> requestableDimensions = new ArrayList<>();
        String[] singleValues = value.split( "," );
        for ( String singleValue : singleValues ) {
            requestableDimensions.add( parseSingleRequestableDimension( units, singleValue ) );
        }
        return new RequestableDimensionList( requestableDimensions );
    }

    private static RequestableDimension parseSingleRequestableDimension( String units, String singleValue )
                    throws ParseException {
        if ( singleValue.contains( "/" ) )
            return parseInterval( units, singleValue );
        return parseSingleValue( units, singleValue );
    }

    private static RequestableDimension parseInterval( String units, String token )
                    throws ParseException {
        String[] minMaxRes = token.split( "/" );
        if ( minMaxRes.length != 3 )
            throw new IllegalArgumentException( "Cannot parse " + token + " as interval." );
        if ( "ISO8601".equals( units ) ) {
            DateTime min = parseDateTime( minMaxRes[0] );
            DateTime max = parseDateTime( minMaxRes[1] );
            Period resolution = parseResolution( minMaxRes[2] );
            return new DateTimeDimensionInterval( min, max, resolution );
        }
        Number min = parseNumber( minMaxRes[0] );
        Number max = parseNumber( minMaxRes[1] );
        Number resolution = parseNumber( minMaxRes[2] );
        return new NumberDimensionInterval( min, max, resolution );
    }

    private static Period parseResolution( String resolution ) {
        if ( "0".equals( resolution ) )
            return null;
        return ISOPeriodFormat.standard().parsePeriod( resolution );
    }

    private static RequestableDimension parseSingleValue( String units, String value )
                    throws ParseException {
        if ( "ISO8601".equals( units ) ) {
            DateTime dateTime = parseDateTime( value );
            return new DateTimeRequestableDimension( dateTime );
        }
        Number number = parseNumber( value );
        return new NumberRequestableDimension( number );
    }

    private static Number parseNumber( String token )
                    throws ParseException {
        NumberFormat instance = NumberFormat.getInstance( Locale.ENGLISH );
        instance.setParseIntegerOnly( false );
        return instance.parse( token );
    }

    private static DateTime parseDateTime( String token ) {
        Calendar dateTime = DatatypeConverter.parseDateTime( token );
        return new DateTime( dateTime.getTimeInMillis() );
    }

    private static LayerInfo parseLayerInfo( XPath xPath, Node layerNode )
                    throws XPathExpressionException, ParseException {
        String layerName = (String) xPath.evaluate( "wms:Name", layerNode, XPathConstants.STRING );
        boolean isQueryable = parseQueryable( xPath, layerNode );
        List<BoundingBox> bboxes = parseBoundingBoxes( xPath, layerNode );
        List<Dimension> dimensions = parseDimensions( xPath, layerNode );
        BoundingBox geographicBbox = parseGeographicBoundingBox( layerNode );
        return new LayerInfo( layerName, isQueryable, bboxes, dimensions, geographicBbox );
    }

    private static boolean parseQueryable( XPath xPath, Node layerNode )
                    throws XPathExpressionException {
        String queryableAttribute = (String) xPath.evaluate( "@queryable", layerNode, XPathConstants.STRING );
        return queryableAttribute != null && "1".equals( queryableAttribute ) ? true : false;
    }

    private static List<BoundingBox> parseBoundingBoxes( XPath xPath, Node layerNode )
                    throws XPathExpressionException {
        Map<String, BoundingBox> bboxes = new HashMap<>();
        String bboxesExpr = "ancestor-or-self::wms:Layer/wms:BoundingBox";
        NodeList bboxNodes = (NodeList) xPath.evaluate( bboxesExpr, layerNode, XPathConstants.NODESET );
        for ( int bboxNodeIndex = 0; bboxNodeIndex < bboxNodes.getLength(); bboxNodeIndex++ ) {
            Node bboxNode = bboxNodes.item( bboxNodeIndex );
            BoundingBox bbox = parseBoundingBox( bboxNode );
            bboxes.put( bbox.getCrs(), bbox );
        }
        return new ArrayList<>( bboxes.values() );
    }

    private static List<Dimension> parseDimensions( XPath xPath, Node layerNode )
                    throws XPathExpressionException, ParseException {
        ArrayList<Dimension> dimensions = new ArrayList<>();
        String dimensionExpr = "wms:Dimension";
        NodeList dimensionNodes = (NodeList) xPath.evaluate( dimensionExpr, layerNode, XPathConstants.NODESET );
        for ( int dimensionNodeIndex = 0; dimensionNodeIndex < dimensionNodes.getLength(); dimensionNodeIndex++ ) {
            Node dimensionNode = dimensionNodes.item( dimensionNodeIndex );
            Dimension dimension = parseDimension( xPath, dimensionNode );
            if ( dimension != null )
                dimensions.add( dimension );
        }
        return dimensions;
    }

    private static BoundingBox parseBoundingBox( Node bboxNode )
                    throws XPathExpressionException {
        XPath xPath = createXPath();
        double minx = asDouble( bboxNode, "@minx", xPath );
        double miny = asDouble( bboxNode, "@miny", xPath );
        double maxx = asDouble( bboxNode, "@maxx", xPath );
        double maxy = asDouble( bboxNode, "@maxy", xPath );
        String crs = (String) xPath.evaluate( "@CRS", bboxNode, XPathConstants.STRING );
        return new BoundingBox( crs, minx, miny, maxx, maxy );
    }

    private static Dimension parseDimension( XPath xPath, Node dimensionNode )
                    throws XPathExpressionException, ParseException {
        String name = (String) xPath.evaluate( "@name", dimensionNode, XPathConstants.STRING );
        if ( name != null ) {
            String units = (String) xPath.evaluate( "@units", dimensionNode, XPathConstants.STRING );
            String value = (String) xPath.evaluate( "text()", dimensionNode, XPathConstants.STRING );
            RequestableDimension requestableDimension = parseRequestableDimension( units, value );
            DimensionUnitValue unitValue = new DimensionUnitValue( units, requestableDimension );
            return new Dimension( name, unitValue );
        }
        return null;
    }

    private static double asDouble( Node node, String xPathExpr, XPath xPath )
                    throws XPathExpressionException {
        String content = (String) xPath.evaluate( xPathExpr, node, XPathConstants.STRING );
        return Double.parseDouble( content );
    }

    private static boolean isOperationBindingSupported( Document wmsMetadata, String opName, ProtocolBinding binding ) {
        String exprTemplate = "count(/wms:WMS_Capabilities/wms:Capability/wms:Request/wms:%s/wms:DCPType/wms:HTTP/wms:%s)";
        String xPathExpr = String.format( exprTemplate, opName, binding.getElementName() );
        try {
            XPath xPath = createXPath();
            Double bindings = (Double) xPath.evaluate( xPathExpr, wmsMetadata, XPathConstants.NUMBER );
            if ( bindings > 0 ) {
                return true;
            }
        } catch ( XPathExpressionException xpe ) {
            throw new RuntimeException( "Error evaluating XPath expression against capabilities doc. ", xpe );
        }
        return false;
    }

    private static URI createEndpoint( String href ) {
        if ( href == null || href.isEmpty() )
            return null;
        URI endpoint = URI.create( href );
        if ( null != endpoint.getQuery() ) {
            String uri = endpoint.toString();
            endpoint = URI.create( uri.substring( 0, uri.indexOf( '?' ) ) );
        }
        return endpoint;
    }

    private static XPath createXPath() {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext( NS_BINDINGS );
        return xPath;
    }

    public static URI getSoapOperationEndpoint( Document wmsCapabilities, String operation ) {
        if ( isSoapSupported( wmsCapabilities, operation ) ) {
            String href = parseSoapEndpoint( wmsCapabilities );
            return createEndpoint( href );
        }
        return null;
    }

    private static String parseSoapEndpoint( Document wmsCapabilities ) {
        final String xPathExpr = "//wms:WMS_Capabilities/wms:Capability/soapwms:ExtendedCapabilities/soapwms:SOAP/wms:OnlineResource/@xlink:href";
        String href = null;
        try {
            XPath xPath = createXPath();
            href = xPath.evaluate( xPathExpr, wmsCapabilities );
        } catch ( XPathExpressionException ex ) {
            TestSuiteLogger.log( Level.INFO, ex.getMessage() );
        }
        return href;
    }

    private static boolean isSoapSupported( Document wmsCapabilities, String operation ) {
        final String exprTemplate = "//wms:WMS_Capabilities/wms:Capability/soapwms:ExtendedCapabilities/soapwms:SOAP/soapwms:SupportedOperations/soapwms:Operation[@name='%s']";
        String xPathExpr = String.format( exprTemplate, operation );
        XPath xPath = createXPath();
        try {
            return (Boolean) xPath.evaluate( xPathExpr, wmsCapabilities, XPathConstants.BOOLEAN );
        } catch ( XPathExpressionException xpe ) {
            throw new RuntimeException( "Error evaluating XPath expression against capabilities doc. ", xpe );
        }
    }

}