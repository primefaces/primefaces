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
package org.primefaces.context;

/**
 * @see https://stackoverflow.com/questions/3578604/how-to-solve-the-double-checked-locking-is-broken-declaration-in-java
 *
 * @author Kyle Stiemann
 */
abstract class ThreadSafeLazyAccessor<T> {

    // Instance field must be declared volatile in order for the double-check idiom to work (requires JRE 1.5+)
    private volatile T t;

    /**
     * Returns the {@link ThreadSafeLazyAccessor} value. The value is lazily initialized by the first thread that attempts
     * to access it.
     */
    public final T get() {

        T t = this.t;

        // First check without locking (not yet thread-safe)
        if (t == null) {

            synchronized (this) {

                t = this.t;

                // Second check with locking (thread-safe)
                if (t == null) {
                    //CHECKSTYLE:OFF
                    t = this.t = computeValue();
                    //CHECKSTYLE:ON
                }
            }
        }

        return t;
    }

    /**
     * Returns the initial value of the {@link ThreadSafeLazyAccessor}. This method will only be called once by a single
     * thread to obtain the initial value. Subclasses must override this method to provide the process necessary to
     * compute the initial value of the {@link ThreadSafeLazyAccessor}.
     */
    protected abstract T computeValue();
}
