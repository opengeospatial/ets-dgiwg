package de.latlon.ets.wms13.core.domain;

/**
 * An enumerated type that indicates how a request message is bound to an application protocol. In effect, a binding
 * prescribes how the message content is mapped into a concrete exchange format.
 * 
 * <ul>
 * <li>HTTP GET (element name: 'Get')</li>
 * <li>HTTP POST (element name : 'Post')</li>
 * </ul>
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public enum ProtocolBinding {

    /** HTTP GET method */
    GET( DGIWGWMS.KVP_ENC ),
    /** HTTP POST method */
    POST( DGIWGWMS.XML_ENC ),
    /** SOAP */
    SOAP( DGIWGWMS.SOAP_ENC ),
    /** Any supported binding */
    ANY( "" );

    private final String elementName;

    private ProtocolBinding( String elementName ) {
        this.elementName = elementName;
    }

    public String getElementName() {
        return elementName;
    }

}