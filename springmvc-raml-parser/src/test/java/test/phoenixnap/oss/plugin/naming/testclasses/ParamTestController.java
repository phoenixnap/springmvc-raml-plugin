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

import javax.validation.constraints.NotNull;

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
public class ParamTestController {

	public static class ModelClassParent {
		@NotNull private String param1;
		
		public String getParam1() {
			return param1;
		}
		
		public void setParam1(String param1) {
			this.param1 = param1;
		}
	}
	
	public static class ModelClass extends ModelClassParent {
		
		private String param2;
		
		public String getParam2() {
			return param2;
		}
		
		public void setParam2(String param2) {
			this.param2 = param2;
		}
	}
	
	public void unannotatedMethod() {

	}

	@RequestMapping(value = "/base/endpointWithGet", method = { RequestMethod.GET})
	public String endpointWithGet(@RequestParam(required=true) String param1, @RequestParam(required=false) String param2) {
		return null;
	}
	
	@RequestMapping(value = "/base/endpointWithURIParam/{uriParam}", method = { RequestMethod.GET})
	public String endpointWithURIParamGet(@RequestParam(name="param3", required=true) String arg0, @RequestParam(value="param4", required=false) String arg1) {
		return null;
	}

	@RequestMapping(value = "/base/endpointWithObjectAsParameter", method = { RequestMethod.GET})
	public String endpointWithObjectAsParameter(@RequestParam ModelClass parameters) {
		return null;
	}
	
}
