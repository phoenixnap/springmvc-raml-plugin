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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/base/v1")
public class DoubleLevelPrefixController {

	public void unannotatedMethod() {

	}

	@RequestMapping("/simpleMethodAll")
	public String simpleMethodAllHttpMethods() {
		return null;
	}

	@RequestMapping(value = "/oneParameter", method = { RequestMethod.POST })
	public String postMethodSimpleParameter(@RequestParam String param1) {
		return null;
	}
}
