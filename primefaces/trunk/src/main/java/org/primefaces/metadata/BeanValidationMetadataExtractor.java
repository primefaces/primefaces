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

import java.util.Set;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.faces.context.FacesContext;
import javax.validation.Validator;
import javax.validation.groups.Default;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import org.primefaces.context.RequestContext;
import org.primefaces.el.ValueExpressionAnalyzer;

public class BeanValidationMetadataExtractor {

    public static Set<ConstraintDescriptor<?>> extractAllConstraintDescriptors(FacesContext context, RequestContext requestContext, ValueExpression ve) {

        PropertyDescriptor propertyDescriptor = extractPropertyDescriptor(context, requestContext, ve);

        if (propertyDescriptor != null) {
            return propertyDescriptor.getConstraintDescriptors();
        }
        
        return null;
    }

    public static Set<ConstraintDescriptor<?>> extractDefaultConstraintDescriptors(FacesContext context, RequestContext requestContext, ValueExpression ve) {

        return extractConstraintDescriptors(context, requestContext, ve, Default.class);
    }
    
    public static Set<ConstraintDescriptor<?>> extractConstraintDescriptors(FacesContext context, RequestContext requestContext, ValueExpression ve, Class... groups) {

        PropertyDescriptor propertyDescriptor = extractPropertyDescriptor(context, requestContext, ve);

        if (propertyDescriptor != null) {
            return propertyDescriptor.findConstraints().unorderedAndMatchingGroups(groups).getConstraintDescriptors();
        }
        
        return null;
    }

    public static PropertyDescriptor extractPropertyDescriptor(FacesContext context, RequestContext requestContext, ValueExpression ve) {

        if (ve != null) {
            ELContext elContext = context.getELContext();
            ValueReference vr = ValueExpressionAnalyzer.getReference(elContext, ve);
            
            if (vr != null) {
                Validator validator = requestContext.getApplicationContext().getValidatorFactory().getValidator();
                Object base = vr.getBase();
                Object property = vr.getProperty();
                
                if (base != null && property != null) {
                    BeanDescriptor beanDescriptor = validator.getConstraintsForClass(base.getClass());
                    
                    if (beanDescriptor != null) {
                        return beanDescriptor.getConstraintsForProperty(property.toString());
                    }
                }
            }
        }
        
        return null;
    }
}
