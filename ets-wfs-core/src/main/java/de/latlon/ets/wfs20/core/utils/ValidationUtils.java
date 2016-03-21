package de.latlon.ets.wfs20.core.utils;

import java.net.URL;
import java.util.logging.Level;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.opengis.cite.iso19142.util.TestSuiteLogger;
import org.opengis.cite.validation.XmlSchemaCompiler;
import org.xml.sax.SAXException;

/**
 * A utility class that provides convenience methods to support schema validation.
 */
public class ValidationUtils {

    /**
     * Creates a single Schema object representing the complete set of XML Schema constraints that apply to WFS 2.0
     * message entities.
     *
     * @return An immutable Schema object, or <code>null</code> if one cannot be constructed.
     *
     * @see <a href="http://schemas.opengis.net/wfs/2.0/wfs.xsd" target="_blank">XML Schema for WFS 2.0</a>
     */
    public static Schema createWFSSchema() {
        URL entityCatalog = ValidationUtils.class.getResource( "schema-catalog.xml" );
        XmlSchemaCompiler xsdCompiler = new XmlSchemaCompiler( entityCatalog );
        Schema wfsSchema = null;
        try {
            URL schemaURL = ValidationUtils.class.getResource( "xsd/opengis/wfs/2.0/wfs-2.0.0.xsd" );
            Source xsdSource = new StreamSource( schemaURL.toString() );
            wfsSchema = xsdCompiler.compileXmlSchema( new Source[] { xsdSource } );
        } catch ( SAXException e ) {
            TestSuiteLogger.log( Level.WARNING, "Failed to create WFS Schema object.", e );
        }
        return wfsSchema;
    }

}
