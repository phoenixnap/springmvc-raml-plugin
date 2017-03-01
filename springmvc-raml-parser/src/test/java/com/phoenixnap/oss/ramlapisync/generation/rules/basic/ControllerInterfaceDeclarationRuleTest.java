package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.generation.rules.AbstractRuleTestBase;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JPackage;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public class ControllerInterfaceDeclarationRuleTest extends AbstractRuleTestBase {

    private ControllerInterfaceDeclarationRule rule = new ControllerInterfaceDeclarationRule();

    @Test
    public void applyRule_shouldCreate_validControllerInterface() {
        JPackage jPackage = jCodeModel.rootPackage();
        JClass jClass = rule.apply(getControllerMetadata(), jPackage);
        assertThat(jClass, is(notNullValue()));
        assertThat(jClass.name(), equalTo("BaseController"));
        assertThat(serializeModel(), containsString("interface BaseController"));
    }

    @Test
    public void applyClassRule_shouldBeIdempotent() {
        JPackage jPackage = jCodeModel.rootPackage();

        JClass jClass1 = rule.apply(getControllerMetadata(), jPackage);
        String serialized1 = serializeModel();

        JClass jClass2 = rule.apply(getControllerMetadata(), jPackage);
        String serialized2 = serializeModel();

        assertThat(jClass1, equalTo(jClass2));
        assertEquals(serialized1, serialized2);
    }

}
