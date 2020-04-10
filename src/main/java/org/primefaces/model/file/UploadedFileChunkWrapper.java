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

public class UploadedFileChunkWrapper extends UploadedFileWrapper implements UploadedFileChunk {

    public UploadedFileChunkWrapper() {
        super();
    }

    public UploadedFileChunkWrapper(UploadedFileChunk wrapped) {
        super(wrapped);
    }

    @Override
    public Long getChunkRangeBegin() {
        return ((UploadedFileChunk) getWrapped()).getChunkRangeBegin();
    }

    @Override
    public void setChunkRangeBegin(Long chunkRangeBegin) {
        ((UploadedFileChunk) getWrapped()).setChunkRangeBegin(chunkRangeBegin);
    }

    @Override
    public Long getChunkRangeEnd() {
        return ((UploadedFileChunk) getWrapped()).getChunkRangeEnd();
    }

    @Override
    public void setChunkRangeEnd(Long chunkRangeEnd) {
        ((UploadedFileChunk) getWrapped()).setChunkRangeEnd(chunkRangeEnd);
    }

    @Override
    public Long getChunkTotalFileSize() {
        return ((UploadedFileChunk) getWrapped()).getChunkTotalFileSize();
    }

    @Override
    public void setChunkTotalFileSize(Long chunkTotalFileSize) {
        ((UploadedFileChunk) getWrapped()).setChunkTotalFileSize(chunkTotalFileSize);
    }

    @Override
    public boolean isLastChunk() {
        return ((UploadedFileChunk) getWrapped()).isLastChunk();
    }

    @Override
    public void setLastChunk(boolean lastChunk) {
        ((UploadedFileChunk) getWrapped()).setLastChunk(lastChunk);
    }
}
