
package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class Spring4RestTemplateClientRulesHtppHeadersTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

    public Spring4RestTemplateClientRulesHtppHeadersTest() {
        defaultRamlParser =  new RamlParser("com.gen.test", "/api", false, true);
    }
    @Test
    public void applySpring4SpringTemplateClient_shouldCreate_validCode() throws Exception {
        rule = new Spring4RestTemplateClientRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring4HttpHeadersBaseClient");
    }
    
}
