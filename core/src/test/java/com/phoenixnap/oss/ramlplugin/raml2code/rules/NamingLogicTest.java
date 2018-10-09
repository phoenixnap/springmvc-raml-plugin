package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.junit.Test;

/**
 * @author aleksandars
 * @since 2.0.0
 */
public class NamingLogicTest extends AbstractRuleTestBase {

	@Test
	public void verify_method_naming() throws Exception {
		loadRaml("raml-naming-logic.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("NamingLogicSpring4ControllerDecorator");
	}
}
