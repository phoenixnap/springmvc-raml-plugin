package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MultipartMimeRequestTest  extends AbstractControllerRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiControllerMetadata> rule;

    @BeforeClass
    public static void initRaml() {
        AbstractControllerRuleTestBase.RAML = RamlVerifier.loadRamlFromFile("test-multipart-mime-request.raml");
    }

    @Test
    public void applySpring3ControllerStubRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerStubRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("MultipartRequestStub");
    }
}
