package de.latlon.ets.core.keyword;

import java.util.List;

/**
 * Contains useful methods regarding keywords from DFDD register.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public interface DfddKeywordMatcher {

    /**
     * Checks if the passed list of keywords contains at least one keyword from DFDD register. The check is
     * case-sensitive!
     * 
     * @param keywordsToCheck
     *            the keywords to check, may be empty but never <code>null</code>
     * @return <code>true</code> if the passed list contains at least one keyword from DFDD register, <code>false</code>
     *         if not or the passed list is empty
     */
    boolean containsAtLeastOneDfddKeyword( List<String> keywordsToCheck );

}
