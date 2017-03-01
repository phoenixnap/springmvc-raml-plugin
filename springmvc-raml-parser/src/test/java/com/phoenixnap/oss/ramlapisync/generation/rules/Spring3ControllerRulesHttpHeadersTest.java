
package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class Spring3ControllerRulesHttpHeadersTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

    public Spring3ControllerRulesHttpHeadersTest() {
        defaultRamlParser =  new RamlParser("com.gen.test", "/api", false, true);
    }
    
    @Test
    public void applySpring3ControllerStubRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerStubRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring3HttpHeadersBaseControllerStub");
    }
    
    @Test
    public void applySpring3ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring3HttpHeadersBaseControllerInterface");
    }
    
    @Test
    public void applySpring3ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring3HttpHeadersBaseControllerDecorator");
    }

}
