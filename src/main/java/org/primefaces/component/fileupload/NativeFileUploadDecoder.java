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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.*;
import org.primefaces.util.FileUploadUtils;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NativeFileUploadDecoder {

    private NativeFileUploadDecoder() {
    }

    public static void decode(FacesContext context, FileUpload fileUpload, String inputToDecodeId) {
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            if (fileUpload.getMode().equals("simple")) {
                decodeSimple(context, fileUpload, request, inputToDecodeId);
            }
            else {
                decodeAdvanced(context, fileUpload, request);
            }
        }
        catch (IOException | ServletException e) {
            throw new FacesException(e);
        }
    }

    private static void decodeSimple(FacesContext context, FileUpload fileUpload, HttpServletRequest request, String inputToDecodeId)
            throws IOException, ServletException {

        if (fileUpload.isMultiple()) {
            Long sizeLimit = fileUpload.getSizeLimit();
            Iterable<Part> parts = request.getParts();
            List<SingleUploadedFile> files = StreamSupport.stream(parts.spliterator(), false)
                    .filter(p -> p.getName().equals(inputToDecodeId))
                    .map(p -> new NativeUploadedFile(p, sizeLimit))
                    .collect(Collectors.toList());

            if (!files.isEmpty() && FileUploadUtils.areValidFiles(context, fileUpload, files)) {
                UploadedFile wrapped = new MultipleUploadedFile(files);
                fileUpload.setSubmittedValue(new UploadedFileWrapper(wrapped));
            }
            else {
                fileUpload.setSubmittedValue("");
            }
        }
        else {
            Part part = request.getPart(inputToDecodeId);

            if (part != null) {
                NativeUploadedFile uploadedFile = new NativeUploadedFile(part, fileUpload.getSizeLimit());
                if (FileUploadUtils.isValidFile(context, fileUpload, uploadedFile)) {
                    fileUpload.setSubmittedValue(new UploadedFileWrapper(uploadedFile));
                }
            }
            else {
                fileUpload.setSubmittedValue("");
            }
        }
    }

    private static void decodeAdvanced(FacesContext context, FileUpload fileUpload, HttpServletRequest request) throws IOException, ServletException {
        String clientId = fileUpload.getClientId(context);
        Part part = request.getPart(clientId);

        if (part != null) {
            NativeUploadedFile uploadedFile = new NativeUploadedFile(part, fileUpload.getSizeLimit());
            if (FileUploadUtils.isValidFile(context, fileUpload, uploadedFile)) {
                fileUpload.queueEvent(new FileUploadEvent(fileUpload, uploadedFile));
            }
        }
    }
}
