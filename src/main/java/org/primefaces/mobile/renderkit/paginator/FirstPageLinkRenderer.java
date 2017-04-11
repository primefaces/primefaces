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
package org.primefaces.mobile.renderkit.paginator;

import java.io.IOException;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.Pageable;
import org.primefaces.component.paginator.PaginatorElementRenderer;

public class FirstPageLinkRenderer extends PageLinkRenderer implements PaginatorElementRenderer {

    public void render(FacesContext context, Pageable pageable) throws IOException {
        boolean disabled = pageable.getPage() == 0;
       
        super.render(context, pageable, "ui-paginator-first ui-btn ui-btn-icon-notext ui-icon-carat-l", disabled);
    }
    
}
