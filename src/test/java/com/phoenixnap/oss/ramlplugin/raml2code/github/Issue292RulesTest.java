package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 2.0.5
 */
public class Issue292RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void check_inline_enums_are_not_generated() throws Exception {
		loadRaml("issue-292-1.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue292-1Spring4ControllerDecorator");
	}

	@Test
	public void check_inline_enums_are_merged() throws Exception {
		loadRaml("issue-292-2.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue292-2Spring4ControllerDecorator");
	}
}
