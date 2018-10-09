package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.SpringMvcEndpointGeneratorMojo.MethodsNamingLogic;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.SpringMvcEndpointGeneratorMojo.OverrideNamingLogicWith;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 2.0.1
 */
public class Issue250RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void verify_objects_naming_logic() throws Exception {
		loadRaml("issue-250.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue250-1Spring4ControllerDecorator");
	}

	@Test
	public void verify_overriding_naming_logic_with_displayName() throws Exception {
		TestConfig.setOverrideNamingLogicWith(OverrideNamingLogicWith.DISPLAY_NAME);
		loadRaml("issue-250.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue250-2Spring4ControllerDecorator");
		TestConfig.setOverrideNamingLogicWith(null);
	}

	@Test
	public void verify_overriding_naming_logic_with_annotation() throws Exception {
		TestConfig.setOverrideNamingLogicWith(OverrideNamingLogicWith.ANNOTATION);
		loadRaml("issue-250.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue250-3Spring4ControllerDecorator");
		TestConfig.setOverrideNamingLogicWith(null);
	}

	@Test
	public void verify_resources_naming_logic() throws Exception {
		TestConfig.setMethodsNamingLogic(MethodsNamingLogic.RESOURCES);
		loadRaml("issue-250.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue250-4Spring4ControllerDecorator");
		TestConfig.setMethodsNamingLogic(null);
	}

	@Test
	public void verify_resources_naming_logic_2() throws Exception {
		TestConfig.setMethodsNamingLogic(MethodsNamingLogic.RESOURCES);
		loadRaml("issue-250-1.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue250-5Spring4ControllerDecorator");
		TestConfig.setMethodsNamingLogic(null);
	}
}
