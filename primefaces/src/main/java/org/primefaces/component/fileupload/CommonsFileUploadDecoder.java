/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.primefaces.model.file.CommonsUploadedFile;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.util.FileUploadUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.webapp.MultipartRequest;

/**
 * @deprecated Use {@link NativeFileUploadDecoder} instead
 */
@Deprecated(forRemoval = true, since = "14.0.0")
public class CommonsFileUploadDecoder extends AbstractFileUploadDecoder<MultipartRequest> {

    @Override
    public String getName() {
        return "commons";
    }

    @Override
    protected List<UploadedFile> createUploadedFiles(MultipartRequest request, FileUpload fileUpload, String inputToDecodeId) {
        Long sizeLimit = fileUpload.getSizeLimit();
        return request.getFileItems(inputToDecodeId).stream()
                .filter(p -> LangUtils.isNotBlank(p.getName()))
                .map(p -> new CommonsUploadedFile(p, sizeLimit, null))
                .collect(Collectors.toList());
    }

    @Override
    protected UploadedFile createUploadedFile(MultipartRequest request, FileUpload fileUpload, String inputToDecodeId) {
        FileItem file = request.getFileItem(inputToDecodeId);
        if (file == null || LangUtils.isBlank(file.getName())) {
            return null;
        }

        return new CommonsUploadedFile(file, fileUpload.getSizeLimit(), FileUploadUtils.getWebkitRelativePath(request));
    }

    @Override
    protected MultipartRequest getRequest(FacesContext ctxt) {
        MultipartRequest multipartRequest = null;
        Object request = ctxt.getExternalContext().getRequest();
        while (request instanceof ServletRequestWrapper) {
            if (request instanceof MultipartRequest) {
                multipartRequest = (MultipartRequest) request;
                break;
            }
            else {
                request = ((ServletRequestWrapper) request).getRequest();
            }
        }
        if (multipartRequest == null) {
            throw new FacesException("HTTP request is no " + MultipartRequest.class.getName() + ". "
                    + "Uploader 'commons' requires configuration of servlet filter 'org.primefaces.webapp.filter.FileUploadFilter'. "
                    + "Also make sure to enable multi part by setting enctype to your form (e.g. <h:form enctype=\"multipart/form-data\">... </h:form>");
        }
        return multipartRequest;
    }

    @Override
    public String getUploadDirectory(MultipartRequest request) {
        File uploadDir = request.getUploadDirectory();
        if (uploadDir == null) {
            return super.getUploadDirectory(request);
        }
        return uploadDir.getAbsolutePath();
    }
}
