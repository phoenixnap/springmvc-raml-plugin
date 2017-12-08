
package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.Valid;

public class Issue211RulesTest extends AbstractRuleTestBase {
   JCodeModel cm = new JCodeModel();
   private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;
   private ApiResourceMetadata controllerMetadata;

   @BeforeClass
   public static void initRaml() throws InvalidRamlResourceException {
//      AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("ISSUE-211-Create-Update-naming-resolution.raml");
//      AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("raml08issue.raml");
      AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("raml10NoIssue.raml");
   }

   @Test
   public void applySpring4ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
       jCodeModel = new JCodeModel();
       cm = new JCodeModel();
      rule = new Spring4ControllerInterfaceRule();
      controllerMetadata = getControllerMetadata();
      rule.apply(controllerMetadata, jCodeModel);
      verifyGeneratedCode("Issue211SpringInterfaceWithValidation");
   }
}
