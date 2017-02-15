package com.phoenixnap.oss.ramlapisync.pojo;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public class RamlInterpretationResult {
	
	private PojoBuilder builder;
	private JCodeModel codeModel;
	private JClass resolvedClass;
	
	public PojoBuilder getBuilder() {
		return builder;
	}
	public void setBuilder(PojoBuilder builder) {
		this.builder = builder;
	}
	public JCodeModel getCodeModel() {
		return codeModel;
	}
	public void setCodeModel(JCodeModel codeModel) {
		this.codeModel = codeModel;
	}
	public JClass getResolvedClass() {
		return resolvedClass;
	}
	public void setResolvedClass(JClass resolvedClass) {
		this.resolvedClass = resolvedClass;
	}
	
	public JClass getResolvedClassOrBuiltOrObject() {
		if (getResolvedClass() != null) {
			return getResolvedClass();
		} else if (getBuilder() != null) {
			return getBuilder().getPojo();
		} else {
			return codeModel.ref(Object.class);
		}
	}

}
