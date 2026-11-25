/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek
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
package org.primefaces.renderkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.primefaces.component.selectonemenu.SelectOneMenu;

/**
 * GitHub #13954: disabled select items in {@link SelectRenderer#validateSubmittedValues}.
 */
public class SelectRendererValidateSubmittedValuesTest {

    private SelectRenderer renderer;
    private FacesContext context;
    private SelectOneMenu component;

    @BeforeEach
    public void setup() {
        renderer = mock(SelectRenderer.class, CALLS_REAL_METHODS);

        context = mock(FacesContext.class);
        component = new SelectOneMenu();

        List<SelectItem> items = Arrays.asList(
                new SelectItem("", "Select", null, true, true, true), // disabled placeholder
                new SelectItem(1, "One"),
                new SelectItem(2, "Two (disabled)", null, true),
                new SelectItem(3, "Three (disabled)", null, true));
        doReturn(items).when(renderer).getSelectItems(any(), any());
    }

    private List<String> validate(Object[] oldValues, List<String> disabled, String... submittedValues) {
        return renderer.validateSubmittedValues(context, component, oldValues, disabled, submittedValues);
    }

    @Test
    public void enabledItem() {
        List<String> disabled = new ArrayList<>();
        assertEquals(Collections.singletonList("1"), validate(null, disabled, "1"));
        assertEquals(Collections.emptyList(), disabled);
    }

    @Test
    public void disabledItemNotCheckedBefore() {
        List<String> disabled = new ArrayList<>();
        assertEquals(Collections.emptyList(), validate(new Object[]{1}, disabled, "2"));
        assertEquals(Collections.singletonList("2"), disabled);
    }

    @Test
    public void checkedDisabledIntegerValueIsRestored() {
        // the model value is an Integer, the select item values are compared as strings
        List<String> disabled = new ArrayList<>();
        assertEquals(Collections.singletonList("2"), validate(new Object[]{2}, disabled, ""));
        assertEquals(Collections.emptyList(), disabled);
    }

    @Test
    public void otherDisabledItemWithCheckedDisabledValue() {
        List<String> disabled = new ArrayList<>();
        assertEquals(Collections.singletonList("2"), validate(new Object[]{2}, disabled, "3"));
        assertEquals(Collections.singletonList("3"), disabled);
    }

    @Test
    public void disabledPlaceholderIsNotADisabledItem() {
        List<String> disabled = new ArrayList<>();
        assertEquals(Collections.emptyList(), validate(null, disabled, ""));
        assertEquals(Collections.emptyList(), disabled);
    }

    @Test
    public void withoutDisabledList() {
        assertEquals(Collections.emptyList(), validate(null, null, "2"));
    }
}
