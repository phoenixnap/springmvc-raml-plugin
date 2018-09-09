package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 2.0.4
 */
public class Issue266RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void verify_collection_initialization_false() throws Exception {
		TestConfig.setInitializeCollections(false);
		loadRaml("issue-266.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue266-1Spring4ControllerStub");
		TestConfig.setInitializeCollections(true);
	}

	@Test
	public void verify_collection_initialization_true() throws Exception {
		TestConfig.setInitializeCollections(true);
		loadRaml("issue-266.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue266-2Spring4ControllerStub");
		TestConfig.setInitializeCollections(true);
	}
}
