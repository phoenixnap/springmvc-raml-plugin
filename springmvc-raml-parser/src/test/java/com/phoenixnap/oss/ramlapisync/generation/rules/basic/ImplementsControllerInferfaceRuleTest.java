package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.generation.rules.AbstractRuleTestBase;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public class ImplementsControllerInferfaceRuleTest extends AbstractRuleTestBase {

    private ImplementsControllerInterfaceRule rule;

    @Test
    public void applyRule_shouldCreate_classImplementsInterfaceExpression() throws JClassAlreadyExistsException {

        JPackage jPackage = jCodeModel.rootPackage();
        JDefinedClass jInterface = jPackage._interface("MyInterface");
        JDefinedClass jClass = jPackage._class("MyClass");

        rule = new ImplementsControllerInterfaceRule(jInterface);
        rule.apply(getControllerMetadata(), jClass);

        assertThat(jClass, is(notNullValue()));
        assertThat(jClass.name(), equalTo("MyClass"));
        assertThat(serializeModel(), containsString("public class MyClass"));
        assertThat(serializeModel(), containsString("implements MyInterface"));
    }
}
