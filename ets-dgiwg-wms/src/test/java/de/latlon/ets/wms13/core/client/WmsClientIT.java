package de.latlon.ets.wms13.core.client;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.latlon.ets.wms13.core.domain.DGIWGWMS;
import de.latlon.ets.wms13.core.domain.WmsNamespaces;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class WmsClientIT {

    public void testgetCapabilities()
                    throws Exception {
        WmsClient wmsClient = new WmsClient( wmsCapabilities() );

        Document capabilities = wmsClient.getCapabilities();
        assertThat( capabilities.getLocalName(), is( DGIWGWMS.WMS_CAPABILITIES ) );
        assertThat( capabilities.getNamespaceURI(), is( WmsNamespaces.WMS ) );
    }

    private Document wmsCapabilities()
                    throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream wmsCapabilities = WmsClientIT.class.getResourceAsStream( "capabilities_wms130.xml" );
        return builder.parse( new InputSource( wmsCapabilities ) );
    }

}