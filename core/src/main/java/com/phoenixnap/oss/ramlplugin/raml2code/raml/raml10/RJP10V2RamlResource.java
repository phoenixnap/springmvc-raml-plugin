package com.phoenixnap.oss.ramlplugin.raml2code.raml.raml10;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlAction;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlActionType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlResource;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlSecurityReference;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlUriParameter;

/**
 * @author aweisser
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlResource implements RamlResource {

	private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();

	private final Resource delegate;

	private transient Map<String, RamlResource> childResourceMap;

	public RJP10V2RamlResource(Resource resource) {
		this.delegate = resource;

		rebuildChildren();
	}

	private void rebuildChildren() {
		childResourceMap = new LinkedHashMap<String, RamlResource>();
		List<Resource> resources = delegate.resources();
		if (resources != null) {
			for (Resource resource : resources) {
				boolean skipResource = false;
				if (StringUtils.hasText(Config.getDontGenerateForAnnotation())) {
					for (AnnotationRef annotation : resource.annotations()) {
						if (("(" + Config.getDontGenerateForAnnotation() + ")").equals(annotation.name())) {
							skipResource = true;
						}
					}
				}

				if (!skipResource) {
					childResourceMap.put(resource.relativeUri().value(), new RJP10V2RamlResource(resource));
				}
			}
		}
	}

	@Override
	public Map<String, RamlResource> getResources() {
		return childResourceMap;
	}

	@Override
	public RamlResource getResource(String path) {
		return childResourceMap.get(path);
	}

	@Override
	public String getRelativeUri() {
		return (this.delegate.relativeUri() == null) ? null : this.delegate.relativeUri().value();
	}

	@Override
	public Map<RamlActionType, RamlAction> getActions() {
		Map<RamlActionType, RamlAction> actions = new HashMap<RamlActionType, RamlAction>();
		for (Method method : this.delegate.methods()) {

			boolean skipMethod = false;
			if (StringUtils.hasText(Config.getDontGenerateForAnnotation())) {
				for (AnnotationRef annotation : method.annotations()) {
					if (("(" + Config.getDontGenerateForAnnotation() + ")").equals(annotation.name())) {
						skipMethod = true;
					}
				}
			}

			if (!skipMethod) {
				actions.put(RamlActionType.valueOf(method.method().toUpperCase()), new RJP10V2RamlAction(method));
			}
		}
		return actions;
	}

	@Override
	public Map<String, RamlUriParameter> getUriParameters() {
		Map<String, RamlUriParameter> uriParameters = new LinkedHashMap<>();
		for (TypeDeclaration type : this.delegate.uriParameters()) {
			RJP10V2RamlUriParameter rjp10v2RamlUriParameter = new RJP10V2RamlUriParameter(type);
			uriParameters.put(type.name(), rjp10v2RamlUriParameter);
		}
		// RJP08 detects and adds uri parameters from url even if there isnt an
		// explicit parameter defined.
		List<String> missingUriParams = NamingHelper.extractUriParams(this.getRelativeUri());
		for (String missingParam : missingUriParams) {
			boolean contains = false;
			Iterator<RamlUriParameter> iterator = uriParameters.values().iterator();
			while (iterator.hasNext()) {
				RamlUriParameter ramlUriParameter = iterator.next();
				if (ramlUriParameter.getName().equals(missingParam)) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				uriParameters.put(missingParam, new RJP10V2RamlUriParameter(RamlTypeHelper.createDefaultStringDeclaration(missingParam)));
			}
		}
		return uriParameters;
	}

	@Override
	public void addUriParameter(String name, RamlUriParameter uriParameter) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raml.model.Resource#getResolvedUriParameters()
	 * 
	 * 
	 * @return URI parameters defined for the current resource plus all URI
	 * parameters defined in the resource hierarchy
	 *
	 */
	@Override
	public Map<String, RamlUriParameter> getResolvedUriParameters() {
		Map<String, RamlUriParameter> resolvedUriParameters = new HashMap<>();

		RamlResource resource = this;
		while (resource != null) {
			resolvedUriParameters = Stream
					.concat(resolvedUriParameters.entrySet().stream(), resource.getUriParameters().entrySet().stream())
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
			resource = resource.getParentResource();
		}

		return resolvedUriParameters;
	}

	@Override
	public String getUri() {
		String outUri = delegate.relativeUri().value();
		Resource parentResource = delegate.parentResource();
		while (parentResource != null) {
			if (parentResource.relativeUri() != null) {
				outUri = parentResource.relativeUri().value() + outUri;
			}
			parentResource = parentResource.parentResource();
		}
		return outUri;
	}

	@Override
	public String getDescription() {
		return (this.delegate.description() == null) ? null : this.delegate.description().value();
	}

	@Override
	public String getDisplayName() {
		if (this.delegate.displayName() == null) {
			return null;
		}
		// we need to check if the displayname is the relative uri and remove it
		// since this is an inconsistency between 08 and 10.
		String value = this.delegate.displayName().value();
		if (this.getRelativeUri().equals(value)) {
			return null;
		} else {
			return value;
		}
	}

	@Override
	public RamlResource getParentResource() {
		return (this.delegate.parentResource() == null) ? null : new RJP10V2RamlResource(this.delegate.parentResource());
	}

	@Override
	public String getParentUri() {
		RamlResource parentResource = getParentResource();
		if (parentResource == null) {
			return "";
		} else {
			return parentResource.getUri();
		}
	}

	@Override
	public RamlAction getAction(RamlActionType actionType) {
		List<Method> methods = delegate.methods();
		for (Method method : methods) {
			if (method.method().equalsIgnoreCase(actionType.toString())) {
				return ramlModelFactory.createRamlAction(method);
			}
		}
		return null;
	}

	Resource getResource() {
		return this.delegate;
	}

	@Override
	public List<RamlSecurityReference> getSecuredBy() {
		return ramlModelFactory.createRamlSecurityReferences(this.delegate.securedBy());
	}
}
