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
package org.primefaces.component.api;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;

import jakarta.faces.component.html.HtmlInputText;

/**
 * Extended {@link HtmlInputText} to allow for new events such as "input" and "paste".
 * Remove if Faces 5.0+ ever implements these events.
 */
@FacesComponentBase
public abstract class AbstractPrimeHtmlInputText extends HtmlInputText implements InputAware {

    @Property(description = "Alternate textual description of the input field.")
    public abstract String getAlt();

    @Property(description = "Controls browser autocomplete behavior. Possible values are 'on', 'off', and 'new-password'.")
    public abstract String getAutocomplete();

    @Property(description = "Number of characters used to determine the width of the input element.", defaultValue = "-2147483648")
    public abstract int getSize();

    @Property(description = "Input field type.", defaultValue = "text")
    public abstract String getType();

}
