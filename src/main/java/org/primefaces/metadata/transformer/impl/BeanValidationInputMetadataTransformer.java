/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.metadata.transformer.impl;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.PropertyNotFoundException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import org.primefaces.component.spinner.Spinner;
import org.primefaces.context.RequestContext;
import org.primefaces.metadata.BeanValidationMetadataExtractor;
import org.primefaces.metadata.transformer.AbstractInputMetadataTransformer;

public class BeanValidationInputMetadataTransformer extends AbstractInputMetadataTransformer {

    private static final Logger LOG = Logger.getLogger(BeanValidationInputMetadataTransformer.class.getName());
    
    public void transformInput(FacesContext context, RequestContext requestContext, UIInput input) throws IOException {

        EditableValueHolder editableValueHolder = (EditableValueHolder) input;
       
        if (editableValueHolder.isRequired() && isMaxlenghtSet(input)) {
            return;
        }
         
        try {
            Set<ConstraintDescriptor<?>> constraints = BeanValidationMetadataExtractor.extractDefaultConstraintDescriptors(
                    context, requestContext, input.getValueExpression("value"));
            if (constraints != null && !constraints.isEmpty()) {    
                for (ConstraintDescriptor<?> constraintDescriptor : constraints) {
                    applyConstraint(constraintDescriptor, input, editableValueHolder);
                }
            }
        }
        catch (PropertyNotFoundException e)  {
            String message = "Skip transform metadata for component \"" + input.getClientId(context) + "\" because"
                    + " the ValueExpression of the \"value\" attribute"
                    + " isn't resolvable completely (e.g. a sub-expression returns null)";
            LOG.log(Level.FINE, message);
        }
    }
    
    protected void applyConstraint(ConstraintDescriptor constraintDescriptor, UIInput input, EditableValueHolder editableValueHolder) {
        
        Annotation constraint = constraintDescriptor.getAnnotation();
        
        if (!isMaxlenghtSet(input)) {
            if (constraint.annotationType().equals(Size.class)) {
                Size size = (Size) constraint;
                if (size.max() > 0) {
                    setMaxlength(input, size.max());
                }
            }
        }
        
        if (!editableValueHolder.isRequired()) {
            if (constraint.annotationType().equals(NotNull.class)
                    // see GitHub #14
                    && RequestContext.getCurrentInstance().getApplicationContext().getConfig().isInterpretEmptyStringAsNull()) {
                markAsRequired(input, true);
            }
        }
        
        if (input instanceof Spinner) {
            Spinner spinner = (Spinner) input;

            if (constraint.annotationType().equals(Max.class) && spinner.getMax() == Double.MAX_VALUE) {
                Max max = (Max) constraint;
                spinner.setMax(max.value());
            }
            if (constraint.annotationType().equals(Min.class) && spinner.getMin() == Double.MIN_VALUE) {
                Min min = (Min) constraint;
                spinner.setMin(min.value());
            }
        }
    }
}
