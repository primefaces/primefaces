/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.model.file.UploadedFile;

import java.io.File;
import java.io.IOException;

import jakarta.faces.FacesException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

public interface FileUploadChunkDecoder<T extends HttpServletRequest> {

    String MULTIPARTS = "org.primefaces.file.multiParts";

    default String generateFileInfoKey(T request) {
        String fileInfo = request.getParameter("X-File-Id");
        if (fileInfo == null) {
            throw new FacesException("Missing X-File-Id header");
        }

        return String.valueOf(fileInfo.hashCode());
    }

    default String getUploadDirectory(T request) {
        File tmpDir = (File) request.getServletContext().getAttribute(ServletContext.TEMPDIR);
        if (tmpDir == null) {
            tmpDir = new File(System.getenv("java.io.tmpdir"));
        }
        return tmpDir.getAbsolutePath();
    }

    void decodeContentRange(FileUpload fileUpload, T request, UploadedFile chunk) throws IOException;

    long decodeUploadedBytes(T request);

    void deleteChunks(T request) throws IOException;
}
