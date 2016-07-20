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

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * 
 * Test Class
 * 
 * @author Kurt Paris
 * @since 0.7.1
 *
 */
@RestController
public class WrappedResponseBodyTestController {
		
	@RequestMapping(value = "/base/endpointWithResponseType", method = { RequestMethod.POST}, produces={"application/test+json"})
	public @ResponseBody ResponseEntity<ThreeElementClass> endpointWithResponseType() {
		return null;
	}
	
	@RequestMapping(value = "/base/endpointWithResponseType", method = { RequestMethod.GET}, produces={"application/test+json"})
	public @ResponseBody DeferredResult<ThreeElementClass> anotherEndpointWithResponseType() {
		return null;
	}
	
	@RequestMapping(value = "/base/endpointWithResponseTypeNonGeneric", method = { RequestMethod.GET}, produces={"application/test+json"})
	public @ResponseBody ResponseEntity endpointWithResponseTypeNonGeneric() {
		return null;
	}
	
	@RequestMapping(value = "/base/endpointWithResponseTypeNonGeneric", method = { RequestMethod.POST}, produces={"application/test+json"})
	public @ResponseBody ResponseEntity<?> anotherEndpointWithResponseTypeNonGeneric() {
		return null;
	}

}
