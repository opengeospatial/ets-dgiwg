package de.latlon.ets.wms13.core.util.request;

import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_MAP;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.IMAGE_PNG;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
public class WmsRequestBuilderTest {

    @Test
    public void testGetSupportedTransparentFormat()
                    throws Exception {
        String format = WmsRequestBuilder.getSupportedTransparentFormat( wmsCapabilities(), GET_MAP );

        assertThat( format, is( IMAGE_PNG ) );
    }

    private Document wmsCapabilities()
                    throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream wmsCapabilities = WmsRequestBuilderTest.class.getResourceAsStream( "../../capabilities_wms130.xml" );
        return builder.parse( new InputSource( wmsCapabilities ) );
    }

}