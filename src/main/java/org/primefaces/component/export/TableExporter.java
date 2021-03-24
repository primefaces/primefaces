/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.export;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITable;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

public abstract class TableExporter<T extends UIComponent & UITable> extends Exporter<T> {

    protected void exportColumn(FacesContext context, T table, UIColumn column, List<UIComponent> components,
            boolean joinComponents, Consumer<String> callback) {

        if (LangUtils.isNotBlank(column.getExportValue())) {
            callback.accept(column.getExportValue());
        }
        else if (column.getExportFunction() != null) {
            MethodExpression exportFunction = column.getExportFunction();
            callback.accept((String) exportFunction.invoke(context.getELContext(), new Object[]{column}));
        }
        else if (LangUtils.isNotBlank(column.getField())) {
            String value = table.getConvertedFieldValue(context, column);
            callback.accept(Objects.toString(value, Constants.EMPTY_STRING));
        }
        else {
            StringBuilder sb = null;
            for (UIComponent component : components) {
                if (component.isRendered()) {
                    String value = exportValue(context, component);
                    if (joinComponents) {
                        if (value != null) {
                            if (sb == null) {
                                sb = new StringBuilder();
                            }
                            sb.append(value);
                        }
                    }
                    else {
                        callback.accept(value);
                    }
                }
            }
            if (joinComponents) {
                callback.accept(sb == null ? null : sb.toString());
            }
        }
    }
}
