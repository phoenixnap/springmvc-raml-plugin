/*
 * Copyright 2015-2016 the original author or authors.
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

import org.springframework.util.StringUtils;

import com.google.common.reflect.ClassPath.ResourceInfo;

/**
 * Class containing information about a document to be included in the generated raml file. Documents will be added as
 * resources and must be bundled with the raml file
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class ApiDocumentMetadata {

	/**
	 * The title of the document that will be visible in the RAML file
	 */
	private String title;

	/**
	 * The Path to the document relative to the raml file
	 */
	private String path;

	/**
	 * The document being represented.
	 */
	private ResourceInfo document;

	/**
	 * 
	 * @param document The resource pointing to the document to be represented
	 * @param docSuffix The portion of the filename that should be removed for Title generation
	 */
	public ApiDocumentMetadata(ResourceInfo document, String docSuffix) {
		this.document = document;

		String name = document.getResourceName();
		String title = name;
		if (name.contains("/") && !name.endsWith("/")) {
			name = name.substring(name.lastIndexOf("/") + 1);
			title = StringUtils.capitalize(name).replace(docSuffix, "");
		}
		this.path = name;
		this.title = title;
	}

	/**
	 * Gets the path to the document relative to the Raml file
	 * 
	 * @return The path
	 */
	public String getDocumentPath() {
		return path;
	}

	public String getDocumentTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApiDocumentMetadata other = (ApiDocumentMetadata) obj;
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}
