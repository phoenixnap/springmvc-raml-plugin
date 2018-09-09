package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.TestPojoConfig;

/**
 * @author aleksandars
 * @since 2.0.1
 */
public class Issue245RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void verify_with_validation_enabled() throws Exception {
		loadRaml("issue-245.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue245EnabledSpring4ControllerDecorator");
	}

	@Test
	public void verify_with_validation_disabled() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setIncludeJsr303Annotations(false);
		loadRaml("issue-245.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue245DisabledSpring4ControllerDecorator");
		((TestPojoConfig) Config.getPojoConfig()).setIncludeJsr303Annotations(true);
	}
}
