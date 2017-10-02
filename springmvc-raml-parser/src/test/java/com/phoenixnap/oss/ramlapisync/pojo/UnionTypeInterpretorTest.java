package com.phoenixnap.oss.ramlapisync.pojo;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.rules.AbstractRuleTestBase;
import com.phoenixnap.oss.ramlapisync.generation.rules.RamlLoader;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.phoenixnap.oss.ramlapisync.generation.rules.Spring4ControllerInterfaceRule;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author rahul
 * @since 0.10.6
 */
public class UnionTypeInterpretorTest extends AbstractRuleTestBase {

	private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

	public UnionTypeInterpretorTest() {
		super();
		defaultRamlParser = new RamlParser(new PojoGenerationConfig().withPackage("com.gen.test", null), "/api", false,
				false, 2, 0, false);
	}

	@BeforeClass
	public static void initRaml() throws InvalidRamlResourceException {
		AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "raml-with-union-types.raml");
	}

	@Test
	public void applySpring4ClientStubRule_shouldCreate_validCode() throws Exception {

		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("RamlWithUnionTypeSpring4ControllerInterface", removedSerialVersionUID);
	}
}
