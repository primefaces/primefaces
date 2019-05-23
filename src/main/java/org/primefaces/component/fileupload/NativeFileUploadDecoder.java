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
import org.primefaces.util.FileUploadUtils;
import org.primefaces.virusscan.VirusException;

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

            if (!uploadedInputParts.isEmpty() && isValidFile(context, fileUpload, uploadedInputParts)) {
                fileUpload.setSubmittedValue(new UploadedFileWrapper(new NativeUploadedFile(uploadedInputParts, fileUpload)));
            }
            else {
                fileUpload.setSubmittedValue("");
            }
        }
        else {
            Part part = request.getPart(inputToDecodeId);

            if (part != null) {
                NativeUploadedFile uploadedFile = new NativeUploadedFile(part, fileUpload);
                if (isValidFile(context, fileUpload, uploadedFile)) {
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
            NativeUploadedFile uploadedFile = new NativeUploadedFile(part, fileUpload);
            if (isValidFile(context, fileUpload, uploadedFile)) {
                fileUpload.queueEvent(new FileUploadEvent(fileUpload, uploadedFile));
            }
        }
    }

    private static boolean isValidFile(FacesContext context, FileUpload fileUpload, NativeUploadedFile uploadedFile) throws IOException {
        boolean valid = (fileUpload.getSizeLimit() == null || uploadedFile.getSize() <= fileUpload.getSizeLimit()) && FileUploadUtils.isValidType(fileUpload,
                uploadedFile.getFileName(), uploadedFile.getInputstream());
        if (valid) {
            try {
                FileUploadUtils.performVirusScan(context, fileUpload, uploadedFile.getInputstream());
            }
            catch (VirusException ex) {
                return false;
            }
        }
        return valid;
    }

    private static boolean isValidFile(FacesContext context, FileUpload fileUpload, List<Part> parts) throws IOException {
        long totalPartSize = 0;
        for (int i = 0; i < parts.size(); i++) {
            Part p = parts.get(i);
            totalPartSize += p.getSize();
            NativeUploadedFile uploadedFile = new NativeUploadedFile(p, fileUpload);
            if (!FileUploadUtils.isValidType(fileUpload, uploadedFile.getFileName(), uploadedFile.getInputstream())) {
                return false;
            }
            try {
                FileUploadUtils.performVirusScan(context, fileUpload, uploadedFile.getInputstream());
            }
            catch (VirusException ex) {
                return false;
            }
        }

        return fileUpload.getSizeLimit() == null || totalPartSize <= fileUpload.getSizeLimit();
    }
}
