package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4RestTemplateClientRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.SpringFeignClientInterfaceRule;

/**
 * @author Aleksandar Stojsavljevic (aleksandars@ccbill.com)
 * @since 2.1.0
 */
public class Issue297RulesTest extends GitHubAbstractRuleTestBase {

	private static final String RAML = "issue-282.raml";

	@Before
	public void init() {
		super.setGitHubValidatorBase(DEFAULT_GITHUB_VALIDATOR_BASE + "issue-297/");
		TestConfig.setGeneratedAnnotation(true);
	}

	@After
	public void after() {
		TestConfig.setGeneratedAnnotation(false);
	}

	@Test
	public void check_generated_for_decorator() throws Exception {
		loadRaml(RAML);
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue297-1Spring4ControllerDecorator");
	}

	@Test
	public void check_generated_for_stub() throws Exception {
		loadRaml(RAML);
		rule = new Spring4ControllerStubRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue297-1Spring4ControllerStub");
	}

	@Test
	public void check_generated_for_interface() throws Exception {
		loadRaml(RAML);
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue297-1Spring4ControllerInterface");
	}

	@Test
	public void check_generated_for_resttemplate_client() throws Exception {
		loadRaml(RAML);
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue297-1Spring4RestTemplateClient");
	}

	@Test
	public void check_generated_for_feign_client() throws Exception {
		loadRaml(RAML);
		rule = new SpringFeignClientInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue297-1Spring4FeignClientInterface");
	}
}
