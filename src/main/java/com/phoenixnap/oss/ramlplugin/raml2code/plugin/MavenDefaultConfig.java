package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;

class MavenDefaultConfig extends DefaultConfig {

	private final SpringMvcEndpointGeneratorMojo springMvcEndpointGeneratorMojo;

	MavenDefaultConfig(SpringMvcEndpointGeneratorMojo springMvcEndpointGeneratorMojo) {
		this.springMvcEndpointGeneratorMojo = springMvcEndpointGeneratorMojo;

		if (springMvcEndpointGeneratorMojo != null) {
			setPojoConfig(springMvcEndpointGeneratorMojo.generationConfig);
		} else {
			setPojoConfig(null);
		}
	}

	public Boolean isSeperateMethodsByContentType() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.seperateMethodsByContentType;
		}
		return super.isSeperateMethodsByContentType();
	}

	public Boolean isInjectHttpHeadersParameter() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.injectHttpHeadersParameter;
		}
		return super.isInjectHttpHeadersParameter();
	}

	public Integer getResourceDepthInClassNames() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.resourceDepthInClassNames;
		}
		return super.getResourceDepthInClassNames();
	}

	public Integer getResourceTopLevelInClassNames() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.resourceTopLevelInClassNames;
		}
		return super.getResourceTopLevelInClassNames();
	}

	public Boolean isReverseOrderInClassNames() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.reverseOrderInClassNames;
		}
		return super.isReverseOrderInClassNames();
	}

	public String getBasePackage() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.basePackage;
		}
		return super.getBasePackage();
	}

	public MethodsNamingLogic getMethodsNamingLogic() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.methodsNamingLogic;
		}
		return super.getMethodsNamingLogic();
	}

	public OverrideNamingLogicWith getOverrideNamingLogicWith() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.overrideNamingLogicWith;
		}
		return super.getOverrideNamingLogicWith();
	}

	public String getDontGenerateForAnnotation() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.dontGenerateForAnnotation;
		}
		return super.getDontGenerateForAnnotation();
	}

	public Boolean isInjectHttpRequestParameter() {
		if (springMvcEndpointGeneratorMojo != null) {
			return springMvcEndpointGeneratorMojo.injectHttpRequestParameter;
		}
		return super.isInjectHttpRequestParameter();
	}

	public String getPojoPackage() {
		return getBasePackage() + NamingHelper.getDefaultModelPackage();
	}

}
