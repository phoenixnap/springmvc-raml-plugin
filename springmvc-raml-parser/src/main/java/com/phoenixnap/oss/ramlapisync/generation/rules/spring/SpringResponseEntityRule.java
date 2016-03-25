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
 * Creates a org.springframework.http.ResponseEntity as a return type for an endpoint.
 * If the endpoint declares a response body the first type of the response body will added as a generic type to the ResponseEntity.
 *
 * #%RAML 0.8
 * title: myapi
 * mediaType: application/json
 * baseUri: /
 *
 * /base:
 *   get:
 *   /{id}:
 *     get:
 *       responses:
 *         200:
 *           body:
 *             application/json:
 *               schema: NamedResponseType
 *               ...
 *
 * OUTPUT:
 * @ResponseEntity<NamedResponseType>
 *
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
