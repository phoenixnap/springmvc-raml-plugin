package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.junit.Test;

/**
 * @author kris galea
 * @since 0.5.0
 */
public class Spring4RestTemplateClientRulesTest extends AbstractRuleTestBase {

	@Test
	public void applySpring4SpringTemplateClient_shouldCreate_validCode() throws Exception {
		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Spring4BaseClient");
	}

}
