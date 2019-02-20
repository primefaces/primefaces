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
package org.primefaces.model.chart;

public enum AxisType {

    X("xaxis"), Y("yaxis"),
    X2("x2axis"), Y2("y2axis"),
    X3("x3axis"), Y3("y3axis"),
    X4("x4axis"), Y4("y4axis"),
    X5("x5axis"), Y5("y5axis"),
    X6("x6axis"), Y6("y6axis"),
    X7("x7axis"), Y7("y7axis"),
    X8("x8axis"), Y8("y8axis"),
    X9("x9axis"), Y9("y9axis");

    private String text;

    AxisType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
