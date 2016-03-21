package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertUriIsResolvable;
import static de.latlon.ets.core.assertion.ETSAssert.assertUrl;

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
 * Tests if the DataUrls are resolvable.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesDataUrlTest extends AbstractBaseGetCapabilitiesFixture {

    @DataProvider(name = "dataUrls")
    public Object[][] parseDataUrls( ITestContext testContext )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        if ( this.wmsCapabilities == null )
            initBaseFixture( testContext );
        NodeList dataUrlNodes = parseDataUrlNodes( wmsCapabilities );
        Object[][] dataUrls = new Object[dataUrlNodes.getLength()][];
        for ( int dataUrlNodeIndex = 0; dataUrlNodeIndex < dataUrlNodes.getLength(); dataUrlNodeIndex++ ) {
            Node dataUrlNode = dataUrlNodes.item( dataUrlNodeIndex );
            String dataUrl = (String) createXPath().evaluate( "//@xlink:href", dataUrlNode, XPathConstants.STRING );
            dataUrls[dataUrlNodeIndex] = new Object[] { dataUrl };
        }
        return dataUrls;
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.3., S.17, Requirement 21", dataProvider = "dataUrls")
    public
                    void wmsCapabilitiesDataUrlIsResolvable( String dataUrl )
                                    throws XPathExpressionException, XPathFactoryConfigurationException {
        assertUrl( dataUrl );
        assertUriIsResolvable( dataUrl );
    }

    private NodeList parseDataUrlNodes( Document entity )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        String xPath = "//wms:Layer/wms:DataURL/wms:OnlineResource";
        XPath xpath = createXPath();
        return (NodeList) xpath.evaluate( xPath, entity, XPathConstants.NODESET );
    }

    private XPath createXPath()
                    throws XPathFactoryConfigurationException {
        XPathFactory factory = XPathFactory.newInstance( XPathConstants.DOM_OBJECT_MODEL );
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext( NS_BINDINGS );
        return xpath;
    }

}