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
package org.primefaces.component.messages;

import org.primefaces.mock.FacesContextMock;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesRendererTest {

    @Test
    void collectFacesMessagesWithEmptyIgnores() {
        FacesContext context = new FacesContextMock();
        context.addMessage("test1", new FacesMessage());
        context.addMessage("test2", new FacesMessage());
        context.addMessage("test3", new FacesMessage());

        Messages messages = Mockito.mock(Messages.class);
        Mockito.when(messages.getFor()).thenReturn("");
        Mockito.when(messages.isGlobalOnly()).thenReturn(false);
        Mockito.when(messages.getForIgnores()).thenReturn("");

        MessagesRenderer renderer = new MessagesRenderer();

        assertEquals(3, renderer.collectFacesMessages(messages, context).size());
    }

    @Test
    void collectFacesMessagesWithNullIgnores() {
        FacesContext context = new FacesContextMock();
        context.addMessage("test1", new FacesMessage());
        context.addMessage("test2", new FacesMessage());
        context.addMessage("test3", new FacesMessage());

        Messages messages = Mockito.mock(Messages.class);
        Mockito.when(messages.getFor()).thenReturn("");
        Mockito.when(messages.isGlobalOnly()).thenReturn(false);
        Mockito.when(messages.getForIgnores()).thenReturn("");

        MessagesRenderer renderer = new MessagesRenderer();

        assertEquals(3, renderer.collectFacesMessages(messages, context).size());
    }

    @Test
    void collectFacesMessagesWithIgnores() {
        FacesContext context = new FacesContextMock();
        context.addMessage("test1", new FacesMessage());
        context.addMessage("test2", new FacesMessage());
        context.addMessage("test3", new FacesMessage());
        context.addMessage("test3", new FacesMessage());

        Messages messages = Mockito.mock(Messages.class);
        Mockito.when(messages.getFor()).thenReturn("");
        Mockito.when(messages.isGlobalOnly()).thenReturn(false);
        Mockito.when(messages.getForIgnores()).thenReturn("test2");

        MessagesRenderer renderer = new MessagesRenderer();

        assertEquals(3, renderer.collectFacesMessages(messages, context).size());
    }

    @Test
    void collectFacesMessagesWithMultipleIgnores() {
        FacesContext context = new FacesContextMock();
        context.addMessage("test1", new FacesMessage());
        context.addMessage("test2", new FacesMessage());
        context.addMessage("test2", new FacesMessage());
        context.addMessage("test3", new FacesMessage());
        context.addMessage("test3", new FacesMessage());

        Messages messages = Mockito.mock(Messages.class);
        Mockito.when(messages.getFor()).thenReturn("");
        Mockito.when(messages.isGlobalOnly()).thenReturn(false);
        Mockito.when(messages.getForIgnores()).thenReturn("test3, test2");

        MessagesRenderer renderer = new MessagesRenderer();

        assertEquals(1, renderer.collectFacesMessages(messages, context).size());
    }
}
