package com.phoenixnap.oss.ramlapisync.generation.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;

/**
 * @author kurtpa
 * @since 0.8.1
 */
public class Issue76RulesTest extends AbstractRuleTestBase {

	@BeforeClass
	public static void initRaml() throws InvalidRamlResourceException {
		AbstractRuleTestBase.RAML = RamlParser.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-76.raml");
	}
	
	@Test
    public void applySpring3ControllerStubRule_shouldCreate_validCode() throws Exception {
		
		Set<ApiResourceMetadata> controllers = defaultRamlParser.extractControllers(jCodeModel, RAML);
		assertEquals("Expected 2 contoller", 2, controllers.size());
		assertTrue("Check if we have a files resource", containsResourceName(controllers , "Files"));
		assertTrue("Check if we have a file resource", containsResourceName(controllers , "File"));
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
