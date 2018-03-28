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
package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.CaseFormat;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ClassAnnotationRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ClassCommentRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ClassFieldDeclarationRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ClientInterfaceDeclarationRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ControllerMethodSignatureRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ImplementsControllerInterfaceRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.MethodCommentRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.MethodParamsRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.PackageRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ResourceClassDeclarationRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringResponseEntityRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringRestClientMethodBodyRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * 
 * Builds a client from a parsed RAML document. The resulting code makes use of
 * Spring's REST template and sets the Accept and Content-type headers
 * accordingly. Query string params as well as URI params are also resolved. The
 * following url is generatable :
 * 
 * Uri : /account/{accountId}?hasQueryParams=true HTTP Method: PUT
 *
 * Request body:
 * 
 * { accountName:name }
 * 
 * results in a client method
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

		List<String> grants = new ArrayList<>();
		Set<ApiActionMetadata> apiCalls = metadata.getApiCalls();
		for (ApiActionMetadata actionMetadata : apiCalls) {
			String grant = RamlHelper.getFirstAuthorizationGrant(actionMetadata.getAction(), actionMetadata.getParent().getDocument());
			if (!StringUtils.isEmpty(grant)) {
				grants.add(grant);
			}
		}
		grants = RamlHelper.removeDuplicates(grants);

		JDefinedClass generatedInterface = new GenericJavaClassRule().setPackageRule(new PackageRule())
				.setClassCommentRule(new ClassCommentRule()).setClassRule(new ClientInterfaceDeclarationRule()) // MODIFIED
				.setMethodCommentRule(new MethodCommentRule())
				.setMethodSignatureRule(
						new ControllerMethodSignatureRule(new SpringResponseEntityRule(), new MethodParamsRule(true, allowArrayParameters)))
				.apply(metadata, generatableType);

		GenericJavaClassRule clientGenerator = new GenericJavaClassRule().setPackageRule(new PackageRule())
				.setClassCommentRule(new ClassCommentRule()).addClassAnnotationRule(new ClassAnnotationRule(Component.class))
				.setClassRule(new ResourceClassDeclarationRule(ClientInterfaceDeclarationRule.CLIENT_SUFFIX + "Impl")) // MODIFIED
				.setImplementsExtendsRule(new ImplementsControllerInterfaceRule(generatedInterface))
				.addFieldDeclarationRule(new ClassFieldDeclarationRule(baseUrlFieldName, String.class, getBaseUrlConfigurationName())) //
				.setMethodCommentRule(new MethodCommentRule())
				.setMethodSignatureRule(new ControllerMethodSignatureRule(new SpringResponseEntityRule(),
						new MethodParamsRule(false, allowArrayParameters)))
				.setMethodBodyRule(new SpringRestClientMethodBodyRule(restTemplateFieldName, baseUrlFieldName));

		if (grants.isEmpty()) {
			clientGenerator.addFieldDeclarationRule(
					new ClassFieldDeclarationRule(restTemplateFieldName, RestTemplate.class, true, restTemplateQualifierBeanName)); // Modified
		} else {
			for (String grant : grants) {
				grant = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, grant);

				clientGenerator.addFieldDeclarationRule(
						new ClassFieldDeclarationRule(grant + StringUtils.capitalize(restTemplateFieldName), RestTemplate.class, true,
								StringUtils.isEmpty(restTemplateQualifierBeanName) ? null : restTemplateQualifierBeanName + grant)); // Modified
			}
		}

		return clientGenerator.apply(metadata, generatableType);
	}

	private String getBaseUrlConfigurationName() {
		if (!this.baseUrlConfigurationPath.startsWith("${")) {
			this.baseUrlConfigurationPath = "${" + this.baseUrlConfigurationPath;
		}
		if (!this.baseUrlConfigurationPath.endsWith("}")) {
			this.baseUrlConfigurationPath = this.baseUrlConfigurationPath + "}";
		}
		return baseUrlConfigurationPath;
	}

	@Override
	public void applyConfiguration(Map<String, String> configuration) {
		if (!CollectionUtils.isEmpty(configuration)) {
			if (configuration.containsKey("restTemplateFieldName")) {
				this.restTemplateFieldName = configuration.get("restTemplateFieldName");
			}

			if (configuration.containsKey("restTemplateQualifierBeanName")) {
				this.restTemplateQualifierBeanName = configuration.get("restTemplateQualifierBeanName");
			}

			if (configuration.containsKey("baseUrlConfigurationPath")) {
				this.baseUrlConfigurationPath = configuration.get("baseUrlConfigurationPath");
			}
			if (configuration.containsKey(ARRAY_PARAMETER_CONFIGURATION)) {
				allowArrayParameters = BooleanUtils.toBoolean(configuration.get(ARRAY_PARAMETER_CONFIGURATION));
			}

		}
	}

}
