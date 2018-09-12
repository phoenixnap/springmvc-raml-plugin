package com.phoenixnap.oss.ramlplugin.raml2code.helpers;

import static com.phoenixnap.oss.ramlplugin.raml2code.helpers.CodeModelHelper.findFirstClassBySimpleName;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.http.ResponseEntity;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiBodyMetadata;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public class RuleHelper {

	private RuleHelper() {
	}

	public static JClass getResponseEntity(ApiActionMetadata endpointMetadata, JCodeModel owner, boolean checkBody) {

		return narrow(endpointMetadata, owner, false, checkBody, true);
	}

	public static JClass getCallableResponseEntity(ApiActionMetadata endpointMetadata, JCodeModel owner) {

		return narrow(endpointMetadata, owner, true, true, true);
	}

	public static JClass getObject(ApiActionMetadata endpointMetadata, JCodeModel owner) {

		return narrow(endpointMetadata, owner, false, true, false);
	}

	private static JClass narrow(ApiActionMetadata endpointMetadata, JCodeModel owner, boolean useCallable, boolean checkBody,
			boolean useWildcard) {

		JClass callable = null;
		if (useCallable) {
			callable = owner.ref(Callable.class);
		}
		if (checkBody && endpointMetadata.getResponseBody().isEmpty()) {
			if (useWildcard) {
				return getResponseEntityNarrow(owner, owner.wildcard(), callable);
			} else {
				return owner.ref(Object.class);
			}
		}

		return getReturnEntity(endpointMetadata, owner, callable, useWildcard);
	}

	private static JClass getResponseEntityNarrow(JCodeModel owner, JClass genericType, JClass callable) {

		JClass responseEntity = owner.ref(ResponseEntity.class);
		JClass returnNarrow = responseEntity.narrow(genericType);

		if (callable != null) {
			returnNarrow = callable.narrow(returnNarrow);
		}
		return returnNarrow;
	}

	private static JClass getReturnEntity(ApiActionMetadata endpointMetadata, JCodeModel owner, JClass callable,
			boolean useResponseEntity) {

		ApiBodyMetadata apiBodyMetadata = endpointMetadata.getResponseBody().values().iterator().next();
		JClass type = findFirstClassBySimpleName(apiBodyMetadata.getCodeModel(), apiBodyMetadata.getName());
		if (apiBodyMetadata.isArray()) {
			type = updateType(owner, callable, type, useResponseEntity, true);
		} else if (BigDecimal.class.getSimpleName().equals(apiBodyMetadata.getName())) {
			type = updateType(owner, callable, owner.ref(BigDecimal.class), useResponseEntity, false);
		} else if (BigInteger.class.getSimpleName().equals(apiBodyMetadata.getName())) {
			type = updateType(owner, callable, owner.ref(BigInteger.class), useResponseEntity, false);
		} else {
			type = updateType(owner, callable, type, useResponseEntity, false);
		}
		return type;
	}

	private static JClass updateType(JCodeModel owner, JClass callable, JClass type, boolean useResponseEntity, boolean isArray) {

		JClass arrayType = owner.ref(List.class);
		if (useResponseEntity && isArray) {
			type = getResponseEntityNarrow(owner, arrayType.narrow(type), callable);
		} else if (useResponseEntity) {
			type = getResponseEntityNarrow(owner, type, callable);
		} else if (isArray) {
			type = arrayType.narrow(type);
		}
		return type;
	}
}
