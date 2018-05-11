package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.SpringMvcEndpointGeneratorMojo.LogicForParamsAndMethodsNaming;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.TestPojoConfig;

public class TestConfig {

	public static void resetConfig() {
		Config.resetFields();
		Config.setPojoConfig(new TestPojoConfig());
	}

	public static void setResourceDepthInClassNames(int resourceDepthInClassNames) {
		Config.setResourceDepthInClassNames(resourceDepthInClassNames);
	}

	public static void setBasePackage(String basePackage) {
		Config.setBasePackage(basePackage);
	}

	public static void setIncludeJsr303Annotations(boolean includeJsr303Annotations) {
		((TestPojoConfig) Config.getPojoConfig()).setIncludeJsr303Annotations(includeJsr303Annotations);
	}

	public static void setInjectHttpHeadersParameter(boolean injectHttpHeadersParameter) {
		Config.setInjectHttpHeadersParameter(injectHttpHeadersParameter);
	}

	public static void setLogicForParamsAndMethodsNaming(LogicForParamsAndMethodsNaming logicForParamsAndMethodsNaming) {
		Config.setLogicForParamsAndMethodsNaming(logicForParamsAndMethodsNaming);
	}

	public static void setDateTimeType(String dateTimeType) {
		((TestPojoConfig) Config.getPojoConfig()).setDateTimeType(dateTimeType);
	}

	public static void setDateType(String dateType) {
		((TestPojoConfig) Config.getPojoConfig()).setDateType(dateType);
	}

	public static void setTimeType(String timeType) {
		((TestPojoConfig) Config.getPojoConfig()).setTimeType(timeType);
	}
}
