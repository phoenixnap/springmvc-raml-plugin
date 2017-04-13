
package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class PatternConstraintTest extends AbstractRuleTestBase {

   private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

   @BeforeClass
   public static void initRaml() throws InvalidRamlResourceException {
      AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("test-pattern-constraint.raml");
   }

   @Test
   public void applySpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
      rule = new Spring4ControllerDecoratorRule();
      rule.apply(getControllerMetadata(), jCodeModel);
      verifyGeneratedCode("PatternConstraintSpring4Decorator");
   }
}
