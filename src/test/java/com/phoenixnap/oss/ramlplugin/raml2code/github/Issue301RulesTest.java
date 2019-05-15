package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlParser;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.ConfigurableRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author manikmagar
 * @since 2.1.0
 */
public class Issue301RulesTest extends GitHubAbstractRuleTestBase {

	private ConfigurableRule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

	public Issue301RulesTest() {
		defaultRamlParser = new RamlParser("/v1/{somekey}");
	}

	@Test
	public void verifyBaseUriParameters() throws Exception {
		loadRaml("issue-301.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue301Spring4Decorator");
	}

}