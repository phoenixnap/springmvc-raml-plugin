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

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Simple visitor implementation for visiting Class, Interface and MethodDeclaration nodes.
 * 
 * @author Kurt Paris
 * @since 0.0.1
 */
public class ClassVisitor extends VoidVisitorAdapter<String> {

	private JavaDocStore javaDoc;

	public ClassVisitor(JavaDocStore javaDoc) {
		this.javaDoc = javaDoc;
	}

	/**
	 * Visit classes and extract javadoc entries to be stored in the supplied JavaDocStore
	 */
	@Override
	public void visit(ClassOrInterfaceDeclaration n, String arg) {
		String javaDocContent = "";

		// Pre process javaDoc to remove useless characters whilst keeping multiline formatting
		if (n.getComment() != null && n.getComment().getContent() != null) {
			javaDocContent = n.getComment().getContent().replaceAll("\\n *\\* *", "\n ");
		}

		// Only append javadoc if available
		if (StringUtils.hasText(javaDocContent)) {
			javaDoc.setClassJavaDoc(javaDocContent);
		}
	}

}