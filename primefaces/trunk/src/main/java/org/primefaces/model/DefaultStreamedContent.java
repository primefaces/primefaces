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
	
	public DefaultStreamedContent() {}
	
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

	public InputStream getStream() {
		return stream;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }    
    public String getContentEncoding() {
        return contentEncoding;
    }
}