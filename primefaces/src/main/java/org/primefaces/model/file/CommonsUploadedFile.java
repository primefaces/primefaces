/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.model.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.fileupload.FileItem;
import org.primefaces.util.FileUploadUtils;

/**
 *
 * Default UploadedFile implementation based on Commons FileUpload FileItem
 */
public class CommonsUploadedFile extends AbstractUploadedFile<FileItem> implements Serializable {

    private static final long serialVersionUID = 1L;

    public CommonsUploadedFile() {
        // NOOP
    }

    public CommonsUploadedFile(FileItem source, Long sizeLimit, String webKitRelativePath) {
        super(source, source.getName(), sizeLimit, webKitRelativePath);
    }

    @Override
    public long getSize() {
        return getOriginalSource().getSize();
    }

    @Override
    public byte[] getContent() {
        // FileItem#get() has his own cache, therefore use it!
        return getOriginalSource().get();
    }

    @Override
    public String getContentType() {
        return getOriginalSource().getContentType();
    }

    @Override
    public void delete() throws IOException {
        getOriginalSource().delete();
    }

    @Override
    protected InputStream getOriginalSourceInputStream() throws IOException {
        return getOriginalSource().getInputStream();
    }

    @Override
    protected void write(File file) throws IOException {
        try {
            getOriginalSource().write(file);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
