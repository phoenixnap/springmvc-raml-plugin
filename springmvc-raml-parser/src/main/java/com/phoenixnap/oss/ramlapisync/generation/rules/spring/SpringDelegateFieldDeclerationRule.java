package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.exception.RuleCanNotProcessModelException;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Creates a private field declaration with a Spring @Autowired annotation.
 * The type of the field is derived from the super class of the given JDefinedClass.
 * The name of the field can be injected by the caller. Default is "delegate".
 *
 * EXAMPLE OUTPUT:
 * @Autowired
 * BaseClass delegate;
 *
 * @author armin.weisser
 * @since 0.3.2
 */
public class SpringDelegateFieldDeclerationRule implements Rule<JDefinedClass, JFieldVar, ApiControllerMetadata> {

    private String delegateFieldName = "delegate";

    public SpringDelegateFieldDeclerationRule(String delegateFieldName) {
        if(StringUtils.hasText(delegateFieldName)) {
            this.delegateFieldName = delegateFieldName;
        }
    }

    @Override
    public JFieldVar apply(ApiControllerMetadata controllerMetadata, JDefinedClass generatableType) {
        if(!generatableType._implements().hasNext()) {
            throw new RuleCanNotProcessModelException("The class "+generatableType.fullName()+ " does not implement a super class that can be delegated to.");
        }
        JClass controllerInterface = generatableType._implements().next();
        JFieldVar field = generatableType.field(JMod.PRIVATE, controllerInterface, delegateFieldName);
        field.annotate(Autowired.class);
        return field;
    }
}
