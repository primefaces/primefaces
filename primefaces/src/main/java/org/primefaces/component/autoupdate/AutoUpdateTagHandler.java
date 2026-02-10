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
package org.primefaces.component.autoupdate;

import org.primefaces.cdk.api.FacesTagHandler;
import org.primefaces.cdk.api.Property;
import org.primefaces.util.LangUtils;
import org.primefaces.util.Lazy;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.el.ELException;
import jakarta.faces.FacesException;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.FaceletException;
import jakarta.faces.view.facelets.TagAttribute;
import jakarta.faces.view.facelets.TagConfig;
import jakarta.faces.view.facelets.TagHandler;

@FacesTagHandler("AutoUpdate is a TagHandler to mark the parent component to be updated at every AJAX request."
        + " AutoUpdate is ignored if the trigger (p:commandButton, p:ajax...) of AJAX request has the attribute ignoreAutoUpdate set to 'true'")
public class AutoUpdateTagHandler extends TagHandler {

    private static final Logger LOGGER = Logger.getLogger(AutoUpdateTagHandler.class.getName());

    @Property(description = "If autoUpdate should be disabled.", type = Boolean.class, defaultValue = "false")
    private final TagAttribute disabled;

    @Property(description = "Defines the observer event, which will trigger the auto update.", type = String.class)
    private final TagAttribute on;

    private Lazy<ProjectStage> projectStage = new Lazy<>(() -> FacesContext.getCurrentInstance().getApplication().getProjectStage());

    public AutoUpdateTagHandler(TagConfig tagConfig) {
        super(tagConfig);

        disabled = getAttribute("disabled");
        on = getAttribute("on");
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
        if (this.on != null) {
            on = this.on.getValue(faceletContext);
        }

        if (disabled == null) {
            // enabled
            AutoUpdateListener.subscribe(parent, on);
        }
        else {
            if (disabled.isLiteral()) {
                // static
                if (!disabled.getBoolean(faceletContext)) {
                    // enabled
                    AutoUpdateListener.subscribe(parent, on);
                }
            }
            else {
                // dynamic
                AutoUpdateListener.subscribe(parent,
                        disabled.getValueExpression(faceletContext, Boolean.class),
                        on);
            }
        }
    }
}
