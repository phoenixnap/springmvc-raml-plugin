package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JPackage;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4ControllerClassRuleTest extends AbstractControllerRuleTestBase {

    @Test
    public void applyClassRule_shouldCreate_validControllerClass() {
        JClass jClass = new Spring4ControllerClassRule().apply(getControllerMetadata(), jCodeModel.rootPackage());
        assertThat(jClass, is(notNullValue()));
        assertThat(jClass.name(), equalTo("BaseController"));
    }

    @Test
    public void applyClassRule_shouldBeIdempotent() {
        JPackage jPackage = jCodeModel.rootPackage();
        Spring4ControllerClassRule spring4ControllerClassRule = new Spring4ControllerClassRule();

        JClass jClass1 = spring4ControllerClassRule.apply(getControllerMetadata(), jPackage);
        String serialized1 = serializeModel();

        JClass jClass2 = spring4ControllerClassRule.apply(getControllerMetadata(), jPackage);
        String serialized2 = serializeModel();

        assertThat(jClass1, equalTo(jClass2));
        assertEquals(serialized1, serialized2);
    }

    @Test
    public void applyClassRule_shouldCreate_RestControllerAnnotation() {
        new Spring4ControllerClassRule().apply(getControllerMetadata(), jCodeModel.rootPackage());
        String serializeModel = serializeModel();
        assertThat(serializeModel, containsString("@RestController"));
        assertThat(serializeModel, containsString("import org.springframework.web.bind.annotation.RestController;"));
    }

    @Test
    public void applyClassRule_shouldCreate_RequestMappingAnnotation() {
        new Spring4ControllerClassRule().apply(getControllerMetadata(), jCodeModel.rootPackage());
        assertThat(serializeModel(), containsString("@RequestMapping(value = \"/api/base\", produces = \"application/json\")"));
    }

}
