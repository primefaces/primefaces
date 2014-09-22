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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.servlet.http.Part;

public class NativeUploadedFile implements UploadedFile, Serializable {

    private Part part;
    private String filename;
    
    public NativeUploadedFile() {}

	public NativeUploadedFile(Part part) {
		this.part = part;
        this.filename = resolveFilename(part);
	}
    
    public String getFileName() {
        return filename;
    }

    public InputStream getInputstream() throws IOException {
        return part.getInputStream();
    }

    public long getSize() {
        return part.getSize();
    }

    public byte[] getContents() {
        return null;
    }

    public String getContentType() {
       return part.getContentType();
    }
    
    private String resolveFilename(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        
        return null;
    }

    public void write(String filePath) throws Exception {
        part.write(filePath);
    }
}
