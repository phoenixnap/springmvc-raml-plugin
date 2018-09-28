package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Before;
import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.ConfigurableRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4RestTemplateClientRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.TestPojoConfig;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author aleksandars
 * @since 2.0.4
 */
public class Issue152RulesTest extends GitHubAbstractRuleTestBase {

	private ConfigurableRule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

	@Before
	public void init() {
		super.setGitHubValidatorBase(DEFAULT_GITHUB_VALIDATOR_BASE + "issue-152/");
	}

	@Test
	public void collision_java_keywords_decorator() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-152.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue152-Spring4ControllerDecorator");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void collision_java_keywords_interface() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-152.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue152-Spring4ControllerInterface");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void collision_java_keywords_client() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-152.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue152-Spring4RestTemplateClient");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

}