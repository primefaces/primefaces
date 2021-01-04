/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import javax.faces.context.FacesContext;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NativeFileUploadDecoder extends AbstractFileUploadDecoder<HttpServletRequest> {

    @Override
    public String getName() {
        return "native";
    }

    @Override
    protected List<UploadedFile> createUploadedFiles(HttpServletRequest request, FileUpload fileUpload, String inputToDecodeId)
            throws IOException, ServletException {
        Long sizeLimit = fileUpload.getSizeLimit();
        Iterable<Part> parts = request.getParts();
        return StreamSupport.stream(parts.spliterator(), false)
                .filter(p -> p.getName().equals(inputToDecodeId))
                .map(p -> new NativeUploadedFile(p, sizeLimit))
                .collect(Collectors.toList());
    }

    @Override
    protected UploadedFile createUploadedFile(HttpServletRequest request, FileUpload fileUpload, String inputToDecodeId)
            throws IOException, ServletException {
        Part part = request.getPart(inputToDecodeId);
        return new NativeUploadedFile(part, fileUpload.getSizeLimit());
    }

    @Override
    protected HttpServletRequest getRequest(FacesContext ctxt) {
        return (HttpServletRequest) ctxt.getExternalContext().getRequest();
    }

    @Override
    public String getUploadDirectory(HttpServletRequest request) {
        return  Stream.of(request.getAttributeNames())
                .map(o -> request.getAttribute(o.nextElement()))
                .filter(MultipartConfigElement.class::isInstance)
                .map(MultipartConfigElement.class::cast)
                .findFirst()
                .map(MultipartConfigElement::getLocation)
                .orElse(super.getUploadDirectory(request));
    }
}
