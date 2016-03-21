<?xml version="1.0" encoding="UTF-8"?>
<ctl:package xmlns:ctl="http://www.occamlab.com/ctl" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:tns="http://www.opengis.net/cite/iso19142" xmlns:saxon="http://saxon.sf.net/" xmlns:interactive="https://lat-lon.de/wms-1.3.0/dgiwg/ctl/interactive.xml"
  xmlns:tec="java:com.occamlab.te.TECore" xmlns:tng="java:de.latlon.ets.wms13.dgiwg.WmsDgiwgTestNGController">

  <ctl:function name="tns:run-ets-${ets-code}">
    <ctl:param name="testRunArgs">A Document node containing test run arguments (as XML properties).</ctl:param>
    <ctl:param name="outputDir">The directory in which the test results will be written.</ctl:param>
    <ctl:return>The test results as a Source object (root node).</ctl:return>
    <ctl:description>Runs the DGIWG WMS 1.3.0 (${version}) test suite.</ctl:description>
    <ctl:code>
      <xsl:variable name="controller" select="tng:new($outputDir)" />
      <xsl:copy-of select="tng:doTestRun($controller, $testRunArgs)" />
    </ctl:code>
  </ctl:function>

  <ctl:suite name="tns:ets-${ets-code}-${version}">
    <ctl:title>DGIWG WMS 1.3.0 Conformance Test Suite</ctl:title>
    <ctl:description>Checks DGIWG WMS 1.3.0 implementations for conformance to DGIWG WMS 1.3.0.</ctl:description>
    <ctl:starting-test>tns:Main</ctl:starting-test>
  </ctl:suite>

  <ctl:test name="tns:Main">
    <ctl:assertion>The test subject satisfies all applicable constraints.</ctl:assertion>
    <ctl:code>
      <xsl:variable name="form-data">
        <ctl:form method="POST" width="800" height="600" xmlns="http://www.w3.org/1999/xhtml">
          <h2>DGIWG WMS 1.3.0 Conformance Test Suite</h2>
          <div style="background:#F0F8FF" bgcolor="#F0F8FF">
            <fieldset style="background:#ccffff">
              <legend
                style="font-family: sans-serif; color: #000099; 
			   background-color:#F0F8FF; border-style: solid; border-width: medium; padding:4px">
                Implementation under test
              </legend>
              <p>
                <label for="wms-uri">
                  <h4 style="margin-bottom: 0.5em">Location of WMS capabilities document (http: or file: URI)</h4>
                </label>
                <input id="wms-uri" name="wms-uri" size="96" type="text" value="" />
              </p>
              <p>
                <label for="wms-vector">
                  <h5 style="margin-bottom: 0.5em">Contains vector data layers?</h5>
                </label>
                <input id="wms-vector-false" name="wms-vector" type="radio" value="no" checked="checked">
                  <label for="wms-vector-false"> no</label>
                </input>
                <br />
                <input id="wms-vector-true" name="wms-vector" type="radio" value="yes">
                  <label for="wms-vector-true"> yes</label>
                </input>
              </p>
            </fieldset>
          </div>
          <p>
            <input class="form-button" type="submit" value="Start" />
            |
            <input class="form-button" type="reset" value="Clear" />
          </p>
        </ctl:form>
      </xsl:variable>
      <xsl:variable name="capbilitiesInEnglishLanguage">
        <ctl:call-function name="interactive:capabilitiesInEnglishLanguage">
          <ctl:with-param name="wms.capabilities.url" select="normalize-space($form-data/values/value[@key='wms-uri'])" />
        </ctl:call-function>
      </xsl:variable>
      <xsl:variable name="getFeatureInfoInEnglishLanguage">
        <ctl:call-function name="interactive:featureInfoResponseInEnglishLanguage">
          <ctl:with-param name="wms.capabilities.url" select="normalize-space($form-data/values/value[@key='wms-uri'])" />
        </ctl:call-function>
      </xsl:variable>
      <xsl:variable name="getFeatureInfoExceptionInEnglishLanguage">
        <ctl:call-function name="interactive:featureInfoExceptionInEnglishLanguage">
          <ctl:with-param name="wms.capabilities.url" select="normalize-space($form-data/values/value[@key='wms-uri'])" />
        </ctl:call-function>
      </xsl:variable>
      <xsl:variable name="getMapExceptionInEnglishLanguage">
        <ctl:call-function name="interactive:mapExceptionInEnglishLanguage">
          <ctl:with-param name="wms.capabilities.url" select="normalize-space($form-data/values/value[@key='wms-uri'])" />
        </ctl:call-function>
      </xsl:variable>
      <xsl:variable name="test-run-props">
        <properties version="1.0">
          <entry key="wms">
            <xsl:value-of select="normalize-space($form-data/values/value[@key='wms-uri'])" />
          </entry>
          <entry key="vector">
            <xsl:choose>
              <xsl:when test="normalize-space($form-data/values/value[@key='wms-vector']) = 'yes'">
                <xsl:text>true</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>false</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </entry>
          <entry key="capabilities_in_english">
            <xsl:value-of select="$capbilitiesInEnglishLanguage" />
          </entry>
          <entry key="getfeatureinfo_in_english">
            <xsl:value-of select="$getFeatureInfoInEnglishLanguage" />
          </entry>
          <entry key="getfeatureinfo_exception_in_english">
            <xsl:value-of select="$getFeatureInfoExceptionInEnglishLanguage" />
          </entry>
          <entry key="getmap_exception_in_english">
            <xsl:value-of select="$getMapExceptionInEnglishLanguage" />
          </entry>
        </properties>
      </xsl:variable>
      <xsl:variable name="testRunDir">
        <xsl:value-of select="tec:getTestRunDirectory($te:core)" />
      </xsl:variable>
      <xsl:variable name="test-results">
        <ctl:call-function name="tns:run-ets-${ets-code}">
          <ctl:with-param name="testRunArgs" select="$test-run-props" />
          <ctl:with-param name="outputDir" select="$testRunDir" />
        </ctl:call-function>
      </xsl:variable>
      <xsl:call-template name="tns:testng-report">
        <xsl:with-param name="results" select="$test-results" />
        <xsl:with-param name="outputDir" select="$testRunDir" />
      </xsl:call-template>
      <xsl:variable name="summary-xsl" select="tec:findXMLResource($te:core, '/testng-summary.xsl')" />
      <ctl:message>
        <xsl:value-of select="saxon:transform(saxon:compile-stylesheet($summary-xsl), $test-results)" />
        See detailed test report in the TE_BASE/users/
        <xsl:value-of select="concat(substring-after($testRunDir, 'users/'), '/html/')" />
        directory.
      </ctl:message>
      <xsl:if test="xs:integer($test-results/testng-results/@failed) gt 0">
        <xsl:for-each select="$test-results//test-method[@status='FAIL' and @description='prerequisite']">
          <ctl:message>
            Test prerequisite
            <xsl:value-of select="./@name" />
            failed:
            <xsl:value-of select=".//message" />
          </ctl:message>
        </xsl:for-each>
        <xsl:for-each select="$test-results//test-method[@status='FAIL' and not(@is-config='true')]">
          <ctl:message>
            Test method
            <xsl:value-of select="./@name" />
            :
            <xsl:value-of select=".//message" />
          </ctl:message>
        </xsl:for-each>
        <ctl:fail />
      </xsl:if>
    </ctl:code>
  </ctl:test>

  <xsl:template name="tns:testng-report">
    <xsl:param name="results" />
    <xsl:param name="outputDir" />
    <xsl:variable name="stylesheet" select="tec:findXMLResource($te:core, '/testng-report.xsl')" />
    <xsl:variable name="reporter" select="saxon:compile-stylesheet($stylesheet)" />
    <xsl:variable name="report-params" as="node()*">
      <xsl:element name="testNgXslt.outputDir">
        <xsl:value-of select="concat($outputDir, '/html')" />
      </xsl:element>
    </xsl:variable>
    <xsl:copy-of select="saxon:transform($reporter, $results, $report-params)" />
  </xsl:template>
</ctl:package>
