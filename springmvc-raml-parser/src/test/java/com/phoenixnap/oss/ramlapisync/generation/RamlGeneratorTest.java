package com.phoenixnap.oss.ramlapisync.generation;

import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import test.phoenixnap.oss.plugin.naming.testclasses.DoubleLevelPrefixController;
import test.phoenixnap.oss.plugin.naming.testclasses.TestController;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author tsarnowski
 */
public class RamlGeneratorTest {


	private RamlGenerator generator = new RamlGenerator();

	@Before
	public void init(){
		ResourceParser scanner = new SpringMvcResourceParser(FileUtils.getTempDirectory(), "", ResourceParser.CATCH_ALL_MEDIA_TYPE, false);
		generator = new RamlGenerator(scanner);
	}

	@Test
	public void shouldRemovePrefix(){
	    //given
		String uriPrefix = "/base";

	    //when
		RamlGenerator result = generator.generateRamlForClasses("Test", "testVersion", "/", new Class[]{
				TestController.class}, null, uriPrefix);

		//then
		assertFalse("Should not contains '/base' resource", result.getRaml().getResources().containsKey("/base"));
	}

	@Test
	public void shouldWorlForEmptyPrefix(){
		//given
		String uriPrefix = "";

		//when
		RamlGenerator result = generator.generateRamlForClasses("Test", "testVersion", "/", new Class[]{
				TestController.class}, null, uriPrefix);

		//then
		assertTrue("Should contains '/base' resource", result.getRaml().getResources().containsKey("/base"));
	}

	@Test
	public void shouldRemoveTwoLevelsOfResource(){
		//given
		String uriPrefix = "/base/v1";

		//when
		RamlGenerator result = generator.generateRamlForClasses("Test", "testVersion", "/", new Class[]{
				DoubleLevelPrefixController.class}, null, uriPrefix);

		//then
		assertFalse("Should not contains '/base' resource", result.getRaml().getResources().containsKey("/base"));
		assertTrue("Should contains '/simpleMethodAll'", result.getRaml().getResources().containsKey("/simpleMethodAll"));
		assertTrue("Should contains '/oneParameter'", result.getRaml().getResources().containsKey("/oneParameter"));
	}

	@Test
	public void shouldRemoveLevelWithEmptyResources(){
		//given
		String uriPrefix = "/base/v1/simpleMethodAll";

		//when
		RamlGenerator result = generator.generateRamlForClasses("Test", "testVersion", "/", new Class[]{
				DoubleLevelPrefixController.class}, null, uriPrefix);

		//then
		assertFalse("Should not contains '/base' resource", result.getRaml().getResources().containsKey("/base"));
		assertTrue("Should contains '/oneParameter'", result.getRaml().getResources().containsKey("/oneParameter"));
	}

}