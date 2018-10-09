package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.junit.Test;

/**
 * Test for Multipart mime requests
 * 
 * @author boskiantonio
 * @since 0.4.3
 *
 */
public class MultipartMimeRequestTest extends AbstractRuleTestBase {

	@Test
	public void applySpring3ControllerStubRule_shouldCreate_validCode() throws Exception {
		loadRaml("test-multipart-mime-request.raml");
		rule = new Spring4ControllerStubRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("MultipartRequestStub");
	}
}
