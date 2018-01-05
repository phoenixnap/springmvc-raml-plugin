package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.TestPojoConfig;

/**
 * @author aleksandars
 * @since 0.10.13
 */
public class Issue224RulesTest extends GitHubAbstractRuleTestBase {
	
	public Issue224RulesTest() {
		((TestPojoConfig) Config.getPojoConfig()).setIncludeJsr303Annotations(true);
	}

    @Test
    public void verify_valid_annotations_on_complex_types() throws Exception {
    	loadRaml("issue-224.raml");
        rule = new Spring4ControllerDecoratorRule();
        rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue224Spring4ControllerStub");
    }
}
