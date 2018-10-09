package com.phoenixnap.oss.ramlplugin.raml2code.rules.basic;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.AbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ImplementsControllerInterfaceRule;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

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
