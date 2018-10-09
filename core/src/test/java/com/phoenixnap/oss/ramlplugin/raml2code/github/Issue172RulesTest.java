package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule;

/**
 * @author aleksandars
 * @since 0.10.8
 */
public class Issue172RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
		loadRaml("issue-172.raml");
		rule = new Spring4ControllerStubRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue172Spring4ControllerStub");
	}
}
