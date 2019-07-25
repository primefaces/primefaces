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

import org.apache.commons.fileupload.FileItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.DefaultSingleUploadedFile;
import org.primefaces.model.file.SingleUploadedFile;
import org.primefaces.model.file.UploadedFileWrapper;
import org.primefaces.util.FileUploadUtils;
import org.primefaces.webapp.MultipartRequest;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequestWrapper;
import java.io.IOException;

public class CommonsFileUploadDecoder {

    private CommonsFileUploadDecoder() {
    }

    public static void decode(FacesContext context, FileUpload fileUpload, String inputToDecodeId) {
        MultipartRequest multipartRequest = null;
        Object request = context.getExternalContext().getRequest();

        while (request instanceof ServletRequestWrapper) {
            if (request instanceof MultipartRequest) {
                multipartRequest = (MultipartRequest) request;
                break;
            }
            else {
                request = ((ServletRequestWrapper) request).getRequest();
            }
        }

        if (multipartRequest != null) {
            try {
                if (fileUpload.getMode().equals("simple")) {
                    decodeSimple(context, fileUpload, multipartRequest, inputToDecodeId);
                }
                else {
                    decodeAdvanced(context, fileUpload, multipartRequest);
                }
            }
            catch (IOException e) {
                throw new FacesException(e);
            }
        }
    }

    private static void decodeSimple(FacesContext context, FileUpload fileUpload, MultipartRequest request, String inputToDecodeId) throws IOException {
        FileItem file = request.getFileItem(inputToDecodeId);

        if (file != null && !file.getName().isEmpty()) {
            SingleUploadedFile uploadedFile = new DefaultSingleUploadedFile(file, fileUpload.getSizeLimit());
            if (FileUploadUtils.isValidFile(context, fileUpload, uploadedFile)) {
                fileUpload.setSubmittedValue(new UploadedFileWrapper(uploadedFile));
            }
            else {
                fileUpload.setSubmittedValue("");
            }
        }
    }

    private static void decodeAdvanced(FacesContext context, FileUpload fileUpload, MultipartRequest request) throws IOException {
        String clientId = fileUpload.getClientId(context);
        FileItem file = request.getFileItem(clientId);

        if (file != null) {
            SingleUploadedFile uploadedFile = new DefaultSingleUploadedFile(file, fileUpload.getSizeLimit());
            if (FileUploadUtils.isValidFile(context, fileUpload, uploadedFile)) {
                fileUpload.queueEvent(new FileUploadEvent(fileUpload, uploadedFile));
            }
        }
    }
}
