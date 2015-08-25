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
package org.primefaces.validate;

import java.util.HashMap;
import java.util.Map;
import org.primefaces.util.HTML;

public class LengthValidator extends javax.faces.validator.LengthValidator implements ClientValidator {

    private Map<String,Object> metadata;
    private Integer maximum;
    private Integer minimum;
    
    public Map<String, Object> getMetadata() {
        if(metadata == null) {
            metadata = new HashMap<String, Object>();
            
            if(minimum!=null)
                metadata.put(HTML.VALIDATION_METADATA.MIN_LENGTH, minimum);
            
            if(maximum!=null)
                metadata.put(HTML.VALIDATION_METADATA.MAX_LENGTH, maximum);
        }
        
        return metadata;
    }

    public String getValidatorId() {
        return LengthValidator.VALIDATOR_ID;
    }
    
    public void setMaximum(int maximum) {
        super.setMaximum(maximum);
        this.maximum = maximum;
    }
    
    public void setMinimum(int minimum) {
        super.setMinimum(minimum);
        this.minimum = minimum;
    }

}
