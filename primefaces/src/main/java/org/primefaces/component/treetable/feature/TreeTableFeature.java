/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.treetable.feature;

import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.TreeTableRenderer;

import java.io.IOException;

import jakarta.faces.context.FacesContext;

public interface TreeTableFeature {

    boolean shouldDecode(FacesContext context, TreeTable table);

    boolean shouldEncode(FacesContext context, TreeTable table);

    default void decode(FacesContext context, TreeTable table) {
        if (!shouldDecode(context, table)) {
            throw new UnsupportedOperationException(getClass().getSimpleName() + " should not decode.");
        }
    }

    default void encode(FacesContext context, TreeTableRenderer renderer, TreeTable table) throws IOException {
        if (!shouldEncode(context, table)) {
            throw new UnsupportedOperationException(getClass().getSimpleName() + " should not encode.");
        }
    }
}
