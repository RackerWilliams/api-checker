
package com.rackspace.com.papi.components.checker.wadl

import scala.xml._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._

import com.rackspace.com.papi.components.checker.TestConfig

@RunWith(classOf[JUnitRunner])
class WADLCheckerPlainParamSpec extends BaseCheckerSpec {

  //
  //  Register some common prefixes, you'll need the for XPath
  //  assertions.
  //
  register ("xsd", "http://www.w3.org/2001/XMLSchema")
  register ("wadl","http://wadl.dev.java.net/2009/02")
  register ("xsl","http://www.w3.org/1999/XSL/Transform")
  register ("chk","http://www.rackspace.com/repose/wadl/checker")
  register ("tst","http://www.rackspace.com/repose/wadl/checker/step/test")

  feature ("The WADLCheckerBuilder can correctly transforma a WADL into checker format") {

    info ("As a developer")
    info ("I want to be able to transform a WADL which references multiple plain parameters into a ")
    info ("a description of a machine that can validate the API in checker format")
    info ("so that an API validator can process the checker format to validate the API")


    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                         <param name="stepType" style="plain" path="/tst:a/@stepType" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 3")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
            XPath("/tst:a/@id"), XPath("/tst:a/@stepType"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), XPath("/tst:a/@stepType"), ContentFail)
    }

    scenario("The WADL contains a POST  operation with plain params but a missing mediaType, should validaate but not create XPaths") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                         <param name="stepType" style="plain" path="/tst:a/@stepType" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true))
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 0")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 0")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 0")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 0")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"),  Accept)
    }

    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (rax:code extension)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test"
                   xmlns:rax="http://docs.rackspace.com/api">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" rax:code="401" required="true"/>
                         <param name="stepType" style="plain" path="/tst:a/@stepType" rax:code="500" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 3")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id", 401), XPath("/tst:a/@stepType", 500), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id", 401), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id", 401), XPath("/tst:a/@stepType", 500), ContentFail)
    }


    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (with rax:message extension)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test"
                   xmlns:rax="http://docs.rackspace.com/api">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true" rax:message="Missing id attribute"/>
                         <param name="stepType" style="plain" path="/tst:a/@stepType" required="true" rax:message="Missing stepType attribute"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
             XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true, false, false, "Xalan",
                                                  false, false, false, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 3")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id","Missing id attribute"), XPath("/tst:a/@stepType", "Missing stepType attribute"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id","Missing id attribute"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id","Missing id attribute"), XPath("/tst:a/@stepType", "Missing stepType attribute"), ContentFail)
    }

    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (with rax:message, rax:code extension)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test"
                   xmlns:rax="http://docs.rackspace.com/api">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true" rax:message="Missing id attribute" rax:code="401"/>
                         <param name="stepType" style="plain" path="/tst:a/@stepType" required="true" rax:message="Missing stepType attribute" rax:code="500"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true, false, false, "Xalan",
                                                  false, false, false, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 3")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id","Missing id attribute", 401), XPath("/tst:a/@stepType", "Missing stepType attribute", 500), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id","Missing id attribute", 401), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id","Missing id attribute", 401), XPath("/tst:a/@stepType", "Missing stepType attribute", 500), ContentFail)
    }

    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (different namespaces)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <wadl:application xmlns:wadl="http://wadl.dev.java.net/2009/02">
        <wadl:grammars>
            <wadl:include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </wadl:grammars>
        <wadl:resources base="https://test.api.openstack.com" xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
           <wadl:resource path="/a/b">
               <wadl:method name="POST">
                  <wadl:request>
                      <wadl:representation mediaType="application/xml" element="tst:a">
                         <wadl:param name="id" style="plain" path="/tst:a/@id" required="true"/>
                         <wadl:param name="stepType" style="plain" path="/tst:a/@stepType" required="true"/>
                      </wadl:representation>
                  </wadl:request>
               </wadl:method>
           </wadl:resource>
        </wadl:resources>
    </wadl:application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 3")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), XPath("/tst:a/@stepType"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), XPath("/tst:a/@stepType"), ContentFail)
    }


    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (wellform checks to false)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                         <param name="stepType" style="plain" path="/tst:a/@stepType" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, false, true, true, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 3")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), XPath("/tst:a/@stepType"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), XPath("/tst:a/@stepType"), ContentFail)
    }

    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (different reps)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                      </representation>
                      <representation mediaType="application/xml" element="tst:e">
                         <param name="id" style="plain" path="/tst:e/tst:id" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 4")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:id"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:id"), ContentFail)
    }

    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (different reps, multiple params)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                      </representation>
                      <representation mediaType="application/xml" element="tst:e">
                         <param name="id" style="plain" path="/tst:e/tst:id" required="true"/>
                         <param name="stepType" style="plain" path="/tst:e/tst:stepType" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 5")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:id"), XPath("/tst:e/tst:stepType"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:id"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:id"), XPath("/tst:e/tst:stepType"), ContentFail)
    }


    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (different reps, multiple params, same element in multiple reps)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                      </representation>
                      <representation mediaType="application/xml" element="tst:e">
                         <param name="id" style="plain" path="/tst:e/tst:id" required="true"/>
                      </representation>
                      <representation mediaType="application/xml" element="tst:e">
                         <param name="stepType" style="plain" path="/tst:e/tst:stepType" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 6")
      assert (checker, "count(/chk:checker/chk:step[@type='WELL_XML']) = 3")
      assert (checker, "count(/chk:checker/chk:step[@type='REQ_TYPE' and @match='(?i)(application/xml)(;.*)?']) = 3")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e']) = 2")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:id"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:stepType"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"), ContentFail)
    }


    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (different reps, multiple params, same element in multiple reps, rax message extension)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test"
                   xmlns:rax="http://docs.rackspace.com/api">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true" rax:message="Missing id in tst:a"/>
                      </representation>
                      <representation mediaType="application/xml" element="tst:e">
                         <param name="id" style="plain" path="/tst:e/tst:id" required="true" rax:message="Missing id in tst:e"/>
                      </representation>
                      <representation mediaType="application/xml" element="tst:e">
                         <param name="stepType" style="plain" path="/tst:e/tst:stepType" required="true" rax:message="Missing stepType in tst:e"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true, false, false, "Xalan",
                                                      false, false, false, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 6")
      assert (checker, "count(/chk:checker/chk:step[@type='WELL_XML']) = 3")
      assert (checker, "count(/chk:checker/chk:step[@type='REQ_TYPE' and @match='(?i)(application/xml)(;.*)?']) = 3")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e']) = 2")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id", "Missing id in tst:a"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id", "Missing id in tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:id", "Missing id in tst:e"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:stepType", "Missing stepType in tst:e"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"), ContentFail)
    }


    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (different reps, multiple params, same element in multiple reps, rax:message, rax:code extensions)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test"
                   xmlns:rax="http://docs.rackspace.com/api">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true" rax:message="Missing id in tst:a" rax:code="500"/>
                      </representation>
                      <representation mediaType="application/xml" element="tst:e">
                         <param name="id" style="plain" path="/tst:e/tst:id" required="true" rax:message="Missing id in tst:e" rax:code="501"/>
                      </representation>
                      <representation mediaType="application/xml" element="tst:e">
                         <param name="stepType" style="plain" path="/tst:e/tst:stepType" required="true" rax:message="Missing stepType in tst:e" rax:code="502"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true, false, false, "Xalan",
                                                      false, false, false, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 6")
      assert (checker, "count(/chk:checker/chk:step[@type='WELL_XML']) = 3")
      assert (checker, "count(/chk:checker/chk:step[@type='REQ_TYPE' and @match='(?i)(application/xml)(;.*)?']) = 3")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e']) = 2")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id", "Missing id in tst:a", 500), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id", "Missing id in tst:a", 500), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:id", "Missing id in tst:e", 501), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:stepType", "Missing stepType in tst:e", 502), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"), ContentFail)
    }


    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (different reps, multiple params, same element in multiple reps, dups on)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                      </representation>
                      <representation mediaType="application/xml" element="tst:e">
                         <param name="id" style="plain" path="/tst:e/tst:id" required="true"/>
                      </representation>
                      <representation mediaType="application/xml" element="tst:e">
                         <param name="stepType" style="plain" path="/tst:e/tst:stepType" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(true, false, true, true, true, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 5")
      assert (checker, "count(/chk:checker/chk:step[@type='WELL_XML']) = 1")
      assert (checker, "count(/chk:checker/chk:step[@type='REQ_TYPE' and @match='(?i)(application/xml)(;.*)?']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:id"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:stepType"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"), ContentFail)
    }

    scenario("The WADL contains a POST  operation accepting xml which must validate against an XSD with elements specified and multiple required plain params (different reps, multiple params one with required == false)") {
      Given ("a WADL that contains a POST operation with XML that must validate against an XSD with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                      </representation>
                      <representation mediaType="application/xml" element="tst:e">
                         <param name="id" style="plain" path="/tst:e/tst:id" required="true"/>
                         <param name="stepType" style="plain" path="/tst:e/tst:stepType" required="false"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 4")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:e/tst:stepType']) = 0")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:id"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:e","Expecting the root element to be: tst:e"),
              XPath("/tst:e/tst:id"), ContentFail)
    }

    scenario("The WADL contains a POST  operation accepting xml with elements specified and multiple required plain params") {
      Given ("a WADL that contains a POST operation with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                         <param name="stepType" style="plain" path="/tst:a/@stepType" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, false, true, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XSD']) = 0")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 3")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), XPath("/tst:a/@stepType"), Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a","Expecting the root element to be: tst:a"),
              XPath("/tst:a/@id"), ContentFail)
    }

    scenario("The WADL contains a POST  operation accepting xml with elements specified and multiple required plain params (no XSD, element, or well-form checks)") {
      Given ("a WADL that contains a POST operation with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                         <param name="stepType" style="plain" path="/tst:a/@stepType" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, false, false, false, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XSD']) = 0")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 2")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 0")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML,
              XPath("/tst:a/@id"), XPath("/tst:a/@stepType"), Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a/@id"), ContentFail)
    }

    scenario("The WADL contains a POST  operation accepting xml with elements specified and multiple required plain params (no element check)") {
      Given ("a WADL that contains a POST operation with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml" element="tst:a">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                         <param name="stepType" style="plain" path="/tst:a/@stepType" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, false, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XSD']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 2")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 0")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML,
              XPath("/tst:a/@id"), XPath("/tst:a/@stepType"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a/@id"),
              ContentFail)
    }

    scenario("The WADL contains a POST  operation accepting xml with no elements specified and multiple required plain params") {
      Given ("a WADL that contains a POST operation with elemenst specified and multiple plain params")
      val inWADL =
      <application xmlns="http://wadl.dev.java.net/2009/02"
                   xmlns:tst="http://www.rackspace.com/repose/wadl/checker/step/test">
        <grammars>
            <include href="src/test/resources/xsd/test-urlxsd.xsd"/>
        </grammars>
        <resources base="https://test.api.openstack.com">
           <resource path="/a/b">
               <method name="POST">
                  <request>
                      <representation mediaType="application/xml">
                         <param name="id" style="plain" path="/tst:a/@id" required="true"/>
                         <param name="stepType" style="plain" path="/tst:a/@stepType" required="true"/>
                      </representation>
                  </request>
               </method>
           </resource>
        </resources>
    </application>
      register("test://app/src/test/resources/xsd/test-urlxsd.xsd",
               XML.loadFile("src/test/resources/xsd/test-urlxsd.xsd"))
      val checker = builder.build (inWADL, TestConfig(false, false, true, true, true, 1, true))
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "in-scope-prefixes(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'tst'")
      assert(checker, "namespace-uri-for-prefix('tst', /chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 'http://www.rackspace.com/repose/wadl/checker/step/test'")
      assert(checker, "count(/chk:checker/chk:step[@type='XSD']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH']) = 2")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a']) = 0")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@id']) = 1")
      assert(checker, "count(/chk:checker/chk:step[@type='XPATH' and @match='/tst:a/@stepType']) = 1")
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML,
              XPath("/tst:a/@id"), XPath("/tst:a/@stepType"), XSD, Accept)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, ContentFail)
      assert (checker, Start, URL("a"), URL("b"), Method("POST"), ReqType("(application/xml)(;.*)?"), WellXML, XPath("/tst:a/@id"),
              ContentFail)
    }
  }
}
