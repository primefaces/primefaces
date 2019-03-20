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
package org.primefaces.util;

import java.util.function.Supplier;

/**
 * Inspired by commons-lang LazyInitializer.
 *
 * @param <T> The type to be lazy initialized.
 */
public class Lazy<T> {

    private static final Object NOT_INITIALIZED = new Object();

    @SuppressWarnings("unchecked")
    private volatile T value = (T) NOT_INITIALIZED;
    private volatile Supplier<T> init;

    public Lazy(Supplier<T> init) {
        this.init = init;
    }

    public synchronized void reset(Supplier<T> init) {
        this.init = init;
        this.value = (T) NOT_INITIALIZED;
    }

    public synchronized void reset(T value) {
        this.value = value;
    }

    public T get() {
        T result = value;

        if (result == NOT_INITIALIZED) {
            synchronized (this) {
                result = value;
                if (result == NOT_INITIALIZED) {
                    value = init.get();
                    result = value;
                }
            }
        }

        return result;
    }

    public boolean isInitialized() {
        return value != NOT_INITIALIZED;
    }
}
