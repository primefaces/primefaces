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
package org.primefaces.component.api;

import org.primefaces.component.tree.Tree;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.util.HashMap;

import javax.faces.component.UIOutput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UITreeTest {

    /**
     * #15531: an exception thrown by a node event listener must not leave the tree positioned on the event's node.
     */
    @Test
    void broadcastRestoresRowKeyWhenListenerThrows() {
        FacesContext context = mock(FacesContext.class);
        ExternalContext externalContext = mock(ExternalContext.class);
        when(context.getAttributes()).thenReturn(new HashMap<>());
        when(context.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getRequestMap()).thenReturn(new HashMap<>());

        TreeNode<String> root = new DefaultTreeNode<>("root", null);
        new DefaultTreeNode<>("child", root);

        Tree tree = new Tree() {
            @Override
            protected FacesContext getFacesContext() {
                return context;
            }
        };
        tree.setVar("node");
        tree.setValue(root);

        UIOutput source = new UIOutput() {
            @Override
            public void broadcast(FacesEvent event) {
                throw new AbortProcessingException("abort");
            }
        };

        WrapperEvent event = new WrapperEvent(tree, new ActionEvent(source), "0");

        assertThrows(AbortProcessingException.class, () -> tree.broadcast(event));
        assertNull(tree.getRowKey());
    }
}
