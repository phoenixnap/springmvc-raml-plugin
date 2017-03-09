package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author kris galea
 * @since 0.5.0
 */
public class Spring4RestTemplateClientRulesTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;
    
    @Test
    public void applySpring4SpringTemplateClient_shouldCreate_validCode() throws Exception {
        rule = new Spring4RestTemplateClientRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Spring4BaseClient");
    }
    
}
