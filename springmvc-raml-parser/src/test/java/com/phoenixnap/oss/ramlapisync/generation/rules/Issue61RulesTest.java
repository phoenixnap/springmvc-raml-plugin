/*
 * Copyright (c) 2016 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class Issue61RulesTest extends AbstractRuleTestBase  {

   private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

   @BeforeClass
   public static void initRaml() {
      AbstractRuleTestBase.RAML = RamlParser.loadRamlFromFile("issue-61.raml");

      class P extends ParameterizedTypeReference<String> {
      }
      ParameterizedTypeReference<String>
      j = new  ParameterizedTypeReference<String>() {};

   }

   @Test
   public void applySpring4RestTemplateClientRule_shouldCreate_validCode() throws Exception {
      rule = new Spring4RestTemplateClientRule();
      rule.apply(getControllerMetadata(), jCodeModel);
      verifyGeneratedCode("Issue61BaseClient");
   }
}
