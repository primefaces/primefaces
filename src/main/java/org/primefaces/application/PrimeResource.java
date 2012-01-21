/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.application;

import javax.faces.application.Resource;
import javax.faces.application.ResourceWrapper;
import org.primefaces.util.Constants;

public class PrimeResource extends ResourceWrapper {

    private Resource resource;

    public PrimeResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Resource getWrapped() {
        return this.resource;
    }

    @Override
    public String getRequestPath() {
        return super.getRequestPath() + "&amp;v=" + Constants.VERSION;
    }

    @Override
    public String getContentType() {
        return getWrapped().getContentType();
    }

    @Override
    public String getLibraryName() {
        return getWrapped().getLibraryName();
    }

    @Override
    public String getResourceName() {
        return getWrapped().getResourceName();
    }

    @Override
    public void setContentType(String contentType) {
        getWrapped().setContentType(contentType);
    }

    @Override
    public void setLibraryName(String libraryName) {
        getWrapped().setLibraryName(libraryName);
    }

    @Override
    public void setResourceName(String resourceName) {
        getWrapped().setResourceName(resourceName);
    }

    @Override
    public String toString() {
        return getWrapped().toString();
    }
}