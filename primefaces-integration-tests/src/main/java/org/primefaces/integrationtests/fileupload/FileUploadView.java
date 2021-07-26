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
package org.primefaces.integrationtests.fileupload;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import lombok.Data;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.model.file.UploadedFile;

@Named
@ViewScoped
@Data
public class FileUploadView implements Serializable {
    private static final long serialVersionUID = 1L;

    // Assume the file upload component always has the same id in all views
    private static final String FILE_UPLOAD_ID = "form:fileupload";

    private List<UploadFile> uploadedFiles = Collections.synchronizedList(new ArrayList<>());
    private String _allowTypes;

    // Note: this method may be called in parallel threads
    public void doFileUpload(UploadedFile uf) {
        UploadFile file = new UploadFile(uf);
        try {
            readFile(uf);
        }
        catch (Exception ex) {
            file.setErrorMessage(ex.getMessage());
        }
        uploadedFiles.add(file);
    }

    private void readFile(UploadedFile uf) throws IOException {
        byte[] buf = new byte[(int) uf.getSize() + 1];
        int len = uf.getInputStream().read(buf);
        if (Math.max(0, len) != (int) uf.getSize()) {
            throw new IllegalStateException("unexpected file size " + uf.getSize() + " <> " + len);
        }
        if (!isAllowedType(uf.getFileName())) {
            throw new IllegalStateException("unexpected file type " + uf.getFileName());
        }
    }

    private boolean isAllowedType(String fileName) {
        String allowTypes = getAllowTypes();
        return allowTypes == null || allowTypes.length() < 3 || // JS RegExp has at least 3 chars
                Pattern.compile(allowTypes.substring(1, allowTypes.length() - 1))
                         .matcher(fileName).find();
    }

    private String getAllowTypes() {
        // some tests break in Mojarra when binding is used
        // search the FileUpload component by its ID
        if (_allowTypes == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            Object component = ctx.getViewRoot().findComponent(FILE_UPLOAD_ID);
            if (component instanceof FileUpload) {
                _allowTypes = ((FileUpload) component).getAllowTypes();
            }
        }
        return _allowTypes;
    }

    @Data
    public static final class UploadFile implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String fileName;
        private final int size;
        private String errorMessage;

        public UploadFile(UploadedFile file) {
            this.fileName = file.getFileName();
            this.size = (int) file.getSize();
        }
    }
}
