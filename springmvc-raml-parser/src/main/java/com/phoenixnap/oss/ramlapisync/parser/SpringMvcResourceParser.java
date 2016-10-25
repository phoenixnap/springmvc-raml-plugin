/*
 * Copyright 2002-2016 the original author or authors.
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
package com.phoenixnap.oss.ramlapisync.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.phoenixnap.oss.ramlapisync.annotations.Description;
import com.phoenixnap.oss.ramlapisync.annotations.data.PathDescription;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;
import com.phoenixnap.oss.ramlapisync.javadoc.JavaDocEntry;
import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.naming.RamlHelper;
import com.phoenixnap.oss.ramlapisync.naming.SchemaHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactoryOfFactories;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Service scanner that handles generation from a Spring MVC codebase
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class SpringMvcResourceParser extends ResourceParser {

	protected static final Logger logger = LoggerFactory.getLogger(SpringMvcResourceParser.class);
	
	/**
	 * IF this is set to true, we will only parse methods that consume, produce or accept the requested defaultMediaType
	 */
	protected boolean restrictOnMediaType;

	public SpringMvcResourceParser(File path, String version, String defaultMediaType, boolean restrictOnMediaType) {
		super(path, version, defaultMediaType);
		this.restrictOnMediaType = restrictOnMediaType;
	}

	@Override
	protected Pair<String, RamlMimeType> extractRequestBody(Method method, Map<String, String> parameterComments,
															String comment, List<ApiParameterMetadata> apiParameters) {
		RamlMimeType mimeType = RamlModelFactoryOfFactories.createRamlModelFactory().createRamlMimeType();
		String type;
		//Handle empty body
		if (apiParameters != null && apiParameters.size() == 0) {
			// do nothing here
			return null;
		} else if (apiParameters != null && apiParameters.size() == 1 && String.class.equals(apiParameters.get(0).getType())
			// Handle Plain Text parameters
				&& apiParameters.get(0).isAnnotationPresent(RequestBody.class)) {
			ApiParameterMetadata apiParameterMetadata = apiParameters.get(0);
			type = "text/plain";
			if (StringUtils.hasText(apiParameterMetadata.getExample())) {
				mimeType.setExample(apiParameterMetadata.getExample());
			}
			ObjectMapper m = new ObjectMapper();
			SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
			try {
				m.acceptJsonFormatVisitor(m.constructType(String.class), visitor);
				JsonSchema jsonSchema = visitor.finalSchema();
				String description = parameterComments.get(apiParameterMetadata.getJavaName());
				if (description == null) {
					description = apiParameterMetadata.getName();
				}
				jsonSchema.setDescription(description);
				jsonSchema.setRequired(!apiParameterMetadata.isNullable());
				mimeType.setSchema(m.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema));
			} catch (JsonProcessingException e) {
				throw new IllegalStateException(e);
			}
			return new Pair<>(type, mimeType);
		} else if (apiParameters != null
				&& (apiParameters.size() > 1 
						|| (!apiParameters.get(0).isAnnotationPresent(RequestBody.class) && String.class.equals(apiParameters.get(0).getType())))) {
			type = "application/x-www-form-urlencoded";
			for (ApiParameterMetadata param : apiParameters) {
				RamlFormParameter formParameter = RamlModelFactoryOfFactories.createRamlModelFactory().createRamlFormParameter();
				formParameter.setDisplayName(param.getName());
				formParameter.setExample(param.getExample());
				RamlParamType simpleType = SchemaHelper.mapSimpleType(param.getType());
				formParameter.setType(simpleType == null ? RamlParamType.STRING : simpleType);
				String description = parameterComments.get(param.getJavaName());
				if (description == null) {
					description = param.getName();
				}
				formParameter.setDescription(description);
				formParameter.setRequired(!param.isNullable());
				Map<String, List<RamlFormParameter>> paramMap;
				if (mimeType.getFormParameters() == null) {
					paramMap = new TreeMap<>();
					mimeType.setFormParameters(paramMap);
				} else {
					paramMap = mimeType.getFormParameters();
				}
				mimeType.addFormParameters(param.getName(), Collections.singletonList(formParameter));
			}
			return new Pair<>(type, mimeType);
		} else {
			
			return super.extractRequestBody(method, parameterComments, comment, apiParameters);
		}
	}

	/**
	 * Check for produces annotation first. else use super implementation
	 */
	protected String extractMimeTypeFromMethod(Method method) {
		RequestMapping requestMapping = getRequestMapping(method);
		if (requestMapping != null) {
			if (requestMapping.produces() != null && requestMapping.produces().length > 0) {
				// sanity check
				if (requestMapping.produces().length > 1) {
					logger.warn("Method " + method.getName() + " is annotated with multiple Produces entries.");
				}
				return requestMapping.produces()[0];
			}
		}
		return super.extractMimeTypeFromMethod(method);
	}

	/**
	 * Gets the RequestMapping annotation from the method and any interfaces it might implement
	 * 
	 * @param method
	 * @return
	 */
	private RequestMapping getRequestMapping(Method method) {
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
		if (requestMapping == null) {
			for (Class<?> cInterface : method.getDeclaringClass().getInterfaces()) {
				try {
					Method methodInInterface = cInterface.getMethod(method.getName(), method.getParameterTypes());
					requestMapping = methodInInterface.getAnnotation(RequestMapping.class);
					if (requestMapping != null) {
						return requestMapping;
					}
				} catch (NoSuchMethodException nsme) {
					// Possibly Expected outcome. Not really an error but meh.
				}
			}
		}

		return requestMapping;
	}

	/**
	 * Gets a specified annotation from a class and optionally checks other types it inherits from
	 * 
	 * @param clazz
	 * @param annotation
	 * @param inherit
	 * @return
	 */
	private <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotation, boolean inherit) {
		T foundAnnotation = clazz.getAnnotation(annotation);
		if (foundAnnotation == null && inherit) {
			for (Class<?> cInterface : clazz.getInterfaces()) {
				foundAnnotation = cInterface.getAnnotation(annotation);
				if (foundAnnotation != null) {
					return foundAnnotation;
				}
			}
		}

		return foundAnnotation;
	}

	/**
	 * Check for consumes annotation first. else use super implementation
	 */
	protected String extractExpectedMimeTypeFromMethod(Method method) {
		RequestMapping requestMapping = getRequestMapping(method);
		if (requestMapping != null) {
			if (requestMapping.consumes() != null && requestMapping.consumes().length > 0) {
				return requestMapping.consumes()[0];
			}
		}
		return super.extractMimeTypeFromMethod(method);
	}

	@Override
	protected boolean shouldAddMethodToApi(Method method) {
		RequestMapping requestMapping = getRequestMapping(method);
		if (requestMapping != null) {
			if (restrictOnMediaType) {
				for (String cHeader : requestMapping.consumes()) {
					if (cHeader.toLowerCase().contains(defaultMediaType.toLowerCase())) {
						return true;
					}
				}
				for (String cHeader : requestMapping.produces()) {
					if (cHeader.toLowerCase().contains(defaultMediaType.toLowerCase())) {
						return true;
					}
				}
				for (String cHeader : requestMapping.headers()) {
					if (cHeader.toLowerCase().contains(defaultMediaType.toLowerCase())
							&& cHeader.toLowerCase().contains("accept")) {
						return true;
					}
				}
			} else {
				return true;
			}
		}
		return false;
	}

	protected String getParameterName(String preferredOption, String fallback) {
		if (StringUtils.hasText(preferredOption)) {
			return preferredOption;
		} else {
			return fallback;
		}
	}

	/**
	 * Checks if a parameter has any metadata that has been attached using the RequestParam data which identifies it as
	 * an API request parameter
	 * @param param The Parameter to be checked
	 * @return If true the parameter should be added to the api
	 */
	protected boolean shouldAddParameter(Parameter param) {
		for (Annotation annotation : param.getAnnotations()) {
			if ((annotation.getClass().equals(RequestParam.class)) || (annotation.getClass().equals(RequestBody.class))
					|| (annotation.getClass().equals(PathVariable.class))) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected List<ApiParameterMetadata> getApiParameters(Method method, boolean includeUrlParameters,
			boolean includeNonUrlParameters) {
		List<ApiParameterMetadata> params = new ArrayList<>();
		for (Parameter param : method.getParameters()) {
			boolean nonPathParameter = isNonPathParameter(param);
			ApiParameterMetadata parameterMetadata = new ApiParameterMetadata(param);
			if (parameterMetadata != null) {
				if (nonPathParameter && includeNonUrlParameters) {
					params.add(parameterMetadata);
				} else if (!nonPathParameter && includeUrlParameters) {
					params.add(parameterMetadata);
				}
			}
		}
		return params;
	}

	protected boolean isParameter(Parameter param) {
		return isPathParameter(param) || isNonPathParameter(param);
	}

	protected boolean isPathParameter(Parameter param) {
		return param.isAnnotationPresent(PathVariable.class);
	}

	protected boolean isNonPathParameter(Parameter param) {
		return param.isAnnotationPresent(RequestParam.class) || param.isAnnotationPresent(RequestBody.class);
	}

	protected boolean isQueryParameter(Parameter param) {
		return param.isAnnotationPresent(RequestParam.class);
	}
	
	/**
	 * Checks for instances of @Description annotations in 
	 * @param method The method to Inspect
	 * @return a Map of Description and Partial URL Keys
	 */
	protected Map<String, String> getPathDescriptionsForMethod(Method method) {
		Map<String, String> outDescriptions = new HashMap<>();
		Description description = getAnnotation(method.getDeclaringClass(), Description.class, true);
		if (description != null) {
			for (PathDescription descriptions : description.pathDescriptions()) {
				outDescriptions.put(NamingHelper.cleanLeadingAndTrailingNewLineAndChars(descriptions.key()), descriptions.value());
			}
		}
		if(method.isAnnotationPresent(Description.class)) {
			Description methodDescription = method.getAnnotation(Description.class);
			for (PathDescription descriptions : methodDescription.pathDescriptions()) {
				outDescriptions.put(NamingHelper.cleanLeadingAndTrailingNewLineAndChars(descriptions.key()), descriptions.value());
			}
		}
		
		return outDescriptions;
	}

	@Override
	protected ApiParameterMetadata[] extractResourceIdParameter(Method method) {
		Map<String, ApiParameterMetadata> pathVariables = new HashMap<>();
		for (Parameter param : method.getParameters()) {
			if (isPathParameter(param)) {
				ApiParameterMetadata extractParameterMetadata = new ApiParameterMetadata(param);
				pathVariables.put(extractParameterMetadata.getName(), extractParameterMetadata);
			}

		}
		// TODO HASH MAP AND REORDER BY URL
		return pathVariables.values().toArray(new ApiParameterMetadata[pathVariables.size()]);
	}

	@Override
	protected Map<RamlActionType, String> getHttpMethodAndName(Class<?> clazz, Method method) {
		RequestMapping methodMapping = getRequestMapping(method);
		RequestMapping classMapping = getAnnotation(clazz, RequestMapping.class, false);
		RestController classRestController = getAnnotation(clazz, RestController.class, false);
		Controller classController = getAnnotation(method.getDeclaringClass(), Controller.class, false);

		RequestMethod[] verbs = methodMapping.method();
		if (verbs == null || verbs.length == 0) {
			verbs = RequestMethod.values();
		}
		String name = "";

		if (classMapping != null && classMapping.value() != null && classMapping.value().length > 0) {
			name += NamingHelper.resolveProperties(classMapping.value()[0]);
		}
		if (classRestController != null && classRestController.value() != null) {
			name += NamingHelper.resolveProperties(classRestController.value());
		}
		if (classController != null && classController.value() != null) {
			name += NamingHelper.resolveProperties(classController.value());
		}

		if (methodMapping.value() != null && methodMapping.value().length > 0) {
			if (name.endsWith("/") && methodMapping.value()[0].startsWith("/")) {
				name = name.substring(0, name.length() - 1);
			} else if (name != "" && !name.endsWith("/") && !methodMapping.value()[0].startsWith("/")) {
				name += "/";
			}
			name += NamingHelper.resolveProperties(methodMapping.value()[0]);
		}

		Map<RamlActionType, String> outMap = new HashMap<>();
		for (RequestMethod rm : verbs) {
			try {
				RamlActionType apiAction = RamlActionType.valueOf(rm.name());
				outMap.put(apiAction, name);
			} catch (Exception ex) {
				// skip verb not supported by RAML
				logger.warn("Skipping unknown verb " + rm);
			}
		}
		return outMap;// TODO sort value out
	}

	@Override
	protected boolean isActionOnResourceWithoutCommand(Method method) {
		if (!method.isAnnotationPresent(RequestMapping.class)) {
			return true;
		} else {
			RequestMapping requestMapping = getRequestMapping(method);
			if (requestMapping.value().length == 0) {
				return true;
			}

			String url = requestMapping.value()[0];
			if (StringUtils.hasText(url)) {
				return true;
			}
			logger.debug("Parsing url: [" + url + "]");
			List<ApiParameterMetadata> apiParameters = getApiParameters(method, true, false);
			for (ApiParameterMetadata parameterMetadata : apiParameters) {
				url.replace(parameterMetadata.getName(), "");
			}
			url = url.replaceAll("[^\\w]", "");
			if (StringUtils.hasText(url)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected String getResourceName(Class<?> clazz) {
		RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);
		String outMapping = "";
		if (mapping != null && StringUtils.hasText(mapping.name())) {
			outMapping = mapping.name();
		}
		return NamingHelper.resolveProperties(outMapping);
	}

	@Override
	protected void extractAndAppendResourceInfo(Class<?> clazz, Method method, JavaDocEntry docEntry, RamlResource parentResource) {

		RamlModelFactory ramlModelFactory = RamlModelFactoryOfFactories.createRamlModelFactory();

		Map<RamlActionType, String> methodActions = getHttpMethodAndName(clazz, method);
		for (Entry<RamlActionType, String> methodAction : methodActions.entrySet()) {
			RamlAction action = ramlModelFactory.createRamlAction();
			RamlActionType apiAction = methodAction.getKey();
			String apiName = methodAction.getValue();
			//Method assumes that the name starts with /
			if (apiName != null && !apiName.startsWith("/")) {
				apiName = "/" + apiName;
			}
			Map<String, String> pathDescriptions = getPathDescriptionsForMethod(method);
			logger.info("Added call: " + apiName + " " +apiAction  + " from method: " + method.getName()  );

			String responseComment = docEntry == null ? null : docEntry.getReturnTypeComment();
			RamlResponse response = extractResponseFromMethod(method, responseComment);
			Map<String, String> parameterComments = (docEntry == null ? Collections.emptyMap() : docEntry
					.getParameterComments());
			// Lets extract any query parameters (for Verbs that don't support bodies) and insert them in the Action
			// model
			action.addQueryParameters(extractQueryParameters(apiAction, method, parameterComments));

			// Lets extract any request data that should go in the request body as json and insert it in the action
			// model
			action.setBody(extractRequestBodyFromMethod(apiAction, method, parameterComments));
			// Add any headers we need for the method
			addHeadersForMethod(action, apiAction, method);

			String description = docEntry == null ? null : docEntry.getComment();
			if (StringUtils.hasText(description)) {
				action.setDescription(description);
			} else {
				action.setDescription(method.getName());
			}
			action.addResponse("200", response);

			RamlResource idResource = null;
			RamlResource leafResource = null;
			ApiParameterMetadata[] resourceIdParameters = extractResourceIdParameter(method);
			Map<String, ApiParameterMetadata> resourceIdParameterMap = new HashMap<>();
			for (ApiParameterMetadata apiParameterMetadata : resourceIdParameters) {
				resourceIdParameterMap.put("{" + apiParameterMetadata.getName() + "}", apiParameterMetadata);
			}

			String[] splitUrl = apiName.replaceFirst("/", "").split("/");

			for (String partialUrl : splitUrl) {
				if (leafResource == null) {
					leafResource = parentResource;
				} else {
					leafResource = idResource;
				}
				//Clean Path variable defaults
				partialUrl = cleanPathVariableRegex(partialUrl);
				
				String resourceName = "/" + partialUrl;
				idResource = leafResource.getResource(resourceName);// lets check if the parent resource already
																	// contains a resource

				if (idResource == null) {
					idResource = ramlModelFactory.createRamlResource();
					idResource.setRelativeUri(resourceName);
					String displayName = StringUtils.capitalize(partialUrl) + " Resource";
					String resourceDescription = displayName;
					if (pathDescriptions.containsKey(partialUrl)) {
						resourceDescription = pathDescriptions.get(partialUrl);
					}
					if (resourceIdParameterMap.containsKey(partialUrl)) {
						displayName = "A specific "
								+ StringUtils.capitalize(partialUrl).replace("{", "").replace("}", "");
						ApiParameterMetadata resourceIdParameter = resourceIdParameterMap.get(partialUrl);
						RamlUriParameter uriParameter = ramlModelFactory.createRamlUriParameterWithName(resourceIdParameter.getName());
						RamlParamType simpleType = SchemaHelper.mapSimpleType(resourceIdParameter.getType());
						if (simpleType == null) {
							logger.warn("Only simple parameters are supported for URL Parameters, defaulting " + resourceIdParameter.getType() + " to String");
							simpleType = RamlParamType.STRING;
							// TODO support Map<String, String implementations> or not?
						}
						uriParameter.setType(simpleType);
						uriParameter.setRequired(true);
						if (StringUtils.hasText(resourceIdParameter.getExample())) {
							uriParameter.setExample(resourceIdParameter.getExample());
						}
						String paramComment = parameterComments.get(resourceIdParameter.getName());
						if (StringUtils.hasText(paramComment)) {
							uriParameter.setDescription(paramComment);
						}

						idResource.addUriParameter(resourceIdParameter.getName(), uriParameter);
					}

					idResource.setDisplayName(displayName); // TODO allow the Api annotation to specify this stuff :)
					idResource.setDescription(resourceDescription); // TODO allow the Api annotation to specify this stuff :)
					idResource.setParentResource(leafResource);
					if(leafResource.getUri() != null) {
						String targetUri = leafResource.getUri();
						if(targetUri.startsWith("null/")) {
							targetUri = targetUri.substring(5);
						}
						idResource.setParentUri(targetUri);
					}
					leafResource.addResource(resourceName, idResource);
				}
			}

			// Resources have been created. lets bind the action to the appropriate one
			RamlResource actionTargetResource;
			if (idResource != null) {
				actionTargetResource = idResource;
			} else if (leafResource != null) {
				actionTargetResource = leafResource;
			} else {
				actionTargetResource = parentResource;
			}
			action.setResource(actionTargetResource);
			action.setType(apiAction);
			if (actionTargetResource.getActions().containsKey(apiAction)) {
				//merge action
				RamlAction existingAction = actionTargetResource.getActions().get(apiAction);
				RamlHelper.mergeActions(existingAction, action);
				
			} else {
				actionTargetResource.addAction(apiAction, action);
			}
		}

	}
	
	

	private String cleanPathVariableRegex(String partialUrl) {
		if (partialUrl.startsWith("{") && partialUrl.endsWith("}") && partialUrl.contains(":")) {
			int regexStartIndex = partialUrl.indexOf(":");
			return partialUrl.substring(0, regexStartIndex) + "}";
		} else {
			return partialUrl;
		}
	}

	@Override
	protected void addHeadersForMethod(RamlAction action, RamlActionType actionType, Method method) {

	}

}
