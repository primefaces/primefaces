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

import java.util.List;
import java.util.Map;

public class BeanValidationMetadata {
    
    private Map<String,Object> attributes;
    private List<String> validatorIds;

    public BeanValidationMetadata() {
    }

    public BeanValidationMetadata(Map<String, Object> attributes, List<String> validatorIds) {
        this.attributes = attributes;
        this.validatorIds = validatorIds;
    }
    
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public List<String> getValidatorIds() {
        return validatorIds;
    }

    public void setValidatorIds(List<String> validatorIds) {
        this.validatorIds = validatorIds;
    }
}
