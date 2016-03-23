package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4ControllerMethodSignatureRuleTest extends AbstractControllerRuleTestBase {

    @Test
    public void applyMethodRule_shouldCreate_validMethodSignatureWithEmptyBody() throws JClassAlreadyExistsException {

        JDefinedClass jClass = jCodeModel.rootPackage()._interface("Spring4ControllerTestStub");
        ApiMappingMetadata endpointMetadata = getControllerMetadata().getApiCalls().iterator().next();
        JMethod jMethod = new Spring4ControllerMethodSignatureRule().apply(endpointMetadata, jClass);

        assertThat(jMethod, is(notNullValue()));
        assertThat(jMethod.name(), equalTo("getBase"));
        assertThat(jMethod.mods().getValue(), equalTo(JMod.PUBLIC));
        assertThat(jMethod.type().name(), equalTo("ResponseEntity"));
        assertThat(jMethod.annotations(), hasSize(1));
        assertThat(serializeModel(), containsString("@RequestMapping(value = \"\", method = RequestMethod.GET)"));
        assertThat(jMethod.body().isEmpty(), is(true));
    }
}
