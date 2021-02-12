/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.primefaces.util.Lazy;
import org.primefaces.util.SerializableSupplier;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Default implementation of a StreamedContent
 */
public class DefaultStreamedContent implements StreamedContent, Serializable {

    private Lazy<InputStream> stream;
    private String contentType;
    private String name;
    private String contentEncoding;
    private Integer contentLength;

    public DefaultStreamedContent() {
        // NOOP
    }

    @Override
    public InputStream getStream() {
        InputStream result = null;
        if (this.stream != null) {
            result = stream.get();
        }
        return result;
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
    public Integer getContentLength() {
        return contentLength;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final DefaultStreamedContent streamedContent;

        private Builder() {
            streamedContent = new DefaultStreamedContent();
        }

        public Builder stream(SerializableSupplier<InputStream> is) {
            streamedContent.stream = new Lazy<>(is);
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

        public Builder contentLength(Integer contentLength) {
            streamedContent.contentLength = contentLength;
            return this;
        }

        public DefaultStreamedContent build() {
            return streamedContent;
        }
    }
}
