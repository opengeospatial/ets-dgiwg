package de.latlon.ets.wms13.core.domain;

import javax.xml.XMLConstants;

import de.latlon.ets.core.util.NamespaceBindings;

/**
 * XML namespace names.
 * 
 * @see <a href="http://www.w3.org/TR/xml-names/">Namespaces in XML 1.0</a>
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public final class WmsNamespaces {

    private WmsNamespaces() {
    }

    /** OGC 06-042 (WMS 1.3) */
    public static final String WMS = "http://www.opengis.net/wms";

    /** W3C XLink */
    public static final String XLINK = "http://www.w3.org/1999/xlink";

    /** GML */
    public static final String GML = "http://www.opengis.net/gml";

    /** ExtendedCapabilities Namespace used for SOAP binding */
    public static final String SOAPWMS = "http://schemas.deegree.org/extensions/services/wms/1.3.0";

    /**
     * Creates a NamespaceBindings object that declares the following namespace bindings:
     * 
     * <ul>
     * <li>wms: {@value de.latlon.ets.wms13.core.domain.WmsNamespaces#WMS}</li>
     * <li>xlink: {@value de.latlon.ets.wms13.core.domain.WmsNamespaces#XLINK}</li>
     * </ul>
     * 
     * @return A NamespaceBindings object.
     */
    public static NamespaceBindings withStandardBindings() {
        NamespaceBindings nsBindings = new NamespaceBindings();
        nsBindings.addNamespaceBinding( WmsNamespaces.WMS, "wms" );
        nsBindings.addNamespaceBinding( WmsNamespaces.XLINK, "xlink" );
        nsBindings.addNamespaceBinding( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi" );
        nsBindings.addNamespaceBinding( WmsNamespaces.GML, "gml" );
        nsBindings.addNamespaceBinding( WmsNamespaces.SOAPWMS, "soapwms" );
        return nsBindings;
    }
    
}