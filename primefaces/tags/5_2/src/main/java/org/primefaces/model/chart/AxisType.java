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
package org.primefaces.model.chart;

public enum AxisType {
    
    X("xaxis"),Y("yaxis"),
    X2("x2axis"),Y2("y2axis"),
    X3("x3axis"),Y3("y3axis"),
    X4("x4axis"),Y4("y4axis"),
    X5("x5axis"),Y5("y5axis"),
    X6("x6axis"),Y6("y6axis"),
    X7("x7axis"),Y7("y7axis"),
    X8("x8axis"),Y8("y8axis"),
    X9("x9axis"),Y9("y9axis");
    
    private String text;
    
    AxisType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
