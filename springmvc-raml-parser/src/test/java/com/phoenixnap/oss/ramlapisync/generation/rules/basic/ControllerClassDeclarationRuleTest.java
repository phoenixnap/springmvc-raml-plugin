package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.generation.rules.AbstractRuleTestBase;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JPackage;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public class ControllerClassDeclarationRuleTest extends AbstractRuleTestBase {

    private ControllerClassDeclarationRule rule = new ControllerClassDeclarationRule();

    @Test
    public void applyRule_shouldCreate_validControllerClass() {
        JPackage jPackage = jCodeModel.rootPackage();
        JClass jClass = rule.apply(getControllerMetadata(), jPackage);
        assertThat(jClass, is(notNullValue()));
        assertThat(jClass.name(), equalTo("BaseController"));
        assertThat(serializeModel(), containsString("public class BaseController"));
    }

    @Test
    public void applyRule_shouldBeIdempotent() {
        JPackage jPackage = jCodeModel.rootPackage();

        JClass jClass1 = rule.apply(getControllerMetadata(), jPackage);
        String serialized1 = serializeModel();

        JClass jClass2 = rule.apply(getControllerMetadata(), jPackage);
        String serialized2 = serializeModel();

        assertThat(jClass1, equalTo(jClass2));
        assertEquals(serialized1, serialized2);
    }

}
