package de.latlon.ets.core.util.soap;

import static de.latlon.ets.core.util.soap.SoapUtils.convertToSoapPart;
import static de.latlon.ets.core.util.soap.SoapUtils.readPayloadFromFile;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;

import org.junit.Test;

/**
 * Tests for {@link SoapUtils}.
 * 
 * @author <a href="mailto:stenger@lat-lon.de">Dirk Stenger</a>
 */
public class SoapUtilsTest {

    @Test
    public void testReadPayloadFromFile()
                    throws Exception {
        Source payload = readPayloadFromFile( this.getClass().getResourceAsStream( "GetCapabilitiesRequest_example.xml" ) );

        assertThat( payload, notNullValue() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadPayloadFromFileWithNullShouldThrowException()
                    throws Exception {
        readPayloadFromFile( null );
    }

    @Test
    public void testConvertToSoapPartWithReadPayloadFromFile()
                    throws Exception {
        Source payload = readPayloadFromFile( this.getClass().getResourceAsStream( "GetCapabilitiesRequest_example.xml" ) );
        SOAPPart soapPart = convertToSoapPart( payload );

        assertThat( soapPart, notNullValue() );
        assertTrue( soapPart.getElementsByTagNameNS( "*", "GetCapabilities" ).getLength() > 0 );
    }

    @Test(expected = SOAPException.class)
    public void testConvertToSoapPartWithNullShouldThrowException()
                    throws Exception {
        convertToSoapPart( null );
    }

}