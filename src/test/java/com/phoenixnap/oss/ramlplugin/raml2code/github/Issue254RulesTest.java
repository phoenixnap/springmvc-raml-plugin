package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Before;
import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4RestTemplateClientRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.SpringFeignClientInterfaceRule;

/**
 * @author aleksandars
 * @since 2.0.4
 */
public class Issue254RulesTest extends GitHubAbstractRuleTestBase {

	@Before
	public void init() {
		super.setGitHubValidatorBase(DEFAULT_GITHUB_VALIDATOR_BASE + "issue-254/");
	}

	@Test
	public void verify_decorator_inject_header() throws Exception {

		TestConfig.setInjectHttpHeadersParameter(true);

		loadRaml("issue-254.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue254-1Spring4ControllerDecorator");

		TestConfig.setInjectHttpHeadersParameter(false);
	}

	@Test
	public void verify_decorator_no_inject_header() throws Exception {

		TestConfig.setInjectHttpHeadersParameter(false);

		loadRaml("issue-254.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue254-2Spring4ControllerDecorator");

		TestConfig.setInjectHttpHeadersParameter(false);
	}

	@Test
	public void verify_interface_inject_header() throws Exception {

		TestConfig.setInjectHttpHeadersParameter(true);

		loadRaml("issue-254.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue254-1Spring4ControllerInterface");

		TestConfig.setInjectHttpHeadersParameter(false);
	}

	@Test
	public void verify_interface_no_inject_header() throws Exception {

		TestConfig.setInjectHttpHeadersParameter(false);

		loadRaml("issue-254.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue254-2Spring4ControllerInterface");

		TestConfig.setInjectHttpHeadersParameter(false);
	}

	@Test
	public void verify_client_inject_header() throws Exception {

		TestConfig.setInjectHttpHeadersParameter(true);

		loadRaml("issue-254.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue254-Spring4RestTemplateClient");

		TestConfig.setInjectHttpHeadersParameter(false);
	}

	@Test
	public void verify_feign_client_inject_header() throws Exception {

		TestConfig.setInjectHttpHeadersParameter(true);

		loadRaml("issue-254.raml");
		rule = new SpringFeignClientInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue254-FeignClientInterface");

		TestConfig.setInjectHttpHeadersParameter(false);
	}

}
