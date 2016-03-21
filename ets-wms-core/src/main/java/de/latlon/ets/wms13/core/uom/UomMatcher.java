package de.latlon.ets.wms13.core.uom;

/**
 * Contains useful methods regarding supported Unit of Measures.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public interface UomMatcher {

    /**
     * Checks if the passed UoM is one of the expected UoM (a base unit from
     * ​http://unitsofmeasure.org/ucum.html#section-Base-Units (Base Units, 4.2) or a MeTOC Unit. The check is
     * case-sensitive!
     * 
     * @param uom
     *            the unit to check, may be <code>null</code>
     * @return <code>true</code> if the uom is one of the expected, otherwise <code>false</code>
     */
    boolean isExpectedUoM( String uom );

}