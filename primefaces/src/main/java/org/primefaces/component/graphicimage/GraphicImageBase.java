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
package org.primefaces.component.graphicimage;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;

import jakarta.faces.component.html.HtmlGraphicImage;

@FacesComponentBase
public abstract class GraphicImageBase extends HtmlGraphicImage {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.GraphicImageRenderer";

    public GraphicImageBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "false", description = "Enables/Disables browser from caching the image.")
    public abstract boolean isCache();

    @Property(description = "Name of the image.")
    public abstract String getName();

    @Property(description = "Library name of the image.")
    public abstract String getLibrary();

    @Property(defaultValue = "true", description = "Defines if the image is streamed or rendered directly as data uri / base64 with ViewScoped support.")
    public abstract boolean isStream();

    @Property(description = "One or more strings separated by commas, indicating possible image sources for the user agent to use.")
    public abstract String getSrcset();

    @Property(description = "One or more strings separated by commas, indicating a set of source sizes.")
    public abstract String getSizes();
}