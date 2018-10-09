package com.phoenixnap.oss.ramlplugin.raml2code.github;

import static com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringConfigurableRule.DEFERRED_RESULT_RESPONSE_CONFIGURATION;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule;

/**
 * @author Aleksandar Stojsavljevic (aleksandars@ccbill.com)
 * @since 2.0.4
 */
public class Issue257RulesTest extends GitHubAbstractRuleTestBase {

	@Before
	public void init() {
		super.setGitHubValidatorBase(DEFAULT_GITHUB_VALIDATOR_BASE + "issue-257/");
	}

	@Test
	public void test_deferred_result_decorator_rule() throws Exception {
		loadRaml("issue-250.raml");

		Spring4ControllerDecoratorRule rule = new Spring4ControllerDecoratorRule();
		Map<String, String> configuration = new HashMap<>();
		configuration.put(DEFERRED_RESULT_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue257Spring4ControllerDecorator");
	}

	@Test
	public void test_deferred_result_interface_rule() throws Exception {
		loadRaml("issue-250.raml");

		Spring4ControllerInterfaceRule rule = new Spring4ControllerInterfaceRule();
		Map<String, String> configuration = new HashMap<>();
		configuration.put(DEFERRED_RESULT_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue257Spring4ControllerInterface");
	}

	@Test
	public void test_deferred_result_stub_rule() throws Exception {
		loadRaml("issue-250.raml");

		Spring4ControllerStubRule rule = new Spring4ControllerStubRule();
		Map<String, String> configuration = new HashMap<>();
		configuration.put(DEFERRED_RESULT_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue257Spring4ControllerStub");
	}
}
