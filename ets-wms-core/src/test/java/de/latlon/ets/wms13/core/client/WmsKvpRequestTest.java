package de.latlon.ets.wms13.core.client;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import de.latlon.ets.wms13.core.client.WmsKvpRequest;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class WmsKvpRequestTest {

    @Test
    public void testAsQueryString() {
        WmsKvpRequest wmsKvpRequest = new WmsKvpRequest();
        wmsKvpRequest.addKvp( "key1", "value1" );
        wmsKvpRequest.addKvp( "key2", "value2" );

        String queryString = wmsKvpRequest.asQueryString();

        assertThat( queryString, CoreMatchers.anyOf( is( "key1=value1&key2=value2" ), is( "key2=value2&key1=value1" ) ) );
    }

    @Test
    public void testAsQueryStringOverwriteKey() {
        WmsKvpRequest wmsKvpRequest = new WmsKvpRequest();
        wmsKvpRequest.addKvp( "key2", "value2" );
        wmsKvpRequest.addKvp( "key2", "value3" );

        String queryString = wmsKvpRequest.asQueryString();

        assertThat( queryString, is( "key2=value3" ) );
    }

    @Test
    public void testAsQueryStringNullKey() {
        WmsKvpRequest wmsKvpRequest = new WmsKvpRequest();
        wmsKvpRequest.addKvp( "key1", "value1" );
        wmsKvpRequest.addKvp( null, "value2" );

        String queryString = wmsKvpRequest.asQueryString();

        assertThat( queryString, is( "key1=value1" ) );
    }

}