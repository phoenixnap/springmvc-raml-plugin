package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;

public class Config {

	private static PojoGenerationConfig pojoGenerationConfig = new PojoGenerationConfig();

	private static SpringMvcEndpointGeneratorMojo springMvcEndpointGeneratorMojo;

	private static final Boolean DEFAULT_SEPERATE_METHODS_BY_CONTENTTYPE = Boolean.FALSE;
	private static Boolean seperateMethodsByContentType = DEFAULT_SEPERATE_METHODS_BY_CONTENTTYPE;

	private static final Boolean DEFAULT_INJECT_HTTP_HEADERS_PARAMETER = Boolean.FALSE;
	private static Boolean injectHttpHeadersParameter = DEFAULT_INJECT_HTTP_HEADERS_PARAMETER;

	private static final Integer DEFAULT_RESOURCE_DEPTH_IN_CLASS_NAMES = 1;
	private static Integer resourceDepthInClassNames = DEFAULT_RESOURCE_DEPTH_IN_CLASS_NAMES;

	private static final Integer DEFAULT_RESOURCE_TOP_LEVEL_IN_CLASS_NAMES = 0;
	private static Integer resourceTopLevelInClassNames = DEFAULT_RESOURCE_TOP_LEVEL_IN_CLASS_NAMES;

	private static final Boolean DEFAULT_REVERSE_ORDER_IN_CLASS_NAMES = Boolean.FALSE;
	private static Boolean reverseOrderInClassNames = DEFAULT_REVERSE_ORDER_IN_CLASS_NAMES;

	private static final String DEFAULT_BASE_PACKAGE = "com.gen.test";
	private static String basePackage = DEFAULT_BASE_PACKAGE;

	Config() {
	}

	protected static void setMojo(SpringMvcEndpointGeneratorMojo springMvcEndpointGeneratorMojo) {
		Config.springMvcEndpointGeneratorMojo = springMvcEndpointGeneratorMojo;
		if (springMvcEndpointGeneratorMojo != null) {
			Config.pojoGenerationConfig = springMvcEndpointGeneratorMojo.generationConfig;
		} else {
			Config.pojoGenerationConfig = null;
		}
	}

	protected static void setPojoConfig(PojoGenerationConfig pojoGenerationConfig) {
		Config.pojoGenerationConfig = pojoGenerationConfig;
	}

	public static PojoGenerationConfig getPojoConfig() {
		return pojoGenerationConfig;
	}

	protected static void setSeperateMethodsByContentType(Boolean seperateMethodsByContentType) {
		Config.seperateMethodsByContentType = seperateMethodsByContentType;
	}

	public static Boolean isSeperateMethodsByContentType() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.seperateMethodsByContentType;
		}
		return seperateMethodsByContentType;
	}

	protected static void setInjectHttpHeadersParameter(Boolean injectHttpHeadersParameter) {
		Config.injectHttpHeadersParameter = injectHttpHeadersParameter;
	}

	public static Boolean isInjectHttpHeadersParameter() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.injectHttpHeadersParameter;
		}
		return injectHttpHeadersParameter;
	}

	protected static void setResourceDepthInClassNames(Integer resourceDepthInClassNames) {
		Config.resourceDepthInClassNames = resourceDepthInClassNames;
	}

	public static Integer getResourceDepthInClassNames() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.resourceDepthInClassNames;
		}
		return resourceDepthInClassNames;
	}

	protected static void setResourceTopLevelInClassNames(Integer resourceTopLevelInClassNames) {
		Config.resourceTopLevelInClassNames = resourceTopLevelInClassNames;
	}

	public static Integer getResourceTopLevelInClassNames() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.resourceTopLevelInClassNames;
		}
		return resourceTopLevelInClassNames;
	}

	protected static void setReverseOrderInClassNames(Boolean reverseOrderInClassNames) {
		Config.reverseOrderInClassNames = reverseOrderInClassNames;
	}

	public static Boolean isReverseOrderInClassNames() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.reverseOrderInClassNames;
		}
		return reverseOrderInClassNames;
	}

	protected static void setBasePackage(String basePackage) {
		Config.basePackage = basePackage;
	}

	public static String getBasePackage() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.basePackage;
		}
		return basePackage;
	}

	public static String getPojoPackage() {
		return getBasePackage() + NamingHelper.getDefaultModelPackage();
	}

	protected static void resetFields() {
		setMojo(null);
		setBasePackage(DEFAULT_BASE_PACKAGE);
		setInjectHttpHeadersParameter(DEFAULT_INJECT_HTTP_HEADERS_PARAMETER);
		setResourceDepthInClassNames(DEFAULT_RESOURCE_DEPTH_IN_CLASS_NAMES);
		setResourceTopLevelInClassNames(DEFAULT_RESOURCE_TOP_LEVEL_IN_CLASS_NAMES);
		setReverseOrderInClassNames(DEFAULT_REVERSE_ORDER_IN_CLASS_NAMES);
		setSeperateMethodsByContentType(DEFAULT_SEPERATE_METHODS_BY_CONTENTTYPE);
	}
}
