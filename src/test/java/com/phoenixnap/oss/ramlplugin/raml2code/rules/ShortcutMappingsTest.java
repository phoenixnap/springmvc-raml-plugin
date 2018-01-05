package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;

/**
 * @author aleksandars
 * @since 0.10.5
 */
public class ShortcutMappingsTest extends AbstractRuleTestBase {

	private Spring4ControllerInterfaceRule rule;

	public ShortcutMappingsTest() {
		TestConfig.setResourceDepthInClassNames(2);
	}

	@Test
	public void applySpring4ControllerInterfaceWithShortcutAnnotationsRule_shouldCreate_validCode() throws Exception {
		loadRaml("test-shortcut-mappings.raml");
		rule = new Spring4ControllerInterfaceRule();
		rule.setUseShortcutMethodMappings(true);
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Spring4ControllerInterfaceWithShortcutMappings");
	}
}
