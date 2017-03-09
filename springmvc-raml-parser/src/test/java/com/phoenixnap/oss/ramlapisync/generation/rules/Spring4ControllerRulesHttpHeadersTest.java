
package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;


public class Spring4ControllerRulesHttpHeadersTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

    public Spring4ControllerRulesHttpHeadersTest() {
        defaultRamlParser =  new RamlParser("com.gen.test", "/api", false, true);
    }

    @Test
    public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerStubRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("BaseHttpHeadersControllerStub");
    }

    @Test
    public void applySpring4ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("BaseHttpHeadersControllerInterface");
    }

    @Test
    public void applySpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("BaseHttpHeadersControllerDecorator");
    }

}
