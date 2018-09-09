package com.phoenixnap.oss.ramlplugin.raml2code.github;

import com.phoenixnap.oss.ramlplugin.raml2code.exception.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4RestTemplateClientRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author kurtpa
 * @since 0.4.2
 */
public class Issue275RulesTest extends GitHubAbstractRuleTestBase {

	@BeforeClass
	public static void init() throws InvalidRamlResourceException {
		TestConfig.setUseBigDecimal(true);
		loadRaml("issue-275.raml");
	}

	@AfterClass
	public static void after() {
		TestConfig.resetConfig();
	}

	@Test
	public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue267-Spring4RestTemplateClient");
	}

}
