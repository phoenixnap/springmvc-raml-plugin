package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 0.10.13
 */
public class Issue215RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void optional_param_as_resource_level() throws Exception {
		loadRaml("issue-215-1.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue215-1Spring4ControllerStub");
	}

	@Test
	public void optional_param_as_resource_part() throws Exception {
		loadRaml("issue-215-2.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue215-2Spring4ControllerStub");
	}

	@Test
	public void two_optional_parms() throws Exception {
		loadRaml("issue-215-3.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue215-3Spring4ControllerStub");
	}
}
