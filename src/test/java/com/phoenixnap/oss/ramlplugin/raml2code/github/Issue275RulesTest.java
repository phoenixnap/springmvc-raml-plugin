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
		TestConfig.setUseBigIntegers(true);
	}

	@AfterClass
	public static void after() {
		TestConfig.resetConfig();
	}

	@Test
	public void applySpring4RestTemplateClientRule1_shouldCreate_validCode() throws Exception {
		loadRaml("issue-275-1.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-1-Spring4RestTemplateClient");
	}

	@Test
	public void applySpring4RestTemplateClientRule2_shouldCreate_validCode() throws Exception {
		loadRaml("issue-275-2.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-2-Spring4RestTemplateClient");
	}

}
