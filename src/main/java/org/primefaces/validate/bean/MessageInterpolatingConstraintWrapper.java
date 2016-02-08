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
import java.util.HashMap;
import java.util.Map;

import javax.validation.MessageInterpolator;
import javax.validation.metadata.ConstraintDescriptor;

public class MessageInterpolatingConstraintWrapper extends ConstraintDescriptorWrapper<Annotation> {
    
    private final MessageInterpolator interpolator;
    private final MessageInterpolator.Context context;

    public MessageInterpolatingConstraintWrapper(MessageInterpolator interpolator, ConstraintDescriptor<?> constraintDescriptor) {
        super(constraintDescriptor);
        this.interpolator = interpolator;
        this.context = new ContextImpl(constraintDescriptor, null);
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = super.getAttributes();
        
        // wrap it - could be unmodifiable
        attributes = new HashMap<String, Object>(attributes);

        Object message = attributes.remove(ClientValidationConstraint.ATTR_MESSAGE);
        if (message != null) {
            if (message instanceof String) {
                message = interpolator.interpolate((String) message, getContext());
            }
            attributes.put(ClientValidationConstraint.ATTR_MESSAGE, message);
        }
        return attributes;
    }

    private MessageInterpolator.Context getContext() {
        return context;
    }
    
    public class ContextImpl implements MessageInterpolator.Context {

        private final ConstraintDescriptor<?> constraintDescriptor;
        private final Object validatedValue;
        
        public ContextImpl(ConstraintDescriptor<?> constraintDescriptor, Object validatedValue) {
            this.constraintDescriptor = constraintDescriptor;
            this.validatedValue = validatedValue;
        }
        
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return constraintDescriptor;
        }

        public Object getValidatedValue() {
            return validatedValue;
        }

        //@Override - BV 1.1
        public <T> T unwrap(Class<T> type) {
            return null;
        }
    }
}
