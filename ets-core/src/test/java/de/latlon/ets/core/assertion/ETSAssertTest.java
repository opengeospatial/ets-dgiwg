package de.latlon.ets.core.assertion;

import static de.latlon.ets.core.assertion.ETSAssert.assertUrl;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class ETSAssertTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAssertContentType() {
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
        headers.add( "Content-Type", "text/xml; charset" );
        ETSAssert.assertContentType( headers, "text/xml" );
    }

    @Test
    public void testAssertContentType_expectFalse() {
        thrown.expect( AssertionError.class );
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
        headers.add( "Content-Type", "text/html" );
        ETSAssert.assertContentType( headers, "text/xml" );
    }

    @Test
    public void testAssertUrl()
                    throws Exception {
        assertUrl( "http://validurl.de/test" );
    }

    @Test
    public void testAssertUrl_NullUrl()
                    throws Exception {
        thrown.expect( AssertionError.class );
        assertUrl( null );
    }

    @Test
    public void testAssertUrl_InvalidUrl()
                    throws Exception {
        thrown.expect( AssertionError.class );
        assertUrl( "invalid url" );
    }
}
