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
import java.util.Map;

import javax.validation.MessageInterpolator;
import javax.validation.metadata.ConstraintDescriptor;

public class MessageInterpolatingConstraint extends ConstraintDescriptorWrapper<Annotation>{
    private static final String MESSAGE = "message";
    private MessageInterpolator interpolator;

    public MessageInterpolatingConstraint(MessageInterpolator interpolator, ConstraintDescriptor<?> constraint){
        super(constraint);
        this.interpolator = interpolator;
    }

    @Override
    public Map<String, Object> getAttributes()
    {
        Map<String, Object> attrs = super.getAttributes();
        Object message = attrs.remove(MESSAGE);
        if (message != null)
        {
            if (message instanceof String)
            {
                String template = (String) message;
                message = interpolator.interpolate(template, getContext());
            }
            attrs.put(MESSAGE, message);
        }
        return attrs;
    }

    private MessageInterpolator.Context getContext()
    {
        return new MessageInterpolator.Context()
            {
                public ConstraintDescriptor<?> getConstraintDescriptor()
                {
                    return constraint;
                }

                public Object getValidatedValue()
                {
                    return null;
                }
            };
    }

}
