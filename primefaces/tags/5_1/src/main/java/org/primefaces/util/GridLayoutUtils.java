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
package org.primefaces.util;

import java.util.HashMap;
import java.util.Map;

public class GridLayoutUtils {
    
    private static final Map<Integer, String> COLUMN_MAP = new HashMap<Integer, String>();
    
    static {
        COLUMN_MAP.put(1, "ui-grid-col-12");
        COLUMN_MAP.put(2, "ui-grid-col-6");
        COLUMN_MAP.put(3, "ui-grid-col-4");
        COLUMN_MAP.put(4, "ui-grid-col-3");
        COLUMN_MAP.put(6, "ui-grid-col-2");
        COLUMN_MAP.put(12, "ui-grid-col-1");
    }
    
    private GridLayoutUtils() {}
    
    public static String getColumnClass(int columns) {
        return COLUMN_MAP.get(columns);
    }
}
