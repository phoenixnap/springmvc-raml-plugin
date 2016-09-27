package com.phoenixnap.oss.ramlapisync.generation.rules.pojogen;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.naming.SchemaHelper;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

/**
 * Tests relating to POJO Generation
 * 
 * @author kurtpa
 * @since 0.5.2
 */
public class JsonSchema2PojoTest {
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	@Before
    public void before() {
    }
	
	@After
    public void after() {
    }
	
	private static String path = "pojogen"+ File.separator;
	
	@Test
    public void schemaHelper_ExtractsName_fromId() throws Exception {
		URL url = Resources.getResource(path + "B.json");
		String text = Resources.toString(url, Charsets.UTF_8);
        String nameFromSchema = SchemaHelper.extractNameFromSchema(text, "Not", "Even");
        
        assertEquals("B",nameFromSchema);
        
    }
	
	@Test
    public void schemaHelper_ExtractsName_schemaName_noId() throws Exception {
		URL url = Resources.getResource(path + "B-noid.json");
		String text = Resources.toString(url, Charsets.UTF_8);
        String nameFromSchema = SchemaHelper.extractNameFromSchema(text, "Not", "Even");
        
        assertEquals("Not",nameFromSchema);
        
    }
	
	@Test
    public void schemaHelper_ExtractsName_schemaName_badId() throws Exception {
		URL url = Resources.getResource(path + "B-badid.json");
		String text = Resources.toString(url, Charsets.UTF_8);
        String nameFromSchema = SchemaHelper.extractNameFromSchema(text, "Not", "Even");
        
        assertEquals("Not",nameFromSchema);
        
    }
	
	@Test
    public void schemaHelper_ExtractsName_fallback() throws Exception {
		URL url = Resources.getResource(path + "B-noid.json");
		String text = Resources.toString(url, Charsets.UTF_8);
        String nameFromSchema = SchemaHelper.extractNameFromSchema(text, "1Not", "Even");
        
        assertEquals("Even",nameFromSchema);
        
    }
	
	@Test
    public void schemaHelper_ExtractsPojo() throws Exception {
		URL url = Resources.getResource(path + "B.json");
		String text = Resources.toString(url, Charsets.UTF_8);
        ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null, text, "com.test", "Fallback", null);
        
        assertNotNull(mapSchemaToPojo);
        
    }
	
	@Test
    public void schemaHelper_ExtractsPojo_NestedUsingClasspath() throws Exception {
		URL url = Resources.getResource(path + "A.json");
		String text = Resources.toString(url, Charsets.UTF_8);
        ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null, text, "com.test", "Fallback", null);
        assertNotNull(mapSchemaToPojo);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        try {
        	 mapSchemaToPojo.getCodeModel().build(new SingleStreamCodeWriter(bos));
        	 logger.debug(bos.toString());
        } catch (IOException e) {
            assertThat(e.getMessage(), is(nullValue()));
        }
    }
	
	@Test
    public void schemaHelper_ExtractsPojo_NestedUsingFile() throws Exception {
		String resourceName = path + "A.json";
		URL url = Resources.getResource(resourceName);
		String text = Resources.toString(url, Charsets.UTF_8);
		String path = "file:" + URLDecoder.decode(url.getPath(), Charsets.UTF_8.name()).replace(resourceName, "");
        ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null, text, "com.test", "Fallback", path);
        assertNotNull(mapSchemaToPojo);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        try {
        	 mapSchemaToPojo.getCodeModel().build(new SingleStreamCodeWriter(bos));
        	 logger.debug(bos.toString());
        } catch (IOException e) {
            assertThat(e.getMessage(), is(nullValue()));
        }
    }
	
	@Test
	public void test_schemaNaming_Nested() throws Exception  {
		String resourceName = path + "nested.schema";
		URL url = Resources.getResource(resourceName);
		String text = Resources.toString(url, Charsets.UTF_8);
		String path = "file:" + URLDecoder.decode(url.getPath(), Charsets.UTF_8.name()).replace(resourceName, "");
        ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null, text, "com.test", "Fallback", path);
        assertNotNull(mapSchemaToPojo);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        assertThat(mapSchemaToPojo.getName(), is("JavaName2nd"));
        try {
        	 mapSchemaToPojo.getCodeModel().build(new SingleStreamCodeWriter(bos));
        	 logger.debug(bos.toString());
        } catch (IOException e) {
            assertThat(e.getMessage(), is(nullValue()));
        }
		
	}
	
	@Test
	public void test_schemaNaming_Nested_Ref() throws Exception  {
		String resourceName = path + "nested-ref.schema";
		URL url = Resources.getResource(resourceName);
		String text = Resources.toString(url, Charsets.UTF_8);
		String path = "file:" + URLDecoder.decode(url.getPath(), Charsets.UTF_8.name()).replace(resourceName, "");
        ApiBodyMetadata mapSchemaToPojo = SchemaHelper.mapSchemaToPojo(null, text, "com.test", "Fallback", path);
        assertNotNull(mapSchemaToPojo);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        assertThat(mapSchemaToPojo.getName(), is("JavaName2nd"));
        try {
        	 mapSchemaToPojo.getCodeModel().build(new SingleStreamCodeWriter(bos));
        	 logger.debug(bos.toString());
        } catch (IOException e) {
            assertThat(e.getMessage(), is(nullValue()));
        }
		
	}
    

}
