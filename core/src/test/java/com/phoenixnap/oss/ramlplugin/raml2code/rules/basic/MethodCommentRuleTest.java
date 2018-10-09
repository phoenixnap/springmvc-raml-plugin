package com.phoenixnap.oss.ramlplugin.raml2code.rules.basic;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.AbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.MethodCommentRule;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public class MethodCommentRuleTest extends AbstractRuleTestBase {

	private MethodCommentRule rule = new MethodCommentRule();

	@Test
	public void applyRule_shouldCreate_validMethodComment() throws JClassAlreadyExistsException {

		JDefinedClass jClass = jCodeModel.rootPackage()._class("TestController");
		JMethod jMethod = jClass.method(JMod.PUBLIC, ResponseEntity.class, "getBaseById");
		JDocComment jDocComment = rule.apply(getEndpointMetadata(2), jMethod);
		assertNotNull(jDocComment);
		assertThat(serializeModel(), containsString("* Get base entity by ID"));
	}

}
