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
package org.primefaces.model;

import java.io.InputStream;

/**
 * Default implementation of a StreamedContent
 */
public class DefaultStreamedContent implements StreamedContent {

    private InputStream stream;

    private String contentType;

    private String name;

    private String contentEncoding;

    private Integer contentLength;

    public DefaultStreamedContent() {
    }

    public DefaultStreamedContent(InputStream stream) {
        this.stream = stream;
    }

    public DefaultStreamedContent(InputStream stream, String contentType) {
        this(stream);
        this.contentType = contentType;
    }

    public DefaultStreamedContent(InputStream stream, String contentType, String name) {
        this(stream, contentType);
        this.name = name;
    }

    public DefaultStreamedContent(InputStream stream, String contentType, String name, String contentEncoding) {
        this(stream, contentType, name);
        this.contentEncoding = contentEncoding;
    }

    public DefaultStreamedContent(InputStream stream, String contentType, String name, Integer contentLength) {
        this(stream, contentType, name);
        this.contentLength = contentLength;
    }

    public DefaultStreamedContent(InputStream stream, String contentType, String name, String contentEncoding, Integer contentLength) {
        this(stream, contentType, name);
        this.contentEncoding = contentEncoding;
        this.contentLength = contentLength;
    }

    @Override
    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    @Override
    public Integer getContentLength() {
        return contentLength;
    }
}
