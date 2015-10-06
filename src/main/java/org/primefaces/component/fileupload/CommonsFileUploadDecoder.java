/*
 * Copyright 2009-2014 PrimeTek.
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

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequestWrapper;
import org.apache.commons.fileupload.FileItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.model.UploadedFileWrapper;
import org.primefaces.webapp.MultipartRequest;

public class CommonsFileUploadDecoder{

    public static void decode(FacesContext context, FileUpload fileUpload, String inputToDecodeId) {
        MultipartRequest multipartRequest = null;
        Object request = context.getExternalContext().getRequest();

        while(request instanceof ServletRequestWrapper) {
            if(request instanceof MultipartRequest) {
                    multipartRequest = (MultipartRequest) request;
                break;
            }
            else {
                request = ((ServletRequestWrapper) request).getRequest();
            }
        }

        if(multipartRequest != null) {
            if(fileUpload.getMode().equals("simple")) {
                decodeSimple(context, fileUpload, multipartRequest, inputToDecodeId);
            }
            else {
                decodeAdvanced(context, fileUpload, multipartRequest);
            }
        }
    }

    private static void decodeSimple(FacesContext context, FileUpload fileUpload, MultipartRequest request, String inputToDecodeId) {
        FileItem file = request.getFileItem(inputToDecodeId);

        if(file != null) {
            if(file.getName().equals("")) {
                fileUpload.setSubmittedValue("");
            } else {
                fileUpload.setSubmittedValue(new UploadedFileWrapper(new DefaultUploadedFile(file)));
            }
        }
    }

    private static void decodeAdvanced(FacesContext context, FileUpload fileUpload, MultipartRequest request) {
        String clientId = fileUpload.getClientId(context);
        FileItem file = request.getFileItem(clientId);

        if(file != null) {
            fileUpload.queueEvent(new FileUploadEvent(fileUpload, new DefaultUploadedFile(file)));
        }
    }
}
