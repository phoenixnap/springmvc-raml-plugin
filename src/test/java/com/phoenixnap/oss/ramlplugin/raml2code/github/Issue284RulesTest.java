package com.phoenixnap.oss.ramlplugin.raml2code.github;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringFeignClientInterfaceDecoratorRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author aleksandars
 * @since 2.0.4
 */
public class Issue284RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void check_feign_path_and_param_names() throws Exception {
		loadRaml("issue-284.raml");
		Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule = new SpringFeignClientInterfaceDecoratorRule();
		rule.apply(getControllerMetadata(), jCodeModel);
		verifyGeneratedCode("Issue284Spring4ControllerDecorator");
	}
}
