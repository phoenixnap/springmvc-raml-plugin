package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Before;
import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.SpringMvcEndpointGeneratorMojo.OverrideNamingLogicWith;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4RestTemplateClientRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.SpringFeignClientInterfaceRule;

/**
 * @author aleksandars
 * @since 2.0.4
 */
public class Issue286RulesTest extends GitHubAbstractRuleTestBase {

	@Before
	public void init() {
		super.setGitHubValidatorBase(DEFAULT_GITHUB_VALIDATOR_BASE + "issue-286/");
	}

	@Test
	public void verify_overriding_naming_logic_with_annotation_client() throws Exception {
		TestConfig.setOverrideNamingLogicWith(OverrideNamingLogicWith.ANNOTATION);
		loadRaml("issue-250.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue286-1Spring4RestTemplateClient");
		TestConfig.setOverrideNamingLogicWith(null);
	}

	@Test
	public void verify_overriding_naming_logic_with_display_name_client() throws Exception {
		TestConfig.setOverrideNamingLogicWith(OverrideNamingLogicWith.DISPLAY_NAME);
		loadRaml("issue-250.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue286-2Spring4RestTemplateClient");
		TestConfig.setOverrideNamingLogicWith(null);
	}

	@Test
	public void verify_overriding_naming_logic_with_annotation_feign_client() throws Exception {
		TestConfig.setOverrideNamingLogicWith(OverrideNamingLogicWith.ANNOTATION);
		loadRaml("issue-250.raml");
		rule = new SpringFeignClientInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue286-1Spring4FeignClient");
		TestConfig.setOverrideNamingLogicWith(null);
	}

	@Test
	public void verify_overriding_naming_logic_with_display_name_feign_client() throws Exception {
		TestConfig.setOverrideNamingLogicWith(OverrideNamingLogicWith.DISPLAY_NAME);
		loadRaml("issue-250.raml");
		rule = new SpringFeignClientInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue286-2Spring4FeignClient");
		TestConfig.setOverrideNamingLogicWith(null);
	}
}
