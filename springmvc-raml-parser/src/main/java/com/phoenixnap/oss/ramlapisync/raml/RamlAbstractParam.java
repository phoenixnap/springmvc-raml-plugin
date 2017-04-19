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
package com.phoenixnap.oss.ramlapisync.raml;

/**
 * Abstract Representation of a Raml Parameter
 * 
 * @author armin.weisser
 * @author Aleksandar Stojsavljevic
 * @since 0.8.1
 */
public abstract class RamlAbstractParam { //extends AbstractParam {

    public abstract void setType(RamlParamType paramType);

    public abstract void setType(String type);

    public abstract void setRequired(boolean required);

    public abstract void setExample(String example);

    public abstract void setDescription(String description);

    public abstract boolean isRequired();

    public abstract RamlParamType getType();
    
    public abstract String getFormat();

    public abstract String getExample();

    public abstract void setDisplayName(String displayName);

    public abstract String getDescription();

    public abstract String getDisplayName();

	public abstract String getDefaultValue();

	public abstract String getPattern();
	
	/**
	 * Convenience method for easier processing. Non supporting parameters are assumed to be single.
	 * 
	 * @return True if this is an array/repeatable parameter. Default to false if no implementation found
	 */
	public boolean isRepeat () {
		return false;
	}
	
	
}
