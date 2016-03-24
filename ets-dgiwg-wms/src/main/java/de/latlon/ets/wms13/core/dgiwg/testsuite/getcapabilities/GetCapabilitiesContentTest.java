package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;

import javax.xml.soap.SOAPException;

import org.testng.annotations.Test;

/**
 * Tests if the capabilities provides all mandatory and optional service metadata elements.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesContentTest extends AbstractBaseGetCapabilitiesFixture {

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesNameExists()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:Name/text() != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesTitleExists()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:Title/text() != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesOnlineResourceExists()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:OnlineResource/@xlink:href != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesAbstractExists()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:Abstract/text() != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesKeywordListExists()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:KeywordList != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesContactInformationExists()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:ContactInformation != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesFeesExists()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:Fees/text() != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesAccessConstraintsExists()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:AccessConstraints/text() != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.6.2.2., S.13, Requirement 9")
    public void wmsCapabilitiesLayerLimitExists()
                    throws SOAPException {
        String xPathXml = "//wms:WMS_Capabilities/wms:Service/wms:LayerLimit/text() != ''";
        assertXPath( xPathXml, wmsCapabilities, NS_BINDINGS );
    }

}