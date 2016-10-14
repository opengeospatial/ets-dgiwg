package de.latlon.ets.wms13.core.client;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.xml.transform.Source;

import org.w3c.dom.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import de.latlon.ets.core.util.XMLUtils;
import de.latlon.ets.wms13.core.domain.DGIWGWMS;
import de.latlon.ets.wms13.core.domain.ProtocolBinding;
import de.latlon.ets.wms13.core.util.SOAPMessageConsumer;
import de.latlon.ets.wms13.core.util.ServiceMetadataUtils;

/**
 * A WMS 1.3.0 client component supporting HTTP GET and POST.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class WmsClient {

    private static final Logger LOGR = Logger.getLogger( WmsClient.class.getPackage().getName() );

    private Client client;

    /** A Document that describes the service under test. */
    private Document wmsCapabilities;

    /**
     * Constructs a client that is aware of the capabilities of some WMS. The request and response may be logged to a
     * default JDK logger (in the namespace "com.sun.jersey.api.client").
     * 
     * @param wmsCapabilities
     *            the WMS capabilities document, never <code>null</code>
     */
    public WmsClient( Document wmsCapabilities ) {
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add( SOAPMessageConsumer.class );
        this.client = Client.create( config );
        this.client.addFilter( new LoggingFilter() );
        this.wmsCapabilities = wmsCapabilities;
    }

    /**
     * Retrieves a complete representation of the capabilities document from the WMS implementation described by the
     * service metadata.
     * 
     * @return a document containing the response to a GetCapabilities request
     */
    public Document getCapabilities() {
        if ( null == this.wmsCapabilities ) {
            throw new IllegalStateException( "Service description is unavailable." );
        }
        URI endpoint = ServiceMetadataUtils.getOperationEndpoint( this.wmsCapabilities, DGIWGWMS.GET_CAPABILITIES,
                                                                  ProtocolBinding.GET );
        if (null == endpoint) {
            throw new RuntimeException("GetCapabilities (GET) endpoint not found in capabilities document.");
        }
        WebResource resource = client.resource( endpoint );
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add( DGIWGWMS.REQUEST_PARAM, DGIWGWMS.GET_CAPABILITIES );
        queryParams.add( DGIWGWMS.SERVICE_PARAM, DGIWGWMS.SERVICE_TYPE_CODE );
        queryParams.add( DGIWGWMS.VERSION_PARAM, DGIWGWMS.VERSION );
        return resource.queryParams( queryParams ).get( Document.class );
    }

    /**
     * Submits a HTTP GET request.
     * 
     * @param request
     *            the KVP encoded request, never <code>null</code>
     * @param endpoint
     *            the service endpoint, never <code>null</code>
     * @return the {@link ClientResponse} object representing the response message
     */
    public ClientResponse submitRequest( WmsKvpRequest request, URI endpoint ) {
        WebResource resource = client.resource( endpoint );
        return submitGetRequest( resource, request );
    }

    /**
     * Submits a HTTP POST request.
     * 
     * @param payload
     *            the payload in XML format
     * @param endpoint
     *            the service endpoint
     * @return the response message
     */
    public ClientResponse submitRequest( Source payload, URI endpoint ) {
        if ( payload == null || endpoint == null )
            throw new IllegalArgumentException( "Neither payload nor endpoint must be null" );
        WebResource resource = client.resource( endpoint );
        resource.uri( UriBuilder.fromUri( endpoint ).build() );
        return submitPostRequest( resource, payload );
    }

    private ClientResponse submitPostRequest( WebResource resource, Source payload ) {
        LOGR.log( Level.FINE, String.format( "Submitting POST request to URI %s", resource.getURI() ) );
        LOGR.log( Level.FINE, String.format( "Request Payload: %s", XMLUtils.transformToString( payload ) ) );
        ClientResponse response = null;
        try {
            response = resource.accept( DGIWGWMS.SOAP_XML ).type( DGIWGWMS.SOAP_XML ).post( ClientResponse.class,
                                                                                            payload );
            if ( LOGR.isLoggable( Level.FINE ) ) {
                LOGR.log( Level.FINE, String.format( "SOAP Response: %s", response.toString() ) );
            }
        } catch ( UniformInterfaceException | ClientHandlerException ex ) {
            LOGR.log( Level.SEVERE, "Failed to process SOAP request/response: " + resource.getURI(), ex );
        }
        return response;
    }

    private ClientResponse submitGetRequest( WebResource resource, WmsKvpRequest requestParameter ) {
        LOGR.log( Level.FINE, String.format( "Submitting GET request to URI %s", resource.getURI() ) );
        String queryString = requestParameter.asQueryString();
        URI requestURI = UriBuilder.fromUri( resource.getURI() ).replaceQuery( queryString ).build();
        LOGR.log( Level.FINE, String.format( "Request URI: %s", requestURI ) );
        resource = resource.uri( requestURI );
        return resource.get( ClientResponse.class );
    }

}