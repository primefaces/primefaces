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
package org.primefaces.cdk.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a client behavior event for a Faces component.
 *
 * <p>Used to declare what events a component supports for attaching client behaviors (e.g., AJAX).
 * Events can be either implicit (automatically handled by the renderer) or explicit (user-defined).</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * @FacesBehaviorEvent(name = "page", event = PageEvent.class, implicit = true)
 * @FacesBehaviorEvent(name = "rowSelect", event = SelectEvent.class)
 * public abstract class DataTableBase extends UIData {
 * }
 * }</pre>
 *
 * @see FacesBehaviorEvents
 * @see jakarta.faces.component.behavior.ClientBehaviorHolder
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(FacesBehaviorEvents.class)
public @interface FacesBehaviorEvent {

    /**
     * The event name (e.g., "page", "sort", "filter").
     *
     * @return the event name
     */
    String name();

    /**
     * The event class that will be fired.
     *
     * @return the BehaviorEvent class
     */
    Class<? extends jakarta.faces.event.BehaviorEvent> event();

    /**
     * Optional description of the event's purpose.
     *
     * @return the description
     */
    String description() default "";

    /**
     * Whether this is an implicit event automatically handled by the renderer.
     *
     * <p>When true, the renderer provides this behavior automatically even if no
     * explicit ajax behavior is attached. For example, DataTable's "page" event
     * is implicit because the renderer always includes pagination AJAX handling.</p>
     *
     * <p>When false (default), the event is only active when explicitly defined by the user.</p>
     *
     * @return true if this is an implicit/built-in event, false otherwise
     */
    boolean implicit() default false;

    /**
     * Whether this is the default event.
     *
     * <p>When true, the event is the default event for the component. For example, TabViews's "tabChange" event
     * is the default event because it is the event that is fired when the user switches to a different tab.</p>
     *
     * @return true if this is the default event, false otherwise
     */
    boolean defaultEvent() default false;
}