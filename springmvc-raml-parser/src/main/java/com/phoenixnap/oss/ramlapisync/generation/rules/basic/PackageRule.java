package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import org.springframework.util.StringUtils;

/**
 * Generates a simple package declaration based on the base package defined in ApiControllerMetadata.
 * If no base package is defined the empty root package is used by default.
 *
 * INPUT:
 * ApiControllerMetadata (with base package set to "my.api"
 *
 * OUTPUT:
 * package my.api;
 *
 * @author armin.weisser
 * @since 0.3.2
 */
public class PackageRule implements Rule<JCodeModel, JPackage, ApiControllerMetadata> {

    @Override
    public JPackage apply(ApiControllerMetadata controllerMetadata, JCodeModel generatableType) {
        if(StringUtils.hasText(controllerMetadata.getBasePackage())) {
            return generatableType._package(controllerMetadata.getBasePackage());
        }
        return generatableType.rootPackage();
    }
}
