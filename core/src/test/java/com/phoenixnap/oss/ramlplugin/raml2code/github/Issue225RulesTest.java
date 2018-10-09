package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 0.10.13
 */
public class Issue225RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void verify_raml_10_with_json_schema() throws Exception {
		loadRaml("issue-225.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue225Spring4ControllerStub");
	}
}
