package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.pojo.PojoGenerationConfig;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author aleksandars
 * @since 0.10.5
 */
public class ShortcutMappingsTest extends AbstractRuleTestBase {

	private Spring4ControllerInterfaceRule rule;

	public ShortcutMappingsTest() {
		super();
		defaultRamlParser = new RamlParser(new PojoGenerationConfig().withPackage("com.gen.test", null), "/api", false,
				false, 2, 0, false);
	}

	@BeforeClass
	public static void initRaml() throws InvalidRamlResourceException {
		AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "shortcut-mappings.raml");
	}

	@Test
	public void applySpring4ControllerInterfaceWithShortcutAnnotationsRule_shouldCreate_validCode() throws Exception {

		rule = new Spring4ControllerInterfaceRule();
		rule.setUseShortcutMethodMappings(true);
		rule.apply(getControllerMetadata(), jCodeModel);
		String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("Spring4ControllerInterfaceWithShortcutMappings", removedSerialVersionUID);
	}
}
