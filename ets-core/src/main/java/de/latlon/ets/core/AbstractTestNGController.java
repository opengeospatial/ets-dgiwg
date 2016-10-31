package de.latlon.ets.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.xml.transform.Source;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.occamlab.te.spi.executors.TestRunExecutor;
import com.occamlab.te.spi.executors.testng.TestNGExecutor;
import com.occamlab.te.spi.jaxrs.TestSuiteController;

import de.latlon.ets.core.util.TestSuiteLogger;

/**
 * Main test run controller oversees execution of TestNG test suites.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public abstract class AbstractTestNGController implements TestSuiteController {

    private TestRunExecutor executor;

    private Properties etsProperties = new Properties();

    /**
     * Default constructor uses the location given by the "user.home" system property as the root output directory.
     */
    public AbstractTestNGController() {
        this( new File( System.getProperty( "user.home" ) ).toURI().toString() );
    }

    /**
     * Construct a controller that writes results to the given output directory.
     * 
     * @param outputDirUri
     *            A file URI that specifies the location of the directory in which test results will be written. It will
     *            be created if it does not exist.
     */
    public AbstractTestNGController( String outputDirUri ) {
        InputStream is = getClass().getResourceAsStream( "ets.properties" );
        try {
            this.etsProperties.load( is );
        } catch ( IOException ex ) {
            TestSuiteLogger.log( Level.WARNING, "Unable to load ets.properties. " + ex.getMessage() );
        }
        URL tngSuite = getTestNgConfiguration();
        File resultsDir = new File( URI.create( outputDirUri ) );
        TestSuiteLogger.log( Level.CONFIG, "Using TestNG config: " + tngSuite );
        TestSuiteLogger.log( Level.INFO, "Using outputDirPath: " + resultsDir.getAbsolutePath() );
        // NOTE: setting third argument to 'true' enables the default listeners
        this.executor = new TestNGExecutor( tngSuite.toString(), resultsDir.getAbsolutePath(), false );
    }

    @Override
    public String getCode() {
        return etsProperties.getProperty( "ets-code" );
    }

    @Override
    public String getVersion() {
        return etsProperties.getProperty( "ets-version" );
    }

    @Override
    public String getTitle() {
        return etsProperties.getProperty( "ets-title" );
    }

    @Override
    public Source doTestRun( Document testRunArgs )
                    throws Exception {
        validateTestRunArgs( testRunArgs );
        return executor.execute( testRunArgs );
    }

    /**
     * Validates the given set of test run arguments. The test run is aborted if any checks fail.
     * 
     * @param testRunArgs
     *            a DOM Document containing a set of XML properties (key-value pairs)
     * @throws IllegalArgumentException
     *             if any required arguments are missing or invalid for some reason
     */
    void validateTestRunArgs( Document testRunArgs )
                    throws Exception {
        if ( null == testRunArgs || !testRunArgs.getDocumentElement().getNodeName().equals( "properties" ) ) {
            throw new IllegalArgumentException( "Input is not an XML properties document." );
        }
        NodeList entries = testRunArgs.getDocumentElement().getElementsByTagName( "entry" );
        if ( entries.getLength() == 0 ) {
            throw new IllegalArgumentException( "No test run arguments found." );
        }
        Map<String, String> args = new HashMap<String, String>();
        for ( int i = 0; i < entries.getLength(); i++ ) {
            Element entry = (Element) entries.item( i );
            args.put( entry.getAttribute( "key" ), entry.getTextContent() );
        }
        validateTestRunArgs( args );
    }

    protected abstract void validateTestRunArgs( Map<String, String> args );

    protected abstract URL getTestNgConfiguration();

    protected static File findXmlArgs( String[] args ) {
        File xmlArgs = null;
        if ( args.length > 0 ) {
            xmlArgs = ( args[0].startsWith( "file:" ) ) ? new File( URI.create( args[0] ) ) : new File( args[0] );
        } else {
            String homeDir = System.getProperty( "user.home" );
            xmlArgs = new File( homeDir, "test-run-props.xml" );
        }
        if ( !xmlArgs.exists() ) {
            throw new IllegalArgumentException( "Test run arguments not found at " + xmlArgs );
        }
        return xmlArgs;
    }

}
