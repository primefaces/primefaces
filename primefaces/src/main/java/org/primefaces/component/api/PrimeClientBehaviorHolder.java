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
package org.primefaces.component.api;

import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import jakarta.faces.event.BehaviorEvent;

public interface PrimeClientBehaviorHolder extends org.primefaces.cdk.api.component.PrimeClientBehaviorHolder {

    Set<String> DEFAULT_SELECT_EVENT_NAMES =
            Set.of("blur", "change", "valueChange", "click", "dblclick", "focus", "keydown", "keypress", "keyup",
                    "mousedown", "mousemove", "mouseout", "mouseover", "mouseup");

    Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping();

    @Override
    default Collection<String> getImplicitBehaviorEventNames() {
        return Collections.emptyList(); // not required yet
    }

    // just for now until all components using CDK
    @Override
    default PrimeClientBehaviorEventKeys[] getClientBehaviorEventKeys() {
        return null;
    }
}
