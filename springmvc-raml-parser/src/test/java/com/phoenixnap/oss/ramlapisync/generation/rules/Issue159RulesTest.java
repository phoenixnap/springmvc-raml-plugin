package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.pojo.PojoGenerationConfig;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author aleksandars
 * @since 0.10.5
 */
public class Issue159RulesTest extends AbstractRuleTestBase {

	private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

	public Issue159RulesTest() {
		super();
		defaultRamlParser = new RamlParser(new PojoGenerationConfig().withPackage("com.gen.test", null), "/api", false,
				false, 2);
	}

	@BeforeClass
	public static void initRaml() throws InvalidRamlResourceException {
		AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-159.raml");
	}

	@Test
	public void applySpring4ClientStubRule_shouldCreate_validCode() throws Exception {

		rule = new Spring4RestTemplateClientRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("Issue159Spring4ControllerStub", removedSerialVersionUID);
	}
}
