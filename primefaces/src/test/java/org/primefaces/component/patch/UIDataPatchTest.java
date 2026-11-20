/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.component.patch;

import java.util.HashMap;

import jakarta.faces.component.UIOutput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.FacesEvent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UIDataPatchTest {

    /**
     * #15531: an exception thrown by a row event listener must not leave the table positioned on the event's row.
     */
    @Test
    void broadcastRestoresRowIndexWhenListenerThrows() {
        FacesContext context = mock(FacesContext.class);
        when(context.getAttributes()).thenReturn(new HashMap<>());

        TestData data = new TestData();
        UIOutput source = new UIOutput() {
            @Override
            public void broadcast(FacesEvent event) {
                throw new AbortProcessingException("abort");
            }
        };

        WrapperEvent event = new WrapperEvent(data, new ActionEvent(source), 0) {
            @Override
            public FacesContext getFacesContext() {
                return context;
            }
        };

        assertThrows(AbortProcessingException.class, () -> data.broadcast(event));
        assertEquals(-1, data.getRowIndex());
    }

    private static class TestData extends UIDataPatch {

        private int rowIndex = -1;

        @Override
        public int getRowIndex() {
            return rowIndex;
        }

        @Override
        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        @Override
        protected Boolean isNestedWithinIterator() {
            return false;
        }
    }
}
