package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;

/**
 * @author aleksandars
 * @since 0.10.12
 */
public class Issue204RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void applySpring4ControllerInterfaceRule_shouldCreate_valid_class_inheritance() throws Exception {
		loadRaml("issue-204.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue204Spring4ControllerStub");
	}
}
