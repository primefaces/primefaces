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
package org.primefaces.component.fileupload;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.model.UploadedFileWrapper;
import org.primefaces.util.FileUploadUtils;
import org.primefaces.webapp.MultipartRequest;

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
            catch (IOException ioe) {
                throw new FacesException(ioe);
            }
        }
    }

    private static void decodeSimple(FacesContext context, FileUpload fileUpload, MultipartRequest request, String inputToDecodeId) throws IOException {
        FileItem file = request.getFileItem(inputToDecodeId);

        if (file != null && !file.getName().isEmpty()) {
            DefaultUploadedFile uploadedFile = new DefaultUploadedFile(file, fileUpload);
            if (isValidFile(fileUpload, uploadedFile)) {
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
            DefaultUploadedFile uploadedFile = new DefaultUploadedFile(file, fileUpload);
            if (isValidFile(fileUpload, uploadedFile)) {
                fileUpload.queueEvent(new FileUploadEvent(fileUpload, uploadedFile));
            }
        }
    }

    private static boolean isValidFile(FileUpload fileUpload, DefaultUploadedFile uploadedFile) throws IOException {
        return (fileUpload.getSizeLimit() == null || uploadedFile.getSize() <= fileUpload.getSizeLimit()) && FileUploadUtils.isValidType(fileUpload,
                uploadedFile.getFileName(), uploadedFile.getInputstream());
    }

}
