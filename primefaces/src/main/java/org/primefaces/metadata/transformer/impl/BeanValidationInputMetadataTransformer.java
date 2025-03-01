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
package org.primefaces.metadata.transformer.impl;

import org.primefaces.component.api.UICalendar;
import org.primefaces.component.inputnumber.InputNumber;
import org.primefaces.component.spinner.Spinner;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.metadata.BeanValidationMetadataExtractor;
import org.primefaces.metadata.transformer.AbstractInputMetadataTransformer;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.validate.bean.FutureOrPresentClientValidationConstraint;
import org.primefaces.validate.bean.NegativeClientValidationConstraint;
import org.primefaces.validate.bean.NegativeOrZeroClientValidationConstraint;
import org.primefaces.validate.bean.PastOrPresentClientValidationConstraint;
import org.primefaces.validate.bean.PositiveClientValidationConstraint;
import org.primefaces.validate.bean.PositiveOrZeroClientValidationConstraint;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.el.PropertyNotFoundException;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import jakarta.validation.metadata.ConstraintDescriptor;

public class BeanValidationInputMetadataTransformer extends AbstractInputMetadataTransformer {

    private static final Logger LOGGER = Logger.getLogger(BeanValidationInputMetadataTransformer.class.getName());

    @Override
    public void transformInput(FacesContext context, PrimeApplicationContext applicationContext, UIInput input) throws IOException {
        if (ComponentUtils.isDisabledOrReadonly(input) || (input.isRequired() && isMaxlengthSet(input))) {
            return;
        }

        try {
            Set<ConstraintDescriptor<?>> constraints = BeanValidationMetadataExtractor.extractDefaultConstraintDescriptors(
                    context, applicationContext, input.getValueExpression("value"));
            if (constraints != null && !constraints.isEmpty()) {
                for (ConstraintDescriptor<?> constraintDescriptor : constraints) {
                    applyConstraint(constraintDescriptor, input);
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

    protected void applyConstraint(ConstraintDescriptor<?> constraintDescriptor, UIInput input) {

        Annotation constraint = constraintDescriptor.getAnnotation();
        Class<? extends Annotation> annotationType = constraint.annotationType();
        // for BeanValidation 2.0
        String annotationClassName = annotationType.getSimpleName();

        if (!isMaxlengthSet(input)) {
            if (annotationType.equals(Size.class)) {
                Size size = (Size) constraint;
                if (size.max() > 0) {
                    setMaxlength(input, size.max());
                }
            }
        }

        if (input instanceof Spinner) {
            Spinner spinner = (Spinner) input;

            if (annotationType.equals(Max.class) && spinner.getMax() == Spinner.MAX_VALUE) {
                Max max = (Max) constraint;
                spinner.setMax(max.value());
            }
            if (annotationType.equals(Min.class) && spinner.getMin() == Spinner.MIN_VALUE) {
                Min min = (Min) constraint;
                spinner.setMin(min.value());
            }
            if (annotationType.equals(DecimalMax.class) && spinner.getMax() == Spinner.MAX_VALUE) {
                DecimalMax max = (DecimalMax) constraint;
                try {
                    spinner.setMax(Double.parseDouble(max.value()));
                }
                catch (NumberFormatException ex) {
                    LOGGER.log(Level.WARNING, () -> "Failed setting Spinner max value: " + ex.getMessage());
                }
            }
            if (annotationType.equals(DecimalMin.class) && spinner.getMin() == Spinner.MIN_VALUE) {
                DecimalMin min = (DecimalMin) constraint;
                try {
                    spinner.setMin(Double.parseDouble(min.value()));
                }
                catch (NumberFormatException ex) {
                    LOGGER.log(Level.WARNING, () -> "Failed setting Spinner min value: " + ex.getMessage());
                }
            }
        }

        if (input instanceof InputNumber) {
            InputNumber inputNumber = (InputNumber) input;

            if (LangUtils.isBlank(inputNumber.getMaxValue())) {
                if (annotationType.equals(Max.class)) {
                    Max max = (Max) constraint;
                    inputNumber.setMaxValue(String.valueOf(max.value()));
                }
                if (annotationType.equals(DecimalMax.class)) {
                    DecimalMax max = (DecimalMax) constraint;
                    inputNumber.setMaxValue(max.value());
                }
                if (annotationClassName.equals(NegativeClientValidationConstraint.CONSTRAINT_ID)) {
                    inputNumber.setMaxValue(NegativeClientValidationConstraint.MAX_VALUE);
                }
                if (annotationClassName.equals(NegativeOrZeroClientValidationConstraint.CONSTRAINT_ID)) {
                    inputNumber.setMaxValue(NegativeOrZeroClientValidationConstraint.MAX_VALUE);
                }
            }

            if (LangUtils.isBlank(inputNumber.getMinValue())) {
                if (annotationType.equals(Min.class)) {
                    Min min = (Min) constraint;
                    inputNumber.setMinValue(String.valueOf(min.value()));
                }
                if (annotationType.equals(DecimalMin.class)) {
                    DecimalMin min = (DecimalMin) constraint;
                    inputNumber.setMinValue(min.value());
                }
                if (annotationClassName.equals(PositiveClientValidationConstraint.CONSTRAINT_ID)) {
                    inputNumber.setMinValue(PositiveClientValidationConstraint.MIN_VALUE);
                }
                if (annotationClassName.equals(PositiveOrZeroClientValidationConstraint.CONSTRAINT_ID)) {
                    inputNumber.setMinValue(PositiveOrZeroClientValidationConstraint.MIN_VALUE);
                }
            }

            if (annotationType.equals(Digits.class) && LangUtils.isBlank(inputNumber.getDecimalPlaces())) {
                Digits digits = (Digits) constraint;
                inputNumber.setDecimalPlaces(String.valueOf(digits.fraction()));
            }
        }

        if (input instanceof UICalendar) {
            UICalendar uicalendar = (UICalendar) input;
            boolean hasTime = uicalendar.hasTime();
            Temporal now = CalendarUtils.now(uicalendar);

            if (annotationType.equals(Past.class) && uicalendar.getMaxdate() == null) {
                Object maxDate = now;
                Class<?> dataType = uicalendar.getValueType();
                if (dataType != null) {
                    if (LocalDate.class.isAssignableFrom(dataType)) {
                        maxDate = now.minus(1, ChronoUnit.DAYS);
                    }
                    else if (YearMonth.class.isAssignableFrom(dataType)) {
                        maxDate = now.minus(1, ChronoUnit.MONTHS);
                    }
                }
                uicalendar.setMaxdate(maxDate);
            }
            if (annotationClassName.equals(PastOrPresentClientValidationConstraint.CONSTRAINT_ID) && uicalendar.getMaxdate() == null) {
                uicalendar.setMaxdate(now);
            }
            if (annotationType.equals(Future.class) && uicalendar.getMindate() == null) {
                Object minDate = now;
                if (!hasTime) {
                    Class<?> dataType = uicalendar.getValueType();
                    if (dataType != null && YearMonth.class.isAssignableFrom(dataType)) {
                        minDate = now.plus(1, ChronoUnit.MONTHS);
                    }
                    else {
                        minDate = now.plus(1, ChronoUnit.DAYS);
                    }
                }
                uicalendar.setMindate(minDate);
            }
            if (annotationClassName.equals(FutureOrPresentClientValidationConstraint.CONSTRAINT_ID) && uicalendar.getMindate() == null) {
                uicalendar.setMindate(now);
            }
        }
    }
}
