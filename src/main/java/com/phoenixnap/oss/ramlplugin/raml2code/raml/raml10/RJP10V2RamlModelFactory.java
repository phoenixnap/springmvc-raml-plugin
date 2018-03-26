package com.phoenixnap.oss.ramlplugin.raml2code.raml.raml10;

import java.util.List;
import java.util.stream.Collectors;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.DocumentationItem;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.security.SecurityScheme;
import org.raml.v2.api.model.v10.security.SecuritySchemeRef;

import com.phoenixnap.oss.ramlplugin.raml2code.data.RamlFormParameter;
import com.phoenixnap.oss.ramlplugin.raml2code.exception.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlAction;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlDocumentationItem;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlHeader;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlMimeType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlParamType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlResource;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlResponse;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlSecurityReference;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlSecurityScheme;

/**
 * @author aweisser
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlModelFactory implements RamlModelFactory {

	@Override
	public RamlRoot buildRamlRoot(String ramlFileUrl) {
		RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlFileUrl);
		if (ramlModelResult.hasErrors()) {
			List<String> errors = ramlModelResult.getValidationResults().stream().map(validationResult -> validationResult.getMessage())
					.collect(Collectors.toList());
			throw new InvalidRamlResourceException(ramlFileUrl, errors);
		}

		// The Api is created by RamlModelBuilder during runtime via a yagi
		// ModelProxyBuilder.
		// In org.raml.v2 there is no direct implementation for Api interface
		// during compile time.
		Api api = ramlModelResult.getApiV10();
		return new RJP10V2RamlRoot(api);
	}

	@Override
	public RamlResource createRamlResource(Object resource) {
		if (resource == null) {
			return null;
		}
		return new RJP10V2RamlResource((Resource) resource);
	}

	@Override
	public RamlAction createRamlAction(Object action) {
		if (action == null) {
			return null;
		}
		return new RJP10V2RamlAction((Method) action);
	}

	@Override
	public RamlDocumentationItem createRamlDocumentationItem(Object documentationItem) {
		return new RJP10V2RamlDocumentationItem((DocumentationItem) documentationItem);
	}

	@Override
	public RamlResponse createRamlResponse(Object response) {
		if (response == null) {
			return null;
		}
		return new RJP10V2RamlResponse((Response) response);
	}

	@Override
	public RamlMimeType createRamlMimeType(Object mimeType) {
		if (mimeType == null) {
			return null;
		}
		return new RJP10V2RamlMimeType((TypeDeclaration) mimeType);
	}

	@Override
	public RamlHeader createRamlHeader(Object haeder) {
		if (haeder == null) {
			return null;
		}
		return new RJP10V2RamlHeader((TypeDeclaration) haeder);
	}

	@Override
	public RamlQueryParameter createRamlQueryParameter(Object queryParameter) {
		if (queryParameter == null) {
			return null;
		}
		return new RJP10V2RamlQueryParameter((TypeDeclaration) queryParameter);
	}

	@Override
	public List<RamlFormParameter> createRamlFormParameters(List<? extends Object> formParameters) {
		return formParameters.stream().map(this::createRamlFormParameter).collect(Collectors.toList());
	}

	@Override
	public RamlFormParameter createRamlFormParameter(Object formParameter) {
		return new RJP10V2RamlFormParameter((TypeDeclaration) formParameter);
	}

	@Override
	public List<RamlSecurityReference> createRamlSecurityReferences(List<? extends Object> securityReferences) {
		return securityReferences.stream().map(this::createRamlSecurityReference).collect(Collectors.toList());
	}

	@Override
	public RamlSecurityReference createRamlSecurityReference(Object securityReference) {
		return new RJP10V2RamlSecurityReference((SecuritySchemeRef) securityReference);
	}

	@Override
	public List<RamlSecurityScheme> createRamlSecuritySchemes(List<? extends Object> securitySchemes) {
		return securitySchemes.stream().map(this::createRamlSecurityScheme).collect(Collectors.toList());
	}

	@Override
	public RamlSecurityScheme createRamlSecurityScheme(Object securityReference) {
		return new RJP10V2RamlSecurityScheme((SecurityScheme) securityReference);
	}

	@Override
	public RamlParamType createRamlParamType(Object paramType) {
		if (paramType == null) {
			return RamlParamType.STRING;
		}

		String param = ((String) paramType).toUpperCase();
		switch (param) {
			case "DATE":
			case "DATE-ONLY":
			case "TIME-ONLY":
			case "DATETIME-ONLY":
			case "DATETIME":
				return RamlParamType.DATE;
			case "STRING":
				return RamlParamType.STRING;
			case "NUMBER":
				return RamlParamType.NUMBER;
			case "INTEGER":
				return RamlParamType.INTEGER;
			case "FILE":
				return RamlParamType.FILE;
			case "BOOLEAN":
				return RamlParamType.BOOLEAN;
			default:
				return RamlParamType.DATA_TYPE;
		}
	}
}
