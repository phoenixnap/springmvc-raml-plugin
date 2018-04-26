package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.SpringMvcEndpointGeneratorMojo.LogicForParamsAndMethodsNaming;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 2.0.1
 */
public class Issue250RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void verify_default_naming_logic() throws Exception {
		loadRaml("issue-250.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue250-1Spring4ControllerDecorator");
	}

	@Test
	public void verify_displayName_naming_logic() throws Exception {
		TestConfig.setLogicForParamsAndMethodsNaming(LogicForParamsAndMethodsNaming.DISPLAY_NAME);
		loadRaml("issue-250.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue250-2Spring4ControllerDecorator");
		TestConfig.setLogicForParamsAndMethodsNaming(LogicForParamsAndMethodsNaming.DEFAULT);
	}

	@Test
	public void verify_annotation_naming_logic() throws Exception {
		TestConfig.setLogicForParamsAndMethodsNaming(LogicForParamsAndMethodsNaming.ANNOTATION);
		loadRaml("issue-250.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue250-3Spring4ControllerDecorator");
		TestConfig.setLogicForParamsAndMethodsNaming(LogicForParamsAndMethodsNaming.DEFAULT);
	}
}
