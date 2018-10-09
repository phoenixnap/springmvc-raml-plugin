package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;

/**
 * @author aleksandars
 * @since 2.0.2
 */
public class Issue261RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void verify_naming_logic_for_delete() throws Exception {
		loadRaml("issue-261.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue261Spring4ControllerDecorator");
	}

}
