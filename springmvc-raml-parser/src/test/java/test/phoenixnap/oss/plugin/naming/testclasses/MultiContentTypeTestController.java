/*
 * Copyright 2002-2016 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package test.phoenixnap.oss.plugin.naming.testclasses;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



/**
 * 
 * Test Class
 * 
 * @author Kurt Paris
 * @since 0.1.0
 *
 */
@RestController
public class MultiContentTypeTestController {

	public void unannotatedMethod() {

	}


	@RequestMapping(value = "/base/endpointWithNoContentType", method = { RequestMethod.GET })
	public String endpointWithNoContentType() {
		return null;
	}


	@RequestMapping(value = "/base/endpointWithRequestType", method = { RequestMethod.POST })
	public String endpointWithRequestType(@RequestBody ThreeElementClass request) {
		return null;
	}


	@RequestMapping(value = "/base/endpointWithRequestAndResponseType", method = { RequestMethod.POST })
	public @ResponseBody ThreeElementClass endpointWithRequestAndResponseType(@RequestBody ThreeElementClass request) {
		return null;
	}


	@RequestMapping(value = "/base/endpointWithResponseType", method = { RequestMethod.POST }, produces = "text/html")
	public @ResponseBody String endpointWithResponseType() {
		return "";
	}


	@RequestMapping(value = "/base/endpointWithResponseType", method = { RequestMethod.POST }, produces = "application/json")
	public @ResponseBody ThreeElementClass endpointWithDifferentResponseType() {
		return null;
	}


}
