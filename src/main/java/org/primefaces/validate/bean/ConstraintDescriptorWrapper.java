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

import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.metadata.ConstraintDescriptor;

public class ConstraintDescriptorWrapper<T extends Annotation> implements ConstraintDescriptor<T>{
    protected final ConstraintDescriptor<T> constraint;

    @SuppressWarnings("unchecked")
    public ConstraintDescriptorWrapper(ConstraintDescriptor<?> constraint){
        this.constraint = (ConstraintDescriptor<T>) constraint;
    }

    public T getAnnotation(){
        return constraint.getAnnotation();
    }

    public Set<Class<?>> getGroups(){
        return constraint.getGroups();
    }

    public Set<Class<? extends Payload>> getPayload(){
        return constraint.getPayload();
    }

    public List<Class<? extends ConstraintValidator<T, ?>>> getConstraintValidatorClasses(){
        return constraint.getConstraintValidatorClasses();
    }

    public Map<String, Object> getAttributes(){
        return constraint.getAttributes();
    }

    public Set<ConstraintDescriptor<?>> getComposingConstraints(){
        return constraint.getComposingConstraints();
    }

    public boolean isReportAsSingleViolation(){
        return constraint.isReportAsSingleViolation();
    }

}
