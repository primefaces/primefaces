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

import java.io.*;
import java.net.URLDecoder;
import javax.faces.FacesException;
import javax.servlet.http.Part;

import org.primefaces.shaded.owasp.SafeFile;
import org.primefaces.util.FileUploadUtils;

public class NativeUploadedFile extends AbstractUploadedFile<Part> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String CONTENT_DISPOSITION_FILENAME_ATTR = "filename";

    public NativeUploadedFile() {
        // NOOP
    }

    public NativeUploadedFile(Part source, Long sizeLimit, String webKitRelativePath) {
        super(source, resolveFilename(source), sizeLimit, webKitRelativePath);
    }

    @Override
    public long getSize() {
        return getOriginalSource().getSize();
    }

    @Override
    public String getContentType() {
        return getOriginalSource().getContentType();
    }

    @Override
    public void delete() throws IOException {
        getOriginalSource().delete();
    }

    @Override
    protected InputStream getOriginalSourceInputStream() throws IOException {
        return getOriginalSource().getInputStream();
    }

    @Override
    protected void write(File file) throws IOException {
        getOriginalSource().write(file.getCanonicalPath());
    }

    /**
     * Use {@link Part#getSubmittedFileName()} instead
     */
    @Deprecated
    private static String resolveFilename(Part part) {
        if (part == null) {
            return null;
        }

        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }

        return FileUploadUtils.getValidFilename(getContentDispositionFileName(contentDisposition));
    }

    protected static String getContentDispositionFileName(final String line) {
        // skip to 'filename'
        int i = line.indexOf(CONTENT_DISPOSITION_FILENAME_ATTR);
        if (i == -1) {
            return null; // does not contain 'filename'
        }

        // skip past 'filename'
        i += CONTENT_DISPOSITION_FILENAME_ATTR.length();

        final int lineLength = line.length();

        // skip whitespace
        while (i < lineLength && Character.isWhitespace(line.charAt(i))) {
            i++;
        }

        // expect '='
        if (i == lineLength || line.charAt(i++) != '=') {
            throw new FacesException("Content-Disposition filename property did not have '='.");
        }

        // skip whitespace again
        while (i < lineLength && Character.isWhitespace(line.charAt(i))) {
            i++;
        }

        // expect '"'
        if (i == lineLength || line.charAt(i++) != '"') {
            throw new FacesException("Content-Disposition filename property was not quoted.");
        }

        // buffer to hold the file name
        final StringBuilder b = new StringBuilder();

        for (; i < lineLength; i++) {
            final char c = line.charAt(i);

            if (c == '"') {
                return decode(b.toString());
            }

            // only unescape double quote, leave all others as-is, but still skip 2 characters
            if (c == '\\' && i + 2 != lineLength) {
                char next = line.charAt(++i);
                if (next == '"') {
                    b.append('"');
                }
                else {
                    b.append(c);
                    b.append(next);
                }
            }
            else {
                b.append(c);
            }
        }

        return decode(b.toString());
    }

    private static String decode(String encoded) {
        try {
            // GitHub #3916 escape + and % before decode
            encoded = encoded.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            encoded = encoded.replace("+", "%2B");
            return URLDecoder.decode(encoded, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new FacesException(ex);
        }
    }
}
