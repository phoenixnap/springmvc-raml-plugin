package com.phoenixnap.oss.ramlplugin.raml2code.github;

import java.util.Set;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.SpringMvcEndpointGeneratorMojo.OverrideNamingLogicWith;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule;

/**
 * @author aleksandars
 * @since 0.10.8
 */
public class Issue177RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void displayName_not_used_for_naming_logic() throws Exception {
		loadRaml("issue-177.raml");
		rule = new Spring4ControllerStubRule();
		Set<ApiResourceMetadata> allControllersMetadata = getAllControllersMetadata();
		for (ApiResourceMetadata apiResourceMetadata : allControllersMetadata) {
			rule.apply(apiResourceMetadata, jCodeModel);
		}
		verifyGeneratedCode("Issue177-1Spring4ControllerStub", serializeModel());
	}

	@Test
	public void displayName_used_for_naming_logic() throws Exception {
		TestConfig.setOverrideNamingLogicWith(OverrideNamingLogicWith.DISPLAY_NAME);
		loadRaml("issue-177.raml");
		rule = new Spring4ControllerStubRule();
		Set<ApiResourceMetadata> allControllersMetadata = getAllControllersMetadata();
		for (ApiResourceMetadata apiResourceMetadata : allControllersMetadata) {
			rule.apply(apiResourceMetadata, jCodeModel);
		}
		verifyGeneratedCode("Issue177-2Spring4ControllerStub", serializeModel());
		TestConfig.setOverrideNamingLogicWith(null);
	}
}
