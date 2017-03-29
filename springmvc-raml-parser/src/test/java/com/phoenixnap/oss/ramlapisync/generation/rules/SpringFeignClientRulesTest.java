package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.SpringFeignClientInterfaceDecoratorRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author Aleksandar Stojsavljevic
 * @since 0.8.6
 */
public class SpringFeignClientRulesTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;
    
    @Test
	public void applySpringFeignClient_shouldCreate_validCode() throws Exception {

		AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("test-feign-client.raml");

		rule = new SpringFeignClientInterfaceDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("FeignClient");
	}

	@Test
	public void applySpringFeignClient_shouldCreate_defaultVaules() throws Exception {

		AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("test-default-values.raml");

        rule = new SpringFeignClientInterfaceDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("FeignClientDefaultValues");
    }
    
}
