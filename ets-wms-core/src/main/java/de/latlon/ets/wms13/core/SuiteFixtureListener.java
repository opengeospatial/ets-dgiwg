package de.latlon.ets.wms13.core;

import static de.latlon.ets.wms13.core.util.ServiceMetadataUtils.parseLayerInfo;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.Reporter;
import org.w3c.dom.Document;

import de.latlon.ets.core.util.TestSuiteLogger;
import de.latlon.ets.core.util.URIUtils;
import de.latlon.ets.wms13.core.domain.DGIWGWMS;
import de.latlon.ets.wms13.core.domain.InteractiveTestResult;
import de.latlon.ets.wms13.core.domain.SuiteAttribute;

/**
 * A listener that performs various tasks before and after a test suite is run, usually concerned with maintaining a
 * shared test suite fixture. Since this listener is loaded using the ServiceLoader mechanism, its methods will be
 * called before those of other suite listeners listed in the test suite definition and before any annotated
 * configuration methods.
 * 
 * Attributes set on an ISuite instance are not inherited by constituent test group contexts (ITestContext). However,
 * suite attributes are still accessible from lower contexts.
 * 
 * @see org.testng.ISuite ISuite interface
 */
public class SuiteFixtureListener implements ISuiteListener {

    @Override
    public void onStart( ISuite suite ) {
        processWmsParameter( suite );
        Reporter.clear(); // clear output from previous test runs
        StringBuilder str = new StringBuilder( "Initial test run parameters:\n" );
        str.append( suite.getXmlSuite().getAllParameters().toString() );
        Reporter.log( str.toString() );
        TestSuiteLogger.log( Level.CONFIG, str.toString() );
    }

    @Override
    public void onFinish( ISuite suite ) {
        Reporter.log( "Success? " + !suite.getSuiteState().isFailed() );
        String reportDir = suite.getOutputDirectory();
        String msg = String.format( "Test run directory: %s",
                                    reportDir.substring( 0, reportDir.lastIndexOf( File.separatorChar ) ) );
        Reporter.log( msg );
    }

    /**
     * Processes the "wms" test suite parameter that specifies a URI reference for the service description (capabilities
     * document). The URI is dereferenced and the entity is parsed; the resulting Document object is set as the value of
     * the {@link SuiteAttribute#TEST_SUBJECT testSubject} suite attribute.
     * 
     * @param suite
     *            An ISuite object representing a TestNG test suite.
     */
    void processWmsParameter( ISuite suite ) {
        Map<String, String> params = suite.getXmlSuite().getParameters();
        String wmsRef = params.get( TestRunArg.WMS.toString() );
        if ( ( null == wmsRef ) || wmsRef.isEmpty() ) {
            throw new IllegalArgumentException( "Required parameter not found" );
        }
        URI wmsURI = URI.create( wmsRef );
        Document doc = null;
        try {
            doc = URIUtils.resolveURIAsDocument( wmsURI );
            if ( !DGIWGWMS.WMS_CAPABILITIES.equals( doc.getDocumentElement().getLocalName() ) ) {
                throw new RuntimeException( "Did not receive WMS capabilities document: "
                                            + doc.getDocumentElement().getNodeName() );
            }
        } catch ( Exception ex ) {
            // push exception up through TestNG ISuiteListener interface
            throw new RuntimeException( "Failed to parse resource located at " + wmsURI, ex );
        }
        if ( null != doc ) {
            suite.setAttribute( SuiteAttribute.TEST_SUBJECT.getName(), doc );
            suite.setAttribute( SuiteAttribute.LAYER_INFO.getName(), parseLayerInfo( doc ) );
            suite.setAttribute( SuiteAttribute.IS_VECTOR.getName(), parseBoolean( params, TestRunArg.VECTOR ) );
            suite.setAttribute( SuiteAttribute.INTERACTIVE_TEST_RESULT.getName(), parseInteractiveTestResults( params ) );
        }
    }

    private Object parseInteractiveTestResults( Map<String, String> params ) {
        boolean capabilitiesInEnglishLanguage = parseBoolean( params, TestRunArg.CAPABILITIES_IN_ENGLISH );
        boolean getFeatureInfoInEnglishLanguage = parseBoolean( params, TestRunArg.GETFEATUREINFO_IN_ENGLISH );
        boolean getFeatureInfoExceptionInEnglishLanguage = parseBoolean( params,
                                                                         TestRunArg.GETFEATUREINFO_EXCEPTION_IN_ENGLISH );
        boolean getMapExceptionInEnglishLanguage = parseBoolean( params, TestRunArg.GETMAP_EXCEPTION_IN_ENGLISH );
        return new InteractiveTestResult( capabilitiesInEnglishLanguage, getFeatureInfoInEnglishLanguage,
                        getFeatureInfoExceptionInEnglishLanguage, getMapExceptionInEnglishLanguage );
    }

    private boolean parseBoolean( Map<String, String> params, TestRunArg arg ) {
        String key = arg.toString();
        if ( params.containsKey( key ) ) {
            String vectorParam = params.get( key );
            return Boolean.parseBoolean( vectorParam );
        }
        return false;
    }

}