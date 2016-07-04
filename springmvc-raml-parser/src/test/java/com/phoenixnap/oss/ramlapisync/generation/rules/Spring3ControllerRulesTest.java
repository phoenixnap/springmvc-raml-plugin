package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public class Spring3ControllerRulesTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;
    
    @Test
    public void applySpring3ControllerStubRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerStubRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring3BaseControllerStub");
    }
    
    @Test
    public void applySpring3ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring3BaseControllerInterface");
    }
    
    @Test
    public void applySpring3ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring3BaseControllerDecorator");
    }

}
