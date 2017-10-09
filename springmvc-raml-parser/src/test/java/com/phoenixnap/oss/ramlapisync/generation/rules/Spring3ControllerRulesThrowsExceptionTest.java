
package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.Test;

public class Spring3ControllerRulesThrowsExceptionTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

    public Spring3ControllerRulesThrowsExceptionTest() {
        defaultRamlParser =  new RamlParser("com.gen.test", "/api", false, false, true);
    }
    
    @Test
    public void applySpring3ControllerStubRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerStubRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring3ThrowsExceptionBaseControllerStub");
    }
    
    @Test
    public void applySpring3ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring3ThrowsExceptionBaseControllerInterface");
    }


    @Test
    public void applySpring3ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring3ThrowsExceptionBaseControllerDecorator");
    }

}
