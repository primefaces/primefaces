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
package org.primefaces.validate.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import org.primefaces.metadata.BeanValidationMetadataExtractor;

import org.primefaces.context.RequestContext;

public class BeanValidationMetadataMapper {

    private static final Logger LOG = Logger.getLogger(BeanValidationMetadataMapper.class.getName());

    private static final Map<Class<?>, ClientValidationConstraint> CONSTRAINT_MAPPER = new HashMap<Class<?>, ClientValidationConstraint>();
    
    static {
        CONSTRAINT_MAPPER.put(NotNull.class, new NotNullClientValidationConstraint());
        CONSTRAINT_MAPPER.put(Null.class, new NullClientValidationConstraint());
        CONSTRAINT_MAPPER.put(Size.class, new SizeClientValidationConstraint());
        CONSTRAINT_MAPPER.put(Min.class, new MinClientValidationConstraint());
        CONSTRAINT_MAPPER.put(Max.class, new MaxClientValidationConstraint());
        CONSTRAINT_MAPPER.put(DecimalMin.class, new DecimalMinClientValidationConstraint());
        CONSTRAINT_MAPPER.put(DecimalMax.class, new DecimalMaxClientValidationConstraint());
        CONSTRAINT_MAPPER.put(AssertTrue.class, new AssertTrueClientValidationConstraint());
        CONSTRAINT_MAPPER.put(AssertFalse.class, new AssertFalseClientValidationConstraint());
        CONSTRAINT_MAPPER.put(Digits.class, new DigitsClientValidationConstraint());
        CONSTRAINT_MAPPER.put(Past.class, new PastClientValidationConstraint());
        CONSTRAINT_MAPPER.put(Future.class, new FutureClientValidationConstraint());
        CONSTRAINT_MAPPER.put(Pattern.class, new PatternClientValidationConstraint());
    }
    
    public static BeanValidationMetadata resolveValidationMetadata(FacesContext context, UIComponent component, RequestContext requestContext)
            throws IOException {

        Map<String,Object> metadata = new HashMap<String, Object>();
        List<String> validatorIds = new ArrayList<String>();
        
        Set<ConstraintDescriptor<?>> constraints = BeanValidationMetadataExtractor.extractConstraintDescriptors(context, requestContext, component.getValueExpression("value"));

        if (constraints != null && !constraints.isEmpty()) {
            for (ConstraintDescriptor<?> constraintDescriptor : constraints) {
                Class<?> annotationType = constraintDescriptor.getAnnotation().annotationType();
                ClientValidationConstraint clientValidationConstraint = CONSTRAINT_MAPPER.get(annotationType);

                if (clientValidationConstraint != null) {
                    String validatorId = clientValidationConstraint.getValidatorId();
                    Map<String,Object> constraintMetadata = clientValidationConstraint.getMetadata(constraintDescriptor);

                    if (constraintMetadata != null)
                        metadata.putAll(constraintMetadata);

                    if (validatorId != null)
                        validatorIds.add(validatorId);
                }
                else {
                    ClientConstraint clientConstraint = annotationType.getAnnotation(ClientConstraint.class);
                    if (clientConstraint != null) {
                        Class<?> resolvedBy = clientConstraint.resolvedBy();

                        if (resolvedBy != null) {
                            try {
                                ClientValidationConstraint customClientValidationConstraint = (ClientValidationConstraint) resolvedBy.newInstance();

                                String validatorId = customClientValidationConstraint.getValidatorId();
                                Map<String,Object> constraintMetadata = customClientValidationConstraint.getMetadata(constraintDescriptor);

                                if (constraintMetadata != null)
                                    metadata.putAll(constraintMetadata);

                                if (validatorId != null)
                                    validatorIds.add(validatorId);

                            }
                            catch (InstantiationException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                            catch (IllegalAccessException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }

        return new BeanValidationMetadata(metadata, validatorIds);
    }
}
