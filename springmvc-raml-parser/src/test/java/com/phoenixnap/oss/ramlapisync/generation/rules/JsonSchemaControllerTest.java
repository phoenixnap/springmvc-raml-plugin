/*
 * Copyright (c) 2016 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;

public class JsonSchemaControllerTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

    @BeforeClass
    public static void initRaml() throws InvalidRamlResourceException {
        AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("json-type-raml-schema.raml");
    }

    @Test
    public void applySpring4RestTemplateClientRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("JsonSchemaAsType");
    }
}
