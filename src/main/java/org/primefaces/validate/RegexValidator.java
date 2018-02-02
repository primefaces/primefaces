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
package org.primefaces.validate;

import java.util.HashMap;
import java.util.Map;
import org.primefaces.util.HTML;

public class RegexValidator extends javax.faces.validator.RegexValidator implements ClientValidator {

    private Map<String, Object> metadata;

    public Map<String, Object> getMetadata() {
        metadata = new HashMap<String, Object>();
        String regex = this.getPattern();

        if (regex != null) {
            metadata.put(HTML.VALIDATION_METADATA.REGEX, regex);
        }

        return metadata;
    }

    public String getValidatorId() {
        return RegexValidator.VALIDATOR_ID;
    }
}
