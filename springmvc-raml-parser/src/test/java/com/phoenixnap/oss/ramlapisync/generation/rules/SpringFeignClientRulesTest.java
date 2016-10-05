package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.SpringFeignClientInterfaceDecoratorRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author Aleksandar Stojsavljevic
 * @since 0.8.6
 */
public class SpringFeignClientRulesTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;
    
    @BeforeClass
	public static void initRaml() {
		AbstractRuleTestBase.RAML = RamlParser.loadRamlFromFile("test-feign-client.raml");
	}
    
    @Test
    public void applySpring4SpringTemplateClient_shouldCreate_validCode() throws Exception {
        rule = new SpringFeignClientInterfaceDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("SongClient");
    }
    
}
