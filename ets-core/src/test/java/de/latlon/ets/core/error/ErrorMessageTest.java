package de.latlon.ets.core.error;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.latlon.ets.core.error.ErrorMessage;
import de.latlon.ets.core.error.ErrorMessageKey;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class ErrorMessageTest {

    @Test
    public void testGet() {
        for ( ErrorMessageKey errorMessageKey : ErrorMessageKey.values() ) {
            String errorMessage = ErrorMessage.get( errorMessageKey );
            assertThat( errorMessage, allOf( notNullValue(), not( is( "" ) ) ) );
        }
    }

}
