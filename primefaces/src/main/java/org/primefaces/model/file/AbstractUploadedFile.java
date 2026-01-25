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

import org.primefaces.util.FileUploadUtils;
import org.primefaces.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import jakarta.faces.FacesException;

import org.apache.commons.io.input.BoundedInputStream;

public abstract class AbstractUploadedFile<T> implements UploadedFile, Serializable {

    private String filename;
    private byte[] cachedContent;
    private Long sizeLimit;
    private String webKitRelativePath;
    private transient T source;

    protected AbstractUploadedFile() {
        // NOOP
    }

    protected AbstractUploadedFile(T source, String filename, Long sizeLimit, String webKitRelativePath) {
        this.source = source;
        this.filename = filename;
        this.sizeLimit = sizeLimit;
        this.webKitRelativePath = webKitRelativePath;
    }

    @Override
    public String getFileName() {
        return filename;
    }

    @Override
    public String getWebkitRelativePath() {
        return webKitRelativePath;
    }

    public T getSource() {
        return source;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return sizeLimit == null
                ? getSourceInputStream()
                : new BoundedInputStream(getSourceInputStream(), sizeLimit);
    }

    @Override
    public byte[] getContent() {
        if (cachedContent != null) {
            return cachedContent;
        }

        try (InputStream is = getInputStream()) {
            cachedContent = IOUtils.toByteArray(is);
        }
        catch (IOException ex) {
            throw new FacesException(ex);
        }

        return cachedContent;
    }

    @Override
    public File write(String directoryPath) throws Exception {
        File directory = new File(directoryPath);
        FileUploadUtils.requireValidFilePath(directory.getCanonicalPath(), true);
        File outputFile = new File(directory, filename);
        write(outputFile);
        return outputFile;
    }

    protected abstract InputStream getSourceInputStream() throws IOException;

    protected abstract void write(File file) throws IOException;
}
