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
package org.primefaces.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.faces.FacesWrapper;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

/**
 * Wrapper to avoid a UploadedFile to beeing saved in the ViewState.
 */
public class UploadedFileWrapper implements UploadedFile, FacesWrapper<UploadedFile>, StateHolder {

    private UploadedFile wrapped;

    public UploadedFileWrapper() {

    }

    public UploadedFileWrapper(UploadedFile wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getFileName() {
        return getWrapped().getFileName();
    }

    @Override
    public List<String> getFileNames() {
        return getWrapped().getFileNames();
    }

    @Override
    public InputStream getInputstream() throws IOException {
        return getWrapped().getInputstream();
    }

    @Override
    public long getSize() {
        return getWrapped().getSize();
    }

    @Override
    public byte[] getContents() {
        return getWrapped().getContents();
    }

    @Override
    public String getContentType() {
        return getWrapped().getContentType();
    }

    @Override
    public void write(String filePath) throws Exception {
        getWrapped().write(filePath);
    }

    @Override
    public UploadedFile getWrapped() {
        return wrapped;
    }

    @Override
    public Object saveState(FacesContext fc) {
        return null;
    }

    @Override
    public void restoreState(FacesContext fc, Object o) {

    }

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public void setTransient(boolean value) {

    }

}
