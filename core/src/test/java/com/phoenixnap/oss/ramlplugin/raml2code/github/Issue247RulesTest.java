package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 2.0.1
 */
public class Issue247RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void verify_sensitive_attribute() throws Exception {
		loadRaml("issue-247.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue247Spring4ControllerDecorator");
	}
}
