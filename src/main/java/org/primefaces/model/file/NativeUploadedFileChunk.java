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

import javax.servlet.http.Part;

public class NativeUploadedFileChunk extends NativeUploadedFile implements  UploadedFileChunk {

    private Long chunkRangeBegin;
    private Long chunkRangeEnd;
    private Long chunkTotalFileSize;
    private boolean lastChunk = false;

    public NativeUploadedFileChunk() {
        super();
    }

    public NativeUploadedFileChunk(Part part, Long sizeLimit) {
        super(part, sizeLimit);
    }

    public Long getChunkRangeBegin() {
        return chunkRangeBegin;
    }

    public void setChunkRangeBegin(Long chunkRangeBegin) {
        this.chunkRangeBegin = chunkRangeBegin;
    }

    public Long getChunkRangeEnd() {
        return chunkRangeEnd;
    }

    public void setChunkRangeEnd(Long chunkRangeEnd) {
        this.chunkRangeEnd = chunkRangeEnd;
    }

    public Long getChunkTotalFileSize() {
        return chunkTotalFileSize;
    }

    public void setChunkTotalFileSize(Long chunkTotalFileSize) {
        this.chunkTotalFileSize = chunkTotalFileSize;
    }

    public boolean isLastChunk() {
        return lastChunk;
    }

    public void setLastChunk(boolean lastChunk) {
        this.lastChunk = lastChunk;
    }
}
