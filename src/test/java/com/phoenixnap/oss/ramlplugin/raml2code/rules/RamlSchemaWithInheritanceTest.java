package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.junit.Test;

/**
 * @author slavisam
 * @since 0.10.9
 */
public class RamlSchemaWithInheritanceTest extends AbstractRuleTestBase {

	@Test
	public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
		loadRaml("test-schema-with-inheritance.raml");
		rule = new Spring4ControllerStubRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("RamlSchemaWithInheritanceTest");
	}
}
