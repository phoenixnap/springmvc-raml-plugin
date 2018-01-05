package com.phoenixnap.oss.ramlplugin.raml2code.github;

import java.util.Set;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule;

/**
 * @author aleksandars
 * @since 0.10.11
 */
public class Issue183RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
		loadRaml("issue-183.raml");
		rule = new Spring4ControllerStubRule();
		Set<ApiResourceMetadata> allControllersMetadata = getAllControllersMetadata();
		for (ApiResourceMetadata apiResourceMetadata : allControllersMetadata) {
			rule.apply(apiResourceMetadata, jCodeModel);
		}
		verifyGeneratedCode("Issue183Spring4ControllerStub");
	}
}
