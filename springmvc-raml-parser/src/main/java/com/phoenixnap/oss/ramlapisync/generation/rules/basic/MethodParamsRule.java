package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

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
            param(paramMetaData, generatableType);
        });

        if (endpointMetadata.getRequestBody() != null) {
            param(endpointMetadata, generatableType);
        }

        return generatableType.get();
    }

    protected JVar param(ApiParameterMetadata paramMetaData, CodeModelHelper.JExtMethod generatableType) {
        return generatableType.get().param(paramMetaData.getType(), paramMetaData.getName());
    }

    protected JVar param(ApiMappingMetadata endpointMetadata, CodeModelHelper.JExtMethod generatableType) {
        String requestBodyName = endpointMetadata.getRequestBody().getName();
        JClass requestBodyType = findFirstClassBySimpleName(generatableType.owner(), requestBodyName);
        return generatableType.get().param(requestBodyType, uncapitalize(requestBodyName));
    }

}
