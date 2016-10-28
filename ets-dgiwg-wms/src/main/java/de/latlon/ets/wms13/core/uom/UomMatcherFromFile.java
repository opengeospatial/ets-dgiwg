package de.latlon.ets.wms13.core.uom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.latlon.ets.core.util.TestSuiteLogger;

/**
 * Implementation of a {@link UomMatcher} retrieving the uoms from a file.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class UomMatcherFromFile implements UomMatcher {

    private static final String UOM_FILE = "uom.txt";
    private List<String> expectedUoms;

    public UomMatcherFromFile() {
        this.expectedUoms = parseUomsFromStream();
    }

    @Override
    public boolean isExpectedUoM(String uom) {
        if (expectedUoms.contains(uom))
            return true;
        return false;
    }

    List<String> parseUomsFromStream() {
        List<String> uoms = new ArrayList<String>();
        try (InputStream resource = getClass().getResourceAsStream(UOM_FILE);
                BufferedReader br = new BufferedReader(new InputStreamReader(resource, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String uom = line.trim();
                if (!uom.isEmpty())
                    uoms.add(uom);
            }
        } catch (IOException e) {
            TestSuiteLogger.log(Level.WARNING, "UoM file " + UOM_FILE + " could not be parsed.", e);
        }
        TestSuiteLogger.log(Level.CONFIG, String.format("Recogized units of measure: %s", uoms.toString()));
        return uoms;
    }

}