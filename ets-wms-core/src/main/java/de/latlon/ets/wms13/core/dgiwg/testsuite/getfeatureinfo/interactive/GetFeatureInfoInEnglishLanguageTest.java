package de.latlon.ets.wms13.core.dgiwg.testsuite.getfeatureinfo.interactive;

import static org.testng.Assert.assertTrue;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.Test;

import de.latlon.ets.wms13.core.domain.InteractiveTestResult;
import de.latlon.ets.wms13.core.domain.SuiteAttribute;

/**
 * Checks the result of the interactive test for the language of the metadata content.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetFeatureInfoInEnglishLanguageTest {

    @Test(description = "DGIWG - Web Map Service 1.3 Profile, 6.5.3.1., S.9, Requirement 4")
    public void featureInfoResponseInEnglishLanguage( ITestContext context )
                    throws XPathExpressionException, XPathFactoryConfigurationException {
        if ( context == null )
            throw new SkipException( "Context is null!" );
        Object attribute = context.getSuite().getAttribute( SuiteAttribute.INTERACTIVE_TEST_RESULT.getName() );
        if ( attribute == null )
            throw new SkipException( "Missing testresult!" );

        InteractiveTestResult interactiveTestResult = (InteractiveTestResult) attribute;
        boolean getFeatureInfoResponseInEnglishLanguage = interactiveTestResult.isGetFeatureInfoInEnglishLanguage();
        assertTrue( getFeatureInfoResponseInEnglishLanguage,
                    "Content of the GetFeatureInfo response is not in English language." );
    }

}