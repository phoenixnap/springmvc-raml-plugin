package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

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

}
