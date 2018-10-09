/*
 * Copyright 2002-2017 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlplugin.raml2code.helpers;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.ExternalTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLFacetInfo;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.declarations.AnnotationTarget;
import org.raml.v2.api.model.v10.system.types.AnnotableStringType;
import org.raml.v2.api.model.v10.system.types.MarkdownString;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.interpreters.RamlInterpretationResult;
import com.phoenixnap.oss.ramlplugin.raml2code.interpreters.RamlInterpreterFactory;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

/**
 * Simple class containing utility methods for dealing with Raml Types
 * 
 * @author Kurt Paris
 * @since 0.10.0
 *
 */
public class RamlTypeHelper {

	/**
	 * IF it has a format defined, this will return it
	 * 
	 * @param param
	 *            The parameter to inspect
	 * @return Format, if defined
	 */
	public static String getFormat(TypeDeclaration param) {
		if (param == null) {
			return null;
		}
		if (param instanceof NumberTypeDeclaration) {
			return ((NumberTypeDeclaration) param).format();
		}
		if (param instanceof DateTimeTypeDeclaration) {
			return ((DateTimeTypeDeclaration) param).format();
		}

		return null;
	}

	/**
	 * Attempts to infer the type in the generic part of the declaration of the
	 * type
	 * 
	 * @param param
	 *            The parameter to inspect
	 * @return The Class in the generic portrion of the typ
	 */
	public static boolean isArray(TypeDeclaration param) {
		return param instanceof ArrayTypeDeclaration;
	}

	/**
	 * Maps a RAML Data Type to a JCodeModel using JSONSchema2Pojo and
	 * encapsulates it along with some metadata into an {@link ApiBodyMetadata}
	 * object.
	 * 
	 * @param pojoCodeModel
	 *            The code model containing the classes generated during
	 *            generation
	 * @param document
	 *            The Raml document being parsed
	 * @param type
	 *            The RAML type declaration
	 * @return Object representing this Body
	 */
	public static ApiBodyMetadata mapTypeToPojo(JCodeModel pojoCodeModel, RamlRoot document, TypeDeclaration type) {
		RamlInterpretationResult interpret = RamlInterpreterFactory.getInterpreterForType(type).interpret(document, type, pojoCodeModel,
				false);

		// here we expect that a new object is created i guess... we'd need to
		// see how primitive arrays fit in
		JClass pojo = null;
		if (interpret.getBuilder() != null) {
			pojo = interpret.getBuilder().getPojo();
		} else if (interpret.getResolvedClass() != null) {
			pojo = interpret.getResolvedClass();
		}

		if (pojo == null) {
			throw new IllegalStateException("No Pojo created or resolved for type " + type.getClass().getSimpleName() + ":" + type.name());
		}

		if (pojo.name().equals("Void")) {
			return null;
		}

		boolean array = false;
		String pojoName = pojo.name();
		if (pojo.name().contains("List<") || pojo.name().contains("Set<")) {
			array = true;
			pojoName = pojo.getTypeParameters().get(0).name();
		}

		return new ApiBodyMetadata(pojoName, type, array, pojoCodeModel);
	}

	/**
	 * Check to determine if this is a RAML 1.0 data type or if an external
	 * format such as JSON schema is used
	 * 
	 * @param type
	 *            The RAML TypeDeclaration to check
	 * @return True if this is an external type (ie schema or other)
	 */
	public static boolean isSchemaType(TypeDeclaration type) {
		if (type != null && type instanceof ExternalTypeDeclaration) {
			return true;
		}
		return false;
	}

	/**
	 * Safely get description from a type with null checks
	 * 
	 * @param type
	 *            The RAML TypeDeclaration to check
	 * @return The description if defined or null if empty
	 */
	public static String getDescription(TypeDeclaration type) {
		if (type == null || type.description() == null) {
			return null;
		} else {
			return type.description().value();
		}
	}

	/**
	 * Safely get example from a type with null checks
	 * 
	 * @param type
	 *            The RAML TypeDeclaration to check
	 * @return The example if defined or null if empty
	 */
	public static String getExample(TypeDeclaration type) {
		if (type == null || type.example() == null) {
			return null;
		} else {
			return type.example().value();
		}
	}

	/**
	 * Safely get Display name from a type with null checks
	 * 
	 * @param type
	 *            The RAML TypeDeclaration to check
	 * @return The display name if defined or null if empty
	 */
	public static String getDisplayName(TypeDeclaration type) {
		if (type == null || type.displayName() == null) {
			return null;
		} else {
			return type.displayName().value();
		}
	}

