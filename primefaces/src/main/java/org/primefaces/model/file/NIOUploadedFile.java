/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.apache.commons.io.FilenameUtils;
import org.primefaces.util.FileUploadUtils;

import javax.faces.FacesException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NIOUploadedFile implements UploadedFile {

    private Path file;
    private String filename;
    private byte[] content;
    private String contentType;

    public NIOUploadedFile() {
        // NOOP
    }

    public NIOUploadedFile(Path file, String filename, String contentType) {
        this.file = file;
        this.filename = filename;
        this.contentType = contentType;
    }

    @Override
    public String getFileName() {
        return filename;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(file);
    }

    @Override
    public byte[] getContent() {
        if (content == null) {
            try {
                content = Files.readAllBytes(file);
            }
            catch (IOException e) {
                throw new FacesException(e);
            }
        }
        return content;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getSize() {
        try {
            return Files.size(file);
        }
        catch (IOException e) {
            throw new FacesException(e);
        }
    }

    @Override
    public void write(String filePath) throws Exception {
        String validFileName = FileUploadUtils.getValidFilename(FilenameUtils.getName(getFileName()));
        Files.copy(file, Paths.get(validFileName));
    }

    @Override
    public void delete() throws IOException {
        Files.delete(file);
    }
}
