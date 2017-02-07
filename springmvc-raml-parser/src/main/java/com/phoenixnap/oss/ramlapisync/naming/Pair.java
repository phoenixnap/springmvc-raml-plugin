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
package com.phoenixnap.oss.ramlapisync.naming;

/**
 * Convenience class used to group together two objects
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 * @param <K> The type of the first portion of the pair
 * @param <Y> The type of the second portion of the pair
 */
public class Pair<K, Y> {

	private K first;

	private Y second;

	public Pair(K first, Y second) {
		this.first = first;
		this.second = second;
	}

	public K getFirst() {
		return first;
	}

	public void setFirst(K first) {
		this.first = first;
	}

	public Y getSecond() {
		return second;
	}

	public void setSecond(Y second) {
		this.second = second;
	}
	
	/**
	 * Convenience method to create a pair
	 * 
	 * @param first The first object in the pair
	 * @param second The second object in the pair
	 * @param <A> The type of the first portion of the pair
	 * @param <B> The type of the second portion of the pair
	 * @return A Pair containing both objects in order
	 */
	public static <A, B> Pair<A, B> pairify (A first, B second) {
		return new Pair<A, B>(first, second);
	}

}
