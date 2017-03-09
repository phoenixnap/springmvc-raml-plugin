package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import static com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.ext;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.phoenixnap.oss.ramlapisync.generation.rules.AbstractRuleTestBase;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public class MethodParamsRuleTest extends AbstractRuleTestBase {

    private MethodParamsRule rule = new MethodParamsRule();

    @Test
    public void applyRule_shouldCreate_validMethodParams() throws JClassAlreadyExistsException {

        JDefinedClass jClass = jCodeModel.rootPackage()._class("TestController");
        JMethod jMethod = jClass.method(JMod.PUBLIC, ResponseEntity.class, "getBaseById");
        jMethod = rule.apply(getEndpointMetadata(2), ext(jMethod, jCodeModel));

        assertThat(jMethod.params(), hasSize(1));
        assertThat(serializeModel(), containsString("getBaseById(String id)"));
    }

}
