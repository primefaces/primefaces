/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.cdk.api.FacesTagHandler;
import org.primefaces.cdk.api.Property;

import java.io.IOException;

import jakarta.el.ELException;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.ActionSource;
import jakarta.faces.component.UIComponent;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.FaceletException;
import jakarta.faces.view.facelets.TagAttribute;
import jakarta.faces.view.facelets.TagConfig;
import jakarta.faces.view.facelets.TagException;
import jakarta.faces.view.facelets.TagHandler;

@FacesTagHandler("Input components keep their local values at state when validation fails." +
        " ResetInput is used to clear the cached values from state so that components retrieve their values from the backing bean model instead.")
public class ResetInputTagHandler extends TagHandler {

    @Property(description = "Comma or white-space separated list of component ids.", required = true)
    private final TagAttribute target;

    @Property(description = "Whether to assign null values to bound values as well.", type = Boolean.class)
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
