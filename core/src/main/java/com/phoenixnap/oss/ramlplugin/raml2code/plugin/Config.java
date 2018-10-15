package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;

public class Config {

	private static GenerationConfig pojoGenerationConfig = new DefaultGenerationConfig();

	private static MojoConfig mojoConfig;

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

	private static final MethodsNamingLogic DEFAULT_METHODS_NAMING_LOGIC = MethodsNamingLogic.OBJECTS;
	private static MethodsNamingLogic methodsNamingLogic = DEFAULT_METHODS_NAMING_LOGIC;

	private static final OverrideNamingLogicWith DEFAULT_OVERRIDE_NAMING_LOGIC_WITH = null;
	private static OverrideNamingLogicWith overrideNamingLogicWith = DEFAULT_OVERRIDE_NAMING_LOGIC_WITH;

	private static final String DEFAULT_DONT_GENERATE_FOR_ANNOTATION = null;
	private static String dontGenerateForAnnotation = DEFAULT_DONT_GENERATE_FOR_ANNOTATION;

	Config() {
	}

	public static void setMojoConfig(MojoConfig springMvcEndpointGeneratorMojo) {
		Config.mojoConfig = springMvcEndpointGeneratorMojo;
		if (springMvcEndpointGeneratorMojo != null) {
			Config.pojoGenerationConfig = springMvcEndpointGeneratorMojo.getGenerationConfig();
		} else {
			Config.pojoGenerationConfig = null;
		}
	}

	protected static void setPojoConfig(GenerationConfig pojoGenerationConfig) {
		Config.pojoGenerationConfig = pojoGenerationConfig;
	}

	public static GenerationConfig getPojoConfig() {
		return pojoGenerationConfig;
	}

	protected static void setSeperateMethodsByContentType(Boolean seperateMethodsByContentType) {
		Config.seperateMethodsByContentType = seperateMethodsByContentType;
	}

	public static Boolean isSeperateMethodsByContentType() {
		if (mojoConfig != null) {
			return mojoConfig.getSeperateMethodsByContentType();
		}
		return seperateMethodsByContentType;
	}

	protected static void setInjectHttpHeadersParameter(Boolean injectHttpHeadersParameter) {
		Config.injectHttpHeadersParameter = injectHttpHeadersParameter;
	}

	public static Boolean isInjectHttpHeadersParameter() {
		if (mojoConfig != null) {
			return mojoConfig.getInjectHttpHeadersParameter();
		}
		return injectHttpHeadersParameter;
	}

	protected static void setResourceDepthInClassNames(Integer resourceDepthInClassNames) {
		Config.resourceDepthInClassNames = resourceDepthInClassNames;
	}

	public static Integer getResourceDepthInClassNames() {
		if (mojoConfig != null) {
			return mojoConfig.getResourceDepthInClassNames();
		}
		return resourceDepthInClassNames;
	}

	protected static void setResourceTopLevelInClassNames(Integer resourceTopLevelInClassNames) {
		Config.resourceTopLevelInClassNames = resourceTopLevelInClassNames;
	}

	public static Integer getResourceTopLevelInClassNames() {
		if (mojoConfig != null) {
			return mojoConfig.getResourceTopLevelInClassNames();
		}
		return resourceTopLevelInClassNames;
	}

	protected static void setReverseOrderInClassNames(Boolean reverseOrderInClassNames) {
		Config.reverseOrderInClassNames = reverseOrderInClassNames;
	}

	public static Boolean isReverseOrderInClassNames() {
		if (mojoConfig != null) {
			return mojoConfig.getReverseOrderInClassNames();
		}
		return reverseOrderInClassNames;
	}

	protected static void setBasePackage(String basePackage) {
		Config.basePackage = basePackage;
	}

	public static String getBasePackage() {
		if (mojoConfig != null) {
			return mojoConfig.getBasePackage();
		}
		return basePackage;
	}

	public static MethodsNamingLogic getMethodsNamingLogic() {
		if (mojoConfig != null) {
			return mojoConfig.getMethodsNamingLogic();
		}
		if (methodsNamingLogic == null) {
			return DEFAULT_METHODS_NAMING_LOGIC;
		}
		return methodsNamingLogic;
	}

	protected static void setMethodsNamingLogic(MethodsNamingLogic methodsNamingLogic) {
		Config.methodsNamingLogic = methodsNamingLogic;
	}

	public static OverrideNamingLogicWith getOverrideNamingLogicWith() {
		if (mojoConfig != null) {
			return mojoConfig.getOverrideNamingLogicWith();
		}
		return overrideNamingLogicWith;
	}

	protected static void setOverrideNamingLogicWith(OverrideNamingLogicWith overrideNamingLogicWith) {
		Config.overrideNamingLogicWith = overrideNamingLogicWith;
	}

	public static String getDontGenerateForAnnotation() {
		if (mojoConfig != null) {
			return mojoConfig.getDontGenerateForAnnotation();
		}
		return dontGenerateForAnnotation;
	}

	protected static void setDontGenerateForAnnotation(String dontGenerateForAnnotation) {
		Config.dontGenerateForAnnotation = dontGenerateForAnnotation;
	}

	public static String getPojoPackage() {
		return getBasePackage() + NamingHelper.getDefaultModelPackage();
	}

	protected static void resetFields() {
		setMojoConfig(null);
		setBasePackage(DEFAULT_BASE_PACKAGE);
		setInjectHttpHeadersParameter(DEFAULT_INJECT_HTTP_HEADERS_PARAMETER);
		setResourceDepthInClassNames(DEFAULT_RESOURCE_DEPTH_IN_CLASS_NAMES);
		setResourceTopLevelInClassNames(DEFAULT_RESOURCE_TOP_LEVEL_IN_CLASS_NAMES);
		setReverseOrderInClassNames(DEFAULT_REVERSE_ORDER_IN_CLASS_NAMES);
		setSeperateMethodsByContentType(DEFAULT_SEPERATE_METHODS_BY_CONTENTTYPE);
		setMethodsNamingLogic(DEFAULT_METHODS_NAMING_LOGIC);
		setOverrideNamingLogicWith(DEFAULT_OVERRIDE_NAMING_LOGIC_WITH);
		setDontGenerateForAnnotation(DEFAULT_DONT_GENERATE_FOR_ANNOTATION);
	}

}
