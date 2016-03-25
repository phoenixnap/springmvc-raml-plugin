package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JDefinedClass;

/**
 * Generates an implements expression based on a given JDefinedClass.
 *
 * INPUT:
 * JDefinedClass interfaceType
 *
 * OUTPUT:
 * implements <interfaceType>
 *
 * @author armin.weisser
 * @since 0.3.2
 */
public class ImplementsControllerInterfaceRule implements Rule<JDefinedClass, JDefinedClass, ApiControllerMetadata> {

    private final JDefinedClass interfaceType;

    public ImplementsControllerInterfaceRule(JDefinedClass interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public JDefinedClass apply(ApiControllerMetadata controllerMetadata, JDefinedClass generatableType) {
        return generatableType._implements(this.interfaceType);
    }

}