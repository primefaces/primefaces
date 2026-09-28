/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.selectonelistbox;

import org.primefaces.component.column.Column;
import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;

import java.util.Arrays;
import java.util.List;

import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectOneListboxRendererTest {

    private static List<SelectItem> selectItems() {
        return Arrays.asList(new SelectItem("one", "One"), new SelectItem("two", "Two"));
    }

    private static SelectOneListbox selectOneListbox() {
        SelectOneListbox listbox = new SelectOneListbox();
        listbox.setId("selectonelistbox");
        listbox.setVar("item");
        listbox.getChildren().add(new Column());
        return listbox;
    }

    /**
     * The advanced (column based) layout renders a table, so it takes the generic table modifier classes
     * the themes ship. They have to land on the table element itself, alongside the component's own
     * classes, because that is what the theme rules are anchored on.
     */
    @Test
    void encodeListWritesAdvancedStyleClass() throws Exception {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);

        SelectOneListbox listbox = selectOneListbox();
        listbox.setAdvancedStyleClass("ui-table-gridlines");

        new SelectOneListboxRenderer().encodeList(context, listbox, selectItems());

        String html = writer.toString();
        assertTrue(html.contains("class=\"" + SelectOneListbox.LIST_CLASS + " ui-table-gridlines\""), html);
    }

    @Test
    void encodeListWithoutAdvancedStyleClass() throws Exception {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);

        new SelectOneListboxRenderer().encodeList(context, selectOneListbox(), selectItems());

        String html = writer.toString();
        assertTrue(html.contains("class=\"" + SelectOneListbox.LIST_CLASS + "\""), html);
    }
}
