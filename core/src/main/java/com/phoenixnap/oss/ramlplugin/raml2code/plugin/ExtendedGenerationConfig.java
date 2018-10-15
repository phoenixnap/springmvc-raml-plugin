package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

import org.jsonschema2pojo.DefaultGenerationConfig;

public class ExtendedGenerationConfig extends DefaultGenerationConfig {

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * creation of BigDecimals instead of Floats
	 */
	protected Boolean useBigDecimals = Boolean.FALSE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * creation of Doubles instead of Floats
	 */
	protected Boolean useDoubleNumbers = Boolean.FALSE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * creation of Longs instead of Ints
	 */
	protected Boolean useLongIntegers = Boolean.FALSE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * creation of JSR303 Annotations
	 */
	protected Boolean includeJsr303Annotations = Boolean.TRUE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * creation of primitives instead of Objects
	 */
	protected Boolean usePrimitives = Boolean.FALSE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for use of
	 * commons-lang 3.x imports instead of commons-lang 2.x
	 */
	protected Boolean useCommonsLang3 = Boolean.FALSE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * generation of builder-style methods of the form <code>withXxx(value)</code>
	 * that return <code>this</code>
	 */
	protected Boolean generateBuilders = Boolean.FALSE;
	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * generation of getters and setters
	 */
	protected Boolean includeAccessors = Boolean.TRUE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for support
	 * of additional properties
	 */
	protected Boolean includeAdditionalProperties = Boolean.TRUE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * generation of constructors
	 */
	protected Boolean includeConstructors = Boolean.FALSE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * generation of constructors with only required properties
	 */
	protected Boolean constructorsRequiredPropertiesOnly = Boolean.FALSE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * generation of <code>hashCode</code> and <code>equals</code> methods
	 */
	protected Boolean includeHashcodeAndEquals = Boolean.TRUE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * generation of <code>toString</code> method
	 */
	protected Boolean includeToString = Boolean.TRUE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * initialization of collections
	 */
	protected Boolean initializeCollections = Boolean.TRUE;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * defining the target version of generated source files
	 */
	protected String targetVersion = "1.6";

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * defining the object type used to create string type fields with date-time
	 * format
	 */
	protected String dateTimeType;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * defining the object type used to create string type fields with date format
	 */
	protected String dateType;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * defining the object type used to create string type fields with time format
	 */
	protected String timeType;

	/**
	 * We will pass on this configuration to the jsonschema2pojo library for
	 * creation of BigIntegers instead of Ints
	 */
	protected Boolean useBigIntegers = Boolean.FALSE;

	@Override
	public boolean isUseBigDecimals() {
		if (useBigDecimals != null) {
			return useBigDecimals;
		}
		return super.isUseBigDecimals();
	}

	@Override
	public boolean isUseDoubleNumbers() {
		if (useDoubleNumbers != null) {
			return useDoubleNumbers;
		}
		return super.isUseDoubleNumbers();
	}

	@Override
	public boolean isUseLongIntegers() {
		if (useLongIntegers != null) {
			return useLongIntegers;
		}
		return super.isUseLongIntegers();
	}

	@Override
	public boolean isIncludeJsr303Annotations() {
		if (includeJsr303Annotations != null) {
			return includeJsr303Annotations;
		}
		return super.isIncludeJsr303Annotations();
	}

	@Override
	public boolean isUsePrimitives() {
		if (usePrimitives != null) {
			return usePrimitives;
		}
		return super.isUsePrimitives();
	}

	@Override
	public boolean isUseCommonsLang3() {
		if (useCommonsLang3 != null) {
			return useCommonsLang3;
		}
		return super.isUseCommonsLang3();
	}

	@Override
	public boolean isGenerateBuilders() {
		if (generateBuilders != null) {
			return generateBuilders;
		}
		return super.isGenerateBuilders();
	}

	@Override
	public boolean isIncludeAccessors() {
		if (includeAccessors != null) {
			return includeAccessors;
		}
		return super.isIncludeAccessors();
	}

	@Override
	public boolean isIncludeAdditionalProperties() {
		if (includeAdditionalProperties != null) {
			return includeAdditionalProperties;
		}
		return super.isIncludeAdditionalProperties();
	}

	@Override
	public boolean isIncludeConstructors() {
		if (includeConstructors != null) {
			return includeConstructors;
		}
		return super.isIncludeConstructors();
	}

	@Override
	public boolean isConstructorsRequiredPropertiesOnly() {
		if (constructorsRequiredPropertiesOnly != null) {
			return constructorsRequiredPropertiesOnly;
		}
		return super.isConstructorsRequiredPropertiesOnly();
	}

	@Override
	public boolean isIncludeHashcodeAndEquals() {
		if (includeHashcodeAndEquals != null) {
			return includeHashcodeAndEquals;
		}
		return super.isIncludeHashcodeAndEquals();
	}

	@Override
	public boolean isIncludeToString() {
		if (includeToString != null) {
			return includeToString;
		}
		return super.isIncludeToString();
	}

	@Override
	public boolean isInitializeCollections() {
		if (initializeCollections != null) {
			return initializeCollections;
		}
		return super.isInitializeCollections();
	}

	@Override
	public String getTargetVersion() {
		if (targetVersion != null) {
			return targetVersion;
		}
		return super.getTargetVersion();
	}

	@Override
	public String getDateTimeType() {
		if (dateTimeType != null) {
			return dateTimeType;
		}
		return super.getDateTimeType();
	}

	@Override
	public String getDateType() {
		if (dateType != null) {
			return dateType;
		}
		return super.getDateType();
	}

	@Override
	public String getTimeType() {
		if (timeType != null) {
			return timeType;
		}
		return super.getTimeType();
	}

	@Override
	public boolean isUseBigIntegers() {
		if (useBigIntegers != null) {
			return useBigIntegers;
		}
		return super.isUseBigIntegers();
	}

	public void setUseBigIntegers(boolean useBigIntegers) {
		this.useBigIntegers = useBigIntegers;
	}

}
