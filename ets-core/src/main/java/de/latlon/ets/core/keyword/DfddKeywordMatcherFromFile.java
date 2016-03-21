package de.latlon.ets.core.keyword;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.latlon.ets.core.util.TestSuiteLogger;

/**
 * Implementation of a {@link DfddKeywordMatcher} retrieving the keywords from a file.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class DfddKeywordMatcherFromFile implements DfddKeywordMatcher {

    private static final String KEYWORD_FILE = "dfdd.keywords";

    private List<String> dfddKeywords;

    @Override
    public boolean containsAtLeastOneDfddKeyword( List<String> keywordsToCheck ) {
        parseKeywords();
        for ( String keyword : keywordsToCheck ) {
            if ( dfddKeywords.contains( keyword ) )
                return true;
        }
        return false;
    }

    private void parseKeywords() {
        if ( dfddKeywords == null ) {
            InputStream resource = DfddKeywordMatcherFromFile.class.getResourceAsStream( KEYWORD_FILE );
            dfddKeywords = parseKeywordsFromStream( resource );
        }
    }

    private List<String> parseKeywordsFromStream( InputStream resource ) {
        List<String> keywords = new ArrayList<String>();
        if ( resource != null ) {
            try ( BufferedReader br = new BufferedReader( new InputStreamReader( resource, "UTF-8" ) ) ) {
                String line;
                while ( ( line = br.readLine() ) != null ) {
                    String keyword = line.trim();
                    if ( !keyword.isEmpty() )
                        keywords.add( keyword );
                }
            } catch ( IOException e ) {
                TestSuiteLogger.log( Level.WARNING, "Keywords file " + KEYWORD_FILE + " could not be parsed.", e );
            }
        } else {
            TestSuiteLogger.log( Level.WARNING, "Could not find keywords file '" + KEYWORD_FILE + "'." );
        }
        return keywords;
    }

}
