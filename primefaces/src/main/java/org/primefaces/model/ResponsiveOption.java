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
package org.primefaces.model;

import org.primefaces.util.EscapeUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

public class ResponsiveOption implements Serializable {

    private static final long serialVersionUID = 1L;

    private String breakpoint;
    private int numVisible;
    private int numScroll;

    public ResponsiveOption() {

    }

    public ResponsiveOption(String breakpoint, int numVisible) {
        this.breakpoint = breakpoint;
        this.numVisible = numVisible;
    }

    public ResponsiveOption(String breakpoint, int numVisible, int numScroll) {
        this(breakpoint, numVisible);
        this.numScroll = numScroll;
    }

    public String getBreakpoint() {
        return breakpoint;
    }

    public void setBreakpoint(String breakpoint) {
        this.breakpoint = breakpoint;
    }

    public int getNumVisible() {
        return numVisible;
    }

    public void setNumVisible(int numVisible) {
        this.numVisible = numVisible;
    }

    public int getNumScroll() {
        return numScroll;
    }

    public void setNumScroll(int numScroll) {
        this.numScroll = numScroll;
    }

    public void encode(Writer writer) throws IOException {
        writer.write("{");
        writer.write("breakpoint:\"" + EscapeUtils.forJavaScript(breakpoint) + "\"");
        writer.write(",numVisible:" + this.numVisible);
        writer.write(",numScroll:" + this.numScroll);
        writer.write("}");
    }
}
