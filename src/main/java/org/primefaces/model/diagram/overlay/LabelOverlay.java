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
package org.primefaces.model.diagram.overlay;

import org.primefaces.util.EscapeUtils;

import java.io.Serializable;

public class LabelOverlay implements Overlay, Serializable {

    private static final long serialVersionUID = 1L;

    private String label;

    private String styleClass;

    private double location = 0.5;

    public LabelOverlay() {
    }

    public LabelOverlay(String label) {
        this.label = label;
    }

    public LabelOverlay(String label, String styleClass, double location) {
        this(label);
        this.styleClass = styleClass;
        this.location = location;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public double getLocation() {
        return location;
    }

    public void setLocation(double location) {
        this.location = location;
    }

    @Override
    public String getType() {
        return "Label";
    }

    @Override
    public String toJS(StringBuilder sb) {
        sb.append("['Label',{label:\"").append(EscapeUtils.forJavaScript(label)).append("\"");

        if (styleClass != null) sb.append(",cssClass:'").append(styleClass).append("'");
        if (location != 0.5) sb.append(",location:").append(location);

        sb.append("}]");

        return sb.toString();
    }
}
