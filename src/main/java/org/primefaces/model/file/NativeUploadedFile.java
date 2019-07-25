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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.primefaces.shaded.owasp.SafeFile;
import org.primefaces.util.FileUploadUtils;

import javax.faces.FacesException;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URLDecoder;

public class NativeUploadedFile implements SingleUploadedFile, Serializable {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final String CONTENT_DISPOSITION_FILENAME_ATTR = "filename";

    private Part part;
    private String filename;
    private byte[] cachedContent;
    private Long sizeLimit;

    public NativeUploadedFile() {
    }

    public NativeUploadedFile(Part part, Long sizeLimit) {
        this.part = part;
        this.sizeLimit = sizeLimit;
        filename = resolveFilename(part);
    }

    @Override
    public String getFileName() {
        return filename;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return sizeLimit == null
                ? part.getInputStream()
                : new BoundedInputStream(part.getInputStream(), sizeLimit);
    }

    @Override
    public long getSize() {
        return part.getSize();
    }

    @Override
    public byte[] getContent() {
        if (cachedContent != null) {
            return cachedContent;
        }

        try (InputStream input = getInputStream()) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            cachedContent = output.toByteArray();
        }
        catch (IOException ex) {
            cachedContent = null;
            throw new FacesException(ex);
        }
        return cachedContent;
    }

    @Override
    public String getContentType() {
        return part.getContentType();
    }

    @Override
    public void write(String filePath) throws Exception {
        SafeFile file = new SafeFile(filePath);
        String validFileName = FileUploadUtils.getValidFilename(FilenameUtils.getName(file.getPath()));
        part.write(validFileName);
    }

    public Part getPart() {
        return part;
    }

    private String resolveFilename(Part part) {
        return FileUploadUtils.getValidFilename(getContentDispositionFileName(part.getHeader("content-disposition")));
    }

    protected String getContentDispositionFileName(final String line) {
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

    private String decode(String encoded) {
        try {
            // GitHub #3916 escape + and % before decode
            encoded = encoded.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            encoded = encoded.replaceAll("\\+", "%2B");
            return URLDecoder.decode(encoded, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new FacesException(ex);
        }
    }
}
