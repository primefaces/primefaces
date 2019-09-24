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
package org.primefaces.metadata.transformer.impl;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.PropertyNotFoundException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;

import org.primefaces.component.api.UICalendar;
import org.primefaces.component.inputnumber.InputNumber;
import org.primefaces.component.spinner.Spinner;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.metadata.BeanValidationMetadataExtractor;
import org.primefaces.metadata.transformer.AbstractInputMetadataTransformer;
import org.primefaces.util.LangUtils;

public class BeanValidationInputMetadataTransformer extends AbstractInputMetadataTransformer {

    private static final Logger LOGGER = Logger.getLogger(BeanValidationInputMetadataTransformer.class.getName());

    @Override
    public void transformInput(FacesContext context, PrimeApplicationContext applicationContext, UIInput input) throws IOException {

        EditableValueHolder editableValueHolder = (EditableValueHolder) input;

        if (editableValueHolder.isRequired() && isMaxlenghtSet(input)) {
            return;
        }

        try {
            Set<ConstraintDescriptor<?>> constraints = BeanValidationMetadataExtractor.extractDefaultConstraintDescriptors(
                    context, applicationContext, input.getValueExpression("value"));
            if (constraints != null && !constraints.isEmpty()) {
                for (ConstraintDescriptor<?> constraintDescriptor : constraints) {
                    applyConstraint(constraintDescriptor, input, editableValueHolder);
                }
            }
        }
        catch (PropertyNotFoundException e) {
            String message = "Skip transform metadata for component \"" + input.getClientId(context) + "\" because"
                    + " the ValueExpression of the \"value\" attribute"
                    + " isn't resolvable completely (e.g. a sub-expression returns null)";
            LOGGER.log(Level.FINE, message);
        }
    }

    protected void applyConstraint(ConstraintDescriptor constraintDescriptor, UIInput input, EditableValueHolder editableValueHolder) {

        Annotation constraint = constraintDescriptor.getAnnotation();
        Class<? extends Annotation> annotationType = constraint.annotationType();

        if (!isMaxlenghtSet(input)) {
            if (annotationType.equals(Size.class)) {
                Size size = (Size) constraint;
                if (size.max() > 0) {
                    setMaxlength(input, size.max());
                }
            }
        }

        if (input instanceof Spinner) {
            Spinner spinner = (Spinner) input;

            if (annotationType.equals(Max.class) && spinner.getMax() == Double.MAX_VALUE) {
                Max max = (Max) constraint;
                spinner.setMax(max.value());
            }
            if (annotationType.equals(Min.class) && spinner.getMin() == Double.MIN_VALUE) {
                Min min = (Min) constraint;
                spinner.setMin(min.value());
            }
            if (annotationType.equals(DecimalMax.class) && spinner.getMax() == Double.MAX_VALUE) {
                DecimalMax max = (DecimalMax) constraint;
                try {
                    spinner.setMax(Double.parseDouble(max.value()));
                }
                catch (NumberFormatException ex) {
                    LOGGER.log(Level.WARNING, "Failed setting Spinner max value: " + ex.getMessage());
                }
            }
            if (annotationType.equals(DecimalMin.class) && spinner.getMin() == Double.MIN_VALUE) {
                DecimalMin min = (DecimalMin) constraint;
                try {
                    spinner.setMin(Double.parseDouble(min.value()));
                }
                catch (NumberFormatException ex) {
                    LOGGER.log(Level.WARNING, "Failed setting Spinner min value: " + ex.getMessage());
                }
            }
        }

        if (input instanceof InputNumber) {
            InputNumber inputNumber = (InputNumber) input;

            if (annotationType.equals(Max.class) && LangUtils.isValueBlank(inputNumber.getMaxValue())) {
                Max max = (Max) constraint;
                inputNumber.setMaxValue(String.valueOf(max.value()));
            }
            if (annotationType.equals(Min.class) && LangUtils.isValueBlank(inputNumber.getMinValue())) {
                Min min = (Min) constraint;
                inputNumber.setMinValue(String.valueOf(min.value()));
            }
            if (annotationType.equals(DecimalMax.class) && LangUtils.isValueBlank(inputNumber.getMaxValue())) {
                DecimalMax max = (DecimalMax) constraint;
                inputNumber.setMaxValue(max.value());
            }
            if (annotationType.equals(DecimalMin.class) && LangUtils.isValueBlank(inputNumber.getMinValue())) {
                DecimalMin min = (DecimalMin) constraint;
                inputNumber.setMinValue(min.value());
            }
            if (annotationType.equals(Digits.class) && LangUtils.isValueBlank(inputNumber.getDecimalPlaces())) {
                Digits digits = (Digits) constraint;
                inputNumber.setDecimalPlaces(String.valueOf(digits.fraction()));
            }
        }

        if (input instanceof UICalendar) {
            UICalendar uicalendar = (UICalendar) input;
            boolean hasTime = uicalendar.hasTime();
            // for BeanValidation 2.0
            String annotationClassName = annotationType.getSimpleName();

            if (annotationType.equals(Past.class) && uicalendar.getMaxdate() == null) {
                uicalendar.setMaxdate(hasTime ? LocalDate.now() : LocalDate.now().minusDays(1));
            }
            if (annotationClassName.equals("PastOrPresent") && uicalendar.getMaxdate() == null) {
                uicalendar.setMaxdate(LocalDate.now());
            }
            if (annotationType.equals(Future.class) && uicalendar.getMindate() == null) {
                uicalendar.setMindate(hasTime ? LocalDate.now() : LocalDate.now().plusDays(1));
            }
            if (annotationClassName.equals("FutureOrPresent") && uicalendar.getMindate() == null) {
                uicalendar.setMindate(LocalDate.now());
            }
        }
    }
}
