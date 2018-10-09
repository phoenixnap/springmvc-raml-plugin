package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;

/**
 * @author yuranos
 * @since 0.10.13
 */
public class Issue193RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void testValidInQueryParam() throws Exception {
		loadRaml("issue-193.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue193-validqueryparameters");
	}
}
