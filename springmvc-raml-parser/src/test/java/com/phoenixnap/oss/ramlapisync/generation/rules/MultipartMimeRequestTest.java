package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * Test for Multipart mime requests
 * 
 * @author boskiantonio
 * @since 0.4.3
 *
 */
public class MultipartMimeRequestTest  extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

    @BeforeClass
    public static void initRaml() {
        AbstractRuleTestBase.RAML = RamlVerifier.loadRamlFromFile("test-multipart-mime-request.raml");
    }

    @Test
    public void applySpring3ControllerStubRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerStubRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("MultipartRequestStub");
    }
}
