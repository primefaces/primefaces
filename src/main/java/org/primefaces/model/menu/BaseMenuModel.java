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
package org.primefaces.model.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation for model of a programmatic menu
 */
public class BaseMenuModel implements MenuModel, Serializable {

    public static final String ID_SEPARATOR = "_";

    private static final long serialVersionUID = 1L;


    private List<MenuElement> elements;

    public BaseMenuModel() {
        elements = new ArrayList<>();
    }

    @Override
    public void addElement(MenuElement element) {
        elements.add(element);
    }

    @Override
    public List<MenuElement> getElements() {
        return elements;
    }

    @Override
    public void generateUniqueIds() {
        this.generateUniqueIds(getElements(), null);
    }

    private void generateUniqueIds(List<MenuElement> elements, String seed) {
        if (elements == null || elements.isEmpty()) {
            return;
        }

        int counter = 0;

        for (MenuElement element : elements) {
            String id = (seed == null) ? String.valueOf(counter++) : seed + ID_SEPARATOR + counter++;
            element.setId(id);

            if (element instanceof MenuGroup) {
                generateUniqueIds(((MenuGroup) element).getElements(), id);
            }
        }
    }
}
