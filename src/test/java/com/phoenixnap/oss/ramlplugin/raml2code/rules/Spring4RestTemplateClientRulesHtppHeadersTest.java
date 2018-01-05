
package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;

public class Spring4RestTemplateClientRulesHtppHeadersTest extends AbstractRuleTestBase {

    public Spring4RestTemplateClientRulesHtppHeadersTest() {
    	TestConfig.setInjectHttpHeadersParameter(true);
    }
    
    @Test
    public void applySpring4SpringTemplateClient_shouldCreate_validCode() throws Exception {
        rule = new Spring4RestTemplateClientRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring4HttpHeadersBaseClient");
    }
    
}
