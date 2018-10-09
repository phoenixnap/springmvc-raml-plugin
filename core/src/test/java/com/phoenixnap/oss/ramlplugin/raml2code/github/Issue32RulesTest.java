package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.exception.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule;

/**
 * @author kurtpa
 * @since 0.4.2
 */
public class Issue32RulesTest extends GitHubAbstractRuleTestBase {

	@BeforeClass
	public static void init() throws InvalidRamlResourceException {
		loadRaml("issue-32.raml");
	}

	@Test
	public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerStubRule();
		rule.apply(getControllerMetadata(), jCodeModel);
	}

	@Test
	public void applySpring4ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
	}

	@Test
	public void applySpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
	}
}
