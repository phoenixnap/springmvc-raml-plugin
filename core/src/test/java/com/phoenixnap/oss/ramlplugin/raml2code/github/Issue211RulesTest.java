package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;

public class Issue211RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void applyPostPutRaml10_shouldCreate_validCode() throws Exception {
		loadRaml("issue-211-create-update-naming-resolution.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue211SpringInterfaceForRaml10");
	}
}
