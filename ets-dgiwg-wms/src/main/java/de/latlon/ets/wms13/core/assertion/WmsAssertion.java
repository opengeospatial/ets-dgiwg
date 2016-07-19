package de.latlon.ets.wms13.core.assertion;

import static de.latlon.ets.core.assertion.ETSAssert.assertQualifiedName;
import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

import de.latlon.ets.wms13.core.domain.DGIWGWMS;
import de.latlon.ets.wms13.core.domain.WmsNamespaces;

/**
 * Provides WMS 1.3.0 specific test assertion methods
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public final class WmsAssertion {

    private WmsAssertion() {
    }

    /**
     * Asserts that the given DOM document has the expected root element 'WMS_Capabilities' in namespace
     * {http://www.opengis.net/wms}.
     * 
     * @param doc
     *            A Document node having {http://www.opengis.net/wms} {@value DGIWGWMS#WMS_CAPABILITIES} as the root
     *            element.
     */
    public static void assertSimpleWMSCapabilities( Document doc ) {
        assertQualifiedName( doc.getDocumentElement(), new QName( WmsNamespaces.WMS, DGIWGWMS.WMS_CAPABILITIES ) );
    }

    /**
     * Asserts that the actual content type matches the expected content type.
     *
     * @param response
     *            A Document node having {http://www.opengis.net/wms} {@value DGIWGWMS#WMS_CAPABILITIES} as the root
     *            element.
     */
    public static void assertVersion130( Document response ) {
        assertXPath( "//wms:WMS_Capabilities/@version = '1.3.0'", response, WmsNamespaces.withStandardBindings() );
    }

}