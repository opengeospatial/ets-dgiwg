package de.latlon.ets.wms13.core.domain;

/**
 * Wraps the results from interactive tests.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class InteractiveTestResult {

    private final boolean capabilitiesInEnglishLanguage;

    private final boolean getFeatureInfoInEnglishLanguage;

    private final boolean getFeatureInfoExceptionInEnglishLanguage;

    private final boolean getMapExceptionInEnglishLanguage;

    public InteractiveTestResult( boolean capabilitiesInEnglishLanguage, boolean getFeatureInfoInEnglishLanguage,
                                  boolean getFeatureInfoExceptionInEnglishLanguage,
                                  boolean getMapExceptionInEnglishLanguage ) {
        this.capabilitiesInEnglishLanguage = capabilitiesInEnglishLanguage;
        this.getFeatureInfoInEnglishLanguage = getFeatureInfoInEnglishLanguage;
        this.getFeatureInfoExceptionInEnglishLanguage = getFeatureInfoExceptionInEnglishLanguage;
        this.getMapExceptionInEnglishLanguage = getMapExceptionInEnglishLanguage;
    }

    /**
     * @return <code>true</code> if the test (capabilities in english language) passed, <code>false</code> otherwise
     */
    public boolean isCapabilitiesInEnglishLanguage() {
        return capabilitiesInEnglishLanguage;
    }

    /**
     * @return <code>true</code> if the test (GetFeatureInfo in english language) passed, <code>false</code> otherwise
     */
    public boolean isGetFeatureInfoInEnglishLanguage() {
        return getFeatureInfoInEnglishLanguage;
    }

    /**
     * @return <code>true</code> if the test (GetFeatureInfo exception in english language) passed, <code>false</code>
     *         otherwise
     */
    public boolean isGetFeatureInfoExceptionInEnglishLanguage() {
        return getFeatureInfoExceptionInEnglishLanguage;
    }

    /**
     * @return <code>true</code> if the test (GetMap exception in english language) passed, <code>false</code> otherwise
     */
    public boolean isGetMapExceptionInEnglishLanguage() {
        return getMapExceptionInEnglishLanguage;
    }

}