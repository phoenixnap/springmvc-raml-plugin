package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author aleksandars
 * @since 0.10.13
 */
public class Issue215RulesTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

    @Test
    public void optional_param_as_resource_level() throws Exception {
    	AbstractRuleTestBase.RAML = RamlLoader
				.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-215-1.raml");
    	
        rule = new Spring4ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("Issue215-1Spring4ControllerStub", removedSerialVersionUID);
    }
    
    @Test
    public void optional_param_as_resource_part() throws Exception {
    	AbstractRuleTestBase.RAML = RamlLoader
				.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-215-2.raml");
    	
        rule = new Spring4ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("Issue215-2Spring4ControllerStub", removedSerialVersionUID);
    }
    
    @Test
    public void two_optional_parms() throws Exception {
    	AbstractRuleTestBase.RAML = RamlLoader
				.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-215-3.raml");
    	
        rule = new Spring4ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("Issue215-3Spring4ControllerStub", removedSerialVersionUID);
    }
}
