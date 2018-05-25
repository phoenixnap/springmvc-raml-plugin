package com.phoenixnap.oss.ramlplugin.raml2code.github;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;
import org.junit.Test;

/**
 * @author vpashynskyi
 * @since 2.0.2
 */
public class Issue258RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void testValidInQueryParam() throws Exception {
		loadRaml("issue-258.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue258-retainPropertyName");
	}
}
