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
package com.phoenixnap.oss.ramlapisync.raml;

import java.math.BigDecimal;

/**
 * Abstract Representation of a Raml Query Parameter
 * 
 * @author armin.weisser
 * @author Aleksandar Stojsavljevic
 * @since 0.8.1
 */
public abstract class RamlQueryParameter extends RamlAbstractParam {

    public abstract void setRepeat(boolean repeat);

    public abstract Integer getMinLength();

    public abstract Integer getMaxLength();

    public abstract BigDecimal getMinimum();

    public abstract BigDecimal getMaximum();

    public abstract String getPattern();

    public abstract boolean isRepeat();

	public abstract String getDefaultValue();
}
