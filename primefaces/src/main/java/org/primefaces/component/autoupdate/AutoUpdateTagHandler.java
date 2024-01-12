/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.*;
import org.primefaces.util.LangUtils;
import org.primefaces.util.Lazy;

public class AutoUpdateTagHandler extends TagHandler {

    private static final Logger LOGGER = Logger.getLogger(AutoUpdateTagHandler.class.getName());

    private final TagAttribute disabledAttribute;
    private final TagAttribute onAttribute;

    private Lazy<ProjectStage> projectStage = new Lazy<>(() -> FacesContext.getCurrentInstance().getApplication().getProjectStage());

    public AutoUpdateTagHandler(TagConfig tagConfig) {
        super(tagConfig);

        disabledAttribute = getAttribute("disabled");
        onAttribute = getAttribute("on");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        if (LangUtils.isBlank(parent.getRendererType())) {
            if (projectStage.get() == ProjectStage.Development) {
                LOGGER.log(Level.WARNING, "Can not auto-update component \"{0}\" with id \"{1}\" without an attached renderer.",
                        new Object[] {parent.getClass().getName(), parent.getClientId()});
            }
            return;
        }

        String on = null;
        if (onAttribute != null) {
            on = onAttribute.getValue(faceletContext);
        }

        if (disabledAttribute == null) {
            // enabled
            AutoUpdateListener.subscribe(parent, on);
        }
        else {
            if (disabledAttribute.isLiteral()) {
                // static
                if (!disabledAttribute.getBoolean(faceletContext)) {
                    // enabled
                    AutoUpdateListener.subscribe(parent, on);
                }
            }
            else {
                // dynamic
                AutoUpdateListener.subscribe(parent,
                        disabledAttribute.getValueExpression(faceletContext, Boolean.class),
                        on);
            }
        }
    }
}
