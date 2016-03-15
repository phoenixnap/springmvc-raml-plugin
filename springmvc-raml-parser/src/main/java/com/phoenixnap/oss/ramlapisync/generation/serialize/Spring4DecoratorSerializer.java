package com.phoenixnap.oss.ramlapisync.generation.serialize;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;

/**
 * @author armin.weisser
 */
public class Spring4DecoratorSerializer extends Spring4ControllerSerializer {

    public Spring4DecoratorSerializer(ApiControllerMetadata controller, String header) {
        super(controller, header);
    }

    @Override
    protected void addImports() {
        gen += "import org.springframework.beans.factory.annotation.Autowired;\n";
        super.addImports();
    }

    @Override
    protected String generateControllerClassName() {
        return controller.getName() + "Decorator";
    }

    @Override
    protected String generateImplementsExtends() {
        return "implements " + generateInterfaceName();
    }

    @Override
    protected void addClassFields() {
        String fields = "\t@Autowired\n";
        fields += "\tprivate "+ generateInterfaceName() + " " + generateDelegateName() + ";\n\n";
        gen += fields;
    }

    private String generateInterfaceName() {
        return controller.getName();
    }

    @Override
    protected String generateMethodBody(ApiMappingMetadata mapping) {
        String paramList = generateMethodParameters(mapping, parameterNamesOnlyStrategy(), requestBodyParameterParameterNamesOnlyStrategy());
        return "\t\t return this." + generateDelegateName() + "." + mapping.getName() + "(" + paramList + ");\n";
    }

    protected String generateMethodResponseType(ApiMappingMetadata mapping) {
        String response = "ResponseEntity";
        if (!mapping.getResponseBody().isEmpty()) {
            ApiBodyMetadata apiBodyMetadata = mapping.getResponseBody().values().iterator().next();
            response += "<" + generateRequestBodyParameterType(apiBodyMetadata) +">";
        }
        return response;
    }

    private String generateDelegateName() {
        String delegateName = generateDelegateClassName();
        delegateName = delegateName.substring(0, 1).toLowerCase() + delegateName.substring(1);
        return delegateName;
    }

    private String generateDelegateClassName() {
        return controller.getName() + "Delegate";
    }
}
