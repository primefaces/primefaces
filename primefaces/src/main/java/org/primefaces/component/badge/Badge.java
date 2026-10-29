/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.badge;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.model.badge.BadgeModel;
import org.primefaces.model.badge.DefaultBadgeModel;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;

@FacesComponent(value = Badge.COMPONENT_TYPE, namespace = Badge.COMPONENT_FAMILY)
@FacesComponentInfo(description = "Badge is a small status indicator for another element like a button or a link.")
@ResourceDependency(library = "primefaces", name = "components.css")
public class Badge extends BadgeBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Badge";

    public static final String STYLE_CLASS = "p-badge p-widget";
    public static final String OVERLAY_CLASS = "p-overlay-badge";
    public static final String NO_GUTTER_CLASS = "p-badge-no-gutter";
    public static final String DOT_CLASS = "p-badge-dot";
    public static final String SIZE_LARGE_CLASS = "p-badge-lg";
    public static final String SIZE_XLARGE_CLASS = "p-badge-xl";
    public static final String SEVERITY_INFO_CLASS = "p-badge-info";
    public static final String SEVERITY_SUCCESS_CLASS = "p-badge-success";
    public static final String SEVERITY_WARNING_CLASS = "p-badge-warning";
    public static final String SEVERITY_DANGER_CLASS = "p-badge-danger";
    public static final String SEVERITY_SECONDARY_CLASS = "p-badge-secondary";
    public static final String SEVERITY_HELP_CLASS = "p-badge-help";
    public static final String LABEL_CLASS = "p-badge-label";
    public static final String ICON_CLASS = "p-badge-icon";

    public BadgeRenderer getRenderer() {
        return (BadgeRenderer) getFacesContext().getRenderKit().getRenderer(getFamily(), getRendererType());
    }

    public static Badge create(FacesContext context) {
        return (Badge) context.getApplication().createComponent(Badge.COMPONENT_TYPE);
    }

    public BadgeModel toBadgeModel() {
        return DefaultBadgeModel.builder()
                .value(getValue())
                .severity(getSeverity())
                .size(getSize())
                .style(getStyle())
                .styleClass(getStyleClass())
                .icon(getIcon())
                .iconPos(getIconPos())
                .onClick(getOnclick())
                .visible(isVisible())
                .build();
    }

    public static BadgeModel getBadgeModel(Object object) {
        if (object instanceof BadgeModel model) {
            return model;
        }
        if (object instanceof String string) {
            return DefaultBadgeModel.builder()
                    .value(string)
                    .build();
        }
        return null;
    }

}
