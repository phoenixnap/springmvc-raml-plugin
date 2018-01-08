package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringFeignClientInterfaceDecoratorRule;

/**
 * @author Aleksandar Stojsavljevic
 * @since 0.8.6
 */
public class SpringFeignClientRulesTest extends AbstractRuleTestBase {

	@Test
	public void applySpringFeignClient_shouldCreate_validCode() throws Exception {

		loadRaml("test-feign-client.raml");

		rule = new SpringFeignClientInterfaceDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("FeignClient");
	}

	@Test
	public void applySpringFeignClient_shouldCreate_defaultVaules() throws Exception {

		loadRaml("test-default-values.raml");

		rule = new SpringFeignClientInterfaceDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("FeignClientDefaultValues");
	}

}
