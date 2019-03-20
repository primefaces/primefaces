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
package org.primefaces.component.resetinput;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.*;

public class ResetInputTagHandler extends TagHandler {

    private final TagAttribute target;
    private final TagAttribute clearModel;

    public ResetInputTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        target = getRequiredAttribute("target");
        clearModel = getAttribute("clearModel");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (parent == null || !ComponentHandler.isNew(parent)) {
            return;
        }

        if (parent instanceof ActionSource) {
            ValueExpression targetVE = target.getValueExpression(faceletContext, Object.class);
            ValueExpression clearModelVE = clearModel != null ? clearModel.getValueExpression(faceletContext, Boolean.class) : null;

            ActionSource actionSource = (ActionSource) parent;
            actionSource.addActionListener(new ResetInputActionListener(targetVE, clearModelVE));
        }
        else {
            throw new TagException(tag, "ResetInput can only be attached to ActionSource components.");
        }
    }

}
