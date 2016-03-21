package de.latlon.ets.wms13.core.dgiwg.testsuite.getmap;

import static de.latlon.ets.wms13.core.domain.DGIWGWMS.GET_MAP;
import static de.latlon.ets.wms13.core.util.request.WmsRequestBuilder.getSupportedTransparentFormat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;

import com.sun.jersey.api.client.ClientResponse;

import de.latlon.ets.core.util.TestSuiteLogger;
import de.latlon.ets.wms13.core.client.WmsKvpRequest;
import de.latlon.ets.wms13.core.dgiwg.testsuite.AbstractBaseGetFixture;
import de.latlon.ets.wms13.core.util.request.WmsRequestBuilder;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class BaseGetMapFixture extends AbstractBaseGetFixture {

    static final String SUBDIRECTORY = "GetMapTests";

    private Path imageDirectory;

    /**
     * Builds a {@link WmsKvpRequest} representing a GetMap request.
     */
    @BeforeClass
    public void buildGetMapRequest() {
        this.reqEntity = WmsRequestBuilder.buildGetMapRequest( wmsCapabilities, layerInfo );
    }

    @BeforeClass
    public void setResultDirectory( ITestContext testContext ) {
        String outputDirectory = retrieveSessionDir( testContext );
        TestSuiteLogger.log( Level.INFO, "Directory to store GetMap responses: " + outputDirectory );
        try {
            Path resultDir = Paths.get( outputDirectory );
            imageDirectory = createDirectory( resultDir, SUBDIRECTORY );
        } catch ( IOException e ) {
            TestSuiteLogger.log( Level.WARNING, "Could not create directory for GetMap response.", e );
        }
    }

    /**
     * If an image format supporting transparency is not supported by the WMS a {@link SkipException} is thrown.
     * 
     * @return image format supporting transparency, never <code>null</code>
     */
    protected String findRequiredImageFormatWithTransparencySupport() {
        String imageFormat = getSupportedTransparentFormat( wmsCapabilities, GET_MAP );
        if ( imageFormat == null )
            throw new SkipException( "WMS does not support an image format supporting transparency." );
        return imageFormat;
    }

    /**
     * Stores the image in a the output directory of the testsuite:
     * testSUiteOutputDirectory/testGroup/testName.extension
     * 
     * @param rsp
     *            containing the image, rsp.getEntityInputStream() is used to retrieve the content as stream, never
     *            <code>null</code>
     * @param testGroup
     *            name of the test group (will be the name of the directory to create), never <code>null</code>
     * @param testName
     *            name of the test (will be the name of the file to create), never <code>null</code>
     * @param requestFormat
     *            the mime type of the image, never <code>null</code>
     */
    protected void storeResponseImage( ClientResponse rsp, String testGroup, String testName, String requestFormat ) {
        if ( imageDirectory == null ) {
            TestSuiteLogger.log( Level.WARNING,
                                 "Directory to store GetMap responses is not set. GetMap response is not written!" );
            return;
        }
        writeIntoFile( rsp, testGroup, testName, requestFormat );
    }

    private void writeIntoFile( ClientResponse rsp, String testGroup, String testName, String requestFormat ) {
        try {
            Path testClassDirectory = createDirectory( imageDirectory, testGroup );

            InputStream imageStream = rsp.getEntityInputStream();
            String fileExtension = detectFileExtension( requestFormat );

            String fileName = testName + fileExtension;
            Path imageFile = testClassDirectory.resolve( fileName );
            Files.copy( imageStream, imageFile );
        } catch ( IOException | MimeTypeException e ) {
            TestSuiteLogger.log( Level.WARNING, "Writing the GetMap response into file failed.", e );
        }
    }

    private String detectFileExtension( String requestFormat )
                    throws MimeTypeException {
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        MimeType mimeType = allTypes.forName( requestFormat );
        return mimeType.getExtension();
    }

    private Path createDirectory( Path parent, String child )
                    throws IOException {
        Path testClassDirectory = parent.resolve( child );
        Files.createDirectories( testClassDirectory );
        return testClassDirectory;
    }

    private String retrieveSessionDir( ITestContext testContext ) {
        String outputDirectory = testContext.getSuite().getOutputDirectory();
        outputDirectory = outputDirectory.substring( 0, outputDirectory.lastIndexOf( File.separatorChar ) );
        outputDirectory = outputDirectory.substring( 0, outputDirectory.lastIndexOf( File.separatorChar ) );
        outputDirectory = outputDirectory.substring( 0, outputDirectory.lastIndexOf( File.separatorChar ) );
        return outputDirectory;
    }

}