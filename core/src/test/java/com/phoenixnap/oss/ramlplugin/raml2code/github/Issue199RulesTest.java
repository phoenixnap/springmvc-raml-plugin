package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;

/**
 * @author agluque
 * @since 0.10.12
 */
public class Issue199RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void applySpring4ControllerInterfaceRule_shouldCreate_valid_class_inheritance() throws Exception {
		loadRaml("issue-199.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue199Spring4ControllerStub");
	}
}
