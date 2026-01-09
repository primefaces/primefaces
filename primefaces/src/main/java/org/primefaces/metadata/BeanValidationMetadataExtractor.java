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
package org.primefaces.metadata;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.util.LangUtils;

import java.util.Collections;
import java.util.Set;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;
import jakarta.faces.context.FacesContext;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.PropertyDescriptor;

public class BeanValidationMetadataExtractor {

    private BeanValidationMetadataExtractor() {
    }

    public static Set<ConstraintDescriptor<?>> extractAllConstraintDescriptors(FacesContext context, PrimeApplicationContext applicationContext,
            ValueExpression ve) {

        PropertyDescriptor propertyDescriptor = extractPropertyDescriptor(context, applicationContext, ve);

        if (propertyDescriptor != null) {
            return propertyDescriptor.getConstraintDescriptors();
        }

        return Collections.emptySet();
    }

    public static Set<ConstraintDescriptor<?>> extractDefaultConstraintDescriptors(FacesContext context, PrimeApplicationContext applicationContext,
            ValueExpression ve) {

        return extractConstraintDescriptors(context, applicationContext, ve, Default.class);
    }

    public static Set<ConstraintDescriptor<?>> extractConstraintDescriptors(FacesContext context, PrimeApplicationContext applicationContext,
            ValueExpression ve, Class... groups) {

        PropertyDescriptor propertyDescriptor = extractPropertyDescriptor(context, applicationContext, ve);

        if (propertyDescriptor != null) {
            return propertyDescriptor.findConstraints().unorderedAndMatchingGroups(groups).getConstraintDescriptors();
        }

        return Collections.emptySet();
    }

    public static PropertyDescriptor extractPropertyDescriptor(FacesContext context, PrimeApplicationContext applicationContext, ValueExpression ve) {

        if (ve != null) {
            ELContext elContext = context.getELContext();
            ValueReference vr = ValueExpressionAnalyzer.getReference(elContext, ve);

            if (vr != null) {
                Validator validator = applicationContext.getValidator();
                Object base = vr.getBase();
                Object property = vr.getProperty();

                if (base != null && property != null) {
                    Class<?> unproxied = LangUtils.getUnproxiedClass(base.getClass());
                    BeanDescriptor beanDescriptor = validator.getConstraintsForClass(unproxied);

                    if (beanDescriptor != null) {
                        return beanDescriptor.getConstraintsForProperty(property.toString());
                    }
                }
            }
        }

        return null;
    }
}
