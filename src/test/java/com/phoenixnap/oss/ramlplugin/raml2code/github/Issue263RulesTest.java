package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 2.0.2
 */
public class Issue263RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void testDontGenerateForAnnotation() throws Exception {
		TestConfig.setDontGenerateForAnnotation("skipThis");
		loadRaml("issue-263.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue263Spring4ControllerDecorator");
		TestConfig.setDontGenerateForAnnotation(null);
	}
}
