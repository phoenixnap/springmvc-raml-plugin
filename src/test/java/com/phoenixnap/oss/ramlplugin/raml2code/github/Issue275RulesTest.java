package com.phoenixnap.oss.ramlplugin.raml2code.github;

import static com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringConfigurableRule.CALLABLE_RESPONSE_CONFIGURATION;
import static com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringConfigurableRule.SIMPLE_RETURN_TYPES;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.ConfigurableRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule;
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

	@Before
	public void init() {
		super.setGitHubValidatorBase(DEFAULT_GITHUB_VALIDATOR_BASE + "issue-275/");
	}

	@Test
	public void validate_basic_interface_rule_decimal() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Spring4ControllerInterface");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_basic_interface_rule_integer() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigInteger-Spring4ControllerInterface");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void validate_basic_interface_rule_decimal_format() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-2.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Format-Spring4ControllerInterface");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_basic_interface_rule_decimal_integer_format() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-275-2.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-BigInteger-Format-Spring4ControllerInterface");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void validate_callable_interface_rule_decimal() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerInterfaceRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Callable-Spring4ControllerInterface");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_callable_interface_rule_integer() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerInterfaceRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigInteger-Callable-Spring4ControllerInterface");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void validate_callable_interface_rule_decimal_format() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-2.raml");
		rule = new Spring4ControllerInterfaceRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Callable-Format-Spring4ControllerInterface");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_callable_interface_rule_decimal_integer_format() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-275-2.raml");
		rule = new Spring4ControllerInterfaceRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-BigInteger-Callable-Format-Spring4ControllerInterface");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void validate_simple_return_type_interface_rule_decimal() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerInterfaceRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(SIMPLE_RETURN_TYPES, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Simple-Spring4ControllerInterface");
	}

	@Test
	public void validate_basic_decorator_rule_decimal() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Spring4ControllerDecoratorRule");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_basic_decorator_rule_integer() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigInteger-Spring4ControllerDecoratorRule");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void validate_basic_decorator_rule_decimal_format() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-2.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Format-Spring4ControllerDecoratorRule");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_basic_decorator_rule_decimal_integer_format() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-275-2.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-BigInteger-Format-Spring4ControllerDecoratorRule");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void validate_callable_decorator_rule_decimal() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerDecoratorRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Callable-Spring4ControllerDecorator");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_callable_decorator_rule_integer() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerDecoratorRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigInteger-Callable-Spring4ControllerDecorator");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void validate_callable_decorator_rule_decimal_format() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-2.raml");
		rule = new Spring4ControllerDecoratorRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Callable-Format-Spring4ControllerDecorator");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_callable_decorator_rule_decimal_integer_format() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-275-2.raml");
		rule = new Spring4ControllerDecoratorRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-BigInteger-Callable-Format-Spring4ControllerDecorator");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void validate_simple_return_type_decorator_rule_decimal() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerDecoratorRule();

		Map<String, String> configuration = new HashMap<>();
		configuration.put(SIMPLE_RETURN_TYPES, "true");
		rule.applyConfiguration(configuration);

		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Simple-Spring4ControllerDecorator");
	}

	@Test
	public void validate_basic_client_rule_decimal() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Spring4RestTemplateClient");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_basic_client_rule_integer() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigInteger-Spring4RestTemplateClient");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void validate_basic_client_rule_decimal_format() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-2.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Format-Spring4RestTemplateClient");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_basic_client_rule_decimal_integer_format() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-275-2.raml");
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-BigInteger-Format-Spring4RestTemplateClient");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

	@Test
	public void validate_basic_stub_rule_decimal() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerStubRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigDecimal-Spring4ControllerStub");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(false);
	}

	@Test
	public void validate_basic_stub_rule_integer() throws Exception {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);
		loadRaml("issue-275-1.raml");
		rule = new Spring4ControllerStubRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue275-BigInteger-Spring4ControllerStub");
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(false);
	}

}
