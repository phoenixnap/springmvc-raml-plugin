
package com.phoenixnap.oss.ramlplugin.raml2code.rules.spring;

import org.springframework.validation.annotation.Validated;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;

public class SpringValidatedClassAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> {
   @Override
   public JAnnotationUse apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
      return generatableType.annotate(Validated.class);
   }
}
