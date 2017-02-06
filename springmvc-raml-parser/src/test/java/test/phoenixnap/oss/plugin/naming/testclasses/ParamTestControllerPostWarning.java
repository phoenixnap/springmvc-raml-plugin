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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * Test Class
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
@RestController
public class ParamTestControllerPostWarning {

	public void unannotatedMethod() {

	}
	
	@RequestMapping(value = "/base/endpointWithURIParam/{uriParam}", method = { RequestMethod.POST})
	public String endpointWithURIParamPost(@RequestParam(required=true) String param5, @RequestParam(required=true) String param6) {
		return null;
	}

}
