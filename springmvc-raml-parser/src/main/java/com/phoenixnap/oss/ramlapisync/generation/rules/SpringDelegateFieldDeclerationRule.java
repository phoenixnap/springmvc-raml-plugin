package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class SpringDelegateFieldDeclerationRule implements Rule<JDefinedClass, JFieldVar, ApiControllerMetadata> {

    private final String delegateFieldName;

    public SpringDelegateFieldDeclerationRule(String delegateFieldName) {
        this.delegateFieldName = delegateFieldName;
    }

    @Override
    public JFieldVar apply(ApiControllerMetadata controllerMetadata, JDefinedClass generatableType) {
        JClass controllerInterface = generatableType._implements().next();
        JFieldVar field = generatableType.field(JMod.PRIVATE, controllerInterface, delegateFieldName);
        field.annotate(Autowired.class);
        return field;
    }
}
