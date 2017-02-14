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
package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;


import org.raml.v2.api.model.v10.api.DocumentationItem;

import com.phoenixnap.oss.ramlapisync.raml.RamlDocumentationItem;

/**
 * Implementation based on the Raml 1.0 Parser
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlDocumentationItem implements RamlDocumentationItem {

    private final DocumentationItem documentationItem;

	public RJP10V2RamlDocumentationItem(DocumentationItem documentationItem) {
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
		throw new UnsupportedOperationException();
    }

    @Override
    public void setTitle(String title) {
		throw new UnsupportedOperationException();
    }
}
