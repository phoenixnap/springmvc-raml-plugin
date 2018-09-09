package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 2.0.3
 */
public class Issue265RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void testDontGenerateForAnnotation() throws Exception {
		loadRaml("issue-265.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue265Spring4ControllerDecorator");
	}
}
