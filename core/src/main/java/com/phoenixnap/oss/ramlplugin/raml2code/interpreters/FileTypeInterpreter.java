package com.phoenixnap.oss.ramlplugin.raml2code.interpreters;

import java.util.LinkedHashSet;
import java.util.Set;

import org.raml.v2.api.model.v10.datamodel.FileTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.CodeModelHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.sun.codemodel.JCodeModel;

/**
 * Interpreter for File types.
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.2
 *
 */
public class FileTypeInterpreter extends BaseTypeInterpreter {

	private Set<Class<? extends TypeDeclaration>> set;

	@Override
	public Set<Class<? extends TypeDeclaration>> getSupportedTypes() {
		if (set == null) {
			set = new LinkedHashSet<>(1);
			set.add(FileTypeDeclaration.class);
		}
		return set;
	}

	@Override
	public RamlInterpretationResult interpret(RamlRoot document, TypeDeclaration type, JCodeModel builderModel, boolean property) {

		RamlInterpretationResult result = new RamlInterpretationResult(type.required());
		result.setResolvedClass(CodeModelHelper.findFirstClassBySimpleName(builderModel, Object.class.getSimpleName()));
		return result;
	}
}
