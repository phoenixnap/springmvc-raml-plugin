package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4RestTemplateClientRule;

/**
 * @author aleksandars
 * @since 2.0
 */
public class Issue241RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void verify_multiple_rest_templates() throws Exception {
		loadRaml("issue-241.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue241Spring4RestTemplateClient");
	}
}
