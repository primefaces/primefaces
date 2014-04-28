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

import java.util.ArrayList;
import java.util.List;

/**
 * @author jagatai
 */
public class BeanUtils {
    
    private static List<Class<?>> primitiveTypes;
    
    static {
        primitiveTypes = new ArrayList<Class<?>>();
        primitiveTypes.add(Boolean.class);
        primitiveTypes.add(Byte.class);
        primitiveTypes.add(Character.class);
        primitiveTypes.add(Double.class);
        primitiveTypes.add(Float.class);
        primitiveTypes.add(Integer.class);
        primitiveTypes.add(Long.class);
        primitiveTypes.add(Short.class);
        primitiveTypes.add(String.class);
    }
    
    public static boolean isBean(Class<?> valueClass) {
        if (valueClass.isArray()) {
            return isBean(valueClass.getComponentType());
        } else if (valueClass.isPrimitive()) {
            return false;
        } else {
            return !primitiveTypes.contains(valueClass);
        }
    }
}
