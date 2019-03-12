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
package org.primefaces.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;

/**
 * Helper to generate javascript code of a client side validation*
 */
public class CSVBuilder {

    protected StringBuilder buffer;
    protected FacesContext context;

    public CSVBuilder(FacesContext context) {
        this.context = context;
        this.buffer = new StringBuilder();
    }

    public CSVBuilder init() {
        buffer.append("if(PrimeFaces.vb({");
        return this;
    }

    public CSVBuilder source(String source) {
        if (source == null || source.equals("this")) {
            buffer.append("s:").append("this");
        }
        else {
            buffer.append("s:").append("'").append(source).append("'");
        }

        return this;
    }

    public CSVBuilder ajax(boolean value) {
        if (value) {
            buffer.append(",a:").append("true");
        }

        return this;
    }

    public CSVBuilder process(UIComponent component, String expressions) {
        if (!LangUtils.isValueBlank(expressions)) {
            String resolvedExpressions = SearchExpressionFacade.resolveClientIds(context, component, expressions);
            buffer.append(",p:'").append(resolvedExpressions).append("'");
        }

        return this;
    }

    public CSVBuilder update(UIComponent component, String expressions) {
        if (!LangUtils.isValueBlank(expressions)) {
            String resolvedExpressions = SearchExpressionFacade.resolveClientIds(
                    context, component, expressions, SearchExpressionHint.VALIDATE_RENDERER);
            buffer.append(",u:'").append(resolvedExpressions).append("'");
        }

        return this;
    }

    public CSVBuilder command(String command) {
        buffer.append("})){").append(command).append("}");

        return this;
    }

    public String build() {
        buffer.append("else{return false;}");

        String request = buffer.toString();

        reset();

        return request;
    }

    public void reset() {
        buffer.setLength(0);
    }
}
