package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.Test;

/**
 * @author yuranos
 * @since 0.10.13
 */
public class Issue212RulesTest extends AbstractRuleTestBase {

    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

    @Test
    public void testWithLongRootUrl() throws Exception {
    	AbstractRuleTestBase.RAML = RamlLoader
				.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-212-1.raml");
    	
        rule = new Spring4ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("Issue212-1-pathvariablesnaming", removedSerialVersionUID);
    }
    
    @Test
    public void testWithMatchingResourseNameAndPathVariable() throws Exception {
    	AbstractRuleTestBase.RAML = RamlLoader
				.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-212-2.raml");

        rule = new Spring4ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("Issue212-2-pathvariablesnaming", removedSerialVersionUID);
    }

    @Test
    public void testWithNonmatchingResourseNameAndPathVariable() throws Exception {
        AbstractRuleTestBase.RAML = RamlLoader
                .loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-212-3.raml");

        rule = new Spring4ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
        verifyGeneratedCode("Issue212-3-pathvariablesnaming", removedSerialVersionUID);
    }

}
