package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;

public interface ConfigSource {
	PojoGenerationConfig getPojoConfig();

	Boolean isSeperateMethodsByContentType();

	Boolean isInjectHttpHeadersParameter();

	Integer getResourceDepthInClassNames();

	Integer getResourceTopLevelInClassNames();

	Boolean isReverseOrderInClassNames();

	String getBasePackage();

	MethodsNamingLogic getMethodsNamingLogic();

	OverrideNamingLogicWith getOverrideNamingLogicWith();

	String getDontGenerateForAnnotation();

	Boolean isInjectHttpRequestParameter();

	Boolean isGeneratedAnnotation();

	default String getPojoPackage() {
		return getBasePackage() + NamingHelper.getDefaultModelPackage();
	}
}
