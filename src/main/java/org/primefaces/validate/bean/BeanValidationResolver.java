/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.validate.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import org.primefaces.el.ValueExpressionAnalyzer;

public class BeanValidationResolver {
 
    private static final Map<Class<?>, ClientValidationConstraint> CONSTRAINT_MAPPER = new HashMap<Class<?>, ClientValidationConstraint>();
    
    static {
        CONSTRAINT_MAPPER.put(NotNull.class, new NotNullClientValidationConstraint());
        CONSTRAINT_MAPPER.put(Size.class, new SizeClientValidationConstraint());
    }
    
    public static BeanValidationMetadata resolveValidationMetadata(FacesContext context, UIComponent component) throws IOException {
        ValueExpression ve = component.getValueExpression("value");
        ELContext elContext = context.getELContext();
        Map<String,Object> metadata = new HashMap<String, Object>();
        List<String> validatorIds = new ArrayList<String>();
        
        if(ve != null) {
            ValueReference vr = ValueExpressionAnalyzer.getReference(elContext, ve);
            
            if(vr != null) {
                Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
                Object base = vr.getBase();
                Object property = vr.getProperty();
                
                if(base != null && property != null) {
                    BeanDescriptor beanDescriptor = validator.getConstraintsForClass(base.getClass());
                    PropertyDescriptor propertyDescriptor = beanDescriptor.getConstraintsForProperty(property.toString());
                    Set<ConstraintDescriptor<?>> constraints = propertyDescriptor.getConstraintDescriptors();
                    
                    if(constraints != null && !constraints.isEmpty()) {
                        for(ConstraintDescriptor constraintDescriptor : constraints) {
                            ClientValidationConstraint clientValidationConstraint = CONSTRAINT_MAPPER.get(constraintDescriptor.getAnnotation().annotationType());
                            
                            if(clientValidationConstraint != null) {
                                String validatorId = clientValidationConstraint.getValidatorId();
                                Map<String,Object> constraintMetadata = clientValidationConstraint.getMetadata(constraintDescriptor);
                                
                                if(constraintMetadata != null)
                                    metadata.putAll(constraintMetadata);
                                
                                if(validatorId != null)
                                    validatorIds.add(validatorId);
                            }
                        }
                    }
                }
            }
        }
        
        return new BeanValidationMetadata(metadata, validatorIds);
    }
}
