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
package org.primefaces.component.collector;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.*;

public class CollectorTagHandler extends TagHandler {

    private final TagAttribute addTo;
    private final TagAttribute removeFrom;
    private final TagAttribute value;
    private final TagAttribute unique;

    public CollectorTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        addTo = getAttribute("addTo");
        removeFrom = getAttribute("removeFrom");
        value = getRequiredAttribute("value");
        unique = getAttribute("unique");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        ValueExpression addToVE = null;
        ValueExpression removeFromVE = null;
        ValueExpression uniqueVE = null;

        if (addTo != null) {
            addToVE = addTo.getValueExpression(faceletContext, Object.class);
        }

        if (removeFrom != null) {
            removeFromVE = removeFrom.getValueExpression(faceletContext, Object.class);
        }

        if (unique != null) {
            uniqueVE = unique.getValueExpression(faceletContext, Object.class);
        }

        ValueExpression valueVE = value.getValueExpression(faceletContext, Object.class);

        ActionSource actionSource = (ActionSource) parent;
        actionSource.addActionListener(new Collector(addToVE, removeFromVE, valueVE, uniqueVE));
    }
}
