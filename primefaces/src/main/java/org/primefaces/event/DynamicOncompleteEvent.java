/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.event;

import org.primefaces.PrimeFaces;
import org.primefaces.component.api.AjaxSource;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.Behavior;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.BehaviorEvent;
import jakarta.faces.event.FacesListener;
import jakarta.faces.event.PhaseId;

/**
 * Queue this event during decode via {@link #queue(FacesContext, UIComponent, AjaxSource, PhaseId)} in case your {@code oncomplete} attribute is a
 * {@link ValueExpression}. This way the value will be re-evaluated and executed via {@link PrimeFaces#executeScript(String)}, also when the lifecycle
 * skips the phase of this event, e.g. on a validation failure.
 */
public final class DynamicOncompleteEvent extends BehaviorEvent {

    @Serial private static final long serialVersionUID = 1L;

    private static final String PENDING_ONCOMPLETES_KEY = DynamicOncompleteEvent.class.getName();

    private DynamicOncompleteEvent(UIComponent component, Oncomplete oncomplete, PhaseId phaseId) {
        super(component, oncomplete);
        setPhaseId(phaseId);
    }

    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        return false;
    }

    @Override
    public void processListener(FacesListener listener) {
        // NOOP
    }

    /**
     * Queues a new {@link DynamicOncompleteEvent} on the given component and keeps its {@code oncomplete} pending until it is broadcast.
     * A pending {@code oncomplete} is executed by {@link #executePending(FacesContext)} during render response.
     *
     * @param context the current faces context
     * @param component the component to queue the event on
     * @param ajaxSource the ajax source providing the {@code oncomplete}
     * @param phaseId the phase to broadcast the event in
     */
    public static void queue(FacesContext context, UIComponent component, AjaxSource ajaxSource, PhaseId phaseId) {
        Oncomplete oncomplete = new Oncomplete(ajaxSource, component.getClientId(context));
        getPendingOncompletes(context).add(oncomplete);
        component.queueEvent(new DynamicOncompleteEvent(component, oncomplete, phaseId));
    }

    /**
     * Executes the {@code oncomplete} of the events queued via {@link #queue(FacesContext, UIComponent, AjaxSource, PhaseId)} which were never
     * broadcast, because the lifecycle skipped their phase, e.g. on a validation failure.
     *
     * @param context the current faces context
     */
    @SuppressWarnings("unchecked")
    public static void executePending(FacesContext context) {
        List<Oncomplete> pendingOncompletes = (List<Oncomplete>) context.getAttributes().remove(PENDING_ONCOMPLETES_KEY);

        if (pendingOncompletes != null) {
            for (Oncomplete oncomplete : pendingOncompletes) {
                context.getViewRoot().invokeOnComponent(context, oncomplete.clientId, (ctx, target) -> oncomplete.execute());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Oncomplete> getPendingOncompletes(FacesContext context) {
        return (List<Oncomplete>) context.getAttributes().computeIfAbsent(PENDING_ONCOMPLETES_KEY, k -> new ArrayList<>(1));
    }

    private static final class Oncomplete implements Behavior {

        private final AjaxSource ajaxSource;
        private final String clientId;

        private Oncomplete(AjaxSource ajaxSource, String clientId) {
            this.ajaxSource = ajaxSource;
            this.clientId = clientId;
        }

        @Override
        public void broadcast(BehaviorEvent event) {
            getPendingOncompletes(event.getFacesContext()).remove(this);
            execute();
        }

        private void execute() {
            String oncomplete = ajaxSource.getOncomplete();

            if (oncomplete != null) {
                PrimeFaces.current().executeScript(oncomplete);
            }
        }
    }
}
