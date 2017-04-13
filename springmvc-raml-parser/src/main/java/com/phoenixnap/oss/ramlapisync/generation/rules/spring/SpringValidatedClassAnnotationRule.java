
package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import org.springframework.validation.annotation.Validated;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;

public class SpringValidatedClassAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> {
   @Override
   public JAnnotationUse apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
      return generatableType.annotate(Validated.class);
   }
}
