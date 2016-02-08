/*
 * Copyright 2009-2014 PrimeTek.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
    public String getMessageTemplate() {
        return wrapped.getMessageTemplate();
    }

    //@Override - BV 1.1
    public ConstraintTarget getValidationAppliesTo() {
        return wrapped.getValidationAppliesTo();
    }
}
