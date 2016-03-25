package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

/**
 * Generates an class declaration based on the controller name in ApiControllerMetadata.
 *
 * INPUT:
 * #%RAML 0.8
 * title: myapi
 * mediaType: application/json
 * baseUri: /
 * /base:
 *
 * OUTPUT:
 * public class BaseController {
 *
 * }
 *
 * @author armin.weisser
 * @since 0.3.2
 */
public class ControllerClassDeclarationRule implements Rule<JPackage,JDefinedClass, ApiControllerMetadata> {

    private final String classNameSuffix;

    public ControllerClassDeclarationRule() {
        this("");
    }

    public ControllerClassDeclarationRule(String classNameSuffix) {
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