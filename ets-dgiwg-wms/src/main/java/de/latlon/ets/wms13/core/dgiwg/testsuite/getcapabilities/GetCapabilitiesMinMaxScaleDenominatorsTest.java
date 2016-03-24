package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

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
public class GetCapabilitiesMinMaxScaleDenominatorsTest extends AbstractBaseGetCapabilitiesFixture {

    @DataProvider(name = "scaleDenominators")
    public Object[][] parseScaleDenominators( ITestContext testContext )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        if ( this.wmsCapabilities == null )
            initBaseFixture( testContext );
        NodeList layerNodes = parseLayer( wmsCapabilities );
        List<Object[]> scaleDenominators = new ArrayList<>();
        for ( int layerNodeIndex = 0; layerNodeIndex < layerNodes.getLength(); layerNodeIndex++ ) {
            Node layerNode = layerNodes.item( layerNodeIndex );
            Double minScaleDenominator = asDouble( layerNode, "wms:MinScaleDenominator/text()" );
            Double maxScaleDenominator = asDouble( layerNode, "wms:MaxScaleDenominator/text()" );

            if ( !Double.isNaN( minScaleDenominator ) && !Double.isNaN( maxScaleDenominator ) ) {
                scaleDenominators.add( new Object[] { minScaleDenominator, maxScaleDenominator } );
            }
        }
        return scaleDenominators.toArray( new Object[scaleDenominators.size()][] );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.3., S.15, Requirement 19", dataProvider = "scaleDenominators")
    public
                    void wmsCapabilitiesLayerHasCorrectValuesForMinAndMaxDenominator( Double minScaleDenominator,
                                                                                      Double maxScaleDenominator ) {
        String message = String.format( "minScaleDenomintor must be les or equal then maxScaleDenominator, but was %s (min) and %s (max)",
                                        minScaleDenominator, maxScaleDenominator );
        assertTrue( minScaleDenominator <= maxScaleDenominator, message );
    }

    private NodeList parseLayer( Document entity )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        String xPathAbstract = "//wms:Layer";
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

    private double asDouble( Node layerNode, String expr )
                    throws XPathExpressionException, XPathFactoryConfigurationException {
        String value = (String) createXPath().evaluate( expr, layerNode, XPathConstants.STRING );
        if ( value != null && !value.isEmpty() ) {
            return Double.parseDouble( value );
        }
        return Double.NaN;
    }

}