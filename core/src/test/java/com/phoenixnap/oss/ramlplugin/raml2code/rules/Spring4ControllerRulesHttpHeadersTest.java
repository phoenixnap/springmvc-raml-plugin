
package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;

public class Spring4ControllerRulesHttpHeadersTest extends AbstractRuleTestBase {

	public Spring4ControllerRulesHttpHeadersTest() {
		TestConfig.setInjectHttpHeadersParameter(true);
	}

	@Test
	public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerStubRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("BaseHttpHeadersControllerStub");
	}

	@Test
	public void applySpring4ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("BaseHttpHeadersControllerInterface");
	}

	@Test
	public void applySpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("BaseHttpHeadersControllerDecorator");
	}
}
