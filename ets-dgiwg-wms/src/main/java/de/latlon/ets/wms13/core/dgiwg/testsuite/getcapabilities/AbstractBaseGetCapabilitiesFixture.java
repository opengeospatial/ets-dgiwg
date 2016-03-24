package de.latlon.ets.wms13.core.dgiwg.testsuite.getcapabilities;

import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_CAPABILITIES;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.REQUEST_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.SERVICE_PARAM;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.SERVICE_TYPE_CODE;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.VERSION;
import static de.latlon.ets.wms13.core.domain.DGIWGWMS.VERSION_PARAM;

import org.testng.annotations.BeforeClass;

import de.latlon.ets.wms13.core.client.WmsKvpRequest;
import de.latlon.ets.wms13.core.dgiwg.testsuite.AbstractBaseGetFixture;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public abstract class AbstractBaseGetCapabilitiesFixture extends AbstractBaseGetFixture {

    /**
     * Builds a {@link WmsKvpRequest} representing a GetCapabilities request for a complete service metadata document.
     */
    @BeforeClass
    public void buildGetCapabilitiesRequest() {
        this.reqEntity = new WmsKvpRequest();
        this.reqEntity.addKvp( SERVICE_PARAM, SERVICE_TYPE_CODE );
        this.reqEntity.addKvp( REQUEST_PARAM, GET_CAPABILITIES );
        this.reqEntity.addKvp( VERSION_PARAM, VERSION );
    }

}