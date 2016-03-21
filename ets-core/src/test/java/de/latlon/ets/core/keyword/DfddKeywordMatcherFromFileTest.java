package de.latlon.ets.core.keyword;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import de.latlon.ets.core.keyword.DfddKeywordMatcher;
import de.latlon.ets.core.keyword.DfddKeywordMatcherFromFile;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class DfddKeywordMatcherFromFileTest {

    @Test
    public void testContainsAtLeastOneDfddKeyword_emptyList()
                    throws Exception {
        DfddKeywordMatcher dfddKeywordMatcher = new DfddKeywordMatcherFromFile();
        List<String> keywords = Collections.emptyList();
        boolean containsAtLeastOneDfddKeyword = dfddKeywordMatcher.containsAtLeastOneDfddKeyword( keywords );

        assertThat( containsAtLeastOneDfddKeyword, is( false ) );
    }

    @Test
    public void testContainsAtLeastOneDfddKeyword_listContainingOneDfddKeyword()
                    throws Exception {
        DfddKeywordMatcher dfddKeywordMatcher = new DfddKeywordMatcherFromFile();
        List<String> keywords = Collections.singletonList( "Borehole" );
        boolean containsAtLeastOneDfddKeyword = dfddKeywordMatcher.containsAtLeastOneDfddKeyword( keywords );

        assertThat( containsAtLeastOneDfddKeyword, is( true ) );
    }

    @Test
    public void testContainsAtLeastOneDfddKeyword_listContainingTwoDfddOneOtherKeywords()
                    throws Exception {
        DfddKeywordMatcher dfddKeywordMatcher = new DfddKeywordMatcherFromFile();
        List<String> keywords = Arrays.asList( "other", "Borehole", "Environmental monitoring facilities" );
        boolean containsAtLeastOneDfddKeyword = dfddKeywordMatcher.containsAtLeastOneDfddKeyword( keywords );

        assertThat( containsAtLeastOneDfddKeyword, is( true ) );
    }

    @Test
    public void testContainsAtLeastOneDfddKeyword_listContainingTwoOtherKeywords()
                    throws Exception {
        DfddKeywordMatcher dfddKeywordMatcher = new DfddKeywordMatcherFromFile();
        List<String> keywords = Arrays.asList( "other1", "other2" );
        boolean containsAtLeastOneDfddKeyword = dfddKeywordMatcher.containsAtLeastOneDfddKeyword( keywords );

        assertThat( containsAtLeastOneDfddKeyword, is( false ) );
    }

}
