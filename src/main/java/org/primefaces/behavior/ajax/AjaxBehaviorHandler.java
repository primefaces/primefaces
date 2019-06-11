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
package org.primefaces.behavior.ajax;

import org.primefaces.behavior.base.AbstractBehaviorHandler;
import org.primefaces.component.api.PrimeClientBehaviorHolder;

import javax.faces.component.UIComponent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.view.facelets.BehaviorConfig;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import java.util.Map;

public class AjaxBehaviorHandler extends AbstractBehaviorHandler<AjaxBehavior> {

    private static final Class[] EMPTY_PARAMS = new Class[]{};
    private static final Class[] ARG_PARAMS = new Class[]{AjaxBehaviorEvent.class};

    public AjaxBehaviorHandler(BehaviorConfig config) {
        super(config);
    }

    @Override
    public String getBehaviorId() {
        return AjaxBehavior.BEHAVIOR_ID;
    }

    @Override
    protected void init(FaceletContext ctx, AjaxBehavior behavior, String eventName, UIComponent parent) {
        super.init(ctx, behavior, eventName, parent);
        TagAttribute listener = getAttribute(AjaxBehavior.PropertyKeys.listener.name());

        if (listener != null) {

            Class<? extends BehaviorEvent> eventMappingClass = null;

            if (parent instanceof PrimeClientBehaviorHolder) {
                Map<String, Class<? extends BehaviorEvent>> mapping = ((PrimeClientBehaviorHolder) parent).getBehaviorEventMapping();
                if (mapping != null) {
                    eventMappingClass = mapping.get(eventName);
                }
            }

            if (eventMappingClass == null) {
                behavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(
                        listener.getMethodExpression(ctx, Void.class, EMPTY_PARAMS),
                        listener.getMethodExpression(ctx, Void.class, ARG_PARAMS)));
            }
            else {
                behavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(
                        listener.getMethodExpression(ctx, Void.class, EMPTY_PARAMS),
                        listener.getMethodExpression(ctx, Void.class, ARG_PARAMS),
                        listener.getMethodExpression(ctx, Void.class, new Class[]{eventMappingClass})));
            }
        }
    }
}
