package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.rules.AbstractRuleTestBase;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public class DelegatingMethodBodyRuleTest extends AbstractRuleTestBase {

    private DelegatingMethodBodyRule rule = new DelegatingMethodBodyRule("controllerDelegate");

    @Test
    public void applyRule_shouldCreate_methodCall_onDelegate() throws JClassAlreadyExistsException {

        JDefinedClass jClass = jCodeModel.rootPackage()._class(JMod.PUBLIC, "TestClass");
        JMethod jMethod = jClass.method(JMod.PUBLIC, Object.class, "getBase");
        jMethod = rule.apply(getEndpointMetadata(), CodeModelHelper.ext(jMethod, jClass.owner()));

        assertThat(jMethod, is(notNullValue()));
        assertThat(jMethod.body().isEmpty(), is(false));
        assertThat(jMethod.params(), hasSize(0));
        assertThat(serializeModel(), containsString("return this.controllerDelegate.getBase();"));
    }

    @Test
    public void applyRule_onEmptyFieldName_shouldFallBackToDefaultFieldName() throws JClassAlreadyExistsException {

        rule = new DelegatingMethodBodyRule("");

        JDefinedClass jClass = jCodeModel.rootPackage()._class(JMod.PUBLIC, "TestClass");
        JMethod jMethod = jClass.method(JMod.PUBLIC, Object.class, "getBase");
        jMethod = rule.apply(getEndpointMetadata(), CodeModelHelper.ext(jMethod, jClass.owner()));

        assertThat(jMethod, is(notNullValue()));
        assertThat(jMethod.body().isEmpty(), is(false));
        assertThat(jMethod.params(), hasSize(0));
        assertThat(serializeModel(), containsString("return this.delegate.getBase();"));
    }


    @Test
    public void applyRule_onNullFieldName_shouldFallBackToDefaultFieldName() throws JClassAlreadyExistsException {

        rule = new DelegatingMethodBodyRule(null);

        JDefinedClass jClass = jCodeModel.rootPackage()._class(JMod.PUBLIC, "TestClass");
        JMethod jMethod = jClass.method(JMod.PUBLIC, Object.class, "getBase");
        jMethod = rule.apply(getEndpointMetadata(), CodeModelHelper.ext(jMethod, jClass.owner()));

        assertThat(jMethod, is(notNullValue()));
        assertThat(jMethod.body().isEmpty(), is(false));
        assertThat(jMethod.params(), hasSize(0));
        assertThat(serializeModel(), containsString("return this.delegate.getBase();"));
    }

    @Test
    public void applyRuleOnParametrizedEndpoint_shouldCreate_methodCall_onDelegate() throws JClassAlreadyExistsException {

        JDefinedClass jClass = jCodeModel.rootPackage()._class(JMod.PUBLIC, "TestClass");
        JMethod jMethod = jClass.method(JMod.PUBLIC, Object.class, "getBaseById");
        jMethod.param(String.class, "id");
        jMethod = rule.apply(getEndpointMetadata(2), CodeModelHelper.ext(jMethod, jClass.owner()));

        assertThat(jMethod, is(notNullValue()));
        assertThat(jMethod.body().isEmpty(), is(false));
        assertThat(jMethod.params(), hasSize(1));
        assertThat(serializeModel(), containsString("return this.controllerDelegate.getBaseById(id);"));
    }
}
