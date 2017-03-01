package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author kurtpa
 * @since 0.10.1
 */
public class RamlEquivalenceRulesTest extends AbstractEquivalenceRuleTestBase {

	private static final String EXPECTED_GENERATED_CODE_FILENAME = "RamlEquivalenceSpring4Decorator";
	
	private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;
	
	private static String LINE_END = System.getProperty("line.separator");

	@BeforeClass
	public static void initRaml() throws InvalidRamlResourceException  {
		initRamlModels("raml/raml-equivalence-test-v08.raml");
	}
	
	

    @Test
    public void applySpring4ControllerDecoratorRule_shouldCreate_validCode() throws Exception {
        rule = new Spring4ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        rule.apply(getEquivalenceControllerMetadata(), equivalenceJCodeModel);
        verifyGeneratedCode(EXPECTED_GENERATED_CODE_FILENAME, serializeModel());
        String expectedEquivalenceModel = serializeEquivalenceModel();
        expectedEquivalenceModel = removeModelObjects(expectedEquivalenceModel); //Model objects are kept seperate in the 08 Parser, this will be optimised soon to join models
        expectedEquivalenceModel = expectedEquivalenceModel.replaceAll("Person ", "com.gen.test.model.Person "); //byproduct of above, we need to change to fully qualified name
        expectedEquivalenceModel = expectedEquivalenceModel.replaceAll("List<Person>", "List<com.gen.test.model.Person>"); //byproduct of above, we need to change to fully qualified name
        expectedEquivalenceModel = expectedEquivalenceModel.replaceAll("import com.gen.test.model.Person;"+ LINE_END, ""); //byproduct of above, we need to remove import
        verifyGeneratedCode(EXPECTED_GENERATED_CODE_FILENAME, expectedEquivalenceModel);
    }
	
	private String removeModelObjects(String serializeEquivalenceModel) {
		String start = "-----------------------------------com.gen.test.model";
		String end = "}" + LINE_END + "-----------";
		int current = serializeEquivalenceModel.indexOf(start);
		if (current == -1) {
			return serializeEquivalenceModel;
		} else {
			int idxStart = serializeEquivalenceModel.indexOf(start,0);
			int idxEnd = serializeEquivalenceModel.indexOf(end, idxStart);
			return removeModelObjects(serializeEquivalenceModel.substring(idxEnd+3));
		}
	}
 

}
