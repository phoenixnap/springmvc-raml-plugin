package com.phoenixnap.oss.ramlapisync.generation.rules;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.SpringConfigurableRule;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author kurtpa
 * @since 0.4.2
 */
public class Issue117RulesTest extends AbstractRuleTestBase {

	private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

	@BeforeClass
	public static void initRaml() throws InvalidRamlResourceException  {
		AbstractRuleTestBase.RAML = RamlParser.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-117.raml");
	}
	
	@Test
    public void applySpring3ControllerStubRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerStubRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Issue117ControllerStub");
    }
    
    @Test
    public void applySpring3ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
    }
    
    @Test
    public void applySpring3ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
        rule = new Spring3ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
    }
    
    @Test
    public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerStubRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Issue117Spring4ControllerStub");
    }

    @Test
    public void applySpring4ControllerInterfaceRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Issue117Spring4Interface");
    }

    @Test
    public void applySpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Issue117Spring4Decorator");
    }
    
	@Test
    public void applySpring4ControllerDecoratorRule_configuration_disableArray() throws Exception {
        rule = new Spring4ControllerDecoratorRule();
        Map<String, String> configuration = new HashMap<>();
        configuration.put(SpringConfigurableRule.ARRAY_PARAMETER_CONFIGURATION, "false");
        ((ConfigurableRule<JCodeModel, JDefinedClass, ApiResourceMetadata>)rule).applyConfiguration(configuration);
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Issue117Spring4DecoratorNoArray");
    }
    
    @Test
    public void applySpring4SpringTemplateClient_shouldCreate_validCode() throws Exception {
        rule = new Spring4RestTemplateClientRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        verifyGeneratedCode("Issue117Spring4Client");
    }
	
	

}
