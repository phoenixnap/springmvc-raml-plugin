
package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.exception.InvalidRamlResourceException;

public class RequestBodyWithValidationTest extends AbstractRuleTestBase {

	@BeforeClass
	public static void initRaml() throws InvalidRamlResourceException {
		loadRaml("test-requestbody-with-validation.raml");
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
