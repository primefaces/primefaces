/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.datatable;

import java.util.Locale;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;
import org.primefaces.component.api.UIData;
import org.primefaces.facelets.MethodRule;

public class DataTableHandler extends ComponentHandler {

    private static final MetaRule SORT_FUNCTION
            = new MethodRule("sortFunction", Integer.class, new Class[]{Object.class, Object.class});

    private static final MetaRule DRAGGABLE_ROWS_FUNCTION
            = new MethodRule("draggableRowsFunction", null, new Class[]{UIData.class});

    private static final MetaRule GLOBAL_FILTER_FUNCTION
            = new MethodRule("globalFilterFunction", Boolean.class, new Class[]{Object.class, Object.class, Locale.class});

    public DataTableHandler(ComponentConfig config) {
        super(config);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset metaRuleset = super.createMetaRuleset(type);

        metaRuleset.addRule(SORT_FUNCTION);
        metaRuleset.addRule(DRAGGABLE_ROWS_FUNCTION);
        metaRuleset.addRule(GLOBAL_FILTER_FUNCTION);

        return metaRuleset;
    }
}