package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author aleksandars
 * @since 0.10.14
 */
public class Issue235RulesTest extends AbstractRuleTestBase {

	private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

	@Test
	public void verify_request_body_for_delete() throws Exception {
		AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-235.raml");

		rule = new Spring4ControllerDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("Issue235Spring4ControllerDecorator", removedSerialVersionUID);
	}
}
