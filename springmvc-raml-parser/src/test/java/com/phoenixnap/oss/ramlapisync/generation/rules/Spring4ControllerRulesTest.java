package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.Test;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4ControllerRulesTest extends AbstractControllerRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiControllerMetadata> rule;

    @Test
    public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerStubRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("BaseControllerStub");
    }

    @Test
    public void applySpring4ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("BaseControllerInterface");
    }

    @Test
    public void applySpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("BaseControllerDecorator");
    }

}
