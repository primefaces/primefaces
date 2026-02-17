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
package org.primefaces.cdk.impl.subclass;

import org.primefaces.cdk.impl.container.BehaviorEventInfo;
import org.primefaces.cdk.impl.container.FacetInfo;
import org.primefaces.cdk.impl.container.PropertyInfo;

import java.util.Collections;
import java.util.List;

public final class HierarchyScannerResult {

    private final List<PropertyInfo> properties;
    private final List<FacetInfo> facets;
    private final List<BehaviorEventInfo> behaviorEvents;

    public HierarchyScannerResult(List<PropertyInfo> properties,
                                  List<FacetInfo> facets,
                                  List<BehaviorEventInfo> behaviorEvents) {
        this.properties = Collections.unmodifiableList(properties);
        this.facets = Collections.unmodifiableList(facets);
        this.behaviorEvents = Collections.unmodifiableList(behaviorEvents);
    }

    public static HierarchyScannerResult empty() {
        return new HierarchyScannerResult(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public List<PropertyInfo> getProperties() {
        return properties;
    }

    public List<FacetInfo> getFacets() {
        return facets;
    }

    public List<BehaviorEventInfo> getBehaviorEvents() {
        return behaviorEvents;
    }
}