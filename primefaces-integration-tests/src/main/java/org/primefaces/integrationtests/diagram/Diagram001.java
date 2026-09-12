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
package org.primefaces.integrationtests.diagram;

import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Diagram001 implements Serializable {

    private static final long serialVersionUID = 1L;

    private DiagramModel model;

    @PostConstruct
    public void init() {
        model = new DefaultDiagramModel();

        model.addElement(createElement("A", "5em", "5em"));
        model.addElement(createElement("B", "20em", "5em"));
    }

    public void select(String data) {
        TestUtils.addMessage("Selected", data);
    }

    private Element createElement(String data, String x, String y) {
        Element element = new Element(data, x, y);
        element.setId(data);
        // dragging would swallow the click in the browser
        element.setDraggable(false);
        return element;
    }

}
