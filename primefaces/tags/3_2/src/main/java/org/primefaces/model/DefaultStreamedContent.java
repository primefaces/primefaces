/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import java.io.Serializable;

/**
 * Default implementation of a StreamedContent
 */
public class DefaultStreamedContent implements StreamedContent, Serializable {
	
	private InputStream stream;
	
	private String contentType;
	
	private String name;
	
	public DefaultStreamedContent() {}
	
	public DefaultStreamedContent(InputStream stream) {
		this.stream = stream;
	}
	
	public DefaultStreamedContent(InputStream stream, String contentType) {
		this.contentType = contentType;
		this.stream = stream;
	}
	
	public DefaultStreamedContent(InputStream stream, String contentType, String name) {
		this.contentType = contentType;
		this.stream = stream;
		this.name = name;
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
}