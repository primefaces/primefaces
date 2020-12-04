/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.component.paginator;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.UIPageableData;
import org.primefaces.util.MessageFactory;

public class FirstPageLinkRenderer extends PageLinkRenderer implements PaginatorElementRenderer {

    @Override
    public void render(FacesContext context, Pageable pageable) throws IOException {
        boolean disabled = pageable.getPage() == 0;

        String ariaMessage = MessageFactory.getMessage(UIPageableData.ARIA_FIRST_PAGE_LABEL);

        super.render(context, pageable, UIPageableData.PAGINATOR_FIRST_PAGE_LINK_CLASS, UIPageableData.PAGINATOR_FIRST_PAGE_ICON_CLASS, disabled, ariaMessage);
    }
}
