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
package org.primefaces.model;

import org.primefaces.util.Callbacks;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import jakarta.faces.FacesException;

/**
 * Default implementation of a StreamedContent
 */
public class DefaultStreamedContent implements StreamedContent, Serializable {

    public static final DefaultStreamedContent DUMMY = new DefaultStreamedContent();

    private static final long serialVersionUID = 1L;
    private Callbacks.SerializableSupplier<InputStream> stream;
    private String contentType;
    private String name;
    private String contentEncoding;
    private Long contentLength;
    private Callbacks.SerializableConsumer<OutputStream> writer;

    public DefaultStreamedContent() {
        // NOOP
    }

    @Override
    public Supplier<InputStream> getStream() {
        return stream;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getContentEncoding() {
        return contentEncoding;
    }

    @Override
    public Long getContentLength() {
        return contentLength;
    }

    @Override
    public Consumer<OutputStream> getWriter() {
        return writer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final DefaultStreamedContent streamedContent;

        private Builder() {
            streamedContent = new DefaultStreamedContent();
        }

        public Builder stream(Callbacks.SerializableSupplier<InputStream> is) {
            streamedContent.stream = is;
            return this;
        }

        public Builder contentType(String contentType) {
            streamedContent.contentType = contentType;
            return this;
        }

        public Builder name(String name) {
            streamedContent.name = name;
            return this;
        }

        public Builder contentEncoding(String contentEncoding) {
            streamedContent.contentEncoding = contentEncoding;
            return this;
        }

        public Builder contentLength(Long contentLength) {
            streamedContent.contentLength = contentLength;
            return this;
        }

        public Builder writer(Callbacks.SerializableConsumer<OutputStream> writer) {
            streamedContent.writer = writer;
            return this;
        }

        public DefaultStreamedContent build() {
            if (streamedContent.writer == null && streamedContent.stream == null) {
                throw new FacesException("Either provide a 'stream' or 'writer'!");
            }

            return streamedContent;
        }
    }
}
