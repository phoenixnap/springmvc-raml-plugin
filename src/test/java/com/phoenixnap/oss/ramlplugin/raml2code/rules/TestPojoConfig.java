package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.PojoGenerationConfig;

public class TestPojoConfig extends PojoGenerationConfig {

	public void setIncludeJsr303Annotations(boolean includeJsr303Annotations) {
		this.includeJsr303Annotations = includeJsr303Annotations;
	}

	public void setUseBigDecimals(boolean useBigDecimals) {
		this.useBigDecimals = useBigDecimals;
	}

	public void setGenerateBuilders(boolean generateBuilders) {
		this.generateBuilders = generateBuilders;
	}

	public void setDateTimeType(String dateTimeType) {
		this.dateTimeType = dateTimeType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}

	public void setInitializeCollections(boolean initializeCollections) {
		this.initializeCollections = initializeCollections;
	}
}