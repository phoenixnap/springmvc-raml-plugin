package com.phoenixnap.oss.ramlapisync.pojo;

import com.phoenixnap.oss.ramlapisync.naming.RamlTypeHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlDataType;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.rules.RuleFactory;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import org.raml.v2.api.model.v10.datamodel.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class JsonTypeInterpreter extends BaseTypeInterpreter {
    @Override
    public Set<Class<? extends TypeDeclaration>> getSupportedTypes() {
        return Collections.singleton(JSONTypeDeclaration.class);
    }

    @Override
    public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel, PojoGenerationConfig config, boolean property, String customName) {
        RamlInterpretationResult result = new RamlInterpretationResult(type.required());
        typeCheck(type);

        JSONTypeDeclaration objectType = (JSONTypeDeclaration) type;
        String name = StringUtils.capitalize(objectType.name());
        Map<String, RamlDataType> types = document.getTypes();
        String typeName = objectType.type();

        //When we have base arrays with type in the object they differ from Type[] notated types. I'm not sure if this should be handled in the Array or in the ObjectInterpreter...
        if (RamlTypeHelper.isBaseObject(objectType.name()) && !RamlTypeHelper.isBaseObject(typeName)) {
            //lets enter type and use that.
            return interpret(document, type.parentTypes().get(0), builderModel, config, property, null);
        }

        //When we have base objects we need to use them as type not blindly create them
        if (!RamlTypeHelper.isBaseObject(objectType.name()) && !RamlTypeHelper.isBaseObject(typeName) && property) {
            name = typeName;
            if (types.get(name) == null) {
                throw new IllegalStateException("Data type " + name + " can't be found!");
            }
            typeName = types.get(name).getType().type();
        }

        // For mime types we need to take the type not the name
        try {
            MimeType.valueOf(name);
            name = typeName;
            typeName = types.get(name).getType().type();

        } catch (Exception ex) {
            // not a valid mimetype do nothing
//            logger.debug("mime: " + name);
        }

        // Lets check if we've already handled this class before.
        if (builderModel != null) {
            JClass searchedClass = builderModel._getClass(config.getPojoPackage() + "." + name);
            if (searchedClass != null) {
                // we've already handled this pojo in the model, no need to re-interpret
                result.setCodeModel(builderModel);
                result.setResolvedClass(searchedClass);
                return result;
            }
        } else {
            builderModel = new JCodeModel();
            result.setCodeModel(builderModel);
        }

        GenerationConfig config1 = new DefaultGenerationConfig() {
            @Override
            public boolean isGenerateBuilders() { // set config option by overriding method
                return true;
            }
        };

        SchemaMapper mapper = new SchemaMapper(
                new RuleFactory(config1,
                        new Jackson2Annotator(config1),
                        new SchemaStore()),
                new SchemaGenerator());
        try {
            mapper.generate(builderModel, name, config.getPojoPackage(), typeName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PojoBuilder builder = new PojoBuilder(config, builderModel, name);
        result.setBuilder(builder);
        result.setCodeModel(builderModel);

        // Add a constructor with all fields
        builder.withCompleteConstructor();
        return result;
    }
}
