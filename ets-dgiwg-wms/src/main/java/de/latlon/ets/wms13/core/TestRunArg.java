package de.latlon.ets.wms13.core;

/**
 * An enumerated type defining all recognized test run arguments.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public enum TestRunArg {

    /**
     * An absolute URI referring to metadata about the WMS implementation under
     * test. This is expected to be a WMS 1.3.0 capabilities document where the
     * document element is {@code http://www.opengis.net/wms} WMS_Capabilities}.
     */
    WMS,

    /**
     * An absolute URI that specifies the location of the test execution service
     * at which the base WMS test suite is available. If not specified, the OGC
     * beta installation will be used.
     */
    TES,

    /**
     * <code>true</code> if the WMS contains vector data layers,
     * <code>false</code> otherwise
     */
    VECTOR,

    /**
     * <code>true</code> if the interactive test for metadata content in english
     * language is passed, <code>false</code> otherwise
     */
    CAPABILITIES_IN_ENGLISH,

    /**
     * <code>true</code> if the interactive test for GetFeatureInfo response in
     * english language is passed, <code>false</code> otherwise
     */
    GETFEATUREINFO_IN_ENGLISH,

    /**
     * <code>true</code> if the interactive test for GetFeatureInfo exceptions
     * in english language is passed, <code>false</code> otherwise
     */
    GETFEATUREINFO_EXCEPTION_IN_ENGLISH,

    /**
     * <code>true</code> if the interactive test for GetMap response in english
     * language is passed, <code>false</code> otherwise
     */
    GETMAP_EXCEPTION_IN_ENGLISH;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}