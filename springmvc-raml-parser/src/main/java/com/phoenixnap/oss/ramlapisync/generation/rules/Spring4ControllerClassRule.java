package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4ControllerClassRule implements Rule<JPackage,JDefinedClass, ApiControllerMetadata> {

    private Rule<JDefinedClass, JAnnotationUse, ApiControllerMetadata> restControllerAnnotationRule = new Spring4RestControllerAnnotationRule();
    private Rule<JDefinedClass, JAnnotationUse, ApiControllerMetadata> requestMappingAnnotationRule = new Spring4RequestMappingClassAnnotationRule();

    @Override
    public JDefinedClass apply(ApiControllerMetadata controllerMetadata, JPackage generatableType) {
        String controllerClassName = controllerMetadata.getName();
        JDefinedClass definedClass;
        try {
            definedClass = generatableType._class(controllerClassName);
            addClassLevelAnnotations(controllerMetadata, definedClass);

        } catch (JClassAlreadyExistsException e1) {
            definedClass = generatableType._getClass(controllerClassName);
        }
        return definedClass;
    }

    protected void addClassLevelAnnotations(ApiControllerMetadata controllerMetadata, JDefinedClass definedClass) {
        restControllerAnnotationRule.apply(controllerMetadata, definedClass);
        requestMappingAnnotationRule.apply(controllerMetadata, definedClass);
    }

}