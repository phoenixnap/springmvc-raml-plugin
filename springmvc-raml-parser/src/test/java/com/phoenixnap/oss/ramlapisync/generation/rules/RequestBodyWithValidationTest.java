
package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class RequestBodyWithValidationTest extends AbstractRuleTestBase {

   private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

   @BeforeClass
   public static void initRaml() {
      AbstractRuleTestBase.RAML = RamlVerifier.loadRamlFromFile("test-requestbody-with-validation.raml");
   }

   @Test
   public void applySpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
      rule = new Spring4ControllerDecoratorRule();
      rule.apply(getControllerMetadata(), jCodeModel);
      verifyGeneratedCode("BaseDecoratorRequestBodyWithValidation");
   }

   @Test
   public void applySpring4ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
      rule = new Spring4ControllerInterfaceRule();
      rule.apply(getControllerMetadata(), jCodeModel);
      verifyGeneratedCode("BaseInterfaceRequestBodyWithValidation");
   }
   @Test
   public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
      rule = new Spring4ControllerStubRule();
      rule.apply(getControllerMetadata(), jCodeModel);
      verifyGeneratedCode("BaseStubRequestBodyWithValidation");
   }
}
