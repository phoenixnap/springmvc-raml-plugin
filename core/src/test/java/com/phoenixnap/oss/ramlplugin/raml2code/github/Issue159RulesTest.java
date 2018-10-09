package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4RestTemplateClientRule;

/**
 * @author aleksandars
 * @since 0.10.5
 */
public class Issue159RulesTest extends GitHubAbstractRuleTestBase {

	public Issue159RulesTest() {
		TestConfig.setResourceDepthInClassNames(2);
	}

	@Test
	public void applySpring4ClientStubRule_shouldCreate_validCode() throws Exception {
		loadRaml("issue-159.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue159Spring4ControllerStub");
	}
}
