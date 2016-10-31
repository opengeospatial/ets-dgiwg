package de.latlon.ets.core.assertion;

import static org.testng.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.opengis.cite.validation.SchematronValidator;
import org.opengis.cite.validation.ValidationErrorHandler;
import org.testng.Assert;
import org.testng.SkipException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;
import de.latlon.ets.core.util.NamespaceBindings;
import de.latlon.ets.core.util.XMLUtils;

/**
 * Provides a set of custom assertion methods.
 */
public class ETSAssert {

    private final static Logger LOGR = Logger.getLogger( ETSAssert.class.getName() );

    private ETSAssert() {
    }

    /**
     * Asserts that the qualified name of a DOM Node matches the expected value.
     * 
     * @param node
     *            The Node to check.
     * @param qName
     *            A QName object containing a namespace name (URI) and a local part.
     */
    public static void assertQualifiedName( Node node, QName qName ) {
        Assert.assertEquals( node.getLocalName(), qName.getLocalPart(), ErrorMessage.get( ErrorMessageKey.LOCAL_NAME ) );
        Assert.assertEquals( node.getNamespaceURI(), qName.getNamespaceURI(),
                             ErrorMessage.get( ErrorMessageKey.NAMESPACE_NAME ) );
    }

    /**
     * Asserts that an XPath 1.0 expression holds true for the given evaluation context.
     * 
     * The method arguments will be logged at level FINE or lower.
     * 
     * @param expr
     *            A valid XPath 1.0 expression.
     * @param context
     *            The context node.
     * @param nsBindings
     *            A collection of namespace bindings for the XPath expression, where each entry maps a namespace URI
     *            (key) to a prefix (value). Never {@code null}.
     */
    public static void assertXPath( String expr, Node context, NamespaceBindings nsBindings ) {
        Boolean result = checkXPath( expr, context, nsBindings );
        Assert.assertTrue( result, ErrorMessage.format( ErrorMessageKey.XPATH_RESULT, context.getNodeName(), expr ) );
    }

    /**
     * Verify that an XPath 1.0 expression holds true for the given evaluation context.Throws an {@link SkipException}
     * if the xpath results in <code>false</code>.
     * 
     * The method arguments will be logged at level FINE or lower.
     * 
     * @param expr
     *            A valid XPath 1.0 expression.
     * @param context
     *            The context node.
     * @param nsBindings
     *            A collection of namespace bindings for the XPath expression, where each entry maps a namespace URI
     *            (key) to a prefix (value). Never {@code null}.
     * @param skipMessage
     *            message to pass in the {@link SkipException} if the xpath results in <code>false</code>
     */
    public static void verifyXPath( String expr, Node context, NamespaceBindings nsBindings, String skipMessage ) {
        Boolean result = checkXPath( expr, context, nsBindings );
        if ( !result )
            throw new SkipException( skipMessage );
    }

    /**
     * Asserts that an XML resource is schema-valid.
     * 
     * @param schema
     *            to validate against, never <code>null</code>.
     * @param document
     *            to validate, never <code>null</code>
     */
    public static void assertSchemaValid( Schema schema, Document document ) {
        Validator validator = schema.newValidator();
        assertSchemaValid( validator, new DOMSource( document, document.getDocumentURI() ) );
    }

    /**
     * Asserts that an XML resource is schema-valid.
     * 
     * @param validator
     *            The Validator to use.
     * @param source
     *            The XML Source to be validated.
     */
    public static void assertSchemaValid( Validator validator, Source source ) {
        ValidationErrorHandler errHandler = new ValidationErrorHandler();
        validator.setErrorHandler( errHandler );
        try {
            validator.validate( source );
        } catch ( Exception e ) {
            throw new AssertionError( ErrorMessage.format( ErrorMessageKey.XML_ERROR, e.getMessage() ) );
        }
        Assert.assertFalse( errHandler.errorsDetected(), ErrorMessage.format( ErrorMessageKey.NOT_SCHEMA_VALID,
                                                                              errHandler.getErrorCount(),
                                                                              errHandler.toString() ) );
    }

    /**
     * Asserts that an XML resource satisfies all applicable constraints specified in a Schematron (ISO 19757-3) schema.
     * The "xslt2" query language binding is supported. All patterns are checked.
     * 
     * @param schemaRef
     *            A URL that denotes the location of a Schematron schema.
     * @param xmlSource
     *            The XML Source to be validated.
     */
    public static void assertSchematronValid( URL schemaRef, Source xmlSource ) {
        SchematronValidator validator;
        try {
            validator = new SchematronValidator( new StreamSource( schemaRef.toString() ), "#ALL" );
        } catch ( Exception e ) {
            StringBuilder msg = new StringBuilder( "Failed to process Schematron schema at " );
            msg.append( schemaRef ).append( '\n' );
            msg.append( e.getMessage() );
            throw new AssertionError( msg );
        }
        Result result = validator.validate(xmlSource);
        Assert.assertFalse( validator.ruleViolationsDetected(),
                            ErrorMessage.format( ErrorMessageKey.NOT_SCHEMA_VALID, validator.getRuleViolationCount(),
                                                 XMLUtils.resultToString(result) ) );
    }

