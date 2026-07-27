/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.component.api.Pageable;

import java.io.IOException;
import java.util.Objects;

import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

public interface PaginatorElementRenderer {

    /**
     * Key under which the currently rendered paginator position ({@code "top"} or {@code "bottom"}) is stored in the
     * {@link FacesContext} attributes while a paginator is being encoded, so element renderers can build unique ids.
     */
    String PAGINATOR_POSITION_KEY = "primefaces.paginator.Position";

    void render(FacesContext context, Pageable pageable) throws IOException;

    /**
     * Builds a stable id for an interactive paginator element so AJAX focus restoration can re-focus it. The plain
     * {@code clientId + suffix} is used by default; only when the paginator is rendered at both positions is the
     * {@code top}/{@code bottom} position appended to keep the id unique within the document.
     *
     * @param context the current {@link FacesContext}
     * @param pageable the paginator being rendered
     * @param suffix the element-specific suffix (e.g. {@code "jumpToPage"})
     * @return the id to render on the element
     */
    default String buildId(FacesContext context, Pageable pageable, String suffix) {
        char separator = UINamingContainer.getSeparatorChar(context);
        String id = pageable.getClientId(context) + separator + suffix;

        if ("both".equalsIgnoreCase(pageable.getPaginatorPosition())) {
            String position = Objects.toString(context.getAttributes().get(PAGINATOR_POSITION_KEY), null);
            if (position != null) {
                id += "_" + position;
            }
        }

        return id;
    }
}
