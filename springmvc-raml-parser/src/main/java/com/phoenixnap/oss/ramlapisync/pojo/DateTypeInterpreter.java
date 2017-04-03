package com.phoenixnap.oss.ramlapisync.pojo;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.sun.codemodel.JCodeModel;

/**
 * Interpreter for Date types.
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.2
 *
 */
public class DateTypeInterpreter extends BaseTypeInterpreter {

	private Set<Class<? extends TypeDeclaration>> set;

	@Override
	public Set<Class<? extends TypeDeclaration>> getSupportedTypes() {

		if (set == null) {
			set = new LinkedHashSet<>(4);
			set.add(DateTimeOnlyTypeDeclaration.class);
			set.add(DateTimeTypeDeclaration.class);
			set.add(DateTypeDeclaration.class);
			set.add(TimeOnlyTypeDeclaration.class);
		}
		return set;
	}

	@Override
	public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel,
			PojoGenerationConfig config, boolean property) {

		RamlInterpretationResult result = new RamlInterpretationResult(type.required());
		result.setResolvedClass(builderModel.ref(Date.class));
		return result;
	}
}
