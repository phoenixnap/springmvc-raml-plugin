package com.phoenixnap.oss.ramlplugin.raml2code.github;

import static com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringConfigurableRule.CALLABLE_RESPONSE_CONFIGURATION;
import static com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringConfigurableRule.SIMPLE_RETURN_TYPES;

import java.util.HashMap;
import java.util.Map;

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
public class Issue275RulesTest extends GitHubAbstractRuleTestBase {

	private ConfigurableRule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

	@Test
	public void validate_basic_interface_rule() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-1Spring4ControllerInterface");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_callable_interface_rule() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275.raml");
		rule = new Spring4ControllerInterfaceRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-2Spring4ControllerInterface");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_simple_return_type_interface_rule() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275.raml");
		rule = new Spring4ControllerInterfaceRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(SIMPLE_RETURN_TYPES, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-3Spring4ControllerInterface");
	}

	@Test
	public void validate_basic_decorator_rule() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-1Spring4ControllerDecorator");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_callable_decorator_rule() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275.raml");
		rule = new Spring4ControllerDecoratorRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-2Spring4ControllerDecorator");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_simple_return_type_decorator_rule() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275.raml");
		rule = new Spring4ControllerDecoratorRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(SIMPLE_RETURN_TYPES, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-3Spring4ControllerDecorator");
	}

	@Test
	public void validate_basic_client_rule() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-Spring4RestTemplateClient");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

}
