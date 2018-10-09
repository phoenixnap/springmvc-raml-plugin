package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.ConfigurableRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author aleksandars
 * @since 2.0.4
 */
public class Issue282RulesTest extends GitHubAbstractRuleTestBase {

	private ConfigurableRule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

	@Test
	public void check_default_enum_name() throws Exception {
		loadRaml("issue-282.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue282Spring4ControllerStub");
	}
}
