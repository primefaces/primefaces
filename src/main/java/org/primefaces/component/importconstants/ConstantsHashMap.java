/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.importconstants;

import java.util.HashMap;

import javax.faces.FacesException;

/**
 * Custom {@link HashMap} which throws an {@link FacesException} if the key/constant does not exist.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class ConstantsHashMap<K, V> extends HashMap<K, V> {

	private final Class<?> clazz;

	public ConstantsHashMap(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public V get(Object key) {
		if (!containsKey(key)) {
			throw new FacesException("Class " + clazz.getName() + " does not contain the constant " + key);
		}

		return super.get(key);
	}
}
