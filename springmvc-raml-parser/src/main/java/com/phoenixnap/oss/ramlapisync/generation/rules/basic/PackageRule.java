package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import org.springframework.util.StringUtils;

/**
 * @author armin.weisser
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
