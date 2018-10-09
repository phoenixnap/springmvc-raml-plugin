package com.phoenixnap.oss.ramlplugin.raml2code.rules.basic;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.AbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringResponseEntityRule;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public class ControllerMethodSignatureRuleTest extends AbstractRuleTestBase {

	private ControllerMethodSignatureRule rule = new ControllerMethodSignatureRule(new SpringResponseEntityRule(), new MethodParamsRule());

	@Test
	public void applyMethodRule_shouldCreate_validMethodSignatureWithEmptyBody() throws JClassAlreadyExistsException {

		JDefinedClass jClass = jCodeModel.rootPackage()._interface("MyInterface");
		JMethod jMethod = rule.apply(getEndpointMetadata(), jClass);

		assertThat(jMethod, is(notNullValue()));
		assertThat(jMethod.name(), equalTo("getObject"));
		assertThat(jMethod.mods().getValue(), equalTo(JMod.PUBLIC));
		assertThat(jMethod.type().name(), equalTo("ResponseEntity<? extends Object>"));
		assertThat(jMethod.annotations(), hasSize(0)); // no implicit
														// annotations
		assertThat(jMethod.body().isEmpty(), is(true));
	}

	@Test
	public void applyMethodRuleOnSecondEndpoint_shouldCreate_validMethodSignatureWithGenericResponseType()
			throws JClassAlreadyExistsException {

		JDefinedClass jClass = jCodeModel.rootPackage()._interface("MyInterface");
		JMethod jMethod = rule.apply(getEndpointMetadata(2), jClass);

		assertThat(jMethod, is(notNullValue()));
		assertThat(jMethod.name(), equalTo("getNamedResponseTypeById"));
		assertThat(jMethod.mods().getValue(), equalTo(JMod.PUBLIC));
		assertThat(jMethod.annotations(), hasSize(0)); // no implicit
														// annotations
		assertThat(jMethod.body().isEmpty(), is(true));
		assertThat(jMethod.type().name(), equalTo("ResponseEntity<NamedResponseType>"));
		assertThat(serializeModel(), containsString("import com.gen.test.model.NamedResponseType"));
	}

}
