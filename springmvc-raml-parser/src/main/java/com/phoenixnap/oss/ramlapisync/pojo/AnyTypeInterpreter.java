package com.phoenixnap.oss.ramlapisync.pojo;

import java.util.LinkedHashSet;
import java.util.Set;

import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.sun.codemodel.JCodeModel;

/**
 * Interpreter for Any types.
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.2
 *
 */
public class AnyTypeInterpreter extends BaseTypeInterpreter {

	private Set<Class<? extends TypeDeclaration>> set;

	@Override
	public Set<Class<? extends TypeDeclaration>> getSupportedTypes() {
		if (set == null) {
			set = new LinkedHashSet<>(1);
			set.add(AnyTypeDeclaration.class);
		}
		return set;
	}

	@Override
	public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel,
			PojoGenerationConfig config, boolean property) {

		AnyTypeDeclaration anyTypeDeclaration = (AnyTypeDeclaration) type;

		RamlInterpretationResult result = new RamlInterpretationResult(type.required());
		String objectName;
		if ("array".equalsIgnoreCase(anyTypeDeclaration.type())) {
			objectName = Object.class.getSimpleName();
		} else {
			objectName = Void.class.getSimpleName();
		}

		result.setResolvedClass(CodeModelHelper.findFirstClassBySimpleName(builderModel, objectName));
		return result;
	}
}
