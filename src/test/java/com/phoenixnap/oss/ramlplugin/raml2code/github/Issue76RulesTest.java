package com.phoenixnap.oss.ramlplugin.raml2code.github;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;

/**
 * @author kurtpa
 * @since 0.8.1
 */
public class Issue76RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void applySpring3ControllerStubRule_shouldCreate_validCode() throws Exception {
		loadRaml("issue-76.raml");
		Set<ApiResourceMetadata> controllers = defaultRamlParser.extractControllers(jCodeModel, RAML);
		assertEquals("Expected 2 contoller", 2, controllers.size());
		assertTrue("Check if we have a files resource", containsResourceName(controllers, "Files"));
		assertTrue("Check if we have a file resource", containsResourceName(controllers, "File"));
	}

	private boolean containsResourceName(Set<ApiResourceMetadata> resources, String string) {
		for (ApiResourceMetadata resource : resources) {
			if (resource.getResourceName().equals(string)) {
				return true;
			}
		}
		return false;
	}
}
