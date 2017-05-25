/*
 * Copyright 2009-2016 PrimeTek.
 *
 * Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.api;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public interface Pageable {
    
    public String getClientId(FacesContext context);
    
    public String getPaginatorPosition();
    
    public String getCurrentPageReportTemplate();

    public int getRows();

    public int getRowCount();

    public int getPage();

    public int getPageLinks();

    public boolean isPaginatorAlwaysVisible();

    public Object getFooter();

    public Object getHeader();

    public String getPaginatorTemplate();

    public UIComponent getFacet(String element);

    public int getPageCount();
    
    public int getFirst();

    public int getRowsToRender();

    public String getRowsPerPageTemplate();

    public String getRowsPerPageLabel();
}
