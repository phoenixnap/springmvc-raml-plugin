/*
 * Copyright 2002-2016 the original author or authors.
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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phoenixnap.oss.ramlapisync.annotations.Description;
import com.phoenixnap.oss.ramlapisync.annotations.data.PathDescription;

/**
 * 
 * Test Class
 * 
 * @author Kurt Paris
 * @since 0.9.1
 *
 */
@RestController
@RequestMapping("/base")
public class ShorthandTestController {

	public void unannotatedMethod() {

	}

	@GetMapping(value = "/simpleMethod")
	public String simpleGetMethod() {
		return null;
	}

	@PostMapping(value = "/simpleMethod")
	public String simplePostMethod() {
		return null;
	}

	@GetMapping(value = "/oneParameter")
	public String getMethodSimpleParameter(@RequestParam String param1) {
		return null;
	}

	@GetMapping(value = "/oneParameterBody")
	public String getMethodBodyParameter(@RequestBody String param1) {
		return null;
	}

	@PostMapping(value = "/oneParameterBody")
	public String postMethodBodyParameter(@RequestBody String param1) {
		return null;
	}

	@PostMapping(value = "/oneParameter")
	public String postMethodSimpleParameter(@RequestParam String param1) {
		return null;
	}

	@GetMapping(value = "/twoParameter")
	public String getMethodTwoParameter(@RequestParam Integer param1,
			@RequestParam(required = true, value = "nameOverride") String param2) {
		return null;
	}

	@PostMapping(value = "/twoParameter")
	public String postMethodTwoParameter(@RequestParam Integer param1,
			@RequestParam(required = true, value = "nameOverride") String param2) {
		return null;
	}

	@GetMapping(value = "/oneParameter/{pathVariable}")
	public String getMethodSimpleParameterPathVariable(@RequestParam String param1, @PathVariable String pathVariable) {
		return null;
	}

	@PostMapping(value = "/oneParameter/{pathVariable}")
	public String postMethodSimpleParameterPathVariable(@RequestParam String param1, @PathVariable String pathVariable) {
		return null;
	}

	@GetMapping(value = "/miscCases/{pathVariable}")
	public String getMethodMiscCasesPathVariable(@RequestParam String[] param1, @PathVariable Integer pathVariable) {
		return null;
	}

	
	@PutMapping(value = "/methodBodyIgnore")
	public String putMethodBodyIgnore(CamelCaseTest shouldBeIgnored, @RequestBody ThreeElementClass param1, CamelCaseTest shouldBeAlsoIgnored ) {
		return null;
	}
	
	@PutMapping(value = "/descriptionTest/secondBlock/thirdBlock/stuff")
	@Description(pathDescriptions = {
			@PathDescription(key="descriptionTest", value="aaaaaaaaaaaaaaaa"),
			@PathDescription(key="secondBlock", value="bbbbbbbbbbbbbbbbbb"),
			@PathDescription(key="thirdBlock", value="cccccccccccccccccc")	
	})
	public String descriptionTest(CamelCaseTest shouldBeIgnored, @RequestBody ThreeElementClass param1, CamelCaseTest shouldBeAlsoIgnored ) {
		return null;
	}

}
