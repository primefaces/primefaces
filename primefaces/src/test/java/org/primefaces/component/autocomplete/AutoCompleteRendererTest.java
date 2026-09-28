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
package org.primefaces.component.autocomplete;

import org.primefaces.component.column.Column;
import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoCompleteRendererTest {

    private static Map<String, List<String>> suggestions() {
        return Collections.singletonMap("group", Arrays.asList("one", "two"));
    }

    private static AutoComplete autoComplete() {
        AutoComplete autoComplete = new AutoComplete();
        autoComplete.setId("autocomplete");
        autoComplete.getChildren().add(new Column());
        return autoComplete;
    }

    /**
     * The advanced (column based) layout renders a table, so it takes the generic table modifier classes
     * the themes ship. They have to land on the table element itself, alongside the component's own
     * classes, because that is what the theme rules are anchored on.
     */
    @Test
    void encodeSuggestionsAsTableWritesAdvancedStyleClass() throws Exception {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);

        AutoComplete autoComplete = autoComplete();
        autoComplete.setAdvancedStyleClass("ui-table-gridlines ui-table-sm");

        new AutoCompleteRenderer().encodeSuggestionsAsTable(context, autoComplete, suggestions(), null);

        String html = writer.toString();
        assertTrue(html.contains("class=\"" + AutoComplete.TABLE_CLASS + " ui-table-gridlines ui-table-sm\""), html);
    }

    @Test
    void encodeSuggestionsAsTableWithoutAdvancedStyleClass() throws Exception {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);

        new AutoCompleteRenderer().encodeSuggestionsAsTable(context, autoComplete(), suggestions(), null);

        String html = writer.toString();
        assertTrue(html.contains("class=\"" + AutoComplete.TABLE_CLASS + "\""), html);
    }
}
