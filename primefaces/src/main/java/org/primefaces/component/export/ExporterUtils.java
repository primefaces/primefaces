/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.*;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITable;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.overlaypanel.OverlayPanel;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

public final class ExporterUtils {

    private ExporterUtils() {
        // NOOP
    }

    public static String getComponentValue(FacesContext context, UIComponent component) {

        if (component instanceof HtmlCommandLink) {  //support for PrimeFaces and standard HtmlCommandLink
            HtmlCommandLink link = (HtmlCommandLink) component;
            Object value = link.getValue();

            if (value != null) {
                return String.valueOf(value);
            }
            else {
                //export first value holder
                for (UIComponent child : link.getChildren()) {
                    if (child instanceof ValueHolder) {
                        return getComponentValue(context, child);
                    }
                }

                return Constants.EMPTY_STRING;
            }
        }
        else if (component instanceof ValueHolder) {
            if (component instanceof EditableValueHolder) {
                Object submittedValue = ((EditableValueHolder) component).getSubmittedValue();
                if (submittedValue != null) {
                    return submittedValue.toString();
                }
            }

            ValueHolder valueHolder = (ValueHolder) component;
            Object value = valueHolder.getValue();
            if (value == null) {
                return Constants.EMPTY_STRING;
            }

            Converter converter = valueHolder.getConverter();
            if (converter == null) {
                Class valueType = value.getClass();
                converter = context.getApplication().createConverter(valueType);
            }

            if (converter != null) {
                if (component instanceof UISelectMany) {
                    List<Object> collection = null;

                    if (value instanceof List) {
                        collection = (List<Object>) value;
                    }
                    else if (value.getClass().isArray()) {
                        collection = Arrays.asList(value);
                    }
                    else {
                        throw new FacesException("Value of " + component.getClientId(context) + " must be a List or an Array.");
                    }

                    Converter finalConverter = converter;
                    return collection.stream()
                            .map(o -> finalConverter.getAsString(context, component, o))
                            .collect(Collectors.joining(","));
                }
                else {
                    return converter.getAsString(context, component, value);
                }
            }
            else {
                return value.toString();
            }
        }
        else if (component instanceof CellEditor) {
            return getComponentValue(context, component.getFacet("output"));
        }
        else if (component instanceof HtmlGraphicImage) {
            return (String) component.getAttributes().get("alt");
        }
        else if (component instanceof OverlayPanel) {
            return Constants.EMPTY_STRING;
        }
        else {
            //This would get the plain texts on UIInstructions when using Facelets
            String value = component.toString();

            if (value != null) {
                return value.trim();
            }
            else {
                return Constants.EMPTY_STRING;
            }
        }
    }

    public static String getColumnValue(FacesContext context, UITable table, UIColumn column, boolean joinComponents) {
        if (column.getExportValue() != null) {
            return column.getExportValue();
        }
        else if (column.getExportFunction() != null) {
            MethodExpression exportFunction = column.getExportFunction();
            return (String) exportFunction.invoke(context.getELContext(), new Object[]{column});
        }
        else if (LangUtils.isNotBlank(column.getField())) {
            String value = table.getConvertedFieldValue(context, column);
            return Objects.toString(value, Constants.EMPTY_STRING);
        }
        else {
            return column.getChildren()
                    .stream()
                    .filter(UIComponent::isRendered)
                    .limit(!joinComponents ? 1 : column.getChildren().size())
                    .map(c -> getComponentValue(context, c))
                    .collect(Collectors.joining());
        }
    }

    public static String getComponentFacetValue(FacesContext context, UIComponent parent, String facetname) {
        UIComponent facet = parent.getFacet(facetname);
        if (ComponentUtils.shouldRenderFacet(facet)) {
            if (facet instanceof UIPanel) {
                for (UIComponent child : facet.getChildren()) {
                    if (child.isRendered()) {
                        String value = ComponentUtils.getValueToRender(context, child);

                        if (value != null) {
                            return value;
                        }
                    }
                }
            }
            else {
                return ComponentUtils.getValueToRender(context, facet);
            }
        }

        return null;
    }

    public static String getColumnFacetValue(FacesContext context, UIColumn column, TableExporter.ColumnType columnType) {
        String textValue = null;
        if (columnType == TableExporter.ColumnType.HEADER) {
            textValue = Optional.ofNullable(column.getExportHeaderValue()).orElseGet(column::getHeaderText);
        }
        else if (columnType == TableExporter.ColumnType.FOOTER) {
            textValue =  Optional.ofNullable(column.getExportFooterValue()).orElseGet(column::getFooterText);
        }

        UIComponent facet = column.getFacet(columnType.facet());
        if (LangUtils.isBlank(textValue) && ComponentUtils.shouldRenderFacet(facet)) {
            textValue = getComponentValue(context, facet);
        }

        return textValue;
    }
}
