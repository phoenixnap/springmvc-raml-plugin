package com.phoenixnap.oss.ramlapisync.generation.serialize;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;

import java.util.stream.Collectors;

/**
 * @author armin.weisser
 */
public class Spring4DecoratorSerializer extends Spring4ControllerSerializer {

    public Spring4DecoratorSerializer(ApiControllerMetadata controller, String header) {
        super(controller, header);
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
        String fields = "\t@Autowire\n";
        fields += "\tprivate "+ generateInterfaceName() + " " + generateDelegateName() + ";\n\n";
        gen += fields;
    }

    private String generateInterfaceName() {
        return controller.getName();
    }

    @Override
    protected String generateMethodBody(ApiMappingMetadata mapping) {
        String paramList = generateCallingMethodParameters(mapping);
        return "\t\t return this." + generateDelegateName() + "." + mapping.getName() + "("+paramList+");\n";
    }

    private String generateCallingMethodParameters(ApiMappingMetadata mapping) {
        return mapping.getPathVariables().stream().map(p -> p.getName()).collect(Collectors.toList()).stream().collect(Collectors.joining(","));
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
