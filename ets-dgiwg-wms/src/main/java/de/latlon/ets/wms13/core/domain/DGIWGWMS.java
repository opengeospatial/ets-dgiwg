package de.latlon.ets.wms13.core.domain;

/**
 * Contains various constants pertaining to DGIWG WMS service interfaces and related standards.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public final class DGIWGWMS {

    private DGIWGWMS() {
    }

    /** Capabilities element indicating support for HTTP GET method bindings. */
    public static final String KVP_ENC = "Get";

    /** Capabilities element indicating support for HTTP POST method bindings. */
    public static final String XML_ENC = "Post";

    /** Capabilities element indicating support for SOAP HTTP method bindings. */
    public static final String SOAP_ENC = "Post";

    /** Local name of document element in WMS capabilities document. */
    public static final String WMS_CAPABILITIES = "WMS_Capabilities";

    /** common request values **/
    public static final String SERVICE_TYPE_CODE = "WMS";

    public static final String VERSION = "1.3.0";

    /** request types **/
    public static final String GET_CAPABILITIES = "GetCapabilities";

    public static final String GET_MAP = "GetMap";

    public static final String GET_FEATURE_INFO = "GetFeatureInfo";

    /** common request parameters **/
    public static final String REQUEST_PARAM = "request";

    public static final String SERVICE_PARAM = "service";

    public static final String VERSION_PARAM = "version";

    /** GetCapabilities request parameters **/
    public static final String UPDATE_SEQUENCE_PARAM = "UPDATESEQUENCE";

    /** GetMap and GetCapabilities request parameters **/
    public static final String FORMAT_PARAM = "FORMAT";

    /** GetMap and GetFeatureInfo request parameters **/
    public static final String EXCEPTIONS_PARAM = "EXCEPTIONS";

    /** GetMap request parameters **/
    public static final String LAYERS_PARAM = "LAYERS";

    public static final String STYLES_PARAM = "STYLES";

    public static final String CRS_PARAM = "CRS";

    public static final String BBOX_PARAM = "BBOX";

    public static final String WIDTH_PARAM = "WIDTH";

    public static final String HEIGHT_PARAM = "HEIGHT";

    public static final String TRANSPARENT_PARAM = "TRANSPARENT";

    public static final String BGCOLOR_PARAM = "BGCOLOR";

    public static final String ELEVATION_PARAM = "ELEVATION";

    public static final String TIME_PARAM = "TIME";

    public static final String DIMENSION_PARAM = "DIMENSION";

    /** GetFeatureInfo request parameters **/
    public static final String INFO_FORMAT_PARAM = "INFO_FORMAT";

    public static final String QUERY_LAYERS_PARAM = "QUERY_LAYERS";

    public static final String I_PARAM = "I";

    public static final String J_PARAM = "J";

    public static final String FEATURE_COUNT_PARAM = "FEATURE_COUNT";

    /** FORMATS **/
    public static final String TEXT_XML = "text/xml";

    public static final String SOAP_XML = "application/soap+xml";

    public static final String SOAP_MTOM = "application/xop+xml";

    public static final String TEXT_HTML = "text/html";

    public static final String IMAGE_PNG = "image/png";

    public static final String IMAGE_GIF = "image/gif";

    public static final String IMAGE_JPEG = "image/jpeg";

    public static final String IMAGE_SVG = "image/svg+xml";

}