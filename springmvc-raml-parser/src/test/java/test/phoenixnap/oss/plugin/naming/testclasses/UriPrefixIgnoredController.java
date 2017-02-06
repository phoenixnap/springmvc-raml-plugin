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

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * Test Class
 * 
 * @author Kurt Paris
 * @since 0.5.3
 *
 */
@RestController
@RequestMapping(UriPrefixIgnoredController.CONTROLLER_PART)
public class UriPrefixIgnoredController {
	
	public static final String CONTROLLER_PART = "/the/url";
	public static final  String METHOD_PART = "/that/should/be/ignored";
	
	public static final  String IGNORED = CONTROLLER_PART + METHOD_PART;
	
	

	public void unannotatedMethod() {

	}

	@RequestMapping(value = METHOD_PART+"/base/endpoint", method = { RequestMethod.GET}, produces={ MediaType.APPLICATION_JSON_VALUE})
	public String endpoint() {
		return null;
	}
	
	@RequestMapping(value = METHOD_PART+"/base/endpoint", method = { RequestMethod.GET}, produces={ MediaType.TEXT_PLAIN_VALUE})
	public String endpointAgain(@RequestBody ThreeElementClass request) {
		return null;
	}
	
	@RequestMapping(value = METHOD_PART+"/base/endpoint", method = { RequestMethod.POST}, produces={ MediaType.APPLICATION_JSON_VALUE})
	public String secondEndpoint() {
		return null;
	}
	
	@RequestMapping(value = METHOD_PART+"/base/endpoint", method = { RequestMethod.POST}, produces={ MediaType.TEXT_PLAIN_VALUE})
	public String secondEndpointAgain(@RequestBody ThreeElementClass request) {
		return null;
	}
	
	

}
