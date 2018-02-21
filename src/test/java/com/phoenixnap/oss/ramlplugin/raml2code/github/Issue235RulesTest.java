package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 0.10.14
 */
public class Issue235RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void verify_method_naming() throws Exception {
		loadRaml("issue-235.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue235Spring4ControllerDecorator");
	}
}
