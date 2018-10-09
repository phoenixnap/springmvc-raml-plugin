package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 2.0
 */
public class Issue242RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void verify_discriminator_support() throws Exception {
		loadRaml("issue-242.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue242Spring4ControllerDecorator");
	}
}
