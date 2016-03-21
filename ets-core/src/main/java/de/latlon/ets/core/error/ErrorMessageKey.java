package de.latlon.ets.core.error;

/**
 * Defines keys used to access localized messages for assertion errors. The messages are stored in Properties files that
 * are encoded in ISO-8859-1 (Latin-1). For some languages the (@code native2ascii) tool must be used to process the
 * files and produce escaped Unicode characters.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public enum ErrorMessageKey {

    MISSING_XML_ENTITY( "MissingXMLEntity" ),

    LOCAL_NAME( "LocalName" ),

    NAMESPACE_NAME( "NamespaceName" ),

    XPATH_ERROR( "XPathError" ),

    XPATH_RESULT( "XPathResult" ),

    NOT_SCHEMA_VALID( "NotSchemaValid" ),

    XML_ERROR( "XMLError" );

    private final String errorMessageKey;

    private ErrorMessageKey( String errorMessageKey ) {
        this.errorMessageKey = errorMessageKey;
    }

    public String getKey() {
        return errorMessageKey;
    }

}