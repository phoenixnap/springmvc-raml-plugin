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
package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlDocumentationItem;
import org.raml.model.DocumentationItem;

/**
 * Implementation based on the Raml 0.8 Parser
 * 
 * @author armin.weisser
 * @since 0.8.1
 */
public class RJP08V1RamlDocumentationItem implements RamlDocumentationItem {

    private final DocumentationItem documentationItem;

    public RJP08V1RamlDocumentationItem(DocumentationItem documentationItem) {
        this.documentationItem = documentationItem;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    DocumentationItem getDocumentationItem() {
        return documentationItem;
    }

    @Override
    public void setContent(String content) {
        documentationItem.setContent(content);
    }

    @Override
    public void setTitle(String title) {
        documentationItem.setTitle(title);
    }
}
