package de.latlon.ets.wms13.core.crs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.latlon.ets.core.util.TestSuiteLogger;
import de.latlon.ets.wms13.core.domain.BoundingBox;

/**
 * Contains useful methods regarding supported/expected CRS.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class CrsMatcher {

    private static final String BBOX_FILE = "crs.properties";

    private List<BoundingBox> boundingBoxes;

    /**
     * Finds all CRS overlapping the passed {@link BoundingBox}.
     * 
     * @param geographicBoundingBox
     *            to find overlapping bboxes for, the crs of the {@link BoundingBox} must be 'CRS:84', never
     *            <code>null</code>
     * @return a list {@link BoundingBox} overlapping the passed one, may be empty but never <code>null</code>
     */
    public List<String> retrieveOverlappingCrs( BoundingBox geographicBoundingBox ) {
        if ( geographicBoundingBox == null )
            throw new IllegalArgumentException( "geographicBoundingBox must not be null!" );
        if ( !"CRS:84".equals( geographicBoundingBox.getCrs() ) )
            throw new IllegalArgumentException( "geographicBoundingBox must be in CRS:84!" );
        return findOperlappingCrs( geographicBoundingBox );
    }

    /**
     * For test cases only!
     */
    InputStream openStream() {
        return CrsMatcher.class.getResourceAsStream( BBOX_FILE );
    }

    /**
     * @return all bounding boxes parsed from the stream, never <code>null</code>
     */
    List<BoundingBox> parseBoundingBoxes() {
        List<BoundingBox> bboxes = new ArrayList<BoundingBox>();
        InputStream resource = openStream();
        if ( resource != null ) {
            try ( BufferedReader br = new BufferedReader( new InputStreamReader( resource, "UTF-8" ) ) ) {
                String line;
                while ( ( line = br.readLine() ) != null ) {
                    parseLine( bboxes, line );
                }
            } catch ( IOException e ) {
                TestSuiteLogger.log( Level.WARNING, "Keywords file " + BBOX_FILE + " could not be parsed.", e );
            } finally {
                closeQuietly( resource );
            }
        } else {
            TestSuiteLogger.log( Level.WARNING, "Could not find bbox file '" + BBOX_FILE + "'." );
        }
        return bboxes;
    }

    private void parseLine( List<BoundingBox> bboxes, String line ) {
        if ( !line.startsWith( "#" ) ) {
            BoundingBox bbox = parseBbox( line );
            if ( bbox != null )
                bboxes.add( bbox );
        }
    }

    private List<String> findOperlappingCrs( BoundingBox geographicBoundingBox ) {
        parseBoundingBoxesIfRequired();
        List<String> overlappingCrs = new ArrayList<String>();
        for ( BoundingBox bbox : boundingBoxes ) {
            if ( overlaps( geographicBoundingBox, bbox ) )
                overlappingCrs.add( bbox.getCrs() );
        }
        return overlappingCrs;
    }

    private synchronized void parseBoundingBoxesIfRequired() {
        if ( this.boundingBoxes == null ) {
            this.boundingBoxes = parseBoundingBoxes();
        }
    }

    private boolean overlaps( BoundingBox bbox1, BoundingBox bbox2 ) {
        if ( bbox1.getMinX() > bbox2.getMaxX() || bbox2.getMinX() > bbox1.getMaxX() )
            return false;
        if ( bbox1.getMinY() > bbox2.getMaxY() || bbox2.getMinY() > bbox1.getMaxY() )
            return false;
        return true;
    }

    private BoundingBox parseBbox( String line ) {
        String[] keyValue = line.split( "=" );
        if ( keyValue.length != 2 ) {
            TestSuiteLogger.log( Level.WARNING, "Could not line '" + line
                                                + "'. Invalid format, must be e.g. EPSG:32661=-180,60,180,90." );
            return null;
        }
        String crs = keyValue[0].trim();
        String coords = keyValue[1].trim();
        if ( crs.isEmpty() || coords.isEmpty() ) {
            TestSuiteLogger.log( Level.WARNING, "Could not line '" + line
                                                + "'. Invalid format, must be e.g. EPSG:32661=-180,60,180,90." );
            return null;
        }
        String[] coordsArray = coords.split( "," );
        if ( coordsArray.length != 4 ) {
            TestSuiteLogger.log( Level.WARNING, "Could not parse line '" + line
                                                + "', number of coordinates must be 4, but is " + coordsArray.length
                                                + "." );
            return null;
        }
        return createBoundingBox( line, crs, coordsArray );
    }

    private BoundingBox createBoundingBox( String line, String crs, String[] coordsArray ) {
        try {
            double minX = asDouble( coordsArray[0] );
            double minY = asDouble( coordsArray[1] );
            double maxX = asDouble( coordsArray[2] );
            double maxY = asDouble( coordsArray[3] );
            return new BoundingBox( crs, minX, minY, maxX, maxY );
        } catch ( NumberFormatException e ) {
            TestSuiteLogger.log( Level.WARNING, "Could not parse line '" + line
                                                + "', at least one of the coordinate is not a valid double.", e );
        } catch ( IllegalArgumentException e ) {
            TestSuiteLogger.log( Level.WARNING,
                                 "Could not parse line '"
                                                 + line
                                                 + "', coordinates are not in the expected order (must be minX,minY,maxX,maxY.",
                                 e );
        }
        return null;
    }

    private double asDouble( String coord ) {
        String trimmedCoord = coord.trim();
        return Double.parseDouble( trimmedCoord );
    }

    private void closeQuietly( InputStream resource ) {
        try {
            resource.close();
        } catch ( IOException e ) {
            // quietly...
        }
    }

}