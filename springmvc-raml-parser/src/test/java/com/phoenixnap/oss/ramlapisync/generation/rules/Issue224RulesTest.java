package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.pojo.PojoGenerationConfig;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author aleksandars
 * @since 0.10.13
 */
public class Issue224RulesTest extends AbstractRuleTestBase {
	
	private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;
	
	public Issue224RulesTest() {
		super();
		defaultRamlParser = new RamlParser(
				new PojoGenerationConfig().withPackage("com.gen.test", null).withJSR303Annotations(
						true),
				"/api", false, false, RamlParser.DEFAULT_RESOURCE_DEPTH,
				RamlParser.DEFAULT_RESOURCE_TOP_LEVEL, RamlParser.DEFAULT_REVERSE_ORDER);
	}

    @Test
    public void verify_valid_annotations_on_complex_types() throws Exception {
    	AbstractRuleTestBase.RAML = RamlLoader
				.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-224.raml");
        rule = new Spring4ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("Issue224Spring4ControllerStub", removedSerialVersionUID);
    }
}
