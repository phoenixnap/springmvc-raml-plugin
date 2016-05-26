/*
 * Copyright (c) 2016 SAP SE or an SAP affiliate company. All rights reserved. 
 */
package com.phoenixnap.oss.ramlapisync.plugin;

import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.junit.Assert;
import org.junit.Test;

public class JsonSchema2PojoConfigBuilderTest {

	private static final GenerationConfig defaultConfiguration = new DefaultGenerationConfig();
	
	@Test
	public void testEnableUseBigDecimals() {
		Assert.assertFalse(defaultConfiguration.isUseBigDecimals());
		GenerationConfig config = (new JsonSchema2PojoConfigBuilder()).enable("isUseBigDecimals").build();
		Assert.assertTrue(config.isUseBigDecimals());
	}

	@Test
	public void testSetFileExtensions() {
		Assert.assertNotEquals(".pouette", defaultConfiguration.getClassNameSuffix());
		GenerationConfig config = (new JsonSchema2PojoConfigBuilder()).set("getClassNameSuffix", ".pouette").build();
		Assert.assertEquals(".pouette", config.getClassNameSuffix());
	}
	
}
