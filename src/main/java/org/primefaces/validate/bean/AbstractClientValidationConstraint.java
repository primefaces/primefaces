/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
