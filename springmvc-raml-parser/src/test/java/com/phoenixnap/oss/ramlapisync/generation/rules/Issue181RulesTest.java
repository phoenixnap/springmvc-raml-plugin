package com.phoenixnap.oss.ramlapisync.generation.rules;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author aleksandars
 * @since 0.10.10
 */
public class Issue181RulesTest extends AbstractRuleTestBase {

	private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

	@BeforeClass
	public static void initRaml() throws InvalidRamlResourceException {
		AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-181.raml");
	}

	@Test
	public void applySpring4ControllerStubRule_shouldCreate_validCode() throws Exception {

		rule = new Spring4ControllerStubRule();
		Set<ApiResourceMetadata> allControllersMetadata = getAllControllersMetadata();
		for (ApiResourceMetadata apiResourceMetadata : allControllersMetadata) {
			rule.apply(apiResourceMetadata, jCodeModel);
		}
		String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("Issue181Spring4ControllerStub", removedSerialVersionUID);
	}
}
