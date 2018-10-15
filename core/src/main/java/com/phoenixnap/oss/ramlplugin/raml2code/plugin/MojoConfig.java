package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

import org.jsonschema2pojo.GenerationConfig;

import java.util.Map;

public interface MojoConfig {
	String getRamlPath();

	String getOutputRelativePath();

	Boolean getAddTimestampFolder();

	String getBasePackage();

	Boolean getGenerateUnreferencedObjects();

	String getBaseUri();

	Boolean getSeperateMethodsByContentType();

	Boolean getUseJackson1xCompatibility();

	String getRule();

	Map<String, String> getRuleConfiguration();

	GenerationConfig getGenerationConfig();

	Boolean getInjectHttpHeadersParameter();

	Integer getResourceDepthInClassNames();

	Integer getResourceTopLevelInClassNames();

	Boolean getReverseOrderInClassNames();

	MethodsNamingLogic getMethodsNamingLogic();

	OverrideNamingLogicWith getOverrideNamingLogicWith();

	String getDontGenerateForAnnotation();

	String getSchemaLocation();
}
