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
        attributes = new HashMap<>(attributes);

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

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return constraintDescriptor;
        }

        @Override
        public Object getValidatedValue() {
            return validatedValue;
        }

        //@Override - BV 1.1
        @Override
        public <T> T unwrap(Class<T> type) {
            return null;
        }
    }
}
