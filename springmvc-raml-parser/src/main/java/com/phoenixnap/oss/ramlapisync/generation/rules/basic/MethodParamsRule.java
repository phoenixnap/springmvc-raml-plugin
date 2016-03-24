package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JMethod;

import java.util.ArrayList;
import java.util.List;

import static com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.findFirstClassBySimpleName;
import static org.springframework.util.StringUtils.uncapitalize;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class MethodParamsRule implements Rule<CodeModelHelper.JExtMethod, JMethod, ApiMappingMetadata> {

    @Override
    public JMethod apply(ApiMappingMetadata endpointMetadata, CodeModelHelper.JExtMethod generatableType) {

        List<ApiParameterMetadata> parameterMetadataList = new ArrayList<>();
        parameterMetadataList.addAll(endpointMetadata.getPathVariables());
        parameterMetadataList.addAll(endpointMetadata.getRequestParameters());

        parameterMetadataList.forEach( paramMetaData -> {
            generatableType.get().param(paramMetaData.getType(), paramMetaData.getName());
        });

        if (endpointMetadata.getRequestBody() != null) {
            String requestBodyName = endpointMetadata.getRequestBody().getName();
            JClass requestBodyType = findFirstClassBySimpleName(generatableType.owner(), requestBodyName);
            generatableType.get().param(requestBodyType, uncapitalize(requestBodyName));
        }

        return generatableType.get();
    }

}
