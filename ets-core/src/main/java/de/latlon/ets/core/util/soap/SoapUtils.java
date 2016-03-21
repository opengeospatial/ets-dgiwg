package de.latlon.ets.core.util.soap;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.latlon.ets.core.util.TestSuiteLogger;

/**
 * Utils for SOAP.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 */
public final class SoapUtils {

    private SoapUtils() {
    }

    /**
     * Read payload from file.
     * 
     * @param resourceAsStream
     *            resource as stream, never <code>null</code>
     * @return source
     */
    public static Source readPayloadFromFile( InputStream resourceAsStream ) {
        if ( resourceAsStream == null )
            throw new IllegalArgumentException( "InputStream must not be null" );
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            document = builder.parse( resourceAsStream );
        } catch ( ParserConfigurationException e ) {
            TestSuiteLogger.log( Level.SEVERE, "Failed to parse document", e );
        } catch ( SAXException | IOException e ) {
            TestSuiteLogger.log( Level.SEVERE, "Failed to read from stream", e );
        }
        return new DOMSource( document );
    }

    /**
     * Converts source to {@link SOAPPart} instance.
     * 
     * @param source
     *            source, never <code>null</code>
     * @return {@link SOAPPart} instance
     */
    public static SOAPPart convertToSoapPart( Source source )
                    throws SOAPException {
        MessageFactory factory = MessageFactory.newInstance( SOAPConstants.SOAP_1_1_PROTOCOL ); // TODO change this to
                                                                                                // SOAP 1.2
        SOAPMessage message = factory.createMessage();
        SOAPPart soapPart = message.getSOAPPart();
        soapPart.setContent( source );
        return soapPart;
    }

}