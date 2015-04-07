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
package org.primefaces.model.diagram.endpoint;

public enum EndPointAnchor {
    ASSIGN("Assign"),
    AUTO_DEFAULT("AutoDefault"),
    BOTTOM("Bottom"),
    BOTTOM_LEFT("BottomLeft"),
    BOTTOM_RIGHT("BottomRight"),
    CENTER("Center"),
    CONTINUOUS("Continuous"),
    CONTINUOUS_LEFT("ContinuousLeft"),
    CONTINUOUS_RIGHT("ContinuousRight"),
    CONTINUOUS_TOP("ContinuousTop"),
    CONTINUOUS_BOTTOM("ContinuousBottom"),
    LEFT("Left"),
    PERIMETER("Perimeter"),
    RIGHT("Right"),
    TOP("Top"),
    TOP_LEFT("TopLeft"),
    TOP_RIGHT("TopRight");
    
    private final String text;
    
    private EndPointAnchor(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}