    /**
     * Asserts that the given XML entity contains the expected number of descendant elements having the specified name.
     * 
     * @param xmlEntity
     *            A Document representing an XML entity.
     * @param elementName
     *            The qualified name of the element.
     * @param expectedCount
     *            The expected number of occurrences.
     */
    public static void assertDescendantElementCount( Document xmlEntity, QName elementName, int expectedCount ) {
        NodeList features = xmlEntity.getElementsByTagNameNS( elementName.getNamespaceURI(), elementName.getLocalPart() );
        Assert.assertEquals( features.getLength(), expectedCount,
                             String.format( "Unexpected number of %s descendant elements.", elementName ) );
    }

    /**
     * Asserts that the actual HTTP status code matches one of the expected status codes.
     * 
     * @param actualCode
     *            The actual status code.
     * @param expectedCodes
     *            An int[] array containing the expected status codes.
     */
    public static void assertStatusCode( int actualCode, int... expectedCodes ) {
        Arrays.sort( expectedCodes ); // precondition for binary search
        Assert.assertTrue( Arrays.binarySearch( expectedCodes, actualCode ) >= 0,
                           String.format( "Expected status code(s) %s but received %d.",
                                          Arrays.toString( expectedCodes ), actualCode ) );

    }

    /**
     * Asserts that the actual content type matches the expected content type.
     * 
     * @param headers
     *            The header of the response.
     * @param expectedContentType
     *            The expected content type, never <code>null</code>.
     */
    public static void assertContentType( MultivaluedMap<String, String> headers, String expectedContentType ) {
        List<String> contentTypes = headers.get( "Content-Type" );
        boolean containsContentType = containsContentType( contentTypes, expectedContentType );
        String msg = String.format( "Expected content type %s. but received %s", expectedContentType,
                                    asString( contentTypes ) );
        assertTrue( containsContentType, msg );
    }

    /**
     * Asserts that the string is a valid url.
     * 
     * @param url
     *            The url to check.
     */
    public static void assertUrl( String url ) {
        try {
            new URL( url );
            assertTrue( true );
        } catch ( MalformedURLException e ) {
            String msg = String.format( "Invalid URL: %s", url );
            assertTrue( false, msg );
        }
    }

    /**
     * Asserts that the url is resolvable (status code is 200).
     * 
     * @param url
     *            The url to check.
     */
    public static void assertUriIsResolvable( String url ) {
        try {
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create( config );
            WebResource resource = client.resource( new URI( url ) );
            ClientResponse response = resource.get( ClientResponse.class );
            assertStatusCode( response.getStatus(), 200 );
        } catch ( NullPointerException | URISyntaxException e ) {
            String errorMsg = String.format( "Invalid URI %s: %s", url, e.getMessage() );
            throw new AssertionError( errorMsg );
        }
    }

    /**
     * Checks whether an XPath 1.0 expression holds true for the given evaluation context.
     *
     * The method arguments will be logged at level FINE or lower.
     * 
     * @param expr
     *            A valid XPath 1.0 expression.
     * @param context
     *            The context node.
     * @param nsBindings
     *            A collection of namespace bindings for the XPath expression, where each entry maps a namespace URI
     *            (key) to a prefix (value). Never {@code null}.
     * @return true if XPath 1.0 expression holds true for the given evaluation context, false otherwise.
     */
    public static Boolean checkXPath( String expr, Node context, NamespaceBindings nsBindings )
                    throws AssertionError {
        if ( null == context ) {
            throw new NullPointerException( "Context node is null." );
        }
        LOGR.log( Level.FINE, "Evaluating \"{0}\" against context node:\n{1}",
                  new Object[] { expr, XMLUtils.writeNodeToString( context ) } );
        XPathFactory factory = createFactory();
        XPath xpath = factory.newXPath();
        LOGR.log( Level.FINE, "Using XPath implementation: " + xpath.getClass().getName() );
        xpath.setNamespaceContext( nsBindings );
        Boolean result;
        try {
            result = (Boolean) xpath.evaluate( expr, context, XPathConstants.BOOLEAN );
        } catch ( XPathExpressionException xpe ) {
            String msg = ErrorMessage.format( ErrorMessageKey.XPATH_ERROR, expr );
            LOGR.log( Level.WARNING, msg, xpe );
            throw new AssertionError( msg );
        }
        LOGR.log( Level.FINE, "XPath result: " + result );
        return result;
    }

    private static boolean containsContentType( List<String> contentTypes, String expectedContentType ) {
        if ( contentTypes != null )
            for ( String contentType : contentTypes ) {
                if ( contentType.contains( expectedContentType ) )
                    return true;
            }
        return false;
    }

    private static String asString( List<String> values ) {
        StringBuilder sb = new StringBuilder();
        for ( String value : values ) {
            if ( sb.length() > 0 )
                sb.append( ", " );
            sb.append( value );
        }
        return sb.toString();
    }

    private static XPathFactory createFactory() {
        try {
            return XPathFactory.newInstance( XPathConstants.DOM_OBJECT_MODEL );
        } catch ( XPathFactoryConfigurationException e ) {
            // An implementation for the W3C DOM is always available
            throw new RuntimeException( e );
        }
    }

}