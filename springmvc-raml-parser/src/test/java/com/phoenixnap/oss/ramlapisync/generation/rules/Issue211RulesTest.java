
package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class Issue211RulesTest extends AbstractRuleTestBase {
    private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule;

    @BeforeClass
    public static void initRaml() throws InvalidRamlResourceException {
        //To initialize raml inside every test method separately
    }

    @Test
    public void applyPostPutRaml08_shouldCreate_validCode() throws Exception {
        AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("ISSUE-211-RAML08-Create-Update-naming-resolution.raml");
        rule = new Spring4ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        //No idea why fully qualified names are used instead of imports.
        verifyGeneratedCode("Issue211SpringInterfaceForRaml08");
    }

    @Test
    public void applyPostPutRaml10_shouldCreate_validCode() throws Exception {
        AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile("ISSUE-211-RAML10-Create-Update-naming-resolution.raml");
        rule = new Spring4ControllerInterfaceRule();
        rule.apply(getControllerMetadata(), jCodeModel);
        String removedSerialVersionUID = removeSerialVersionUID(serializeModel());
        verifyGeneratedCode("Issue211SpringInterfaceForRaml10", removedSerialVersionUID);
    }
}
