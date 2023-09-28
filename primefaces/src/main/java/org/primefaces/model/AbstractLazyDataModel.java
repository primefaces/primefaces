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
package org.primefaces.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.util.BeanUtils;

public abstract class AbstractLazyDataModel<T> extends LazyDataModel<T> {

    private static final Logger LOG = Logger.getLogger(AbstractLazyDataModel.class.getName());

    public AbstractLazyDataModel() {
        super();
    }

    public AbstractLazyDataModel(Converter<T> rowKeyConverter) {
        super(rowKeyConverter);
    }

    protected Object convertToType(Object value, Class valueType) {
        // skip null
        if (value == null) {
            return null;
        }

        // its already the same type
        if (valueType.isAssignableFrom(value.getClass())) {
            return value;
        }

        FacesContext context = FacesContext.getCurrentInstance();

        // primivites dont need complex conversion, try the ELContext first
        if (BeanUtils.isPrimitiveOrPrimitiveWrapper(valueType)) {
            try {
                return context.getELContext().convertToType(value, valueType);
            }
            catch (ELException e) {
                LOG.log(Level.INFO, e, () -> "Could not convert '" + value + "' to " + valueType + " via ELContext!");
            }
        }

        Converter targetConverter = context.getApplication().createConverter(valueType);
        if (targetConverter == null) {
            LOG.log(Level.FINE, () -> "Skip conversion as no converter was found for " + valueType
                    + "; Create a JSF Converter for it or overwrite Object convertToType(String value, Class<?> valueType)!");
            return value;
        }

        Converter sourceConverter = context.getApplication().createConverter(value.getClass());
        if (sourceConverter == null) {
            LOG.log(Level.FINE, () -> "Skip conversion as no converter was found for " + value.getClass()
                    + "; Create a JSF Converter for it or overwrite Object convertToType(String value, Class<?> valueType)!");
        }

        // first convert the object to string
        String stringValue = sourceConverter == null
                ? value.toString()
                : sourceConverter.getAsString(context, UIComponent.getCurrentComponent(context), value);

        // now convert the string to the required target
        try {
            return targetConverter.getAsObject(context, UIComponent.getCurrentComponent(context), stringValue);
        }
        catch (ConverterException e) {
            LOG.log(Level.INFO, e, () -> "Could not convert '" + stringValue + "' to " + valueType + " via " + targetConverter.getClass().getName());
            return value;
        }
    }
}
