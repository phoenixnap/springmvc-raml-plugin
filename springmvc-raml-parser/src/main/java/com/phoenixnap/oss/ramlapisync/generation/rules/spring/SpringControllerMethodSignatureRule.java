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

package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import org.springframework.web.bind.annotation.RequestHeader;

import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ControllerMethodSignatureRule;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class SpringControllerMethodSignatureRule extends ControllerMethodSignatureRule {

   public SpringControllerMethodSignatureRule(
           Rule<JDefinedClass, JType, ApiActionMetadata> responseTypeRule,
           Rule<CodeModelHelper.JExtMethod, JMethod, ApiActionMetadata> paramsRule) {
      super(responseTypeRule, paramsRule);
   }

   @Override
   public JMethod apply(ApiActionMetadata endpointMetadata, JDefinedClass generatableType) {
      JMethod method = super.apply(endpointMetadata, generatableType);
      for (JVar param : method.params()) {
         if (param.name().equals("httpHeaders")) {
            param.annotate(RequestHeader.class);
            break;
         }
      }
      return method;
   }
}
