package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import com.phoenixnap.oss.ramlapisync.generation.rules.AbstractControllerRuleTestBase;
import com.sun.codemodel.*;
import org.junit.Test;

import java.io.Serializable;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class SpringRulesTest extends AbstractControllerRuleTestBase {

    @Test
    public void applyRestControllerAnnotationRule_shouldCreate_validClassAnnotation() throws JClassAlreadyExistsException {
        Spring4RestControllerAnnotationRule rule = new Spring4RestControllerAnnotationRule();

        JPackage jPackage = jCodeModel.rootPackage();
        JDefinedClass jClass = jPackage._class(JMod.PUBLIC, "MyClass");
        rule.apply(getControllerMetadata(), jClass);

        assertThat(jClass, is(notNullValue()));
        assertThat(jClass.name(), equalTo("MyClass"));
        assertThat(serializeModel(), containsString("import org.springframework.web.bind.annotation.RestController;"));
        assertThat(serializeModel(), containsString("@RestController"));
    }

    @Test
    public void applyRequestMappingAnnotationRule_shouldCreate_validClassAnnotation() throws JClassAlreadyExistsException {
        SpringRequestMappingClassAnnotationRule rule = new SpringRequestMappingClassAnnotationRule();

        JPackage jPackage = jCodeModel.rootPackage();
        JDefinedClass jClass = jPackage._class(JMod.PUBLIC, "MyClass");
        rule.apply(getControllerMetadata(), jClass);

        assertThat(jClass, is(notNullValue()));
        assertThat(jClass.name(), equalTo("MyClass"));
        assertThat(serializeModel(), containsString("import org.springframework.web.bind.annotation.RequestMapping;"));
        assertThat(serializeModel(), containsString("@RequestMapping(value = \"/api/base\", produces = \"application/json\")"));
    }

    @Test
    public void applyRequestMappingAnnotationRule_shouldCreate_validMethodAnnotation() throws JClassAlreadyExistsException {
        SpringRequestMappingMethodAnnotationRule rule = new SpringRequestMappingMethodAnnotationRule();

        JPackage jPackage = jCodeModel.rootPackage();
        JDefinedClass jClass = jPackage._class(JMod.PUBLIC, "MyClass");
        JMethod jMethod = jClass.method(JMod.PUBLIC, Object.class, "getBase");
        rule.apply(getEndpointMetadata(), jMethod);

        assertThat(serializeModel(), containsString("import org.springframework.web.bind.annotation.RequestMapping;"));
        assertThat(serializeModel(), containsString("@RequestMapping(value = \"\", method = RequestMethod.GET)"));
    }

    @Test
    public void applyDelegateFieldDeclarationRule_shouldCreate_validAutowiredField() throws JClassAlreadyExistsException {
        SpringDelegateFieldDeclerationRule rule = new SpringDelegateFieldDeclerationRule("delegate");

        JPackage jPackage = jCodeModel.rootPackage();
        JDefinedClass jClass = jPackage._class(JMod.PUBLIC, "MyClass");
        jClass._implements(Serializable.class);
        rule.apply(getControllerMetadata(), jClass);

        assertThat(serializeModel(), containsString("import org.springframework.beans.factory.annotation.Autowired;"));
        assertThat(serializeModel(), containsString("@Autowired"));
        assertThat(serializeModel(), containsString("private Serializable delegate;"));
    }


}
