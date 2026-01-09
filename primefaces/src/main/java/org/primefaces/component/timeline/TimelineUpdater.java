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
package org.primefaces.component.timeline;

import org.primefaces.model.timeline.TimelineEvent;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.faces.component.search.SearchExpressionContext;
import jakarta.faces.component.search.SearchExpressionHint;
import jakarta.faces.context.FacesContext;

public abstract class TimelineUpdater {

    protected String clientId;

    // serialization
    public TimelineUpdater() {
        super();
    }

    public TimelineUpdater(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets the current thread-safe TimelineUpdater instance.
     *
     * @param expression The expression to find the Timeline instance.
     * @return TimelineUpdater instance.
     * @throws jakarta.faces.component.search.ComponentNotFoundException if the Timeline component can not be found by the given expression.
     */
    public static TimelineUpdater getCurrentInstance(String expression) {
        FacesContext context = FacesContext.getCurrentInstance();

        @SuppressWarnings("unchecked")
        Map<String, TimelineUpdater> map = (Map<String, TimelineUpdater>) context.getAttributes().get(TimelineUpdater.class.getName());
        if (map == null) {
            return null;
        }

        AtomicReference<String> widgetVar = new AtomicReference<>();

        SearchExpressionContext sec = SearchExpressionContext.createSearchExpressionContext(context,
                context.getViewRoot(), EnumSet.of(SearchExpressionHint.RESOLVE_SINGLE_COMPONENT), null);
        context.getApplication().getSearchExpressionHandler().resolveComponent(
                sec,
                expression,
                (ctx, target) -> {
                    Timeline timeline = (Timeline) target;
                    widgetVar.set(timeline.resolveWidgetVar(context));
                });

        return map.get(widgetVar.get());
    }

    public abstract void add(TimelineEvent<?> event);

    public abstract void update(TimelineEvent<?> event);

    public abstract void delete(String id);

    public abstract void select(String id);

    public abstract void clear();
}
