package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;

/**
 * @author yuranos
 * @since 0.10.13
 */
public class Issue212RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void testWithLongRootUrl() throws Exception {
		loadRaml("issue-212-1.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue212-1-pathvariablesnaming");
	}

	@Test
	public void testWithMatchingResourseNameAndPathVariable() throws Exception {
		loadRaml("issue-212-2.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue212-2-pathvariablesnaming");
	}

	@Test
	public void testWithNonmatchingResourseNameAndPathVariable() throws Exception {
		loadRaml("issue-212-3.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue212-3-pathvariablesnaming");
	}

}
