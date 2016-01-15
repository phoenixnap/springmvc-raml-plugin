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
package com.phoenixnap.oss.ramlapisync.javadoc;

import org.springframework.util.StringUtils;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Simple visitor implementation for visiting MethodDeclaration nodes.
 * 
 * @author Kurt Paris
 * @since 0.0.1
 */
public class MethodVisitor extends VoidVisitorAdapter<String> {

	private JavaDocStore javaDoc;

	public MethodVisitor(JavaDocStore javaDoc) {
		this.javaDoc = javaDoc;
	}

	@Override
	public void visit(MethodDeclaration n, String arg) {
		// here you can access the attributes of the method.
		// this method will be called for all methods in this
		// CompilationUnit, including inner class methods
		int parameterCount;
		if (n.getParameters() != null) {
			parameterCount = n.getParameters().size();
		} else {
			parameterCount = 0;
		}
		String javaDocContent = "";

		if (n.getComment() != null && n.getComment().getContent() != null) {
			javaDocContent = n.getComment().getContent().replaceAll("\\n *\\* *", "\n ");
		}

		if (StringUtils.hasText(javaDocContent)) {
			javaDoc.setJavaDoc(n.getName(), parameterCount, javaDocContent);
		}
	}
}