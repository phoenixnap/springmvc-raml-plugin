package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.Test;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4ControllerRulesTest extends AbstractControllerRuleTestBase {

    @Test
    public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
        Spring4ControllerStubRule rule = new Spring4ControllerStubRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("BaseControllerStub");
    }


}
