package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

/**
 * A {@link ConfigSource} which allows overriding the default configuration with
 * values from a delegate {@link ConfigSource}.
 */
class MavenDefaultConfig extends DefaultConfig implements ConfigSource {

	private final ConfigSource delegate;

	MavenDefaultConfig(ConfigSource delegate) {
		this.delegate = delegate;

		if (delegate != null) {
			setPojoConfig(delegate.getPojoConfig());
		} else {
			setPojoConfig(null);
		}
	}

	public Boolean isSeperateMethodsByContentType() {
		if (delegate != null) {
			return delegate.isSeperateMethodsByContentType();
		}
		return super.isSeperateMethodsByContentType();
	}

	public Boolean isInjectHttpHeadersParameter() {
		if (delegate != null) {
			return delegate.isInjectHttpHeadersParameter();
		}
		return super.isInjectHttpHeadersParameter();
	}

	public Integer getResourceDepthInClassNames() {
		if (delegate != null) {
			return delegate.getResourceDepthInClassNames();
		}
		return super.getResourceDepthInClassNames();
	}

	public Integer getResourceTopLevelInClassNames() {
		if (delegate != null) {
			return delegate.getResourceTopLevelInClassNames();
		}
		return super.getResourceTopLevelInClassNames();
	}

	public Boolean isReverseOrderInClassNames() {
		if (delegate != null) {
			return delegate.isReverseOrderInClassNames();
		}
		return super.isReverseOrderInClassNames();
	}

	public String getBasePackage() {
		if (delegate != null) {
			return delegate.getBasePackage();
		}
		return super.getBasePackage();
	}

	public MethodsNamingLogic getMethodsNamingLogic() {
		if (delegate != null) {
			return delegate.getMethodsNamingLogic();
		}
		return super.getMethodsNamingLogic();
	}

	public OverrideNamingLogicWith getOverrideNamingLogicWith() {
		if (delegate != null) {
			return delegate.getOverrideNamingLogicWith();
		}
		return super.getOverrideNamingLogicWith();
	}

	public String getDontGenerateForAnnotation() {
		if (delegate != null) {
			return delegate.getDontGenerateForAnnotation();
		}
		return super.getDontGenerateForAnnotation();
	}

	public Boolean isInjectHttpRequestParameter() {
		if (delegate != null) {
			return delegate.isInjectHttpRequestParameter();
		}
		return super.isInjectHttpRequestParameter();
	}

	@Override
	public Boolean isGeneratedAnnotation() {
		if (delegate != null) {
			return delegate.isGeneratedAnnotation();
		}
		return super.isGeneratedAnnotation();
	}
}
