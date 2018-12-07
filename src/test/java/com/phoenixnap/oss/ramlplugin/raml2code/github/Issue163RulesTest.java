package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 2.0.5
 */
public class Issue163RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void check_http_request_as_method_parameter() throws Exception {
		TestConfig.setInjectHttpRequestParameter(true);
		loadRaml("issue-163.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue163Spring4ControllerDecoratorRule");
		TestConfig.setInjectHttpRequestParameter(false);
	}
}
