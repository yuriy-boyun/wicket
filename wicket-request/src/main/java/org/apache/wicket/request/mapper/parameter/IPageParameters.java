/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.request.mapper.parameter;

import java.util.List;
import java.util.Set;

import org.apache.wicket.util.string.StringValue;

/**
 * Contains only the accessor methods of PageParameters.
 * Used as protection of accidental modifications of Page's PageParameters (page#getPageParameters()).
 *
 * When such modification is really needed {@linkplain #mutable()} method should be used:
 * <code>parameters.mutable().add("name", "value");</code>
 */
public interface IPageParameters
{
	/**
	 * Return set of all named parameter names.
	 *
	 * @return named parameter names
	 */
	Set<String> getNamedKeys();

	/**
	 * Returns parameter value of named parameter with given name
	 *
	 * @param name
	 * @return parameter value
	 */
	StringValue get(final String name);

	/**
	 * Return list of all values for named parameter with given name
	 *
	 * @param name
	 * @return list of parameter values
	 */
	List<StringValue> getValues(final String name);

	/**
	 * @return All named parameters in exact order.
	 */
	List<INamedParameters.NamedPair> getAllNamed();

	/**
	 * Returns the position of a named parameter.
	 *
	 * @param name
	 *            the name of the parameter to look for
	 * @return the position of the parameter. {@code -1} if there is no parameter with that name.
	 */
	int getPosition(String name);

	/**
	 * @param index
	 * @return indexed parameter on given index
	 */
	StringValue get(final int index);

	/**
	 * @return count of indexed parameters
	 */
	int getIndexedCount();

	/**
	 * @return <code>true</code> if the parameters are empty, <code>false</code> otherwise.
	 */
	boolean isEmpty();

	PageParameters mutable();
 }