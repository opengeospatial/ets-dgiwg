package de.latlon.ets.core.util;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Provides various utility methods for accessing or manipulating XML representations.
 */
public final class XMLUtils {

    private static final Logger LOGR = Logger.getLogger( XMLUtils.class.getPackage().getName() );

    private XMLUtils() {
    }

    /**
     * Writes the content of a DOM node to a String. An XML declaration is omitted.
     * 
     * @param node
     *            The node to be serialized.
     * @return A String representing the content of the given node.
     */
    public static String writeNodeToString( Node node ) {
        if ( null == node ) {
            throw new IllegalArgumentException( "Supplied node is null." );
        }
        StringWriter writer = new StringWriter();
        try {
            Transformer idTransformer = TransformerFactory.newInstance().newTransformer();
            Properties outProps = new Properties();
            outProps.setProperty( OutputKeys.ENCODING, "UTF-8" );
            outProps.setProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
            outProps.setProperty( OutputKeys.INDENT, "yes" );
            idTransformer.setOutputProperties( outProps );
            idTransformer.transform( new DOMSource( node ), new StreamResult( writer ) );
        } catch ( TransformerException ex ) {
            LOGR.log( Level.WARNING, "Failed to serialize DOM node: " + node.getNodeName(), ex );
        }
        return writer.toString();
    }

    /**
     * Writes the content of a DOM node to a byte stream. An XML declaration is omitted.
     * 
     * @param node
     *            The node to be serialized.
     * @param outputStream
     *            The destination OutputStream reference.
     */
    public static void writeNode( Node node, OutputStream outputStream ) {
        if ( null == node ) {
            throw new IllegalArgumentException( "Supplied node is null." );
        }
        try {
            Transformer idTransformer = TransformerFactory.newInstance().newTransformer();
            Properties outProps = new Properties();
            outProps.setProperty( OutputKeys.METHOD, "xml" );
            outProps.setProperty( OutputKeys.ENCODING, "UTF-8" );
            outProps.setProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
            outProps.setProperty( OutputKeys.INDENT, "yes" );
            idTransformer.setOutputProperties( outProps );
            idTransformer.transform( new DOMSource( node ), new StreamResult( outputStream ) );
        } catch ( TransformerException ex ) {
            String nodeName = ( node.getNodeType() == Node.DOCUMENT_NODE ) ? Document.class.cast( node ).getDocumentElement().getNodeName()
                                                                          : node.getNodeName();
            LOGR.log( Level.WARNING, "Failed to serialize DOM node: " + nodeName, ex );
        }
    }

    public static String transformToString( Source source ) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult( new StringWriter() );
            transformer.transform( source, result );
            return result.getWriter().toString();
        } catch ( TransformerException ex ) {
            ex.printStackTrace();
            return null;
        }
    }

}