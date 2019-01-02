package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;

// TODO Extract interface
class DefaultConfig {

	private PojoGenerationConfig pojoGenerationConfig = new PojoGenerationConfig();

	private SpringMvcEndpointGeneratorMojo springMvcEndpointGeneratorMojo;

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

	protected void setMojo(SpringMvcEndpointGeneratorMojo springMvcEndpointGeneratorMojo) {
		this.springMvcEndpointGeneratorMojo = springMvcEndpointGeneratorMojo;
		if (springMvcEndpointGeneratorMojo != null) {
			this.pojoGenerationConfig = springMvcEndpointGeneratorMojo.generationConfig;
		} else {
			this.pojoGenerationConfig = null;
		}
	}

	protected void setPojoConfig(PojoGenerationConfig pojoGenerationConfig) {
		this.pojoGenerationConfig = pojoGenerationConfig;
	}

	public PojoGenerationConfig getPojoConfig() {
		return pojoGenerationConfig;
	}

	protected void setSeperateMethodsByContentType(Boolean seperateMethodsByContentType) {
		this.seperateMethodsByContentType = seperateMethodsByContentType;
	}

	public Boolean isSeperateMethodsByContentType() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.seperateMethodsByContentType;
		}
		return seperateMethodsByContentType;
	}

	protected void setInjectHttpHeadersParameter(Boolean injectHttpHeadersParameter) {
		this.injectHttpHeadersParameter = injectHttpHeadersParameter;
	}

	public Boolean isInjectHttpHeadersParameter() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.injectHttpHeadersParameter;
		}
		return injectHttpHeadersParameter;
	}

	protected void setResourceDepthInClassNames(Integer resourceDepthInClassNames) {
		this.resourceDepthInClassNames = resourceDepthInClassNames;
	}

	public Integer getResourceDepthInClassNames() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.resourceDepthInClassNames;
		}
		return resourceDepthInClassNames;
	}

	protected void setResourceTopLevelInClassNames(Integer resourceTopLevelInClassNames) {
		this.resourceTopLevelInClassNames = resourceTopLevelInClassNames;
	}

	public Integer getResourceTopLevelInClassNames() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.resourceTopLevelInClassNames;
		}
		return resourceTopLevelInClassNames;
	}

	protected void setReverseOrderInClassNames(Boolean reverseOrderInClassNames) {
		this.reverseOrderInClassNames = reverseOrderInClassNames;
	}

	public Boolean isReverseOrderInClassNames() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.reverseOrderInClassNames;
		}
		return reverseOrderInClassNames;
	}

	protected void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getBasePackage() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.basePackage;
		}
		return basePackage;
	}

	public MethodsNamingLogic getMethodsNamingLogic() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.methodsNamingLogic;
		}
		if (methodsNamingLogic == null) {
			return DEFAULT_METHODS_NAMING_LOGIC;
		}
		return methodsNamingLogic;
	}

	protected void setMethodsNamingLogic(MethodsNamingLogic methodsNamingLogic) {
		this.methodsNamingLogic = methodsNamingLogic;
	}

	public OverrideNamingLogicWith getOverrideNamingLogicWith() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.overrideNamingLogicWith;
		}
		return overrideNamingLogicWith;
	}

	protected void setOverrideNamingLogicWith(OverrideNamingLogicWith overrideNamingLogicWith) {
		this.overrideNamingLogicWith = overrideNamingLogicWith;
	}

	public String getDontGenerateForAnnotation() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.dontGenerateForAnnotation;
		}
		return dontGenerateForAnnotation;
	}

	protected void setDontGenerateForAnnotation(String dontGenerateForAnnotation) {
		this.dontGenerateForAnnotation = dontGenerateForAnnotation;
	}

	public Boolean isInjectHttpRequestParameter() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.injectHttpRequestParameter;
		}
		return injectHttpRequestParameter;
	}

	public void setInjectHttpRequestParameter(Boolean injectHttpRequestParameter) {
		this.injectHttpRequestParameter = injectHttpRequestParameter;
	}

	public String getPojoPackage() {
		return getBasePackage() + NamingHelper.getDefaultModelPackage();
	}

}
