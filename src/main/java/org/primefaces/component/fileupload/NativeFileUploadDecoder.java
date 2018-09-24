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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.NativeUploadedFile;
import org.primefaces.model.UploadedFileWrapper;

public class NativeFileUploadDecoder {

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
        catch (IOException ioe) {
            throw new FacesException(ioe);
        }
        catch (ServletException se) {
            throw new FacesException(se);
        }
    }

    private static void decodeSimple(FacesContext context, FileUpload fileUpload, HttpServletRequest request, String inputToDecodeId)
            throws IOException, ServletException {

        if (fileUpload.isMultiple()) {
            Iterable<Part> parts = request.getParts();
            List<Part> uploadedInputParts = new ArrayList<>();

            Iterator<Part> iterator = parts.iterator();
            while (iterator.hasNext()) {
                Part p = iterator.next();

                if (p.getName().equals(inputToDecodeId)) {
                    uploadedInputParts.add(p);
                }
            }

            if (uploadedInputParts.size() > 0 && isValidFile(fileUpload, uploadedInputParts)) {
                fileUpload.setSubmittedValue(new UploadedFileWrapper(new NativeUploadedFile(uploadedInputParts, fileUpload)));
            }
            else {
                fileUpload.setSubmittedValue("");
            }
        }
        else {
            Part part = request.getPart(inputToDecodeId);

            if (part != null && isValidFile(fileUpload, part)) {
                fileUpload.setSubmittedValue(new UploadedFileWrapper(new NativeUploadedFile(part, fileUpload)));
            }
            else {
                fileUpload.setSubmittedValue("");
            }
        }
    }

    private static void decodeAdvanced(FacesContext context, FileUpload fileUpload, HttpServletRequest request) throws IOException, ServletException {
        String clientId = fileUpload.getClientId(context);
        Part part = request.getPart(clientId);

        if (part != null && isValidFile(fileUpload, part)) {
            fileUpload.queueEvent(new FileUploadEvent(fileUpload, new NativeUploadedFile(part, fileUpload)));
        }
    }

    private static boolean isValidFile(FileUpload fileUpload, Part part) {
        // TODO some more checks could be performed here, e.g. allowed types
        return fileUpload.getSizeLimit() == null || part.getSize() <= fileUpload.getSizeLimit();
    }

    private static boolean isValidFile(FileUpload fileUpload, List<Part> parts) {
        long totalPartSize = 0;
        for (int i = 0; i < parts.size(); i++) {
            Part p = parts.get(i);
            totalPartSize += p.getSize();
        }

        return fileUpload.getSizeLimit() == null || totalPartSize <= fileUpload.getSizeLimit();
    }
}
