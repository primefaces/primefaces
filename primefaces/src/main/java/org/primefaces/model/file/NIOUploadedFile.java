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

import javax.faces.FacesException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class NIOUploadedFile extends AbstractUploadedFile<Path> implements Serializable {

    private String contentType;

    public NIOUploadedFile() {
        // NOOP
    }

    public NIOUploadedFile(Path source, String filename, String contentType, Long sizeLimit, String webKitRelativePath) {
        super(source, filename, sizeLimit, webKitRelativePath);
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getSize() {
        try {
            return Files.size(getOriginalSource());
        }
        catch (IOException e) {
            throw new FacesException(e);
        }
    }

    @Override
    public void delete() throws IOException {
        Files.delete(getOriginalSource());
    }

    @Override
    protected InputStream getOriginalSourceInputStream() throws IOException {
        return Files.newInputStream(getOriginalSource());
    }

    @Override
    protected void write(File file) throws IOException {
        Files.copy(getOriginalSource(), file.toPath());
    }

}
