package de.latlon.ets.wms13.core.assertion;

import static de.latlon.ets.wms13.core.assertion.WmsAssertion.assertVersion130;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.latlon.ets.wms13.core.util.ServiceMetadataUtilsTest;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class WmsAssertTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAssertVersion130()
                    throws Exception {
        assertVersion130( wmsCapabilities() );
    }

    private Document wmsCapabilities()
                    throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream wmsCapabilities = ServiceMetadataUtilsTest.class.getResourceAsStream( "../capabilities_wms130.xml" );
        return builder.parse( new InputSource( wmsCapabilities ) );
    }

}