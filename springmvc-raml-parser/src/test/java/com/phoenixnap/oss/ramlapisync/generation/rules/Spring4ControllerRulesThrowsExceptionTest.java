
package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.Test;


public class Spring4ControllerRulesThrowsExceptionTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

    public Spring4ControllerRulesThrowsExceptionTest() {
        defaultRamlParser =  new RamlParser("com.gen.test", "/api", false, false, true);
    }

    @Test
    public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerStubRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring4ThrowsExceptionBaseControllerStub");
    }

    @Test
    public void applySpring4ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring4ThrowsExceptionBaseControllerInterface");
    }

    @Test
    public void applySpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring4ThrowsExceptionBaseControllerDecorator");
    }

}
