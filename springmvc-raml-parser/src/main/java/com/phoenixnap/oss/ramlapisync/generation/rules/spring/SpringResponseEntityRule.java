package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import org.springframework.http.ResponseEntity;

import static com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.findFirstClassBySimpleName;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class SpringResponseEntityRule implements Rule<JDefinedClass, JType, ApiMappingMetadata> {

    @Override
    public JType apply(ApiMappingMetadata endpointMetadata, JDefinedClass generatableType) {
        JClass responseEntity = generatableType.owner().ref(ResponseEntity.class);
        if (!endpointMetadata.getResponseBody().isEmpty()) {
            ApiBodyMetadata apiBodyMetadata = endpointMetadata.getResponseBody().values().iterator().next();
            JClass genericType = findFirstClassBySimpleName(apiBodyMetadata.getCodeModel(), apiBodyMetadata.getName());
            return responseEntity.narrow(genericType);
        }
        return responseEntity;
    }
}
