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
package org.primefaces.component.autoupdate;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.*;

public class AutoUpdateTagHandler extends TagHandler {

    private final TagAttribute disabledAttribute;

    public AutoUpdateTagHandler(TagConfig tagConfig) {
        super(tagConfig);

        disabledAttribute = getAttribute("disabled");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        if (disabledAttribute == null) {
            // enabled
            AutoUpdateListener.subscribe(parent);
        }
        else {
            if (disabledAttribute.isLiteral()) {
                // static
                if (!disabledAttribute.getBoolean(faceletContext)) {
                    // enabled
                    AutoUpdateListener.subscribe(parent);
                }
            }
            else {
                // dynamic
                AutoUpdateListener.subscribe(parent, disabledAttribute.getValueExpression(faceletContext, Boolean.class));
            }
        }
    }
}
