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
package test.phoenixnap.oss.plugin.naming.testclasses;

/**
 * 
 * Test Class
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class ThreeElementClass {

	private byte element1;
	private Integer element2;
	private String element3;

	public ThreeElementClass() {

	}

	public ThreeElementClass(byte element1, Integer element2, String element3) {
		super();
		this.element1 = element1;
		this.element2 = element2;
		this.element3 = element3;
	}

	public byte getElement1() {
		return element1;
	}

	public void setElement1(byte element1) {
		this.element1 = element1;
	}

	public Integer getElement2() {
		return element2;
	}

	public void setElement2(Integer element2) {
		this.element2 = element2;
	}

	public String getElement3() {
		return element3;
	}

	public void setElement3(String element3) {
		this.element3 = element3;
	}

}
