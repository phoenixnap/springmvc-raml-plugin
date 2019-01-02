package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

class DefaultConfig implements ConfigSource {

	private PojoGenerationConfig pojoGenerationConfig = new PojoGenerationConfig();

	private static final Boolean DEFAULT_SEPERATE_METHODS_BY_CONTENTTYPE = Boolean.FALSE;
	private Boolean seperateMethodsByContentType = DEFAULT_SEPERATE_METHODS_BY_CONTENTTYPE;

	private static final Boolean DEFAULT_INJECT_HTTP_HEADERS_PARAMETER = Boolean.FALSE;
	private Boolean injectHttpHeadersParameter = DEFAULT_INJECT_HTTP_HEADERS_PARAMETER;

	private static final Integer DEFAULT_RESOURCE_DEPTH_IN_CLASS_NAMES = 1;
	private Integer resourceDepthInClassNames = DEFAULT_RESOURCE_DEPTH_IN_CLASS_NAMES;

	private static final Integer DEFAULT_RESOURCE_TOP_LEVEL_IN_CLASS_NAMES = 0;
	private Integer resourceTopLevelInClassNames = DEFAULT_RESOURCE_TOP_LEVEL_IN_CLASS_NAMES;

	private static final Boolean DEFAULT_REVERSE_ORDER_IN_CLASS_NAMES = Boolean.FALSE;
	private Boolean reverseOrderInClassNames = DEFAULT_REVERSE_ORDER_IN_CLASS_NAMES;

	private static final String DEFAULT_BASE_PACKAGE = "com.gen.test";
	private String basePackage = DEFAULT_BASE_PACKAGE;

	private static final MethodsNamingLogic DEFAULT_METHODS_NAMING_LOGIC = MethodsNamingLogic.OBJECTS;
	private MethodsNamingLogic methodsNamingLogic = DEFAULT_METHODS_NAMING_LOGIC;

	private static final OverrideNamingLogicWith DEFAULT_OVERRIDE_NAMING_LOGIC_WITH = null;
	private OverrideNamingLogicWith overrideNamingLogicWith = DEFAULT_OVERRIDE_NAMING_LOGIC_WITH;

	private static final String DEFAULT_DONT_GENERATE_FOR_ANNOTATION = null;
	private String dontGenerateForAnnotation = DEFAULT_DONT_GENERATE_FOR_ANNOTATION;

	private static final Boolean DEFAULT_INJECT_HTTP_REQUEST_PARAMETER = Boolean.FALSE;
	private Boolean injectHttpRequestParameter = DEFAULT_INJECT_HTTP_REQUEST_PARAMETER;

	public void setPojoConfig(PojoGenerationConfig pojoGenerationConfig) {
		this.pojoGenerationConfig = pojoGenerationConfig;
	}

	@Override
	public PojoGenerationConfig getPojoConfig() {
		return pojoGenerationConfig;
	}

	public void setSeperateMethodsByContentType(Boolean seperateMethodsByContentType) {
		this.seperateMethodsByContentType = seperateMethodsByContentType;
	}

	@Override
	public Boolean isSeperateMethodsByContentType() {
		return seperateMethodsByContentType;
	}

	public void setInjectHttpHeadersParameter(Boolean injectHttpHeadersParameter) {
		this.injectHttpHeadersParameter = injectHttpHeadersParameter;
	}

	@Override
	public Boolean isInjectHttpHeadersParameter() {
		return injectHttpHeadersParameter;
	}

	public void setResourceDepthInClassNames(Integer resourceDepthInClassNames) {
		this.resourceDepthInClassNames = resourceDepthInClassNames;
	}

	@Override
	public Integer getResourceDepthInClassNames() {
		return resourceDepthInClassNames;
	}

	public void setResourceTopLevelInClassNames(Integer resourceTopLevelInClassNames) {
		this.resourceTopLevelInClassNames = resourceTopLevelInClassNames;
	}

	@Override
	public Integer getResourceTopLevelInClassNames() {
		return resourceTopLevelInClassNames;
	}

	public void setReverseOrderInClassNames(Boolean reverseOrderInClassNames) {
		this.reverseOrderInClassNames = reverseOrderInClassNames;
	}

	@Override
	public Boolean isReverseOrderInClassNames() {
		return reverseOrderInClassNames;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	@Override
	public String getBasePackage() {
		return basePackage;
	}

	@Override
	public MethodsNamingLogic getMethodsNamingLogic() {
		if (methodsNamingLogic == null) {
			return DEFAULT_METHODS_NAMING_LOGIC;
		}
		return methodsNamingLogic;
	}

	public void setMethodsNamingLogic(MethodsNamingLogic methodsNamingLogic) {
		this.methodsNamingLogic = methodsNamingLogic;
	}

	@Override
	public OverrideNamingLogicWith getOverrideNamingLogicWith() {
		return overrideNamingLogicWith;
	}

	public void setOverrideNamingLogicWith(OverrideNamingLogicWith overrideNamingLogicWith) {
		this.overrideNamingLogicWith = overrideNamingLogicWith;
	}

	@Override
	public String getDontGenerateForAnnotation() {
		return dontGenerateForAnnotation;
	}

	public void setDontGenerateForAnnotation(String dontGenerateForAnnotation) {
		this.dontGenerateForAnnotation = dontGenerateForAnnotation;
	}

	@Override
	public Boolean isInjectHttpRequestParameter() {
		return injectHttpRequestParameter;
	}

	public void setInjectHttpRequestParameter(Boolean injectHttpRequestParameter) {
		this.injectHttpRequestParameter = injectHttpRequestParameter;
	}
}
