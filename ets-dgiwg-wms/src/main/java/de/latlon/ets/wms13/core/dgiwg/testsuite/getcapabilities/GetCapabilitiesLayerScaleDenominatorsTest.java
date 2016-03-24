package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.wms13.core.util.ServiceMetadataUtils.parseRequestableLayerNodes;
import static org.testng.Assert.assertNotNull;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tests if the Layer has MinScaleDenominator and MaxScaleDenominator.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesLayerScaleDenominatorsTest extends AbstractBaseGetCapabilitiesFixture {

    @DataProvider(name = "layerNodes")
    public Object[][] parseLayerNodes( ITestContext testContext )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        if ( this.wmsCapabilities == null )
            initBaseFixture( testContext );
        NodeList layerNodes = parseRequestableLayerNodes( wmsCapabilities );
        Object[][] layers = new Object[layerNodes.getLength()][];
        for ( int layerNodeIndex = 0; layerNodeIndex < layerNodes.getLength(); layerNodeIndex++ ) {
            Node layerNode = layerNodes.item( layerNodeIndex );
            String name = (String) createXPath().evaluate( "wms:Name", layerNode, XPathConstants.STRING );
            String title = (String) createXPath().evaluate( "wms:Title", layerNode, XPathConstants.STRING );
            layers[layerNodeIndex] = new Object[] { layerNode, name, title };
        }
        return layers;
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.3., S.15, Requirement 18", dataProvider = "layerNodes")
    public
                    void wmsCapabilitiesLayerScaleDenominatorsExists( Node layerNode, String name, String title )
                                    throws XPathExpressionException, XPathFactoryConfigurationException {
        String minScaleExpr = "ancestor-or-self::wms:Layer/wms:MinScaleDenominator";
        Node minScaleDenominatorsNode = (Node) createXPath().evaluate( minScaleExpr, layerNode, XPathConstants.NODE );
        assertNotNull( minScaleDenominatorsNode, "MinScaleDenominator element for layer is missing." );

        String maxScaleExpr = "ancestor-or-self::wms:Layer/wms:MaxScaleDenominator";
        Node maxScaleDenominatorsNode = (Node) createXPath().evaluate( maxScaleExpr, layerNode, XPathConstants.NODE );
        assertNotNull( maxScaleDenominatorsNode, "MaxScaleDenominator element for layer is missing." );
    }

    private XPath createXPath()
                    throws XPathFactoryConfigurationException {
        XPathFactory factory = XPathFactory.newInstance( XPathConstants.DOM_OBJECT_MODEL );
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext( NS_BINDINGS );
        return xpath;
    }

}