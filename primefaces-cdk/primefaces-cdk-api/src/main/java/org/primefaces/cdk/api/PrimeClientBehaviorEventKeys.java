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
package org.primefaces.cdk.api;

import jakarta.faces.event.BehaviorEvent;

/**
 * Interface for behavior event key enums.
 *
 * <p>Generated BehaviorEventKeys enums for behaviors with {@link FacesBehaviorEvent}
 * annotations implement this interface to provide type-safe access to event
 * metadata including event names, classes, and whether they are implicit.</p>
 *
 * <p>Example generated enum:</p>
 * <pre>{@code
 * public enum BehaviorEventKeys implements PrimeBehaviorEventKeys {
 *     page("page", PageEvent.class, true),
 *     rowSelect("rowSelect", SelectEvent.class, false);
 *
 *     // implements getEventName(), getEventClass(), isImplicit()
 * }
 * }</pre>
 *
 * @see FacesBehaviorEvent
 */
public interface PrimeClientBehaviorEventKeys {

    /**
     * Returns the event name.
     *
     * <p>Default implementation returns the enum constant name via toString().</p>
     *
     * @return the event name
     */
    String getName();

    /**
     * Returns the BehaviorEvent class for this event.
     *
     * @return the event class
     */
    Class<? extends BehaviorEvent> getType();

    /**
     * Description of the event for documentation.
     *
     * @return the event description
     */
    String getDescription();

    /**
     * Returns whether this is an implicit event automatically handled by the renderer.
     *
     * @return true if implicit, false if explicit
     */
    boolean isImplicit();

    /**
     * Returns whether this is the default event.
     *
     * @return true if this is the default event, false otherwise
     */
    boolean isDefaultEvent();
}