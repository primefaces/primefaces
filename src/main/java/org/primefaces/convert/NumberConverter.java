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
package org.primefaces.convert;

import java.util.HashMap;
import java.util.Map;
import org.primefaces.util.HTML;

public class NumberConverter extends javax.faces.convert.NumberConverter implements ClientConverter {
    
    private Map<String,Object> metadata;
    
    public Map<String, Object> getMetadata() {
        if(metadata == null) {
            String type = this.getType();
            int maxIntegerDigits = this.getMaxIntegerDigits();
            int minFractionDigits = this.getMinFractionDigits();
            boolean integerOnly = this.isIntegerOnly();

            metadata = new HashMap<String, Object>();
            
            metadata.put(HTML.VALIDATION_METADATA.NUMBER_TYPE, type);
            
            if(maxIntegerDigits != 0) metadata.put(HTML.VALIDATION_METADATA.MAX_INTEGER_DIGITS, maxIntegerDigits);
            if(minFractionDigits != 0) metadata.put(HTML.VALIDATION_METADATA.MIN_FRACTION_DIGITS, minFractionDigits);
            if(integerOnly) metadata.put(HTML.VALIDATION_METADATA.INTEGER_ONLY, "true");
            
            if(type.equals("currency")) {
                String currencySymbol = this.getCurrencySymbol();
                
                if(currencySymbol != null) 
                    metadata.put(HTML.VALIDATION_METADATA.CURRENCY_SYMBOL, currencySymbol);
            }
        }
        
        return metadata;
    }

    public String getConverterId() {
        return NumberConverter.CONVERTER_ID;
    }
}