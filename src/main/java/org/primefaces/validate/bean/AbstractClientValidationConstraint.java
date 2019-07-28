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

import javax.validation.metadata.ConstraintDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractClientValidationConstraint implements ClientValidationConstraint {

    private String messageId;
    private String messageMetadata;

    public AbstractClientValidationConstraint(String messageId, String messageMetadata) {
        this.messageId = messageId;
        this.messageMetadata = messageMetadata;
    }

    @Override
    public Map<String, Object> getMetadata(ConstraintDescriptor constraintDescriptor) {
        Map<String, Object> metadata = new HashMap<>();
        Map<String, Object> attrs = constraintDescriptor.getAttributes();
        Object message = attrs.get(ATTR_MESSAGE);

        processMetadata(metadata, attrs);

        if (!Objects.equals(message, messageId)) {
            metadata.put(messageMetadata, message);
        }

        return metadata;
    }

    protected void processMetadata(Map<String, Object> metadata, Map<String, Object> attrs) {
        // NOOP
    }
}
