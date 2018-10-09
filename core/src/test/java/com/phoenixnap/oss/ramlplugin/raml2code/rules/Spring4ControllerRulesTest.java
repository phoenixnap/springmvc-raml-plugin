package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import static com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringConfigurableRule.CALLABLE_RESPONSE_CONFIGURATION;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public class Spring4ControllerRulesTest extends AbstractRuleTestBase {

	private ConfigurableRule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

	@Test
	public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerStubRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("BaseControllerStub");
	}

	@Test
	public void applyAsyncSpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerStubRule();
		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("BaseControllerStubAsync");
	}

	@Test
	public void applySpring4ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("BaseControllerInterface");
	}

	@Test
	public void applySpring4ControllerInterfaceRuleWithObjectReturnType_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerInterfaceRule();
		((Spring4ControllerInterfaceRule) rule).setSimpleReturnTypes(true);
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("BaseControllerInterfaceWithObjectReturnTypes");
	}

	@Test
	public void applySpring4ControllerInterfaceWithShortcutMappingsRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerInterfaceRule();
		((Spring4ControllerInterfaceRule) rule).setUseShortcutMethodMappings(true);
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("BaseControllerInterfaceWithShortcutMappings");
	}

	@Test
	public void applyAsyncSpring4ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerInterfaceRule();
		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("BaseControllerInterfaceAsync");
	}

	@Test
	public void applySpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("BaseControllerDecorator");
	}

	@Test
	public void applyAsyncSpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerDecoratorRule();
		Map<String, String> configuration = new HashMap<>();
		configuration.put(CALLABLE_RESPONSE_CONFIGURATION, "true");
		rule.applyConfiguration(configuration);
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("BaseControllerDecoratorAsync");
	}

}
