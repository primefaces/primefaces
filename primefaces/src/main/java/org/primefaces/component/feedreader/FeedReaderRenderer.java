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
package org.primefaces.component.feedreader;

import org.primefaces.model.feedreader.FeedItem;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;

public class FeedReaderRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        FeedReader reader = (FeedReader) component;

        try {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            String var = reader.getVar();
            int size = reader.getSize();
            String url = reader.getValue();
            List<FeedItem> entries = RSSUtils.parse(url, size, reader.isPodcast());

            for (FeedItem item : entries) {
                requestMap.put(var, item);
                renderChildren(context, reader);
            }

            requestMap.remove(var);

        }
        catch (Exception e) {
            logDevelopmentWarning(context, this, String.format("Unexpected RSS error: %s", e.getMessage()));
            UIComponent errorFacet = reader.getFacet("error");
            if (FacetUtils.shouldRenderFacet(errorFacet)) {
                errorFacet.encodeAll(context);
            }
        }
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
