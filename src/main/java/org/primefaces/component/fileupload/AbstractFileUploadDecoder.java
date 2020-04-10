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
package org.primefaces.component.fileupload;

import org.primefaces.model.file.*;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractFileUploadDecoder<T extends ServletRequest> implements FileUploadDecoder {

    protected static final Pattern CONTENT_RANGE_PATTERN = Pattern.compile("^bytes (\\d+)-(\\d+)\\/(\\d+|\\*)$");

    @Override
    public void decode(FacesContext context, FileUpload fileUpload) {
        T request = getRequest(context);

        try {
            String inputToDecodeId = resolveInputToDecodeId(context, fileUpload);
            if (fileUpload.getMode().equals("simple")) {
                decodeSimple(fileUpload, request, inputToDecodeId);
            }
            else {
                decodeAdvanced(fileUpload, request, inputToDecodeId);
            }
        }
        catch (IOException | ServletException e) {
            throw new FacesException(e);
        }
    }

    protected void decodeSimple(FileUpload fileUpload, T request, String inputToDecodeId) throws IOException, ServletException {
        if (fileUpload.isMultiple()) {
            List<UploadedFile> files = createUploadedFiles(request, fileUpload, inputToDecodeId);

            if (!files.isEmpty()) {
                UploadedFiles uploadedFiles = new UploadedFiles(files);
                fileUpload.setSubmittedValue(new UploadedFilesWrapper(uploadedFiles));
            }
            else {
                fileUpload.setSubmittedValue("");
            }
        }
        else {
            UploadedFile uploadedFile = createUploadedFile(request, fileUpload, inputToDecodeId);
            if (uploadedFile != null) {
                fileUpload.setSubmittedValue(new UploadedFileWrapper(uploadedFile));
            }
            else {
                fileUpload.setSubmittedValue("");
            }
        }
    }

    protected void decodeAdvanced(FileUpload fileUpload, T request, String inputToDecodeId) throws IOException, ServletException {
        UploadedFile uploadedFile = createUploadedFile(request, fileUpload, inputToDecodeId);
        if (uploadedFile != null) {
            if (uploadedFile instanceof UploadedFileChunk) {
                fileUpload.setSubmittedValue(new UploadedFileChunkWrapper((UploadedFileChunk) uploadedFile));
            }
            else {
                fileUpload.setSubmittedValue(new UploadedFileWrapper(uploadedFile));
            }
        }
    }

    protected String resolveInputToDecodeId(FacesContext context, FileUpload fileUpload) {
        String clientId = fileUpload.getClientId(context);
        String mode = fileUpload.getMode();
        if ("advanced".equals(mode) || "simple".equals(mode) && !fileUpload.isSkinSimple()) {
            return clientId;
        }
        else {
            return clientId + "_input";
        }
    }

    protected abstract List<UploadedFile> createUploadedFiles(T request, FileUpload fileUpload, String inputToDecodeId) throws IOException, ServletException;

    protected abstract UploadedFile createUploadedFile(T request, FileUpload fileUpload, String inputToDecodeId) throws IOException, ServletException;

    protected void processContentRange(String contentRange, UploadedFileChunk uploadedFile) throws IOException {
        Matcher matcher = CONTENT_RANGE_PATTERN.matcher(contentRange);

        if (matcher.matches()) {
            uploadedFile.setChunkRangeBegin(Long.parseLong(matcher.group(1)));
            uploadedFile.setChunkRangeEnd(Long.parseLong(matcher.group(2)));
            uploadedFile.setChunkTotalFileSize(Long.parseLong(matcher.group(3)));
            if ((uploadedFile.getChunkRangeEnd() + 1) == uploadedFile.getChunkTotalFileSize()) {
                uploadedFile.setLastChunk(true);
            }
        }
        else {
            throw new IOException("Content-Range-Header does not match pattern '" + CONTENT_RANGE_PATTERN.pattern() + "'");
        }
    }

    protected abstract T getRequest(FacesContext ctxt);
}
