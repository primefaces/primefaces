/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
import javax.validation.metadata.ConstraintDescriptor;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.metadata.BeanValidationMetadataExtractor;
import org.primefaces.util.MapBuilder;

public class BeanValidationMetadataMapper {

    private static final Logger LOGGER = Logger.getLogger(BeanValidationMetadataMapper.class.getName());

    private static final Map<String, ClientValidationConstraint> CONSTRAINT_MAPPING
            = MapBuilder.<String, ClientValidationConstraint>builder()
                    .put(AssertFalseClientValidationConstraint.CONSTRAINT_CLASS_NAME, new AssertFalseClientValidationConstraint())
                    .put(AssertTrueClientValidationConstraint.CONSTRAINT_CLASS_NAME, new AssertTrueClientValidationConstraint())
                    .put(DecimalMaxClientValidationConstraint.CONSTRAINT_CLASS_NAME, new DecimalMaxClientValidationConstraint())
                    .put(DecimalMinClientValidationConstraint.CONSTRAINT_CLASS_NAME, new DecimalMinClientValidationConstraint())
                    .put(DigitsClientValidationConstraint.CONSTRAINT_CLASS_NAME, new DigitsClientValidationConstraint())
                    .put(EmailClientValidationConstraint.CONSTRAINT_CLASS_NAME, new EmailClientValidationConstraint())
                    .put(FutureClientValidationConstraint.CONSTRAINT_CLASS_NAME, new FutureClientValidationConstraint())
                    .put(FutureOrPresentClientValidationConstraint.CONSTRAINT_CLASS_NAME, new FutureOrPresentClientValidationConstraint())
                    .put(MaxClientValidationConstraint.CONSTRAINT_CLASS_NAME, new MaxClientValidationConstraint())
                    .put(MinClientValidationConstraint.CONSTRAINT_CLASS_NAME, new MinClientValidationConstraint())
                    .put(NegativeClientValidationConstraint.CONSTRAINT_CLASS_NAME, new NegativeClientValidationConstraint())
                    .put(NegativeOrZeroClientValidationConstraint.CONSTRAINT_CLASS_NAME, new NegativeOrZeroClientValidationConstraint())
                    .put(NotBlankClientValidationConstraint.CONSTRAINT_CLASS_NAME, new NotBlankClientValidationConstraint())
                    .put(NotEmptyClientValidationConstraint.CONSTRAINT_CLASS_NAME, new NotEmptyClientValidationConstraint())
                    .put(NotNullClientValidationConstraint.CONSTRAINT_CLASS_NAME, new NotNullClientValidationConstraint())
                    .put(NullClientValidationConstraint.CONSTRAINT_CLASS_NAME, new NullClientValidationConstraint())
                    .put(PastClientValidationConstraint.CONSTRAINT_CLASS_NAME, new PastClientValidationConstraint())
                    .put(PastOrPresentClientValidationConstraint.CONSTRAINT_CLASS_NAME, new PastOrPresentClientValidationConstraint())
                    .put(PatternClientValidationConstraint.CONSTRAINT_CLASS_NAME, new PatternClientValidationConstraint())
                    .put(PositiveClientValidationConstraint.CONSTRAINT_CLASS_NAME, new PositiveClientValidationConstraint())
                    .put(PositiveOrZeroClientValidationConstraint.CONSTRAINT_CLASS_NAME, new PositiveOrZeroClientValidationConstraint())
                    .put(SizeClientValidationConstraint.CONSTRAINT_CLASS_NAME, new SizeClientValidationConstraint())
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
                    ClientValidationConstraint clientValidationConstraint = CONSTRAINT_MAPPING.get(annotationType.getName());

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
                                    clientValidationConstraint = (ClientValidationConstraint) resolvedBy.getConstructor().newInstance();
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
        CONSTRAINT_MAPPING.put(constraint.getName(), clientValidationConstraint);
    }

    public static ClientValidationConstraint removeConstraintMapping(Class<? extends Annotation> constraint) {
        return CONSTRAINT_MAPPING.remove(constraint.getName());
    }
}
