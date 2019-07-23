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
    public List<MenuElement> getElements() {
        return elements;
    }

    @Override
    public void generateUniqueIds() {
        generateUniqueIds(getElements(), null);
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
