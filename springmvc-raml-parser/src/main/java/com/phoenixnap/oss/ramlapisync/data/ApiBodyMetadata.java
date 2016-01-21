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
package com.phoenixnap.oss.ramlapisync.data;

import com.sun.codemodel.JCodeModel;


/**
 * 
 * Class containing the data required to successfully generate code for an api request or response body
 * 
 * @author Kurt Paris
 * @since 0.2.1
 *
 */	
public class ApiBodyMetadata {
	
	private String name;
	private String schema;
	private JCodeModel codeModel;
	
	public ApiBodyMetadata (String name, String schema, JCodeModel codeModel) {
		super();
		this.schema = schema;
		this.name = name;
		this.codeModel = codeModel;
	}
	
	public String getName() {
		return name;
	}
	public String getSchema() {
		return schema;
	}
	public JCodeModel getCodeModel() {
		return codeModel;
	}

}
