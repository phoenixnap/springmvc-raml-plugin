package com.phoenixnap.oss.ramlplugin.raml2code.helpers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiBodyMetadata;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

/**
 * Tests relating to POJO Generation
 * 
 * @author kurtpa
 * @since 0.5.2
 */
public class SchemaHelperTest {

	protected static final Logger logger = LoggerFactory.getLogger(SchemaHelperTest.class);

	private static boolean VISUALISE_MODEL_TO_CONSOLE = false;

	@Before
	public void before() {
	}

	@After
	public void after() {
	}

	private static String path = "schemas" + File.separator;

	// @Test
	public void schemaHelper_ExtractsName_fromId() throws Exception {
		URL url = Resources.getResource(path + "B.json");
		String text = Resources.toString(url, Charsets.UTF_8);
		String nameFromSchema = SchemaHelper.extractNameFromSchema(text, "Not", "Even");

		assertEquals("B", nameFromSchema);

	}

	// @Test
	public void schemaHelper_ExtractsName_schemaName_noId() throws Exception {
		URL url = Resources.getResource(path + "B-noid.json");
		String text = Resources.toString(url, Charsets.UTF_8);
		String nameFromSchema = SchemaHelper.extractNameFromSchema(text, "Not", "Even");

		assertEquals("Not", nameFromSchema);

	}

	// @Test
	public void schemaHelper_ExtractsName_schemaName_badId() throws Exception {
		URL url = Resources.getResource(path + "B-badid.json");
		String text = Resources.toString(url, Charsets.UTF_8);
		String nameFromSchema = SchemaHelper.extractNameFromSchema(text, "Not", "Even");

		assertEquals("Not", nameFromSchema);

	}

	// @Test
	public void schemaHelper_ExtractsName_fallback() throws Exception {
		URL url = Resources.getResource(path + "B-noid.json");
		String text = Resources.toString(url, Charsets.UTF_8);
		String nameFromSchema = SchemaHelper.extractNameFromSchema(text, "1Not", "Even");

		assertEquals("Even", nameFromSchema);

	}

	@Test
	public void schemaHelper_ExtractsPojo() throws Exception {
		URL url = Resources.getResource(path + "B.json");
		String text = Resources.toString(url, Charsets.UTF_8);

		ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null, text, "com.test", "Fallback", null);

