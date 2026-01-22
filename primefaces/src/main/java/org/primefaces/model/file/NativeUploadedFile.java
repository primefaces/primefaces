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
package org.primefaces.model.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import jakarta.servlet.http.Part;

public class NativeUploadedFile extends AbstractUploadedFile<Part> implements Serializable {

    private static final long serialVersionUID = 1L;

    public NativeUploadedFile() {
        // NOOP
    }

    public NativeUploadedFile(Part source, Long sizeLimit, String webKitRelativePath) {
        super(source, source.getSubmittedFileName(), sizeLimit, webKitRelativePath);
    }

    @Override
    public long getSize() {
        return getSource().getSize();
    }

    @Override
    public String getContentType() {
        return getSource().getContentType();
    }

    @Override
    public void delete() throws IOException {
        getSource().delete();
    }

    @Override
    protected InputStream getSourceInputStream() throws IOException {
        return getSource().getInputStream();
    }

    @Override
    protected void write(File file) throws IOException {
        getSource().write(file.getCanonicalPath());
    }
}
