
package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.junit.Test;

public class PatternConstraintTest extends AbstractRuleTestBase {

	@Test
	public void applySpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
		loadRaml("test-pattern-constraint.raml");
		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("PatternConstraintSpring4Decorator");
	}
}
