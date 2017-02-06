/*
 * Copyright 2002-2017 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package test.phoenixnap.oss.plugin.naming.testclasses;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.phoenixnap.oss.ramlapisync.annotations.Description;
import com.phoenixnap.oss.ramlapisync.annotations.data.PathDescription;

/**
 * 
 * Test Class
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
@RestController
@RequestMapping("/base")
public class TestController {

	public void unannotatedMethod() {

	}

	@RequestMapping("/simpleMethodAll")
	public String simpleMethodAllHttpMethods() {
		return null;
	}

	@RequestMapping(value = "/simpleMethod", method = { RequestMethod.GET })
	public String simpleGetMethod() {
		return null;
	}

	@RequestMapping(value = "/simpleMethod", method = { RequestMethod.POST })
	public String simplePostMethod() {
		return null;
	}

	@RequestMapping(value = "/oneParameter", method = { RequestMethod.GET })
	public String getMethodSimpleParameter(@RequestParam String param1) {
		return null;
	}

	@RequestMapping(value = "/oneParameterBody", method = { RequestMethod.GET })
	public String getMethodBodyParameter(@RequestBody String param1) {
		return null;
	}

	@RequestMapping(value = "/oneParameterBody", method = { RequestMethod.POST })
	public String postMethodBodyParameter(@RequestBody String param1) {
		return null;
	}

	@RequestMapping(value = "/oneParameter", method = { RequestMethod.POST })
	public String postMethodSimpleParameter(@RequestParam String param1) {
		return null;
	}

	@RequestMapping(value = "/twoParameter", method = { RequestMethod.GET })
	public String getMethodTwoParameter(@RequestParam Integer param1,
			@RequestParam(required = true, value = "nameOverride") String param2) {
		return null;
	}

	@RequestMapping(value = "/twoParameter", method = { RequestMethod.POST })
	public String postMethodTwoParameter(@RequestParam Integer param1,
			@RequestParam(required = true, value = "nameOverride") String param2) {
		return null;
	}

	@RequestMapping(value = "/oneParameter/{pathVariable}", method = { RequestMethod.GET })
	public String getMethodSimpleParameterPathVariable(@RequestParam String param1, @PathVariable String pathVariable) {
		return null;
	}

	@RequestMapping(value = "/oneParameter/{pathVariable}", method = { RequestMethod.POST })
	public String postMethodSimpleParameterPathVariable(@RequestParam String param1, @PathVariable String pathVariable) {
		return null;
	}

	@RequestMapping(value = "/miscCases/{pathVariable}", method = { RequestMethod.GET })
	public String getMethodMiscCasesPathVariable(@RequestParam String[] param1, @PathVariable Integer pathVariable) {
		return null;
	}

	@RequestMapping(value = "/oneParameterBodyObject", method = { RequestMethod.POST, RequestMethod.GET })
	public String postMethodTwoParameter(@RequestBody ThreeElementClass param1) {
		return null;
	}

	@RequestMapping(value = "/responseObject", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ThreeElementClass postResponseObject() {
		return null;
	}
	
	@RequestMapping(value = "/methodBodyIgnore", method = { RequestMethod.PUT})
	public String putMethodBodyIgnore(CamelCaseTest shouldBeIgnored, @RequestBody ThreeElementClass param1, CamelCaseTest shouldBeAlsoIgnored ) {
		return null;
	}
	
	@RequestMapping(value = "/descriptionTest/secondBlock/thirdBlock/stuff", method = { RequestMethod.PUT})
	@Description(pathDescriptions = {
			@PathDescription(key="descriptionTest", value="aaaaaaaaaaaaaaaa"),
			@PathDescription(key="secondBlock", value="bbbbbbbbbbbbbbbbbb"),
			@PathDescription(key="thirdBlock", value="cccccccccccccccccc")	
	})
	public String descriptionTest(CamelCaseTest shouldBeIgnored, @RequestBody ThreeElementClass param1, CamelCaseTest shouldBeAlsoIgnored ) {
		return null;
	}

}
