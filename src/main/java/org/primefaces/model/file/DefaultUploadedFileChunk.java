/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import org.primefaces.component.fileupload.ContentRange;

import java.io.IOException;
import java.io.InputStream;

public class DefaultUploadedFileChunk implements UploadedFileChunk {

    private UploadedFile wrapped;
    private long chunkRangeBegin;
    private long chunkRangeEnd;
    private long chunkTotalFileSize;
    private boolean lastChunk;

    public DefaultUploadedFileChunk() {
        // NOOP
    }

    public DefaultUploadedFileChunk(UploadedFile uploadedFile, ContentRange contentRange) {
        this.wrapped = uploadedFile;
        this.chunkRangeBegin = contentRange.getChunkRangeBegin();
        this.chunkRangeEnd = contentRange.getChunkRangeEnd();
        this.chunkTotalFileSize = contentRange.getChunkTotalFileSize();
        this.lastChunk = contentRange.isLastChunk();
    }

    @Override
    public long getChunkRangeBegin() {
        return chunkRangeBegin;
    }

    @Override
    public long getChunkRangeEnd() {
        return chunkRangeEnd;
    }

    @Override
    public long getChunkTotalFileSize() {
        return chunkTotalFileSize;
    }

    @Override
    public boolean isLastChunk() {
        return lastChunk;
    }

    @Override
    public String getFileName() {
        return wrapped.getFileName();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return wrapped.getInputStream();
    }

    @Override
    public byte[] getContent() {
        return wrapped.getContent();
    }

    @Override
    public String getContentType() {
        return wrapped.getContentType();
    }

    @Override
    public long getSize() {
        return wrapped.getSize();
    }

    @Override
    public void write(String filePath) throws Exception {
        wrapped.write(filePath);
    }
}
