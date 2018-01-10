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
package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.ConfigurableRule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adds a {@literal @}FeignClient annotation at class level.
 * The "url" of the {@literal @}FeignClient is the endpoint url from the ApiControllerMetadata instance.
 * <br>
 * <p>
 * INPUT:
 * <pre class="code">
 * #%RAML 0.8
 * title: myapi
 * mediaType: application/json
 * baseUri: /api
 * /base:
 * get:
 * </pre>
 * <p>
 * OUTPUT:
 * <pre class="code">
 * {@literal @}FeignClient(url = "/api/base", name = "baseClient")
 * </pre>
 *
 * @author Aleksandar Stojsavljevic
 * @since 0.8.6
 */
public class SpringFeignClientClassAnnotationRule implements ConfigurableRule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> {

    public static final String ANNOTATION_PREFIX = "class.annotation.feign.";
    public static final String SUFFIX_ALL        = "all";
    public static final String FEIGN_NAME_KEY    = "name.";
    public static final String FEIGN_NAME_ALL    = ANNOTATION_PREFIX + FEIGN_NAME_KEY + SUFFIX_ALL;
    public static final String FEIGN_URL_KEY     = "url.";
    public static final String FEIGN_URL_ALL     = ANNOTATION_PREFIX + FEIGN_URL_KEY + SUFFIX_ALL;

    protected static final Logger logger = LoggerFactory.getLogger(SpringFeignClientClassAnnotationRule.class);

    private Map<String, String> annotationConfig = new HashMap<>();

    @Override
    public JAnnotationUse apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
        JAnnotationUse feignClient = generatableType.annotate(FeignClient.class);

        String url = getClientUrl(controllerMetadata);
        String name = getClientName(controllerMetadata);

        if (StringUtils.isNotBlank(url)) {
            feignClient.param("url", url);
        }
        if (StringUtils.isNotBlank(name)) {
            feignClient.param("name", name);
        }

        return feignClient;
    }

    private String getClientUrl(ApiResourceMetadata controllerMetadata) {
        String config = getConfigByKey(FEIGN_URL_KEY, controllerMetadata.getResourceName());
        if (config != null) {
            return config;
        }
        return controllerMetadata.getControllerUrl();
    }

    private String getClientName(ApiResourceMetadata controllerMetadata) {
        String config = getConfigByKey(FEIGN_NAME_KEY, controllerMetadata.getResourceName());
        if (StringUtils.isNotBlank(config)) {
            return config;
        }
        String name = controllerMetadata.getResourceName();

        if (name == null || name.length() == 0) {
            return "Client";
        }
        return name.substring(0, 1).toLowerCase() + name.substring(1) + "Client";
    }

    private String getConfigByKey(String key, String resourceName) {
        String allConfigs = ANNOTATION_PREFIX + key + SUFFIX_ALL;
        String oneConfigs = ANNOTATION_PREFIX + key + resourceName;
        logger.info("Looking for configs: all={}, one={}", allConfigs, oneConfigs);
        if (annotationConfig.containsKey(allConfigs)) {
            logger.info("Found {} with value '{}'", allConfigs, annotationConfig.get(allConfigs));
            return annotationConfig.get(allConfigs);
        } else if (annotationConfig.containsKey(oneConfigs)) {
            logger.info("Found {} with value '{}'", oneConfigs, annotationConfig.get(oneConfigs));
            return annotationConfig.get(oneConfigs);
        }
        return null;
    }

    @Override
    public void applyConfiguration(Map<String, String> configuration) {
        annotationConfig = configuration.entrySet().stream().filter(e -> e.getKey().startsWith(ANNOTATION_PREFIX))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }
}
