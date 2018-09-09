package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 2.0.1
 */
public class Issue253RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void verify_custom_datetime_types() throws Exception {
		TestConfig.setDateTimeType("java.time.ZonedDateTime");
		TestConfig.setDateType("java.time.LocalDate");
		TestConfig.setTimeType("java.time.LocalTime");

		loadRaml("issue-253.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue253Spring4ControllerDecorator");

		TestConfig.setDateTimeType(null);
		TestConfig.setDateType(null);
		TestConfig.setTimeType(null);
	}

}
