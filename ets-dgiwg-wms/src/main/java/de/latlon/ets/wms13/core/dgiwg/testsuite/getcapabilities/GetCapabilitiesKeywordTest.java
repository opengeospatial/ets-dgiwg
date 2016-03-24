package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.latlon.ets.core.keyword.DfddKeywordMatcher;
import de.latlon.ets.core.keyword.DfddKeywordMatcherFromFile;

/**
 * Tests if the service contains at least one expected keywords.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesKeywordTest extends AbstractBaseGetCapabilitiesFixture {

    private static final DfddKeywordMatcher DFDD_KEYWORD_MATCHER = new DfddKeywordMatcherFromFile();

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.3., S.15, Requirement 12")
    public void wmsCapabilitiesContainsKeywordFromDFDDRegister()
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        List<String> keywords = parseKeywords( wmsCapabilities );
        boolean atLeastOneKeywordIsFromDfdd = DFDD_KEYWORD_MATCHER.containsAtLeastOneDfddKeyword( keywords );

        assertTrue( atLeastOneKeywordIsFromDfdd,
                    "Invalid keywords, expected is at least one keyword from DFDD, but is " + keywords );
    }

    private List<String> parseKeywords( Document wmsCapabilities )
                    throws XPathFactoryConfigurationException, XPathExpressionException {
        String xPathExpr = "//wms:WMS_Capabilities/wms:Service/wms:KeywordList/wms:Keyword";
        NodeList keywordNodes = (NodeList) createXPath().evaluate( xPathExpr, wmsCapabilities, XPathConstants.NODESET );
        List<String> keywords = new ArrayList<String>();
        for ( int keywordNodeIndex = 0; keywordNodeIndex < keywordNodes.getLength(); keywordNodeIndex++ ) {
            Node keywordNode = keywordNodes.item( keywordNodeIndex );
            String keyword = keywordNode.getTextContent();
            if ( keyword != null )
                keywords.add( keyword.trim() );
        }
        return keywords;
    }

    private XPath createXPath()
                    throws XPathFactoryConfigurationException {
        XPathFactory factory = XPathFactory.newInstance( XPathConstants.DOM_OBJECT_MODEL );
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext( NS_BINDINGS );
        return xpath;
    }

}