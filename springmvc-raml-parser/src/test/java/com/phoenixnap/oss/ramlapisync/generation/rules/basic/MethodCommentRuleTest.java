package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.generation.rules.AbstractRuleTestBase;
import com.sun.codemodel.*;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
