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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.input.BoundedInputStream;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.util.FileUploadUtils;

/**
 *
 * UploadedFile implementation based on Commons FileUpload FileItem
 */
public class DefaultUploadedFile implements UploadedFile, Serializable {

    private FileItem fileItem;
    private Long sizeLimit;

    public DefaultUploadedFile() {
    }

    public DefaultUploadedFile(FileItem fileItem, FileUpload fileUpload) {
        this.fileItem = fileItem;
        this.sizeLimit = fileUpload.getSizeLimit();
    }

    @Override
    public String getFileName() {
        return FileUploadUtils.getValidFilename(fileItem.getName());
    }

    @Override
    public List<String> getFileNames() {
        return null;
    }

    @Override
    public InputStream getInputstream() throws IOException {
        return sizeLimit == null ? fileItem.getInputStream() : new BoundedInputStream(fileItem.getInputStream(), sizeLimit);
    }

    @Override
    public long getSize() {
        return fileItem.getSize();
    }

    @Override
    public byte[] getContents() {
        return fileItem.get();
    }

    @Override
    public String getContentType() {
        return fileItem.getContentType();
    }

    @Override
    public void write(String filePath) throws Exception {
        String validFilePath = FileUploadUtils.getValidFilePath(filePath);
        fileItem.write(new File(validFilePath));
    }

}
