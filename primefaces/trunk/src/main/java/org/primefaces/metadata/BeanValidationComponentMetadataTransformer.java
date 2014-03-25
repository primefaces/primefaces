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
package org.primefaces.metadata;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import org.primefaces.context.RequestContext;

public class BeanValidationComponentMetadataTransformer extends ComponentMetadataTransformer {

    private static final BeanValidationComponentMetadataTransformer INSTANCE = new BeanValidationComponentMetadataTransformer();
    
    public static BeanValidationComponentMetadataTransformer getInstance() {
        return INSTANCE;
    }
    
    public void transform(FacesContext context, UIComponent component) throws IOException {
        
        if (context.isPostback()) {
            return;
        }
        
        if (!(component instanceof EditableValueHolder) || !(component instanceof UIInput)) {
            return;
        }

        if (component.getAttributes().containsKey("maxlength") && component.getAttributes().containsKey("required")) {
            return;
        }
        
        RequestContext requestContext = RequestContext.getCurrentInstance();
        
        if (!requestContext.getApplicationContext().getConfig().isBeanValidationAvailable()) {
            return;
        }

        UIInput input = (UIInput) component;
        
        Set<ConstraintDescriptor<?>> constraints = BeanValidationMetadataExtractor.extract(context, requestContext, component.getValueExpression("value"));
        if (constraints != null && !constraints.isEmpty()) {
            for (ConstraintDescriptor<?> constraintDescriptor : constraints) {
                applyConstraint(constraintDescriptor, input);
            }
        }
    }
    
    protected void applyConstraint(ConstraintDescriptor constraintDescriptor, UIInput component) {
        
        Annotation constraint = constraintDescriptor.getAnnotation();
        
        EditableValueHolder valueHolder = (EditableValueHolder) component;

        if (!component.getAttributes().containsKey("maxlength")) {
            if (constraint.annotationType().equals(Max.class)) {
                Max max = (Max) constraint;
                if (max.value() > 0) {
                    applyMaxlength(component, Long.valueOf(max.value()).intValue());
                }
            }
            else if (constraint.annotationType().equals(Size.class)) {
                Size size = (Size) constraint;
                if (size.max() > 0) {
                    applyMaxlength(component, size.max());
                }
            }
        }
        
        if (!component.getAttributes().containsKey("required")) {
            if (constraint.annotationType().equals(Min.class)) {
                Min min = (Min) constraint;
                if (min.value() > 0) {
                    valueHolder.setRequired(true);
                }
            }
            else if (constraint.annotationType().equals(Size.class)) {
                Size size = (Size) constraint;
                if (size.min() > 0) {
                    valueHolder.setRequired(true);
                }
            }
            else if (constraint.annotationType().equals(NotNull.class)) {
                valueHolder.setRequired(true);
            }
        }
    }
}
