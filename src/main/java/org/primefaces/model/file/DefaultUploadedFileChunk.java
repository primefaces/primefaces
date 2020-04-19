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
