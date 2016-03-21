package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tests if the Layer/Style contains values for Title and Name.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesStyleTest extends AbstractBaseGetCapabilitiesFixture {

    @DataProvider(name = "styleNodes")
    public Object[][] parseStyleNodes( ITestContext testContext )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        if ( this.wmsCapabilities == null )
            initBaseFixture( testContext );
        NodeList styleNodes = parseStyleNodes( wmsCapabilities );
        Object[][] styles = new Object[styleNodes.getLength()][];
        for ( int styleNodeIndex = 0; styleNodeIndex < styleNodes.getLength(); styleNodeIndex++ ) {
            Node styleNode = styleNodes.item( styleNodeIndex );
            String layerName = (String) createXPath().evaluate( "../wms:Name", styleNode, XPathConstants.STRING );
            String layerTitle = (String) createXPath().evaluate( "../wms:Title", styleNode, XPathConstants.STRING );
            styles[styleNodeIndex] = new Object[] { styleNode, layerName, layerTitle };
        }
        return styles;
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.3., S.15, Requirement 13", dataProvider = "styleNodes")
    public
                    void wmsCapabilitiesStylesHasNameAndTitle( Node styleNode, String layerName, String layerTitle )
                                    throws XPathExpressionException, XPathFactoryConfigurationException {
        String styleName = (String) createXPath().evaluate( "wms:Name/text()", styleNode, XPathConstants.STRING );
        assertNotNull( styleName, "Name element of the style is missing" );
        assertNotEquals( styleName, "Name element of the style is empty" );

        String styleTitle = (String) createXPath().evaluate( "wms:Title/text()", styleNode, XPathConstants.STRING );
        assertNotNull( styleTitle, "Title element of the style is missing" );
        assertNotEquals( styleTitle, "", "Title element of the style is empty" );
    }

    private NodeList parseStyleNodes( Document entity )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        String xPathAbstract = "//wms:Layer/wms:Style";
        XPath xpath = createXPath();
        return (NodeList) xpath.evaluate( xPathAbstract, entity, XPathConstants.NODESET );
    }

    private XPath createXPath()
                    throws XPathFactoryConfigurationException {
        XPathFactory factory = XPathFactory.newInstance( XPathConstants.DOM_OBJECT_MODEL );
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext( NS_BINDINGS );
        return xpath;
    }

}