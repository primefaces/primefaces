/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

    private static final long serialVersionUID = 1L;

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