	/**
	 * Safely get Name from a type with null checks
	 * 
	 * @param type
	 *            The RAML TypeDeclaration to check
	 * @return The Name if defined or null if empty
	 */
	public static String getName(TypeDeclaration type) {
		if (type == null) {
			return null;
		} else {
			return type.name();
		}
	}

	/**
	 * Safely get required from a type with null checks
	 * 
	 * @param type
	 *            The RAML TypeDeclaration to check
	 * @return true if this parameter is required, false if optional
	 */
	public static boolean isRequired(TypeDeclaration type) {
		if (type == null || type.required() == null) {
			return true;
		} else {
			return type.required();
		}
	}

	/**
	 * Checks if this type is the base object type "object"
	 * 
	 * @param type
	 *            The RAML TypeDeclaration type to check
	 * @return true if object
	 */
	public static boolean isBaseObject(String type) {
		return type.equalsIgnoreCase(Object.class.getSimpleName());
	}

	/**
	 * Adds appropriate <code>pattern</code> attribute to provided annotation on
	 * {@link Date} property.
	 * 
	 * @param jAnnotationUse
	 *            annotation to add pattern. Can be for: {@link JsonFormat} or
	 *            {@link DateTimeFormat}
	 * @param type
	 *            RAML type of the property
	 * @param format
	 *            of date if specified
	 */
	public static void annotateDateWithPattern(JAnnotationUse jAnnotationUse, String type, String format) {

		String param = type.toUpperCase();
		switch (param) {
			case "DATE-ONLY":
				// example: 2013-09-29
				jAnnotationUse.param("pattern", "yyyy-MM-dd");
				break;
			case "TIME-ONLY":
				// example: 19:46:19
				jAnnotationUse.param("pattern", "HH:mm:ss");
				break;
			case "DATETIME-ONLY":
				// example: 2013-09-29T19:46:19
				jAnnotationUse.param("pattern", "yyyy-MM-dd'T'HH:mm:ss");
				break;
			case "DATETIME":
				if ("rfc2616".equalsIgnoreCase(format)) {
					// example: Tue, 15 Nov 1994 12:45:26 GMT
					jAnnotationUse.param("pattern", "EEE, dd MMM yyyy HH:mm:ss z");
				} else {
					jAnnotationUse.param("pattern", "yyyy-MM-dd'T'HH:mm:ssXXX");
				}
				break;
			default:
				jAnnotationUse.param("pattern", "yyyy-MM-dd'T'HH:mm:ss");
		}
	}

	/**
	 * Creates a default string typedeclaration (experimental)
	 * 
	 * @param paramName
	 *            The name of this declaration
	 * @return a String RAML 1 TypeDeclaration
	 */
	public static StringTypeDeclaration createDefaultStringDeclaration(String paramName) {
		return new StringTypeDeclaration() {

			String name = paramName;

			@Override
			public List<AnnotationRef> annotations() {
				return null;
			}

			@Override
			public XMLFacetInfo xml() {
				return null;
			}

			@Override
			public List<ValidationResult> validate(String payload) {
				return null;
			}

			@Override
			public String type() {
				return "string";
			}

			@Override
			public String toXmlSchema() {
				return null;
			}

			@Override
			public Boolean required() {
				return true;
			}

			@Override
			public List<TypeDeclaration> parentTypes() {
				return null;
			}

			@Override
			public String name() {
				return name;
			}

			@Override
			public List<TypeDeclaration> facets() {
				return Collections.emptyList();
			}

			@Override
			public List<ExampleSpec> examples() {
				return Collections.emptyList();
			}

			@Override
			public ExampleSpec example() {
				return null;
			}

			@Override
			public AnnotableStringType displayName() {
				return new AnnotableStringType() {

					@Override
					public List<AnnotationRef> annotations() {
						return null;
					}

					@Override
					public String value() {
						return name;
					}

				};
			}

			@Override
			public MarkdownString description() {
				return null;
			}

			@Override
			public String defaultValue() {
				return null;
			}

			@Override
			public List<AnnotationTarget> allowedTargets() {
				return Collections.emptyList();
			}

			@Override
			public String pattern() {
				return null;
			}

			@Override
			public Integer minLength() {
				return null;
			}

			@Override
			public Integer maxLength() {
				return null;
			}

			@Override
			public List<String> enumValues() {
				return null;
			}

			@Override
			public String toJsonSchema() {
				return null;
			}
		};

	}
}
