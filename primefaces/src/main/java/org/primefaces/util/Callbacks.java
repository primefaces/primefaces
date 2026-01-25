/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Collection of callback interfaces. Useful in (mini) visitor and strategy patterns.
 */
public final class Callbacks {

    private Callbacks() {
        // hide the constructor for all static class
    }

    /**
     * Functional interface for a runnable with the potential to throw an exception.
     *
     * @param <E> The type of exception that may be thrown.
     */
    @FunctionalInterface
    public interface FailableRunnable<E extends Throwable> {

        /**
         * Runs the function.
         *
         * @throws E Thrown when the function fails.
         */
        void run() throws E;
    }

    /**
     * Functional interface for a two-argument consumer that can throw an IOException.
     *
     * @param <T> The type of the first argument.
     * @param <U> The type of the second argument.
     */
    @FunctionalInterface
    public interface FailableBiConsumer<T, U> {

        /**
         * Performs the operation on the given arguments.
         *
         * @param t The first input argument.
         * @param u The second input argument.
         * @throws IOException If an I/O error occurs.
         */
        void accept(T t, U u) throws IOException;

        /**
         * Returns a composed IOBiConsumer that performs, in sequence, this operation followed by the after operation.
         *
         * @param after The operation to perform after this operation.
         * @return A composed IOBiConsumer that performs the operations sequentially.
         * @throws IOException If an I/O error occurs.
         */
        default FailableBiConsumer<T, U> andThen(FailableBiConsumer<? super T, ? super U> after) throws IOException {
            Objects.requireNonNull(after);

            return (l, r) -> {
                accept(l, r);
                after.accept(l, r);
            };
        }
    }

    /**
     * A functional interface that extends Consumer interface and Serializable interface.
     */
    @FunctionalInterface
    public interface SerializableConsumer<T> extends Consumer<T>, Serializable {

    }

    /**
     * A functional interface that extends Function interface and Serializable interface.
     */
    @FunctionalInterface
    public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {

    }

    /**
     * A functional interface that extends Predicate interface and Serializable interface.
     */
    @FunctionalInterface
    public interface SerializablePredicate<T> extends Predicate<T>, Serializable {

    }

    /**
     * A functional interface that extends Supplier interface and Serializable interface.
     */
    @FunctionalInterface
    public interface SerializableSupplier<T> extends Supplier<T>, Serializable {

    }

    /**
     * A functional interface for a consumer that accepts three input arguments.
     */
    @FunctionalInterface
    public interface TriConsumer<T, U, R> {
        void accept(T t, U u, R r);
    }

}
