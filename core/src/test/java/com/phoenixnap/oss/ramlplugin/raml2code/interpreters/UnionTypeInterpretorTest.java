package com.phoenixnap.oss.ramlplugin.raml2code.interpreters;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.AbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule;

/**
 * @author rahul
 * @since 0.10.6
 */
public class UnionTypeInterpretorTest extends AbstractRuleTestBase {

	public UnionTypeInterpretorTest() {
		TestConfig.setResourceDepthInClassNames(2);
	}

	@Test
	public void applySpring4ClientStubRule_shouldCreate_validCode() throws Exception {
		loadRaml("raml-with-union-types.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("RamlWithUnionTypeSpring4ControllerInterface");
	}
}
