/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.behavior.ajax;

import org.primefaces.cdk.api.FacesBehaviorDescription;
import org.primefaces.cdk.api.FacesBehaviorHandler;
import org.primefaces.component.api.AjaxSource;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.behavior.ClientBehaviorHint;
import jakarta.faces.component.behavior.FacesBehavior;
import jakarta.faces.event.AjaxBehaviorListener;

@FacesBehavior(AjaxBehavior.BEHAVIOR_ID)
@FacesBehaviorHandler(AjaxBehaviorHandler.class)
@FacesBehaviorDescription("AjaxBehavior is an extension to standard f:ajax.")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
public class AjaxBehavior extends AjaxBehaviorBaseImpl implements AjaxSource {

    public static final String BEHAVIOR_ID = "org.primefaces.component.AjaxBehavior";

    private static final Set<ClientBehaviorHint> HINTS = Collections.unmodifiableSet(EnumSet.of(ClientBehaviorHint.SUBMITTING));

    @Override
    public String getRendererType() {
        return "org.primefaces.component.AjaxBehaviorRenderer";
    }

    @Override
    public Set<ClientBehaviorHint> getHints() {
        return HINTS;
    }

    public boolean isImmediateSet() {
        return getStateHelper().eval(PropertyKeys.immediate, null) != null;
    }

    @Override
    public boolean isPartialSubmitSet() {
        return getStateHelper().eval(PropertyKeys.partialSubmit, null) != null;
    }

    @Override
    public boolean isResetValuesSet() {
        return getStateHelper().eval(PropertyKeys.resetValues, null) != null;
    }

    @Override
    public boolean isAjaxified() {
        return true;
    }

    public void addAjaxBehaviorListener(AjaxBehaviorListener listener) {
        addBehaviorListener(listener);
    }

    public void removeAjaxBehaviorListener(AjaxBehaviorListener listener) {
        removeBehaviorListener(listener);
    }
}
