/*
 * Copyright 2009-2015 PrimeTek.
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
package org.primefaces.component.paginator;

import java.io.IOException;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.UIData;
import org.primefaces.util.MessageFactory;

public class NextPageLinkRenderer extends PageLinkRenderer implements PaginatorElementRenderer {

    public void render(FacesContext context, UIData uidata) throws IOException {
        int currentPage = uidata.getPage();
        int pageCount = uidata.getPageCount();
        
        boolean disabled = (currentPage == (pageCount - 1)) || (currentPage == 0 && pageCount == 0);
        
        String ariaMessage = MessageFactory.getMessage(UIData.ARIA_NEXT_PAGE_LABEL, new Object[]{});
       
        super.render(context, uidata, UIData.PAGINATOR_NEXT_PAGE_LINK_CLASS, UIData.PAGINATOR_NEXT_PAGE_ICON_CLASS, disabled, ariaMessage);
    }   
}
