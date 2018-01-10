package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.generation.rules.spring.SpringFeignClientClassAnnotationRule;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.SpringFeignClientInterfaceDecoratorRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aleksandar Stojsavljevic
 * @since 0.8.6
 */
public class SpringFeignClientRulesTest extends AbstractRuleTestBase {

    private ConfigurableRule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;
    
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

	@Test
	public void applySpringFeignClient_shouldCreate_configVaules_All() throws Exception {

		AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("test-default-values.raml");

		Map<String, String> configuration = new HashMap<>();
		configuration.put(SpringFeignClientClassAnnotationRule.FEIGN_NAME_ALL, "my-feign-client");
		configuration.put(SpringFeignClientClassAnnotationRule.FEIGN_URL_ALL, "");

		rule = new SpringFeignClientInterfaceDecoratorRule();
		rule.applyConfiguration(configuration);
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("FeignClientConfigValues");
	}

	@Test
	public void applySpringFeignClient_shouldCreate_configVaules_Name() throws Exception {

		AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("test-default-values.raml");

		Map<String, String> configuration = new HashMap<>();
		configuration.put(SpringFeignClientClassAnnotationRule.ANNOTATION_PREFIX + SpringFeignClientClassAnnotationRule.FEIGN_NAME_KEY + "Base", "my-feign-client");
		configuration.put(SpringFeignClientClassAnnotationRule.FEIGN_URL_ALL, "");

		rule = new SpringFeignClientInterfaceDecoratorRule();
		rule.applyConfiguration(configuration);
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("FeignClientConfigValues");
	}

	@Test
	public void applySpringFeignClient_shouldCreate_configVaules_NameUrl() throws Exception {

		AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("test-default-values.raml");

		Map<String, String> configuration = new HashMap<>();
		configuration.put(SpringFeignClientClassAnnotationRule.ANNOTATION_PREFIX + SpringFeignClientClassAnnotationRule.FEIGN_NAME_KEY + "Base", "my-feign-client");
		configuration.put(SpringFeignClientClassAnnotationRule.ANNOTATION_PREFIX + SpringFeignClientClassAnnotationRule.FEIGN_URL_KEY + "Base", "http://bla.com/api/songs");

		rule = new SpringFeignClientInterfaceDecoratorRule();
		rule.applyConfiguration(configuration);
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("FeignClientConfig2Values");
	}
    
}
