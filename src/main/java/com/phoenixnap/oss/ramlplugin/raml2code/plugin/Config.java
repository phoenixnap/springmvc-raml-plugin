package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

public class Config {

	private static DefaultConfig CURRENT_CONFIGURATION = new DefaultConfig();

	static synchronized void setInstance(DefaultConfig config) {
		CURRENT_CONFIGURATION = config;
	}

	Config() {
	}

	protected static void setPojoConfig(PojoGenerationConfig pojoGenerationConfig) {
		CURRENT_CONFIGURATION.setPojoConfig(pojoGenerationConfig);
	}

	public static PojoGenerationConfig getPojoConfig() {
		return CURRENT_CONFIGURATION.getPojoConfig();
	}

	protected static void setSeperateMethodsByContentType(Boolean seperateMethodsByContentType) {
		CURRENT_CONFIGURATION.setSeperateMethodsByContentType(seperateMethodsByContentType);
	}

	public static Boolean isSeperateMethodsByContentType() {
		return CURRENT_CONFIGURATION.isSeperateMethodsByContentType();
	}

	protected static void setInjectHttpHeadersParameter(Boolean injectHttpHeadersParameter) {
		CURRENT_CONFIGURATION.setInjectHttpHeadersParameter(injectHttpHeadersParameter);
	}

	public static Boolean isInjectHttpHeadersParameter() {
		return CURRENT_CONFIGURATION.isInjectHttpHeadersParameter();
	}

	protected static void setResourceDepthInClassNames(Integer resourceDepthInClassNames) {
		CURRENT_CONFIGURATION.setResourceDepthInClassNames(resourceDepthInClassNames);
	}

	public static Integer getResourceDepthInClassNames() {
		return CURRENT_CONFIGURATION.getResourceDepthInClassNames();
	}

	protected static void setResourceTopLevelInClassNames(Integer resourceTopLevelInClassNames) {
		CURRENT_CONFIGURATION.setResourceTopLevelInClassNames(resourceTopLevelInClassNames);
	}

	public static Integer getResourceTopLevelInClassNames() {
		return CURRENT_CONFIGURATION.getResourceTopLevelInClassNames();
	}

	protected static void setReverseOrderInClassNames(Boolean reverseOrderInClassNames) {
		CURRENT_CONFIGURATION.setReverseOrderInClassNames(reverseOrderInClassNames);
	}

	public static Boolean isReverseOrderInClassNames() {
		return CURRENT_CONFIGURATION.isReverseOrderInClassNames();
	}

	protected static void setBasePackage(String basePackage) {
		CURRENT_CONFIGURATION.setBasePackage(basePackage);
	}

	public static String getBasePackage() {
		return CURRENT_CONFIGURATION.getBasePackage();
	}

	public static MethodsNamingLogic getMethodsNamingLogic() {
		return CURRENT_CONFIGURATION.getMethodsNamingLogic();
	}

	protected static void setMethodsNamingLogic(MethodsNamingLogic methodsNamingLogic) {
		CURRENT_CONFIGURATION.setMethodsNamingLogic(methodsNamingLogic);
	}

	public static OverrideNamingLogicWith getOverrideNamingLogicWith() {
		return CURRENT_CONFIGURATION.getOverrideNamingLogicWith();
	}

	protected static void setOverrideNamingLogicWith(OverrideNamingLogicWith overrideNamingLogicWith) {
		CURRENT_CONFIGURATION.setOverrideNamingLogicWith(overrideNamingLogicWith);
	}

	public static String getDontGenerateForAnnotation() {
		return CURRENT_CONFIGURATION.getDontGenerateForAnnotation();
	}

	protected static void setDontGenerateForAnnotation(String dontGenerateForAnnotation) {
		CURRENT_CONFIGURATION.setDontGenerateForAnnotation(dontGenerateForAnnotation);
	}

	public static Boolean isInjectHttpRequestParameter() {
		return CURRENT_CONFIGURATION.isInjectHttpRequestParameter();
	}

	public static void setInjectHttpRequestParameter(Boolean injectHttpRequestParameter) {
		CURRENT_CONFIGURATION.setInjectHttpRequestParameter(injectHttpRequestParameter);
	}

	public static String getPojoPackage() {
		return CURRENT_CONFIGURATION.getPojoPackage();
	}

}