		assertNotNull(mapSchemaToPojo);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			mapSchemaToPojo.getCodeModel().build(new SingleStreamCodeWriter(bos));
			printModel(bos);
		} catch (IOException e) {
			assertThat(e.getMessage(), is(nullValue()));
		}
	}

	protected String serializeModel(JCodeModel jCodeModel) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			jCodeModel.build(new SingleStreamCodeWriter(bos));
		} catch (IOException e) {
			assertThat(e.getMessage(), is(nullValue()));
		}
		return bos.toString();
	}

	// @Test
	// public void schemaHelper_ExtractsArray_Issue134() throws Exception {
	// URL url = Resources.getResource(path + "accounts.schema");
	// String text = Resources.toString(url, Charsets.UTF_8);
	// ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null,
	// text, "com.test", "Fallback", null);
	// assertThat(mapSchemaToPojo.isArray(), is(true));
	// assertNotNull(mapSchemaToPojo);
	//
	// }
	//
	// @Test
	// public void schemaHelper_ExtractsPojo_NestedUsingClasspath() throws
	// Exception {
	// URL url = Resources.getResource(path + "A.json");
	// String text = Resources.toString(url, Charsets.UTF_8);
	// ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null,
	// text, "com.test", "Fallback", null);
	// assertNotNull(mapSchemaToPojo);
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	//
	// try {
	// mapSchemaToPojo.getCodeModel().build(new SingleStreamCodeWriter(bos));
	// printModel(bos);
	// } catch (IOException e) {
	// assertThat(e.getMessage(), is(nullValue()));
	// }
	// }
	//
	// @Test
	// public void schemaHelper_ExtractsPojo_NestedUsingFile() throws Exception
	// {
	// String resourceName = path + "A.json";
	// URL url = Resources.getResource(resourceName);
	// String text = Resources.toString(url, Charsets.UTF_8);
	// String path = "file:" + URLDecoder.decode(url.getPath(),
	// Charsets.UTF_8.name()).replace(resourceName, "");
	// ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null,
	// text, "com.test", "Fallback", path);
	// assertNotNull(mapSchemaToPojo);
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	//
	// try {
	// mapSchemaToPojo.getCodeModel().build(new SingleStreamCodeWriter(bos));
	// printModel(bos);
	// } catch (IOException e) {
	// assertThat(e.getMessage(), is(nullValue()));
	// }
	// }
	//
	private void printModel(ByteArrayOutputStream bos) {
		if (VISUALISE_MODEL_TO_CONSOLE) {
			logger.debug(bos.toString());
		}
	}
	//
	// @Test
	// public void test_schemaNaming_Nested() throws Exception {
	// String resourceName = path + "nested.schema";
	// URL url = Resources.getResource(resourceName);
	// String text = Resources.toString(url, Charsets.UTF_8);
	// String path = "file:" + URLDecoder.decode(url.getPath(),
	// Charsets.UTF_8.name()).replace(resourceName, "");
	// ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null,
	// text, "com.test", "Fallback", path);
	// assertNotNull(mapSchemaToPojo);
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	// assertThat(mapSchemaToPojo.getName(), is("JavaName2nd"));
	// try {
	// mapSchemaToPojo.getCodeModel().build(new SingleStreamCodeWriter(bos));
	// printModel(bos);
	// } catch (IOException e) {
	// assertThat(e.getMessage(), is(nullValue()));
	// }
	//
	// }
	//
	// @Test
	// public void test_schemaNaming_Nested_Ref() throws Exception {
	// String resourceName = path + "nested-ref.schema";
	// URL url = Resources.getResource(resourceName);
	// String text = Resources.toString(url, Charsets.UTF_8);
	// String path = "file:" + URLDecoder.decode(url.getPath(),
	// Charsets.UTF_8.name()).replace(resourceName, "");
	// ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null,
	// text, "com.test", "Fallback", path);
	// assertNotNull(mapSchemaToPojo);
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	// assertThat(mapSchemaToPojo.getName(), is("JavaName2nd"));
	// try {
	// mapSchemaToPojo.getCodeModel().build(new SingleStreamCodeWriter(bos));
	// printModel(bos);
	// } catch (IOException e) {
	// assertThat(e.getMessage(), is(nullValue()));
	// }
	//
	// }
	//
	// @Test
	// public void schemaHelper_ExtractsArray_Plural() throws Exception {
	// URL url = Resources.getResource(path + "addresses.schema");
	// String text = Resources.toString(url, Charsets.UTF_8);
	// ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null,
	// text, "com.test", "Fallback", null);
	// assertThat(mapSchemaToPojo.isArray(), is(true));
	// assertThat(mapSchemaToPojo.getName(), is("Address"));
	// }
	//
	// @Test
	// public void test_mapSimpleType() {
	// assertEquals(Boolean.class,
	// SchemaHelper.mapSimpleType(RamlParamType.BOOLEAN, null));
	// assertEquals(Boolean.class,
	// SchemaHelper.mapSimpleType(RamlParamType.BOOLEAN, "anything"));
	//
	// assertEquals(Date.class, SchemaHelper.mapSimpleType(RamlParamType.DATE,
	// null));
	// assertEquals(Date.class, SchemaHelper.mapSimpleType(RamlParamType.DATE,
	// "anything"));
	//
	// assertEquals(MultipartFile.class,
	// SchemaHelper.mapSimpleType(RamlParamType.FILE, null));
	// assertEquals(MultipartFile.class,
	// SchemaHelper.mapSimpleType(RamlParamType.FILE, "anything"));
	//
	// assertEquals(Long.class,
	// SchemaHelper.mapSimpleType(RamlParamType.INTEGER, null));
	// assertEquals(Long.class,
	// SchemaHelper.mapSimpleType(RamlParamType.INTEGER, "unknown"));
	// assertEquals(Long.class,
	// SchemaHelper.mapSimpleType(RamlParamType.INTEGER, "int64"));
	// assertEquals(Long.class,
	// SchemaHelper.mapSimpleType(RamlParamType.INTEGER, "long"));
	// assertEquals(Integer.class,
	// SchemaHelper.mapSimpleType(RamlParamType.INTEGER, "int32"));
	// assertEquals(Integer.class,
	// SchemaHelper.mapSimpleType(RamlParamType.INTEGER, "int"));
	// assertEquals(Short.class,
	// SchemaHelper.mapSimpleType(RamlParamType.INTEGER, "int8"));
	// assertEquals(Short.class,
	// SchemaHelper.mapSimpleType(RamlParamType.INTEGER, "int16"));
	// try {
	// SchemaHelper.mapSimpleType(RamlParamType.INTEGER, "double");
	// SchemaHelper.mapSimpleType(RamlParamType.INTEGER, "float");
	// fail();
	// } catch (IllegalStateException ex) {
	// //ok!
	// }
	//
	// assertEquals(BigDecimal.class,
	// SchemaHelper.mapSimpleType(RamlParamType.NUMBER, null));
	// assertEquals(BigDecimal.class,
	// SchemaHelper.mapSimpleType(RamlParamType.NUMBER, "unknown"));
	// assertEquals(Long.class, SchemaHelper.mapSimpleType(RamlParamType.NUMBER,
	// "int64"));
	// assertEquals(Long.class, SchemaHelper.mapSimpleType(RamlParamType.NUMBER,
	// "long"));
	// assertEquals(Integer.class,
	// SchemaHelper.mapSimpleType(RamlParamType.NUMBER, "int32"));
	// assertEquals(Integer.class,
	// SchemaHelper.mapSimpleType(RamlParamType.NUMBER, "int"));
	// assertEquals(Short.class,
	// SchemaHelper.mapSimpleType(RamlParamType.NUMBER, "int8"));
	// assertEquals(Short.class,
	// SchemaHelper.mapSimpleType(RamlParamType.NUMBER, "int16"));
	// assertEquals(Double.class,
	// SchemaHelper.mapSimpleType(RamlParamType.NUMBER, "double"));
	// assertEquals(Double.class,
	// SchemaHelper.mapSimpleType(RamlParamType.NUMBER, "float"));
	//
	// }

}
