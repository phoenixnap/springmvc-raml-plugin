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
package com.phoenixnap.oss.ramlapisync.data;

import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.GenerationConfig;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.naming.SchemaHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
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
	private TypeDeclaration type;
	private JCodeModel codeModel;
	private boolean array = false;
	
	
	public ApiBodyMetadata (String name, TypeDeclaration type, JCodeModel codeModel) {
		super();
		this.schema = null;
		this.type = type;
		this.name = name;
		this.codeModel = codeModel;
		
		//TODO array detection. i think we can default this to false since we should already be generating lists from the type.
	}
	
	public ApiBodyMetadata (String name, String schema, JCodeModel codeModel) {
		super();
		this.schema = schema;
		this.name = name;
		this.codeModel = codeModel;
		
		int typeIdx = schema.indexOf("type");
		
		if (typeIdx != -1) {
			int quoteIdx = schema.indexOf("\"", typeIdx + 6);
			if (quoteIdx != -1) {
				int nextQuoteIdxIdx = schema.indexOf("\"", quoteIdx+1);
				if (nextQuoteIdxIdx != -1) {
					String possibleType = schema.substring(quoteIdx+1, nextQuoteIdxIdx);
					this.name = NamingHelper.getResourceName(this.name, true);
					if ("array".equals(possibleType.toLowerCase())) {
						array = true;
					}
					if (codeModel.countArtifacts() == 0) {
						if (!"object".equals(possibleType.toLowerCase())) {
							try {
								this.name = SchemaHelper.mapSimpleType(RamlParamType.valueOf(possibleType.toUpperCase())).getSimpleName();
							} catch (Exception ex) {
								this.name = String.class.getSimpleName(); //default to string
							}
							this.codeModel = null;
						}
						
				}
			}
		}
		}
	}
	

	public TypeDeclaration getType() {
		return type;
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

	public boolean isArray() {
		return array;
	}
	
	/**
	 * Builds a JCodeModel for this body
	 *
	 * @param basePackage The package we will be using for the domain objects
	 * @param schemaLocation The location of this schema, will be used to create absolute URIs for $ref tags eg "classpath:/"
	 * @param config JsonSchema2Pojo configuration. if null a default config will be used
	 * @param annotator JsonSchema2Pojo annotator. if null a default annotator will be used
	 * @return built JCodeModel
	 */
	public JCodeModel getCodeModel(String basePackage, String schemaLocation, GenerationConfig config, Annotator annotator) {
		if (type != null) {
			return codeModel;
		} else {
			return SchemaHelper.buildBodyJCodeModel(schemaLocation, basePackage, name, schema, config, annotator);
		}
	}

}
