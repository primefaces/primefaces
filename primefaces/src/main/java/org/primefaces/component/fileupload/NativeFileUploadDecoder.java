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

import org.primefaces.model.file.NativeUploadedFile;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.util.FileUploadUtils;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class NativeFileUploadDecoder extends AbstractFileUploadDecoder<HttpServletRequest> {

    @Override
    public String getName() {
        return "native";
    }

    @Override
    protected List<UploadedFile> createUploadedFiles(HttpServletRequest request, FileUpload fileUpload, String inputToDecodeId)
            throws IOException, ServletException {
        Long sizeLimit = fileUpload.getSizeLimit();
        List<UploadedFile> result = new ArrayList<>();
        for (Part p : request.getParts()) {
            if (p != null && p.getName().equals(inputToDecodeId) && LangUtils.isNotBlank(p.getSubmittedFileName())) {
                result.add(new NativeUploadedFile(p, sizeLimit, null));
            }
        }
        return result;
    }

    @Override
    protected UploadedFile createUploadedFile(HttpServletRequest request, FileUpload fileUpload, String inputToDecodeId)
            throws IOException, ServletException {
        Part part = request.getPart(inputToDecodeId);
        if (part == null || LangUtils.isBlank(part.getSubmittedFileName())) {
            return null;
        }

        return new NativeUploadedFile(part, fileUpload.getSizeLimit(), FileUploadUtils.getWebkitRelativePath(request));
    }

    @Override
    protected HttpServletRequest getRequest(FacesContext ctxt) {
        return (HttpServletRequest) ctxt.getExternalContext().getRequest();
    }

    @Override
    public String getUploadDirectory(HttpServletRequest request) {
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            Object attribute = request.getAttribute(attributeNames.nextElement());
            if (attribute instanceof MultipartConfigElement) {
                MultipartConfigElement multipart = (MultipartConfigElement) attribute;
                return multipart.getLocation();
            }
        }

        return super.getUploadDirectory(request);
    }
}
