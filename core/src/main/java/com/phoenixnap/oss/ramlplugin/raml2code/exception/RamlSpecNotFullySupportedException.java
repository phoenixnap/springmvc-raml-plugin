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
package com.phoenixnap.oss.ramlplugin.raml2code.exception;

/**
 * @author aweisser
 */
public class RamlSpecNotFullySupportedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6312075113685445356L;

	public RamlSpecNotFullySupportedException(String simpleMessage) {
		super(simpleMessage + "\n Please feel free to contribute to https://github.com/phoenixnap/springmvc-raml-plugin");
	}
}
