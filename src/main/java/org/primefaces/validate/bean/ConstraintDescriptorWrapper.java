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
package org.primefaces.validate.bean;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.FacesWrapper;
import javax.validation.ConstraintTarget;

import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.metadata.ConstraintDescriptor;

public class ConstraintDescriptorWrapper<T extends Annotation> implements ConstraintDescriptor<T>, FacesWrapper<ConstraintDescriptor<T>> {

    protected final ConstraintDescriptor<T> wrapped;

    public ConstraintDescriptorWrapper(ConstraintDescriptor<?> wrapped) {
        this.wrapped = (ConstraintDescriptor<T>) wrapped;
    }

    @Override
    public T getAnnotation() {
        return wrapped.getAnnotation();
    }

    @Override
    public Set<Class<?>> getGroups() {
        return wrapped.getGroups();
    }

    @Override
    public Set<Class<? extends Payload>> getPayload() {
        return wrapped.getPayload();
    }

    @Override
    public List<Class<? extends ConstraintValidator<T, ?>>> getConstraintValidatorClasses() {
        return wrapped.getConstraintValidatorClasses();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return wrapped.getAttributes();
    }

    @Override
    public Set<ConstraintDescriptor<?>> getComposingConstraints() {
        return wrapped.getComposingConstraints();
    }

    @Override
    public boolean isReportAsSingleViolation() {
        return wrapped.isReportAsSingleViolation();
    }

    @Override
    public ConstraintDescriptor<T> getWrapped() {
        return wrapped;
    }

    //@Override - BV 1.1
    @Override
    public String getMessageTemplate() {
        return wrapped.getMessageTemplate();
    }

    //@Override - BV 1.1
    @Override
    public ConstraintTarget getValidationAppliesTo() {
        return wrapped.getValidationAppliesTo();
    }
}
