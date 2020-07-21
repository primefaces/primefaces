/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.model.diagram.connector;

public class FlowChartConnector extends Connector {

    private static final long serialVersionUID = 1L;

    private int stub = 30;

    private int gap = 0;

    private double cornerRadius = 0;

    private boolean alwaysRespectStubs;

    public FlowChartConnector() {
    }

    public FlowChartConnector(int stub, int gap, int cornerRadius, boolean alwaysRespectStubs) {
        this.stub = stub;
        this.gap = gap;
        this.cornerRadius = cornerRadius;
        this.alwaysRespectStubs = alwaysRespectStubs;
    }

    public int getStub() {
        return stub;
    }

    public void setStub(int stub) {
        this.stub = stub;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public double getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(double cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public boolean isAlwaysRespectStubs() {
        return alwaysRespectStubs;
    }

    public void setAlwaysRespectStubs(boolean alwaysRespectStubs) {
        this.alwaysRespectStubs = alwaysRespectStubs;
    }

    @Override
    public String getType() {
        return "Flowchart";
    }

    @Override
    public String toJS(StringBuilder sb) {
        return sb.append("['Flowchart',{stub:").append(stub).append(",gap:").append(gap).append(",cornerRadius:").append(cornerRadius)
                .append(",alwaysRespectStubs:").append(alwaysRespectStubs).append("}]").toString();
    }
}
