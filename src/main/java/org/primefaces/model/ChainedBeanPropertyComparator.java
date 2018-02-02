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
package org.primefaces.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChainedBeanPropertyComparator implements Comparator {

    private List<BeanPropertyComparator> comparators;

    public ChainedBeanPropertyComparator() {
        comparators = new ArrayList<BeanPropertyComparator>();
    }

    public void addComparator(BeanPropertyComparator comparator) {
        this.comparators.add(comparator);
    }

    public int compare(Object obj1, Object obj2) {
        for (BeanPropertyComparator comparator : comparators) {
            int result = comparator.compare(obj1, obj2);

            if (result == 0) {
                continue;
            }
            else {
                return result;
            }
        }

        return 0;
    }
}
