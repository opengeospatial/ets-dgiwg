package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.annotations.Test;

/**
 * Tests if MaxWidth greater/equal 800 and MaxHeight greater/equal 800.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesMaxExtendTest extends AbstractBaseGetCapabilitiesFixture {

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.3., S.15, Requirement 15")
    public void wmsCapabilitiesMaxWidth()
                    throws XPathExpressionException, XPathFactoryConfigurationException {
        Double maxWidth = (Double) createXPath().evaluate( "//wms:WMS_Capabilities/wms:Service/wms:MaxWidth/text()",
                                                           wmsCapabilities, XPathConstants.NUMBER );
        assertNotNull( maxWidth, "MaxWidth element is missing" );
        assertTrue( maxWidth >= 800, "MaxWidth element is missing" );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.3., S.15, Requirement 15")
    public void wmsCapabilitiesMaxHeight()
                    throws XPathExpressionException, XPathFactoryConfigurationException {
        Double maxHeight = (Double) createXPath().evaluate( "//wms:WMS_Capabilities/wms:Service/wms:MaxHeight/text()",
                                                            wmsCapabilities, XPathConstants.NUMBER );
        assertNotNull( maxHeight, "MaxHeight element is missing" );
        assertTrue( maxHeight >= 800, "MaxHeight element is missing" );

    }

    private XPath createXPath()
                    throws XPathFactoryConfigurationException {
        XPathFactory factory = XPathFactory.newInstance( XPathConstants.DOM_OBJECT_MODEL );
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext( NS_BINDINGS );
        return xpath;
    }

}