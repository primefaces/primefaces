/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.treetable.feature;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.TreeTableRenderer;
import org.primefaces.model.TreeNode;

public class PageFeature implements TreeTableFeature {

    private static final PageFeature INSTANCE = new PageFeature();

    private PageFeature() {
    }

    public static PageFeature getInstance() {
        return INSTANCE;
    }

    @Override
    public void decode(FacesContext context, TreeTable table) {
        throw new FacesException("PageFeature should not encode.");
    }

    @Override
    public void encode(FacesContext context, TreeTableRenderer renderer, TreeTable tt) throws IOException {
        TreeNode root = tt.getValue();
        tt.updatePaginationData(context);
        renderer.encodeNodeChildren(context, tt, root, root, tt.getFirst(), tt.getRows());
    }

    @Override
    public boolean shouldDecode(FacesContext context, TreeTable table) {
        return false;
    }

    @Override
    public boolean shouldEncode(FacesContext context, TreeTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_pagination");
    }

}
