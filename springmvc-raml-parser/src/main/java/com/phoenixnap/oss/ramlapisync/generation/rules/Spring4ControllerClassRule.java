package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.sun.codemodel.*;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4ControllerClassRule implements ControllerRule<JPackage,JClass> {

    private ControllerRule<JDefinedClass, JAnnotationUse> restControllerAnnotationRule = new Spring4ControllerRestControllerAnnotationRule();
    private ControllerRule<JDefinedClass, JAnnotationUse> requestMappingAnnotationRule = new Spring4ControllerRequestMappingAnnotationRule();

    @Override
    public JClass apply(ApiControllerMetadata controllerMetadata, JPackage generatableType) {
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