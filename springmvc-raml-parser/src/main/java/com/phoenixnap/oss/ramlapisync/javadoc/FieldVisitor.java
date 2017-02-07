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
package com.phoenixnap.oss.ramlapisync.javadoc;

import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Simple visitor implementation for visiting Field Declaration nodes.
 * 
 * @author Kurt Paris
 * @since 0.0.1
 */
public class FieldVisitor extends VoidVisitorAdapter<String> {

	private JavaDocStore javaDoc;

	public FieldVisitor(JavaDocStore javaDoc) {
		this.javaDoc = javaDoc;
	}

	@Override
	public void visit(FieldDeclaration n, String arg) {
		// here you can access the attributes of the method.
		// this method will be called for all methods in this
		// CompilationUnit, including inner class methods
		String javaDocContent = "";

		if (n.getComment() != null && n.getComment().getContent() != null) {
			javaDocContent = NamingHelper.cleanLeadingAndTrailingNewLineAndChars(n.getComment().getContent());
		}

		if (StringUtils.hasText(javaDocContent) && n.getVariables() != null && n.getVariables().size() > 0) {
			javaDoc.setJavaDoc(n.getVariables().get(0).getId().getName(), javaDocContent);
		}
	}
}