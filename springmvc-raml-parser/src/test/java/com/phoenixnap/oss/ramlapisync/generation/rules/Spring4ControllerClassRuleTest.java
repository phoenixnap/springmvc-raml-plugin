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
        JPackage jPackage = new PackageRule().apply(getControllerMetadata(), jCodeModel);
        JClass jClass = new Spring4ControllerClassRule().apply(getControllerMetadata(), jPackage);
        assertThat(jClass, is(notNullValue()));
        assertThat(jClass.name(), equalTo("BaseController"));
    }

    @Test
    public void applyClassRule_shouldBeIdempotent() {
        JPackage jPackage = new PackageRule().apply(getControllerMetadata(), jCodeModel);
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
        JPackage jPackage = new PackageRule().apply(getControllerMetadata(), jCodeModel);
        new Spring4ControllerClassRule().apply(getControllerMetadata(), jPackage);
        String serializeModel = serializeModel();
        assertThat(serializeModel, containsString("@RestController"));
        assertThat(serializeModel, containsString("import org.springframework.web.bind.annotation.RestController;"));
    }

    @Test
    public void applyClassRule_shouldCreate_RequestMappingAnnotation() {
        JPackage jPackage = new PackageRule().apply(getControllerMetadata(), jCodeModel);
        new Spring4ControllerClassRule().apply(getControllerMetadata(), jPackage);
        assertThat(serializeModel(), containsString("@RequestMapping(value = \"/api/base\", produces = \"application/json\")"));
    }

}
