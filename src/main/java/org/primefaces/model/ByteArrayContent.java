/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Byte Array based implementation of a StreamedContent
 */
public class ByteArrayContent implements StreamedContent, Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] data;

    private String contentType;

    private String name;

    private String contentEncoding;

    private Integer contentLength;

    public ByteArrayContent() {
    }

    public ByteArrayContent(byte[] data) {
        this.data = data;
    }

    public ByteArrayContent(byte[] data, String contentType) {
        this(data);
        this.contentType = contentType;
    }

    public ByteArrayContent(byte[] data, String contentType, String name) {
        this(data, contentType);
        this.name = name;
    }

    public ByteArrayContent(byte[] data, String contentType, String name, String contentEncoding) {
        this(data, contentType, name);
        this.contentEncoding = contentEncoding;
    }

    public ByteArrayContent(byte[] data, String contentType, String name, Integer contentLength) {
        this(data, contentType, name);
        this.contentLength = contentLength;
    }

    public ByteArrayContent(byte[] data, String contentType, String name, String contentEncoding, Integer contentLength) {
        this(data, contentType, name);
        this.contentLength = contentLength;
        this.contentEncoding = contentEncoding;
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(this.data);
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

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    @Override
    public String getContentEncoding() {
        return contentEncoding;
    }

    @Override
    public Integer getContentLength() {
        return contentLength;
    }
}
