/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.model.diagram.connector;

public class FlowChartConnector extends Connector {
    
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

    public String getType() {
        return "Flowchart";
    }

    public String toJS(StringBuilder sb) {
        return sb.append("['Flowchart',{stub:").append(stub).append(",gap:").append(gap).append(",cornerRadius:").append(cornerRadius)
                        .append(",alwaysRespectStubs:").append(alwaysRespectStubs).append("}]").toString();
    }
}
