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

import java.util.Map;
import javax.validation.metadata.ConstraintDescriptor;

public interface ClientValidationConstraint {

    public static final String ATTR_MESSAGE = "message";

    public Map<String, Object> getMetadata(ConstraintDescriptor constraintDescriptor);

    public String getValidatorId();
}
