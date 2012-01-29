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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.fileupload.FileItem;

/**
 * 
 * UploadedFile implementation based on Commons FileUpload FileItem
 */
public class DefaultUploadedFile implements UploadedFile, Serializable {

	private FileItem fileItem;
	
	public DefaultUploadedFile() {}

	public DefaultUploadedFile(FileItem fileItem) {
		this.fileItem = fileItem;
	}

	public String getFileName() {
		return fileItem.getName();
	}

	public InputStream getInputstream() throws IOException {
		return fileItem.getInputStream();
	}

	public long getSize() {
		return fileItem.getSize();
	}

	public byte[] getContents() {
		return fileItem.get();
	}

	public String getContentType() {
		return fileItem.getContentType();
	}

}
