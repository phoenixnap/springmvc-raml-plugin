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

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

public class BugController {
	
	/**
     * Making request to resend stuff to user some other javadoc stuff that was here but removed
     *
     * @param id
     *            the user something id
     * @throws Exception exception
     */
    @RequestMapping(value = "forgotStuff/{somethingID}/resendStuff", method = POST)
    public void forgotStuffResendStuff(@PathVariable String tokenID)
            throws IllegalStateException {
    	//donothing
    }


}
