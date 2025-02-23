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
package org.primefaces.metadata.transformer;

import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.context.PrimeApplicationContext;

import java.io.IOException;

import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.html.HtmlInputSecret;
import jakarta.faces.component.html.HtmlInputText;
import jakarta.faces.context.FacesContext;

public abstract class AbstractInputMetadataTransformer implements MetadataTransformer {

    @Override
    public void transform(FacesContext context, PrimeApplicationContext applicationContext, UIComponent component) throws IOException {
        if (component instanceof EditableValueHolder && component instanceof UIInput) {
            transformInput(context, applicationContext, (UIInput) component);
        }
    }

    protected abstract void transformInput(FacesContext context, PrimeApplicationContext applicationContext, UIInput component) throws IOException;

    protected void setMaxlength(UIInput input, int maxlength) {
        if (input instanceof HtmlInputText) {
            ((HtmlInputText) input).setMaxlength(maxlength);
        }
        else if (input instanceof HtmlInputSecret) {
            ((HtmlInputSecret) input).setMaxlength(maxlength);
        }
        else if (input instanceof InputTextarea) {
            ((InputTextarea) input).setMaxlength(maxlength);
        }
    }

    protected int getMaxlength(UIInput input) {
        if (input instanceof HtmlInputText) {
            return ((HtmlInputText) input).getMaxlength();
        }
        else if (input instanceof HtmlInputSecret) {
            return ((HtmlInputSecret) input).getMaxlength();
        }
        else if (input instanceof InputTextarea) {
            ((InputTextarea) input).getMaxlength();
        }

        return Integer.MIN_VALUE;
    }

    protected boolean isMaxlengthSet(UIInput input) {
        return getMaxlength(input) != Integer.MIN_VALUE;
    }

}
