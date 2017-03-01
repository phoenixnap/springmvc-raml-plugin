package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.generation.rules.AbstractRuleTestBase;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

/**
 * 
 * 
 * @author  kris galea
 * @since   0.5.0
 *
 */
public class ClassFieldDeclarationRuleTest extends AbstractRuleTestBase{

    @Test
    public void applyClassFieldDeclarationRule_shouldCreate_validAutowiredField() throws JClassAlreadyExistsException {
        ClassFieldDeclarationRule rule = new ClassFieldDeclarationRule("field",String.class);

        JPackage jPackage = jCodeModel.rootPackage();
        JDefinedClass jClass = jPackage._class(JMod.PUBLIC, "MyClass");        
        jClass._implements(Serializable.class);
        rule.apply(getControllerMetadata(), jClass);

        assertThat(serializeModel(), containsString("import org.springframework.beans.factory.annotation.Autowired;"));
        assertThat(serializeModel(), containsString("@Autowired"));
        assertThat(serializeModel(), containsString("private String field;"));
    }
    
    @Test
    public void applyClassFieldDeclarationRule_shouldCreate_validNonAutowiredField() throws JClassAlreadyExistsException {
        ClassFieldDeclarationRule rule = new ClassFieldDeclarationRule("field",String.class, false);

        JPackage jPackage = jCodeModel.rootPackage();
        JDefinedClass jClass = jPackage._class(JMod.PUBLIC, "MyClass");        
        jClass._implements(Serializable.class);
        rule.apply(getControllerMetadata(), jClass);

        assertFalse(serializeModel().contains("import org.springframework.beans.factory.annotation.Autowired;"));
        assertFalse((serializeModel().contains("@Autowired")));
        assertThat(serializeModel(), containsString("private String field;"));
    }
    
    
    @Test
    public void applyClassFieldDeclarationRule_shouldCreate_validValueAnnotedField() throws JClassAlreadyExistsException {
        ClassFieldDeclarationRule rule = new ClassFieldDeclarationRule("field",String.class, "${sample}");

        JPackage jPackage = jCodeModel.rootPackage();
        JDefinedClass jClass = jPackage._class(JMod.PUBLIC, "MyClass");        
        jClass._implements(Serializable.class);
        rule.apply(getControllerMetadata(), jClass);

        assertFalse(serializeModel().contains("import org.springframework.beans.factory.annotation.Autowired;"));
        assertFalse((serializeModel().contains("@Autowired")));
        assertThat(serializeModel(), containsString("@Value(\"${sample}\")"));
        assertThat(serializeModel(), containsString("private String field;"));
    }
    
}


