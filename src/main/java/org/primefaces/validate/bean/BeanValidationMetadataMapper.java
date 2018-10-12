/**
 * Copyright 2009-2018 PrimeTek.
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
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.PropertyNotFoundException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.validation.MessageInterpolator;
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
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.metadata.BeanValidationMetadataExtractor;
import org.primefaces.util.MapBuilder;

public class BeanValidationMetadataMapper {

    private static final Logger LOGGER = Logger.getLogger(BeanValidationMetadataMapper.class.getName());

    private static final Map<Class<? extends Annotation>, ClientValidationConstraint> CONSTRAINT_MAPPING
            = MapBuilder.<Class<? extends Annotation>, ClientValidationConstraint>builder()
                    .put(NotNull.class, new NotNullClientValidationConstraint())
                    .put(Null.class, new NullClientValidationConstraint())
                    .put(Size.class, new SizeClientValidationConstraint())
                    .put(Min.class, new MinClientValidationConstraint())
                    .put(Max.class, new MaxClientValidationConstraint())
                    .put(DecimalMin.class, new DecimalMinClientValidationConstraint())
                    .put(DecimalMax.class, new DecimalMaxClientValidationConstraint())
                    .put(AssertTrue.class, new AssertTrueClientValidationConstraint())
                    .put(AssertFalse.class, new AssertFalseClientValidationConstraint())
                    .put(Digits.class, new DigitsClientValidationConstraint())
                    .put(Past.class, new PastClientValidationConstraint())
                    .put(Future.class, new FutureClientValidationConstraint())
                    .put(Pattern.class, new PatternClientValidationConstraint())
                    .build();

    private BeanValidationMetadataMapper() {
    }

    public static BeanValidationMetadata resolveValidationMetadata(FacesContext context, UIComponent component, PrimeApplicationContext applicationContext)
            throws IOException {

        Map<String, Object> metadata = null;
        List<String> validatorIds = null;

        try {
            // get BV ConstraintDescriptors
            Set<ConstraintDescriptor<?>> constraints = BeanValidationMetadataExtractor.extractAllConstraintDescriptors(
                    context, applicationContext, component.getValueExpression("value"));

            if (constraints != null && !constraints.isEmpty()) {

                boolean interpolateClientSideValidationMessages =
                        applicationContext.getConfig().isInterpolateClientSideValidationMessages();

                MessageInterpolator messageInterpolator = null;
                if (interpolateClientSideValidationMessages) {
                    messageInterpolator = applicationContext.getValidatorFactory().getMessageInterpolator();
                }

                // loop BV ConstraintDescriptors
                for (ConstraintDescriptor<?> constraintDescriptor : constraints) {
                    Class<?> annotationType = constraintDescriptor.getAnnotation().annotationType();

                    // lookup ClientValidationConstraint by constraint annotation (e.g. @NotNull)
                    ClientValidationConstraint clientValidationConstraint = CONSTRAINT_MAPPING.get(annotationType);

                    // mapping available? Otherwise try to lookup custom constraint
                    if (clientValidationConstraint == null) {
                        // custom constraint must use @ClientConstraint to map the ClientValidationConstraint
                        ClientConstraint clientConstraint = annotationType.getAnnotation(ClientConstraint.class);

                        if (clientConstraint != null) {
                            Class<?> resolvedBy = clientConstraint.resolvedBy();

                            if (resolvedBy != null) {
                                try {
                                    // TODO AppScoped instances? CDI?
                                    // instantiate ClientValidationConstraint
                                    clientValidationConstraint = (ClientValidationConstraint) resolvedBy.newInstance();
                                }
                                catch (Exception e) {
                                    throw new FacesException("Could not instantiate ClientValidationConstraint!", e);
                                }
                            }
                        }
                    }

                    if (clientValidationConstraint != null) {

                        String validatorId = clientValidationConstraint.getValidatorId();

                        Map<String, Object> constraintMetadata;

                        if (interpolateClientSideValidationMessages) {
                            MessageInterpolatingConstraintWrapper interpolatingConstraint
                                    = new MessageInterpolatingConstraintWrapper(messageInterpolator, constraintDescriptor);
                            constraintMetadata = clientValidationConstraint.getMetadata(interpolatingConstraint);
                        }
                        else {
                            constraintMetadata = clientValidationConstraint.getMetadata(constraintDescriptor);
                        }

                        if (constraintMetadata != null) {
                            if (metadata == null) {
                                metadata = new HashMap<>();
                            }
                            metadata.putAll(constraintMetadata);
                        }

                        if (validatorId != null) {
                            if (validatorIds == null) {
                                validatorIds = new ArrayList<>();
                            }
                            validatorIds.add(validatorId);
                        }
                    }
                }
            }
        }
        catch (PropertyNotFoundException e) {
            String message = "Skip resolving of CSV BV metadata for component \"" + component.getClientId(context) + "\" because"
                    + " the ValueExpression of the \"value\" attribute"
                    + " isn't resolvable completely (e.g. a sub-expression returns null)";
            LOGGER.log(Level.FINE, message);
        }

        if (metadata == null && validatorIds == null) {
            return null;
        }

        return new BeanValidationMetadata(metadata, validatorIds);
    }

    public static void registerConstraintMapping(Class<? extends Annotation> constraint, ClientValidationConstraint clientValidationConstraint) {
        CONSTRAINT_MAPPING.put(constraint, clientValidationConstraint);
    }

    public static ClientValidationConstraint removeConstraintMapping(Class<? extends Annotation> constraint) {
        return CONSTRAINT_MAPPING.remove(constraint);
    }
}
