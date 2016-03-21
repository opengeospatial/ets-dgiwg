/**
 * 
 */
package de.latlon.ets.wfs20.core.assertion;

import static de.latlon.ets.wfs20.core.assertion.WfsAssertion.assertVersion200;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class WfsAssertionTest {

    @Test
    public void testAssertVersion200()
                    throws Exception {
        assertVersion200( wfsCapabilities() );
    }

    private Document wfsCapabilities()
                    throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream wfsCapabilities = WfsAssertionTest.class.getResourceAsStream( "../capabilities_wfs200.xml" );
        return builder.parse( new InputSource( wfsCapabilities ) );
    }

}