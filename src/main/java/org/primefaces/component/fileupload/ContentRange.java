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
package org.primefaces.component.fileupload;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentRange {

    private static final Pattern CONTENT_RANGE_PATTERN = Pattern.compile("^bytes (\\d+)-(\\d+)/(\\d+|\\*)$");

    private long chunkRangeBegin;
    private long chunkRangeEnd;
    private long chunkTotalFileSize;
    private boolean lastChunk;
    private long packet;

    private ContentRange(long chunkRangeBegin, long chunkRangeEnd, long chunkTotalFileSize, long chunkSize) {
        this.chunkRangeBegin = chunkRangeBegin;
        this.chunkRangeEnd = chunkRangeEnd;
        this.chunkTotalFileSize = chunkTotalFileSize;
        this.lastChunk = chunkRangeEnd + 1 == chunkTotalFileSize;
        this.packet = chunkRangeBegin / chunkSize;
    }

    public static final ContentRange of(String contentRange, long chunkSize) {
        Matcher matcher = CONTENT_RANGE_PATTERN.matcher(contentRange);
        if (matcher.find()) {
            return new ContentRange(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)), chunkSize);
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

    public long getPacket() {
        return packet;
    }
}
