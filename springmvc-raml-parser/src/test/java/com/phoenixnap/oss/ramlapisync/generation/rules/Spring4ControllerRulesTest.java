package com.phoenixnap.oss.ramlapisync.generation.rules;

import static com.phoenixnap.oss.ramlapisync.generation.rules.spring.SpringControllerDecoratorRule.CALLABLE_RESPONSE_CONFIGURATION;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
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
        configuration.put(CALLABLE_RESPONSE_CONFIGURATION,"true");
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
    public void applyAsyncSpring4ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerInterfaceRule();
        Map<String, String> configuration = new HashMap<>();
        configuration.put(CALLABLE_RESPONSE_CONFIGURATION,"true");
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
        configuration.put(CALLABLE_RESPONSE_CONFIGURATION,"true");
        rule.applyConfiguration(configuration);
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("BaseControllerDecoratorAsync");
    }

}
