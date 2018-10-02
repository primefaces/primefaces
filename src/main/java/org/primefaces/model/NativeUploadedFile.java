/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.model;

import org.primefaces.component.fileupload.FileUpload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import javax.faces.FacesException;
import javax.servlet.http.Part;
import org.apache.commons.io.input.BoundedInputStream;
import org.primefaces.util.FileUploadUtils;

public class NativeUploadedFile implements UploadedFile, Serializable {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final String FILENAME = "filename";

    private Part part;
    private String filename;
    private byte[] cachedContent;
    private Long sizeLimit;
    private List<Part> parts;
    private List<String> filenames;

    public NativeUploadedFile() {
    }

    public NativeUploadedFile(Part part, FileUpload fileUpload) {
        this.part = part;
        this.filename = resolveFilename(part);
        this.sizeLimit = fileUpload.getSizeLimit();
    }

    public NativeUploadedFile(List<Part> parts, FileUpload fileUpload) {
        this.parts = parts;
        this.filenames = resolveFilenames(parts);
        this.sizeLimit = fileUpload.getSizeLimit();
    }

    @Override
    public String getFileName() {
        return filename;
    }

    @Override
    public List<String> getFileNames() {
        return filenames;
    }

    @Override
    public InputStream getInputstream() throws IOException {
        return sizeLimit == null ? part.getInputStream() : new BoundedInputStream(part.getInputStream(), sizeLimit);
    }

    @Override
    public long getSize() {
        return part.getSize();
    }

    @Override
    public byte[] getContents() {
        if (cachedContent != null) {
            return cachedContent;
        }

        InputStream input = null;
        try {
            input = getInputstream();
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
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException ex) {
                }
            }
        }

        return cachedContent;
    }

    @Override
    public String getContentType() {
        return part.getContentType();
    }

    @Override
    public void write(String filePath) throws Exception {
        String validFilePath = FileUploadUtils.getValidFilePath(filePath);

        if (parts != null) {
            for (int i = 0; i < parts.size(); i++) {
                Part p = parts.get(i);
                p.write(validFilePath);
            }
        }
        else {
            part.write(validFilePath);
        }
    }

    public Part getPart() {
        return part;
    }

    private String resolveFilename(Part part) {
        return FileUploadUtils.getValidFilename(getContentDispositionFileName(part.getHeader("content-disposition")));
    }

    private List<String> resolveFilenames(List<Part> parts) {
        filenames = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            Part p = parts.get(i);
            filenames.add(resolveFilename(p));
        }

        return filenames;
    }

    protected String getContentDispositionFileName(final String line) {
        // skip to 'filename'
        int i = line.indexOf(FILENAME);
        if (i == -1) {
            return null; // does not contain 'filename'
        }

        // skip past 'filename'
        i += FILENAME.length();

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
