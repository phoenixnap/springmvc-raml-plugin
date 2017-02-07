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
package com.phoenixnap.oss.ramlapisync.generation.rules;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ClassAnnotationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ClassCommentRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ClassFieldDeclarationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ClientInterfaceDeclarationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ControllerMethodSignatureRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ImplementsControllerInterfaceRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.MethodCommentRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.MethodParamsRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.PackageRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ResourceClassDeclarationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.SpringResponseEntityRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.SpringRestClientMethodBodyRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * 
 * Builds a client from a parsed RAML document. The resulting code makes use of 
 * Spring's REST template and sets the Accept and Content-type headers accordingly.
 * Query string params as well as URI params are also resolved. The following url is generatable : 
 * 
 *  Uri : /account/{accountId}?hasQueryParams=true
 *  HTTP Method: PUT
 *
 *  Request body: 
 *  
 *  {
 *      accountName:name       
 *  }
 *  
 *  results in a client method
 *  
 *   
 *
 * @author kurt paris
 * @author kristian galea
 * @since 0.5.0
 */
public class Spring4RestTemplateClientRule implements ConfigurableRule<JCodeModel, JDefinedClass, ApiResourceMetadata> {
    
 	public static final String ARRAY_PARAMETER_CONFIGURATION = "allowArrayParameters";
	
	String restTemplateFieldName = "restTemplate";
	
	String baseUrlFieldName = "baseUrl";
	
	String baseUrlConfigurationPath = "${client.url}";
	
	String restTemplateQualifierBeanName;
	
	boolean allowArrayParameters = true;
	
    @Override
    public final JDefinedClass apply(ApiResourceMetadata metadata, JCodeModel generatableType) {

        JDefinedClass generatedInterface = new GenericJavaClassRule()
                .setPackageRule(new PackageRule())
                .setClassCommentRule(new ClassCommentRule())
                .setClassRule(new ClientInterfaceDeclarationRule())  //MODIFIED
                .setMethodCommentRule(new MethodCommentRule())
                .setMethodSignatureRule(new ControllerMethodSignatureRule(
                        new SpringResponseEntityRule(),
                        new MethodParamsRule(true, allowArrayParameters)))
                .apply(metadata, generatableType);

        
        GenericJavaClassRule clientGenerator = new GenericJavaClassRule()
                .setPackageRule(new PackageRule())
                .setClassCommentRule(new ClassCommentRule())
                .addClassAnnotationRule(new ClassAnnotationRule(Component.class))                
                .setClassRule(new ResourceClassDeclarationRule(ClientInterfaceDeclarationRule.CLIENT_SUFFIX + "Impl"))   //MODIFIED
                .setImplementsExtendsRule(new ImplementsControllerInterfaceRule(generatedInterface))
                .addFieldDeclarationRule(new ClassFieldDeclarationRule(restTemplateFieldName, RestTemplate.class, true, restTemplateQualifierBeanName)) //Modified
                .addFieldDeclarationRule(new ClassFieldDeclarationRule(baseUrlFieldName, String.class, getBaseUrlConfigurationName())) //
                .setMethodCommentRule(new MethodCommentRule())                
                .setMethodSignatureRule(new ControllerMethodSignatureRule(
                        new SpringResponseEntityRule(),
                        new MethodParamsRule(false, allowArrayParameters)))
                .setMethodBodyRule(new SpringRestClientMethodBodyRule(restTemplateFieldName, baseUrlFieldName));

        return clientGenerator.apply(metadata, generatableType);
    }

	private String getBaseUrlConfigurationName() {
		if(!this.baseUrlConfigurationPath.startsWith("${")) {
			this.baseUrlConfigurationPath = "${" + this.baseUrlConfigurationPath;
		}
		if(!this.baseUrlConfigurationPath.endsWith("}")) {
			this.baseUrlConfigurationPath = this.baseUrlConfigurationPath + "}";
		}
		return baseUrlConfigurationPath;
	}

	@Override
	public void applyConfiguration(Map<String, String> configuration) {
		if(!CollectionUtils.isEmpty(configuration)) {
			if(configuration.containsKey("restTemplateFieldName")) {
				this.restTemplateFieldName = configuration.get("restTemplateFieldName");
			}
			
			if(configuration.containsKey("restTemplateQualifierBeanName")) {
                this.restTemplateQualifierBeanName = configuration.get("restTemplateQualifierBeanName");
            }
			
			if(configuration.containsKey("baseUrlConfigurationPath")) {
				this.baseUrlConfigurationPath = configuration.get("baseUrlConfigurationPath");
			}
			if(configuration.containsKey(ARRAY_PARAMETER_CONFIGURATION)) {
            	allowArrayParameters = BooleanUtils.toBoolean(configuration.get(ARRAY_PARAMETER_CONFIGURATION));
            }
			
		}
	}
    
    
}
