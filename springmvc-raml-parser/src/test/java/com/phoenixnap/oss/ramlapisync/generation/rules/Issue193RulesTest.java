package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author yuranos
 * @since 0.10.13
 */
public class Issue193RulesTest extends AbstractRuleTestBase {

	private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

    @BeforeClass
    public static void initRaml() throws InvalidRamlResourceException {
        AbstractRuleTestBase.RAML = RamlLoader
                .loadRamlFromFile(AbstractRuleTestBase.RESOURCE_BASE + "issue-193.raml");
    }

	@Test
	public void testValidInQueryParam() throws Exception {
		rule = new Spring4ControllerInterfaceRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
		verifyGeneratedCode("Issue193-validqueryparameters", removedSerialVersionUID);
	}
}
