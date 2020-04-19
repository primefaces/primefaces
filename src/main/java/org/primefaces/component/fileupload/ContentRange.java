package org.primefaces.component.fileupload;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentRange {

    private static final Pattern CONTENT_RANGE_PATTERN = Pattern.compile("^bytes (\\d+)-(\\d+)/(\\d+|\\*)$");

    private long chunkRangeBegin;
    private long chunkRangeEnd;
    private long chunkTotalFileSize;
    private boolean lastChunk;

    private ContentRange(long chunkRangeBegin, long chunkRangeEnd, long chunkTotalFileSize) {
        this.chunkRangeBegin = chunkRangeBegin;
        this.chunkRangeEnd = chunkRangeEnd;
        this.chunkTotalFileSize = chunkTotalFileSize;
        this.lastChunk = chunkRangeEnd + 1 == chunkTotalFileSize;
    }

    public static final ContentRange of(String contentRange) {
        Matcher matcher = CONTENT_RANGE_PATTERN.matcher(contentRange);
        if (matcher.find()) {
            return new ContentRange(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)));
        }

        throw new IllegalArgumentException("Content-Range does not follow pattern: " + CONTENT_RANGE_PATTERN.pattern());
    }

    public long getChunkRangeBegin() {
        return chunkRangeBegin;
    }

    public long getChunkRangeEnd() {
        return chunkRangeEnd;
    }

    public long getChunkTotalFileSize() {
        return chunkTotalFileSize;
    }

    public boolean isLastChunk() {
        return lastChunk;
    }
}
