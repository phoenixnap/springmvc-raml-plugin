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

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.containsOnly;
import static org.apache.commons.lang3.StringUtils.difference;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.apache.commons.lang3.StringUtils.upperCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsonschema2pojo.util.NameHelper;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.internal.utils.Inflector;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.SpringMvcEndpointGeneratorMojo.MethodsNamingLogic;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.SpringMvcEndpointGeneratorMojo.OverrideNamingLogicWith;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlActionType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlResource;

/**
 * Class containing methods relating to naming conventions and string cleanup
 * for naming
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class NamingHelper {

	private static final Pattern CONTENT_TYPE_VERSION = Pattern.compile("[^v]*(v[\\d\\.]*).*", Pattern.CASE_INSENSITIVE);

	private static final Pattern SLASH = Pattern.compile("/");

	private static final String ILLEGAL_CHARACTER_REGEX = "[^0-9a-zA-Z_$]";

	private static NameHelper cachedNameHelper;

	private static NameHelper getNameHelper() {
		if (cachedNameHelper != null) {
			return cachedNameHelper;
		}

		cachedNameHelper = new NameHelper(SchemaHelper.getDefaultGenerationConfig());
		return cachedNameHelper;

	}

	/**
	 * Converts an http contentType into a qualifier that can be used within a
	 * Java method
	 * 
	 * @param contentType
	 *            The content type to convert application/json
	 * @return qualifier, example V1Html
	 */
	public static String convertContentTypeToQualifier(String contentType) {
		// lets start off simple since qualifers are better if they are simple
		// :)
		// if we have simple standard types lets add some heuristics
		if (contentType.equals(MediaType.APPLICATION_JSON_VALUE)) {
			return "AsJson";
		}

		if (contentType.equals(MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
			return "AsBinary";
		}

		if (contentType.equals(MediaType.TEXT_PLAIN_VALUE) || contentType.equals(MediaType.TEXT_HTML_VALUE)) {
			return "AsText";
		}

		// we have a non standard type. lets see if we have a version
		Matcher versionMatcher = CONTENT_TYPE_VERSION.matcher(contentType);
		if (versionMatcher.find()) {
			String version = versionMatcher.group(1);

			if (version != null) {
				return StringUtils.capitalize(version).replace(".", "_");
			}
		}

		// if we got here we have some sort of funky content type. deal with it
		int seperatorIndex = contentType.indexOf("/");
		if (seperatorIndex != -1 && seperatorIndex < contentType.length()) {
			String candidate = contentType.substring(seperatorIndex + 1).toLowerCase();
			String out = "";
			if (candidate.contains("json")) {
				candidate = candidate.replace("json", "");
				out += "AsJson";
			}

			candidate = StringUtils.deleteAny(candidate, " ,.+=-'\"\\|~`#$%^&\n\t");
			if (StringUtils.hasText(candidate)) {
				out = StringUtils.capitalize(candidate) + out;
			}
			return "_" + out;
		}
		return "";
	}

	/**
	 * Extracts a list of URI Parameters from a url
	 * 
	 * @param url
	 *            String to extract parameters from
	 * @return A list of the uri parameters in this URL
	 */
	public static List<String> extractUriParams(String url) {
		List<String> outParams = new ArrayList<>();
		if (StringUtils.hasText(url)) {
			String[] split = StringUtils.split(url, "/");
			for (String part : split) {
				int indexOfStart = part.indexOf("{");
				int indexOfEnd = part.indexOf("}");
				if (indexOfStart != -1 && indexOfEnd != -1 && indexOfStart < indexOfEnd) {
					outParams.add(part.substring(indexOfStart + 1, indexOfEnd));
				}
			}

		}
		return outParams;
	}

	/**
	 * Utility method to check if a string can be used as a valid class name
	 * 
	 * @param input
	 *            String to check
	 * @return true if valid
	 */
	public static boolean isValidJavaClassName(String input) {
		if (!StringUtils.hasText(input)) {
			return false;
		}
		if (!Character.isJavaIdentifierStart(input.charAt(0))) {
			return false;
		}
		if (input.length() > 1) {
			for (int i = 1; i < input.length(); i++) {
				if (!Character.isJavaIdentifierPart(input.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Utility method to clean a string for use within javadoc
	 * 
	 * @param input
	 *            String to be cleaned
	 * @return The cleaned string
	 */
	public static String cleanForJavadoc(String input) {
		return cleanLeadingAndTrailingNewLineAndChars(input);
	}

	/**
	 * Utility method to clean New Line,Spaces and other highly useless
	 * characters found (mainly in javadoc)
	 * 
	 * @param input
	 *            The string to be cleaned
	 * @return Cleaned String
	 */
	public static String cleanLeadingAndTrailingNewLineAndChars(String input) {

		if (!StringUtils.hasText(input)) {
			return input;
		}
		String output = input;
		output = output.replaceAll("[\\s]+\\*[\\s]+", " ");
		while (output.startsWith("/") || output.startsWith("\n") || output.startsWith("*") || output.startsWith("-")
				|| output.startsWith("\t") || output.startsWith(" ") || output.startsWith("\\")) {
			output = output.substring(1);
		}

		while (output.endsWith("/") || output.endsWith("\n") || output.endsWith(" ") || output.endsWith(",") || output.endsWith("\t")
				|| output.endsWith("-") || output.endsWith("*")) {
			output = output.substring(0, output.length() - 1);
		}
		return output;
	}

	/**
	 * Convert a name into a java className.
	 *
	 * eg. MonitorServiceImpl becomes Monitor
	 *
	 * @param clazz
	 *            The name
	 * @return The name for this using Java class convention
	 */
	public static String convertToClassName(String clazz) {
		return StringUtils.capitalize(cleanNameForJava(clazz));
	}

	/**
	 * Attempts to infer the name of a resource from a resources's relative URL
	 * 
	 * @param resource
	 *            The raml resource being parsed
	 * @param singularize
	 *            indicates if the resource name should be singularized or not
	 * @return A name representing this resource or null if one cannot be
	 *         inferred
	 */
	public static String getResourceName(RamlResource resource, boolean singularize) {
		String url = resource.getRelativeUri();

		if (StringUtils.hasText(url) && url.contains("/") && (url.lastIndexOf('/') < url.length())) {
			return getResourceName(url.substring(url.lastIndexOf('/') + 1), singularize);
		}

		return null;
	}

	/**
	 * Attempts to infer the name of a resource from a resources's full URL.
	 * 
	 * @param url
	 *            The URL of the raml resource being parsed
	 * @param singularize
	 *            Indicates if the resource name should be singularized or not
	 * @return name of a resource
	 */
	public static String getAllResourcesNames(String url, boolean singularize) {

		StringBuilder stringBuilder = new StringBuilder();
		if (StringUtils.hasText(url)) {
			String[] resources = SLASH.split(url);
			int lengthCounter = 0;
			for (int i = resources.length - 1; i >= Config.getResourceTopLevelInClassNames() + 1; --i) {
				if (StringUtils.hasText(resources[i])) {
					String resourceName = getResourceName(resources[i], singularize);
					if (Config.isReverseOrderInClassNames()) {
						stringBuilder.append(resourceName);
					} else {
						stringBuilder.insert(0, resourceName);
					}
					++lengthCounter;
				}
				if (Config.getResourceDepthInClassNames() > 0 && lengthCounter >= Config.getResourceDepthInClassNames()) {
					break;
				}
			}
		}

		return stringBuilder.toString();
	}

	/**
	 * Attempts to infer the name of a resource from a resources's relative URL
	 * 
	 * @param resource
	 *            The Url representation of this object
	 * @param singularize
	 *            indicates if the resource name should be singularized or not
	 * @return A name representing this resource or null if one cannot be
	 *         inferred
	 */
	public static String getResourceName(String resource, boolean singularize) {
		if (StringUtils.hasText(resource)) {
			String resourceName = StringUtils.capitalize(resource);
			if (singularize) {
				resourceName = singularize(resourceName);
			}
			resourceName = StringUtils.capitalize(cleanNameForJava(resourceName));
			return resourceName;
		}

		return null;
	}

	/**
	 * Singularises a string. uses underlying raml parser system
	 * 
	 * @param target
	 *            name to singularize
	 * @return singularized name
	 */
	public static String singularize(String target) {
		// TODO we should add this as an issue in the RamlBase project and
		// provide a pull request
		String result = Inflector.singularize(target);
		if ((target.endsWith("ss")) && (result.equals(target.substring(0, target.length() - 1)))) {
			result = target;
		}
		return result;
	}

	public static String pluralize(String target) {
		if ((target.endsWith("s"))) {
			return target;
		}
		return Inflector.pluralize(target);
	}

	/**
	 * Converts the name of a parameter into a name suitable for a Java
	 * parameter
	 *
	 * @param name
	 *            The name of a RAML query parameter or request header
	 * @return A name suitable for a Java parameter
	 */
	public static String getParameterName(String name) {
		return StringUtils.uncapitalize(cleanNameForJava(name));
	}

	/**
	 * Cleans a string with characters that are not valid as a java identifier
	 * 
	 * @param resourceName
	 *            The string to clean
	 * @return cleaned string
	 */
	public static String cleanNameForJava(String resourceName) {
		String outString = resourceName;
		if (StringUtils.hasText(resourceName)) {
			outString = getNameHelper().replaceIllegalCharacters(resourceName);
			if (StringUtils.hasText(outString)) {
				outString = getNameHelper().normalizeName(outString);
			}
		}
		return outString;
	}

	/**
	 * Cleans a string with characters that are not valid as a java identifier
	 * enum
	 * 
	 * @param enumConstant
	 *            The string to clean
	 * @return cleaned string
	 */
	public static String cleanNameForJavaEnum(String enumConstant) {
		if (!StringUtils.hasText(enumConstant)) {
			return enumConstant;
		}

		List<String> nameGroups = new ArrayList<>(asList(splitByCharacterTypeCamelCase(enumConstant)));

		nameGroups.removeIf(s -> containsOnly(s.replaceAll(ILLEGAL_CHARACTER_REGEX, "_"), "_"));

		String enumName = upperCase(join(nameGroups, "_"));
		if (isEmpty(enumName)) {
			enumName = "_DEFAULT_";
		} else if (Character.isDigit(enumName.charAt(0))) {
			enumName = "_" + enumName;
		}

		return enumName;
	}

	private static boolean doesUriEndsWithParam(String uri) {
		if (StringUtils.isEmpty(uri)) {
			return false;
		}
		String subUri = uri;
		if (uri.lastIndexOf('/') > -1) {
			subUri = uri.substring(uri.lastIndexOf('/') + 1);
		}
		return subUri.startsWith("{") && subUri.endsWith("}");
	}

	public static String getActionName(ApiActionMetadata apiActionMetadata) {

		if (Config.getOverrideNamingLogicWith() == OverrideNamingLogicWith.DISPLAY_NAME
				&& !StringUtils.isEmpty(apiActionMetadata.getDisplayName())) {
			return StringUtils.uncapitalize(cleanNameForJava(apiActionMetadata.getDisplayName()));
		} else if (Config.getOverrideNamingLogicWith() == OverrideNamingLogicWith.ANNOTATION) {
			for (AnnotationRef annotation : apiActionMetadata.getAnnotations()) {
				if ("(javaName)".equals(annotation.name())) {
					return String.valueOf(annotation.structuredValue().value());
				}
			}
		}

		if (Config.getMethodsNamingLogic() == MethodsNamingLogic.RESOURCES) {
			return getActionNameFromResources(apiActionMetadata.getParent().getResource(), apiActionMetadata.getResource(),
					apiActionMetadata.getActionType());
		}

		return getActionNameFromObjects(apiActionMetadata);
	}

	private static String getActionNameFromObjects(ApiActionMetadata apiActionMetadata) {

		String uri = apiActionMetadata.getResource().getUri();
		String name = convertActionTypeToIntent(apiActionMetadata.getActionType(), doesUriEndsWithParam(uri));

		if (apiActionMetadata.getActionType().equals(RamlActionType.GET)) {
			Map<String, ApiBodyMetadata> responseBody = apiActionMetadata.getResponseBody();
			if (responseBody.size() > 0) {
				ApiBodyMetadata apiBodyMetadata = responseBody.values().iterator().next();
				String responseObjectName = cleanNameForJava(StringUtils.capitalize(apiBodyMetadata.getName()));
				if (apiBodyMetadata.isArray()) {
					responseObjectName = StringUtils.capitalize(NamingHelper.pluralize(responseObjectName));
				}
				name += responseObjectName;
			} else {
				name += "Object";
			}

			name = appendActionNameWithSingleParameter(apiActionMetadata, name);

		} else if (apiActionMetadata.getActionType().equals(RamlActionType.DELETE)) {

			// for DELETE method we'll still use resource name
			String url = cleanLeadingAndTrailingNewLineAndChars(apiActionMetadata.getResource().getUri());
			String[] splitUrl = SLASH.split(url);
			String resourceNameToUse = null;
			if (splitUrl.length > 1 && StringUtils.countOccurrencesOf(splitUrl[splitUrl.length - 1], "{") > 0) {
				resourceNameToUse = splitUrl[splitUrl.length - 2];
			} else {
				resourceNameToUse = splitUrl[splitUrl.length - 1];
			}

			name = name + StringUtils.capitalize(cleanNameForJava(singularize(resourceNameToUse)));
			name = appendActionNameWithSingleParameter(apiActionMetadata, name);

		} else {
			ApiBodyMetadata requestBody = apiActionMetadata.getRequestBody();
			String creationObject;
			if (requestBody != null) {
				creationObject = requestBody.getName();
			} else {
				creationObject = apiActionMetadata.getParent().getResourceUri();
				creationObject = creationObject.substring(creationObject.lastIndexOf('/') + 1);
			}
			return name + cleanNameForJava(StringUtils.capitalize(creationObject));
		}

		return name;
	}

	private static List<ApiParameterMetadata> getParameters(ApiActionMetadata apiActionMetadata) {

		List<ApiParameterMetadata> parameterMetadataList = new ArrayList<>();
		parameterMetadataList.addAll(apiActionMetadata.getPathVariables());
		parameterMetadataList.addAll(apiActionMetadata.getRequestParameters());
		parameterMetadataList.addAll(apiActionMetadata.getRequestHeaders());

		return parameterMetadataList;
	}

	private static String appendActionNameWithSingleParameter(ApiActionMetadata apiActionMetadata, String methodName) {

		String newMethodName = methodName;

		List<ApiParameterMetadata> parameterMetadataList = getParameters(apiActionMetadata);
		if (parameterMetadataList.size() == 1) {
			ApiParameterMetadata paramMetaData = parameterMetadataList.iterator().next();
			newMethodName = newMethodName + "By" + StringUtils.capitalize(paramMetaData.getJavaName());
		}

		return newMethodName;
	}

	/**
	 * Attempts to infer the name of an action (intent) from a resource's
	 * relative URL and action details
	 * 
	 * @param controllerizedResource
	 *            The resource that is mapped to the root controller
	 * @param resource
	 *            The child resource that will be mapped as a method of the root
	 *            controller
	 * @param actionType
	 *            The ActionType/HTTP Verb for this Action
	 * @return The java name of the method that will represent this Action
	 */
	private static String getActionNameFromResources(RamlResource controllerizedResource, RamlResource resource,
			RamlActionType actionType) {

		String url = resource.getUri();
		// Since this will be part of a resource/controller, remove the parent
		// portion of the URL if enough details remain
		// to infer a meaningful method name
		if (controllerizedResource != resource && StringUtils.countOccurrencesOf(url, "{") < StringUtils.countOccurrencesOf(url, "/") - 1) {
			url = reduceToResourceNameAndId(url);
		}

		// sanity check
		if (StringUtils.hasText(url)) {

			// Split the url into segments by seperator
			String[] splitUrl = SLASH.split(url);
			String name = "";
			int numberOfIdsParsed = 0;
			int index = splitUrl.length - 1;
			boolean singularizeNext = false;
			boolean isIdInPath = false;

			// Parse segments until end is reached or we travers a maximum of 2
			// non Path Variable segments, these 2 should both have at least 1
			// id path variable each otherwise they would have been typically
			// part of the parent controller
			// or we have REALLY long URL nesting which isnt really a happy
			// place.
			while (numberOfIdsParsed < 2 && index >= 0) {

				String segment = splitUrl[index];
				// Lets check for ID path variables
				if (segment.contains("{") && segment.contains("}")) {
					// should we add this to Method name
					if (index > 0 && index == splitUrl.length - 1) {
						// set if the last segment of the url is an Id
						isIdInPath = true;
						if (segment.startsWith("{") && segment.endsWith("}")) {
							String peek = splitUrl[index - 1].toLowerCase();
							name = "By" + StringUtils.capitalize(difference(peek, segment.substring(1, segment.length() - 1)));
						} else {
							String[] split = segment.split("[{}]");
							name = "By";
							for (String segmentPart : split) {
								name = name + StringUtils.capitalize(segmentPart);
							}
						}
					}
					// Since we probably found an ID, it means that method acts
					// on a single resource in the collection. probably :)
					singularizeNext = true;
				} else {
					segment = cleanNameForJava(segment);
					if (singularizeNext) { // consume singularisation
						if (!segment.endsWith("details")) {
							name = NamingHelper.singularize(StringUtils.capitalize(segment)) + name;
						} else {
							name = StringUtils.capitalize(segment) + name;
						}
						singularizeNext = false;
					} else {
						name = StringUtils.capitalize(segment) + name;
					}

					numberOfIdsParsed++;
				}
				index--;
			}

			// Add the http verb into the mix
			String tail = splitUrl[splitUrl.length - 1];
			String prefix = convertActionTypeToIntent(actionType, isIdInPath);
			// singularize name if it's a proper POST or PUT
			if (!NamingHelper.singularize(tail).equals(tail) && !tail.endsWith("details")
					&& (RamlActionType.POST.equals(actionType) || RamlActionType.PUT.equals(actionType) && isIdInPath)) {
				name = NamingHelper.singularize(name);
			}

			return prefix + name;
		}
		// Poop happened. return nothing
		return null;
	}

	/**
	 * Reduces long URL paths to a format {@code /resource/{id}}"
	 * 
	 * @param url
	 * @return
	 */
	private static String reduceToResourceNameAndId(String url) {
		String[] splitUrl = SLASH.split(url);
		String slash = "/";
		return slash + splitUrl[splitUrl.length - 2] + slash + splitUrl[splitUrl.length - 1];
	}

	/**
	 * Attempts to convert the Http Verb into a textual representation of Intent
	 * based on REST conventions
	 * 
	 * @param actionType
	 *            The ActionType/Http verb of the action
	 * @param isIdInPath
	 *            True if the path contains an Id meaning that it must be an
	 *            idempotent operation, i.e. PUT
	 * @return method name prefix
	 */
	private static String convertActionTypeToIntent(RamlActionType actionType, boolean isIdInPath) {
		switch (actionType) {
			case DELETE:
				return "delete";
			case GET:
				return "get";
			case POST:
				if (!isIdInPath) {
					return "create";
				}
			case PUT:
				return "update";
			case PATCH:
				return "modify";
			default:
				return "do";
		}
	}

	/**
	 * Returns the default sub package that will be used for model objects used
	 * in the Request/Response body
	 * 
	 * @return the package suffix to be appended.
	 */
	public static String getDefaultModelPackage() {
		return ".model";
	}

}
