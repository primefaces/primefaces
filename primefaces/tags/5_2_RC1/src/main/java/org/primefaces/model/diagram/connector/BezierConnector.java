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
public class BezierConnector extends Connector {
    
    private int curviness = 150;
    
    private int stub = 0;

    public BezierConnector() {
    }
    
    public BezierConnector(int curviness, int stub) {
        this.curviness = curviness;
        this.stub = stub;
    }
    
    public int getCurviness() {
        return curviness;
    }

    public void setCurviness(int curviness) {
        this.curviness = curviness;
    }

    public int getStub() {
        return stub;
    }

    public void setStub(int stub) {
        this.stub = stub;
    }

    public String getType() {
        return "Bezier";
    }

    public String toJS(StringBuilder sb) {
        return sb.append("['Bezier',{curviness:").append(curviness).append(",stub:").append(stub).append("}]").toString();
    }
}
