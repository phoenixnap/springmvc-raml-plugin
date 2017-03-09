package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import static com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.ext;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.rules.AbstractRuleTestBase;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

/**
 * @author armin.weisser
 * @author kris galea
 * @since 0.4.1
 */
public class SpringRulesTest extends AbstractRuleTestBase {

    @Test
    public void applyRestControllerAnnotationRule_shouldCreate_validClassAnnotation() throws JClassAlreadyExistsException {
        SpringRestControllerAnnotationRule rule = new SpringRestControllerAnnotationRule(4);

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

    @Test
    public void applySpringMethodParamsRule_shouldCreate_validMethodParams() throws JClassAlreadyExistsException {

        SpringMethodParamsRule rule = new SpringMethodParamsRule();
        JDefinedClass jClass = jCodeModel.rootPackage()._class("TestController");
        JMethod jMethod = jClass.method(JMod.PUBLIC, ResponseEntity.class, "getBaseById");
        jMethod = rule.apply(getEndpointMetadata(2), ext(jMethod, jCodeModel));

        assertThat(jMethod.params(), hasSize(1));
        String serializeModel = serializeModel();
        assertThat(serializeModel, containsString("import org.springframework.web.bind.annotation.PathVariable;"));
        assertThat(serializeModel, containsString("public ResponseEntity getBaseById("));
        assertThat(serializeModel, containsString("@PathVariable"));
        assertThat(serializeModel, containsString("String id) {"));
    }
    
    @Test
    public void applySpringMethodBodyRule_shouldCreate_valid_body() throws JClassAlreadyExistsException {
        SpringRestClientMethodBodyRule rule = new SpringRestClientMethodBodyRule("restTemplate", "baseUrl");
        
        JPackage jPackage = jCodeModel.rootPackage();
        JDefinedClass jClass = jPackage._class(JMod.PUBLIC, "MyClass");
        JMethod jMethod = jClass.method(JMod.PUBLIC, Object.class, "getBase");                
        jMethod.param(jCodeModel._ref(String.class), "id");
        rule.apply(getEndpointMetadata(2), CodeModelHelper.ext(jMethod, jCodeModel));

        String serializeModel = serializeModel();        
        //ensure that we are adding the ACCEPT headers
        assertThat(serializeModel, containsString("httpHeaders.setAccept(acceptsList);"));
        //ensure that we are concatinating the base URL with the request URI to form the full url 
        assertThat(serializeModel, containsString("String url = baseUrl.concat(\"/base/{id}\""));
        //ensure that we are setting url paths vars in the uri
        assertThat(serializeModel, containsString("uriComponents = uriComponents.expand(uriParamMap)"));
        //ensure that the exchange invocation is as expected 
        assertThat(serializeModel, containsString("return this.restTemplate.exchange(uriComponents.encode().toUri(), HttpMethod.GET, httpEntity, NamedResponseType.class);"));
    }


}
