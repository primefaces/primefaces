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
package org.primefaces.component.signature;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.Widget;

@FacesComponentBase
public abstract class SignatureBase extends AbstractPrimeHtmlInputText implements InputHolder, Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SignatureRenderer";

    public SignatureBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Background color of the signature canvas.")
    public abstract String getBackgroundColor();

    @Property(description = "Color of the signature stroke.")
    public abstract String getColor();

    @Property(description = "Thickness of the signature stroke.", defaultValue = "2")
    public abstract int getThickness();

    @Property(description = "When enabled, displays guideline for signature.", defaultValue = "false")
    public abstract boolean isGuideline();

    @Property(description = "Color of the guideline.")
    public abstract String getGuidelineColor();

    @Property(description = "Offset of the guideline from the top.", defaultValue = "25")
    public abstract int getGuidelineOffset();

    @Property(description = "Indent of the guideline.", defaultValue = "10")
    public abstract int getGuidelineIndent();

    @Property(description = "Base64 encoded value of the signature.")
    public abstract String getBase64Value();

    @Property(description = "Font family for text-based signatures.")
    public abstract String getFontFamily();

    @Property(description = "Font size for text-based signatures.", defaultValue = "40")
    public abstract int getFontSize();

    @Property(description = "ARIA label for accessibility.")
    public abstract String getAriaLabel();

    @Property(description = "Text value for text-based signatures.")
    public abstract String getTextValue();
}