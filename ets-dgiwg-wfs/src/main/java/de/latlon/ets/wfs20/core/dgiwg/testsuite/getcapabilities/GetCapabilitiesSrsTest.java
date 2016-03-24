package de.latlon.ets.wfs20.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.core.assertion.ETSAssert.assertXPath;

import org.testng.annotations.Test;

import de.latlon.ets.wfs20.core.dgiwg.testsuite.WfsBaseFixture;

/**
 * Tests if the capabilities contains the mandatory srs EPSG:4326 and CRS:84..
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetCapabilitiesSrsTest extends WfsBaseFixture {

    private static final String EPSG_4326 = "EPSG:4326";

    private static final String CRS_84 = "CRS:84";

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 9.2., S.30, Requirement 21")
    public void wfsCapabilitiesSrsEpsg4326Supported() {
        assertSrs( EPSG_4326 );
    }

    @Test(description = "DGIWG - Web Feature Service 2.0 Profile, 9.2., S.30, Requirement 21")
    public void wfsCapabilitiesSrsCrs84Supported() {
        assertSrs( CRS_84 );
    }

    private void assertSrs( String expectedSrs ) {
        String expr = "//wfs:WFS_Capabilities/ows:OperationsMetadata/ows:Parameter[@name='srsName']/ows:AllowedValues/ows:Value = '%s'";
        String xPathXml = String.format( expr, expectedSrs );
        assertXPath( xPathXml, this.wfsMetadata, NS_BINDINGS );
    }

}