package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class ControllerClassRule implements Rule<JPackage,JDefinedClass, ApiControllerMetadata> {

    private final String classNameSuffix;

    public ControllerClassRule() {
        this("");
    }

    public ControllerClassRule(String classNameSuffix) {
        this.classNameSuffix = classNameSuffix;
    }

    @Override
    public JDefinedClass apply(ApiControllerMetadata controllerMetadata, JPackage generatableType) {
        String controllerClassName = controllerMetadata.getName() + classNameSuffix;
        JDefinedClass definedClass;
        try {
            definedClass = generatableType._class(controllerClassName);
        } catch (JClassAlreadyExistsException e1) {
            definedClass = generatableType._getClass(controllerClassName);
        }
        return definedClass;
    }

}