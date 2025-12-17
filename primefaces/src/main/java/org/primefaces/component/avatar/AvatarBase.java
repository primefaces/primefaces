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
package org.primefaces.component.avatar;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
public abstract class AvatarBase extends UIComponentBase implements StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.AvatarRenderer";

    public AvatarBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Text to display inside the avatar.")
    public abstract String getLabel();

    @Property(description = "Icon of the avatar.")
    public abstract String getIcon();

    @Property(description = "Size of the avatar, valid options are \"large\" and \"xlarge\".")
    public abstract String getSize();

    @Property(defaultValue = "square", description = "Shape of the avatar, valid options are \"square\" and \"circle\".")
    public abstract String getShape();

    @Property(defaultValue = "false", description = "Whether to generate a dynamic color based on the label.")
    public abstract boolean isDynamicColor();

    @Property(description = "Title attribute of the avatar.")
    public abstract String getTitle();

    @Property(description = "Gravatar email address.")
    public abstract String getGravatar();

    @Property(description = "Gravatar configuration parameters.")
    public abstract String getGravatarConfig();

    @Property(defaultValue = "100", description = "Saturation value for dynamic color generation (0-100).")
    public abstract Integer getSaturation();

    @Property(defaultValue = "40", description = "Lightness value for dynamic color generation (0-100).")
    public abstract Integer getLightness();

    @Property(defaultValue = "100", description = "Alpha value for dynamic color generation (0-100).")
    public abstract Integer getAlpha();

}
