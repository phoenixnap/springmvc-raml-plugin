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
package com.phoenixnap.oss.ramlapisync.generation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import com.phoenixnap.oss.ramlapisync.generation.exception.InvalidCodeModelException;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

/**
 * The class provides static helper methods for JCodeModel related tasks.
 *
 * @author armin.weisser
 * @author kurt paris
 * @since 0.4.1
 */
public abstract class CodeModelHelper {
	
	/**
     *	Searches inside a JCodeModel for a class with a specified name ignoring package
     *
     * @param codeModel[] The codemodels which we will look inside
     * @param simpleClassName The class name to search for
     * @return the first class in any package that matches the simple class name.
     */
    public static JClass findFirstClassBySimpleName(JCodeModel codeModel, String simpleClassName) {
    	return findFirstClassBySimpleName(codeModel == null ? new JCodeModel[]{new JCodeModel()} : new JCodeModel[]{codeModel}, simpleClassName);
    }

    /**
     *	Searches inside a JCodeModel for a class with a specified name ignoring package
     *
     * @param codeModels The codemodels which we will look inside
     * @param simpleClassName The class name to search for
     * @return the first class in any package that matches the simple class name.
     */
    public static JClass findFirstClassBySimpleName(JCodeModel[] codeModels, String simpleClassName) {
    	if (codeModels != null && codeModels.length > 0) {
    		for (JCodeModel codeModel : codeModels) {
		        Iterator<JPackage> packages = codeModel.packages();
		        while(packages.hasNext()) {
		            JPackage jPackage = packages.next();
		            Iterator<JDefinedClass> classes = jPackage.classes();
		            while(classes.hasNext()) {
		                JDefinedClass aClass = classes.next();
		                if(aClass.name().equals(simpleClassName)) {
		                    return aClass;
		                }
		            }
		        }
    		}
    		//Is this a simple type?
    		JType parseType;
			try {
				parseType = codeModels[0].parseType(simpleClassName);
				if (parseType != null) {
	    			if (parseType.isPrimitive()) {
	    				return parseType.boxify();	    			
	    			} else if (parseType instanceof JClass){
	    				return (JClass) parseType;
	    			}
				}
			} catch (ClassNotFoundException e) {
				; //Do nothing we will throw an exception further down
			}
			
			JClass boxedPrimitive = codeModels[0].ref("java.lang." + simpleClassName);
			if (boxedPrimitive != null) {
				return boxedPrimitive;
			}
    			
			throw new InvalidCodeModelException("No unique class found for simple class name " + simpleClassName);
    	}
    	
    	throw new InvalidCodeModelException("No code models provided for " + simpleClassName);
    	
    }

    public static JExtMethod ext(JMethod jMethod, JCodeModel jCodeModel) {
        return new JExtMethod(jMethod, jCodeModel);
    }

    public static String getVersion() {
        try {
            Properties prop = new Properties();
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("parser.properties");
            prop.load(in);
            in.close();
            return prop.getProperty("parser.version");
        } catch (IOException e) {
            return "???";
        }
    }

    /**
     * Helper class because JMethod does not expose it's JCodeModel.
     */
    public static class JExtMethod {

        private final JMethod jMethod;
        private final JCodeModel owner;

        public JExtMethod(JMethod jMethod, JCodeModel jCodeModel) {
            this.jMethod = jMethod;
            this.owner = jCodeModel;
        }

        public JMethod get() {
            return jMethod;
        }

        public JCodeModel owner() {
            return owner;
        }
    }
}
