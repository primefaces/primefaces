/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads bytes up to a maximum length, if its count goes above that, it stops.
 * <p>
 * This is useful to wrap ServletInputStreams. The ServletInputStream will block if you try to read content from it that isn't there, because it doesn't know
 * whether the content hasn't arrived yet or whether the content has finished. So, one of these, initialized with the Content-length sent in the
 * ServletInputStream's header, will stop it blocking, providing it's been sent with a correct content length.
 * </p>
 *
 * @see https://github.com/apache/commons-io/blob/rel/commons-io-2.15.1/src/main/java/org/apache/commons/io/input/BoundedInputStream.java
 */
public class BoundedInputStream extends FilterInputStream {

    /**
     * Represents the end-of-file (or stream).
     */
    public static final int EOF = -1;

    /** The max count of bytes to read. */
    private final long maxCount;

    /** The count of bytes read. */
    private long count;

    /** The marked position. */
    private long mark = EOF;

    /** Flag if close should be propagated. */
    private boolean propagateClose = true;

    /**
     * Constructs a new {@link BoundedInputStream} that wraps the given input
     * stream and is unlimited.
     *
     * @param in The wrapped input stream.
     */
    public BoundedInputStream(final InputStream in) {
        this(in, EOF);
    }

    /**
     * Constructs a new {@link BoundedInputStream} that wraps the given input
     * stream and limits it to a certain size.
     *
     * @param inputStream The wrapped input stream.
     * @param maxLength The maximum number of bytes to return.
     */
    public BoundedInputStream(final InputStream inputStream, final long maxLength) {
        // Some badly designed methods - e.g. the servlet API - overload length
        // such that "-1" means stream finished
        super(inputStream);
        this.maxCount = maxLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() throws IOException {
        if (isMaxLength()) {
            onMaxLength(maxCount, count);
            return 0;
        }
        return in.available();
    }

    /**
     * Invokes the delegate's {@code close()} method
     * if {@link #isPropagateClose()} is {@code true}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        if (propagateClose) {
            in.close();
        }
    }

    /**
     * Gets the count of bytes read.
     *
     * @return The count of bytes read.
     */
    public long getCount() {
        return count;
    }

    /**
     * Gets the max count of bytes to read.
     *
     * @return The max count of bytes to read.
     */
    public long getMaxLength() {
        return maxCount;
    }

    private boolean isMaxLength() {
        return maxCount >= 0 && count >= maxCount;
    }

    /**
     * Tests whether the {@link #close()} method
     * should propagate to the underling {@link InputStream}.
     *
     * @return {@code true} if calling {@link #close()}
     * propagates to the {@code close()} method of the
     * underlying stream or {@code false} if it does not.
     */
    public boolean isPropagateClose() {
        return propagateClose;
    }

    /**
     * Invokes the delegate's {@code mark(int)} method.
     *
     * @param readLimit read ahead limit
     */
    @Override
    public synchronized void mark(final int readLimit) {
        in.mark(readLimit);
        mark = count;
    }

    /**
     * Invokes the delegate's {@code markSupported()} method.
     *
     * @return true if mark is supported, otherwise false
     */
    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    /**
     * A caller has caused a request that would cross the {@code maxLength} boundary.
     *
     * @param maxLength The max count of bytes to read.
     * @param count The count of bytes read.
     * @throws IOException Subclasses may throw.
     */
    protected void onMaxLength(final long maxLength, final long count) throws IOException {
        // for subclasses
    }

    /**
     * Invokes the delegate's {@code read()} method if
     * the current position is less than the limit.
     *
     * @return the byte read or -1 if the end of stream or
     * the limit has been reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        if (isMaxLength()) {
            onMaxLength(maxCount, count);
            return EOF;
        }
        final int result = in.read();
        count++;
        return result;
    }

    /**
     * Invokes the delegate's {@code read(byte[])} method.
     *
     * @param b the buffer to read the bytes into
     * @return the number of bytes read or -1 if the end of stream or
     * the limit has been reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    /**
     * Invokes the delegate's {@code read(byte[], int, int)} method.
     *
     * @param b the buffer to read the bytes into
     * @param off The start offset
     * @param len The number of bytes to read
     * @return the number of bytes read or -1 if the end of stream or
     * the limit has been reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (isMaxLength()) {
            onMaxLength(maxCount, count);
            return EOF;
        }
        final long maxRead = maxCount >= 0 ? Math.min(len, maxCount - count) : len;
        final int bytesRead = in.read(b, off, (int) maxRead);

        if (bytesRead == EOF) {
            return EOF;
        }

        count += bytesRead;
        return bytesRead;
    }

    /**
     * Invokes the delegate's {@code reset()} method.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public synchronized void reset() throws IOException {
        in.reset();
        count = mark;
    }

    /**
     * Sets whether the {@link #close()} method
     * should propagate to the underling {@link InputStream}.
     *
     * @param propagateClose {@code true} if calling
     * {@link #close()} propagates to the {@code close()}
     * method of the underlying stream or
     * {@code false} if it does not.
     */
    public void setPropagateClose(final boolean propagateClose) {
        this.propagateClose = propagateClose;
    }

    /**
     * Invokes the delegate's {@code skip(long)} method.
     *
     * @param n the number of bytes to skip
     * @return the actual number of bytes skipped
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public long skip(final long n) throws IOException {
        final long toSkip = maxCount >= 0 ? Math.min(n, maxCount - count) : n;
        final long skippedBytes = in.skip(toSkip);
        count += skippedBytes;
        return skippedBytes;
    }

    /**
     * Invokes the delegate's {@code toString()} method.
     *
     * @return the delegate's {@code toString()}
     */
    @Override
    public String toString() {
        return in.toString();
    }
}
