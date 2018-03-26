package com.phoenixnap.oss.ramlplugin.raml2code.raml.raml10;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.bodies.MimeType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlplugin.raml2code.exception.RamlSpecNotFullySupportedException;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlDataType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlDocumentationItem;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlResource;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlSecurityReference;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlSecurityScheme;

/**
 * @author aweisser
 * @author kurtpa
 * @author aleks
 */
public class RJP10V2RamlRoot implements RamlRoot {

	private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();

	private final Api api;
	private Map<String, RamlResource> resources = new LinkedHashMap<>();

	public RJP10V2RamlRoot(Api api) {
		this.api = api;
	}

	/**
	 * Expose internal representation only package private
	 * 
	 * @return the internal model
	 */
	Api getApi() {
		return this.api;
	}

	@Override
	public Map<String, RamlResource> getResources() {
		if (api != null) {
			return ramlModelFactory.transformToUnmodifiableMap(api.resources(), resources, ramlModelFactory::createRamlResource,
					r -> r.relativeUri().value());
		} else {
			return Collections.emptyMap();
		}
	}

	@Override
	public String getMediaType() {
		List<MimeType> mediaTypes = this.api.mediaType();
		if (mediaTypes.size() >= 2) {
			throw new RamlSpecNotFullySupportedException("Sorry. Multiple default media types are not supported yet.");
		}
		if (mediaTypes.isEmpty()) {
			return null;
		}
		return mediaTypes.stream().findFirst().orElse(null).value();
	}

	@Override
	public List<Map<String, String>> getSchemas() {
		return api.schemas().stream().map(this::typeDeclarationToMap).collect(Collectors.toList());
	}

	@Override
	public Map<String, RamlDataType> getTypes() {
		Map<String, RamlDataType> types = api.types().stream()
				.collect(Collectors.toMap(this::nameType, this::typeDeclarationToRamlDataType));

		Map<String, RamlDataType> libTypes = api.uses().stream().flatMap(x -> x.types().stream())
				.collect(Collectors.toMap(this::nameType, this::typeDeclarationToRamlDataType));

		types.putAll(libTypes);

		// When searching for all libraries that other libraries use it's
		// possible to pull in same library multiple times.
		// In order to avoid IllegalStateException we'll add basic
		// mergeFunction.
		Map<String, RamlDataType> libOfLibTypes = api.uses().stream().flatMap(x -> x.uses().stream()).flatMap(x -> x.types().stream())
				.collect(Collectors.toMap(this::nameType, this::typeDeclarationToRamlDataType, (x, y) -> x));

		types.putAll(libOfLibTypes);

		return types;
	}

	private Map<String, String> typeDeclarationToMap(TypeDeclaration typeDeclaration) {
		Map<String, String> nameTypeMapping = new LinkedHashMap<>();
		nameTypeMapping.put(typeDeclaration.name(), typeDeclaration.type());
		return nameTypeMapping;
	}

	private String nameType(TypeDeclaration typeDeclaration) {
		return typeDeclaration.name();
	}

	private RamlDataType typeDeclarationToRamlDataType(TypeDeclaration typeDeclaration) {
		return new RJP10V2RamlDataType(typeDeclaration);
	}

	List<RamlDocumentationItem> getDocumentation() {
		return api.documentation().stream().map(ramlModelFactory::createRamlDocumentationItem).collect(Collectors.toList());
	}

	@Override
	public String getBaseUri() {
		return this.api.baseUri() != null ? this.api.baseUri().value() : "";
	}

	public List<Library> getLibs() {
		return this.api.uses();
	}

	@Override
	public List<RamlSecurityReference> getSecuredBy() {
		return ramlModelFactory.createRamlSecurityReferences(this.api.securedBy());
	}

	@Override
	public List<RamlSecurityScheme> getSecuritySchemes() {
		return ramlModelFactory.createRamlSecuritySchemes(this.api.securitySchemes());
	}
}
