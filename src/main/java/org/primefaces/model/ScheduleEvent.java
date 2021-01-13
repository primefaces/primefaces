/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.model;

import java.time.LocalDateTime;
import java.util.Map;

public interface ScheduleEvent<T> {

    String getId();

    void setId(String id);

    String getGroupId();

    T getData();

    String getTitle();

    LocalDateTime getStartDate();

    void setStartDate(LocalDateTime start);

    LocalDateTime getEndDate();

    void setEndDate(LocalDateTime end);

    boolean isAllDay();

    void setAllDay(boolean allDay);

    String getStyleClass();

    /**
     * The rendering type of this event. Can be 'auto', 'block', 'list-item', 'background', 'inverse-background', or 'none'.
     * Events that appear as background highlights can be achieved by setting an Event Object’s display property
     * to "background" or "inverse-background".
     */
    String getDisplay();

    String getBackgroundColor();

    String getBorderColor();

    String getTextColor();

    /**
     * @return Whether the event should be draggable. Returning {@code null}
     * means that the default of the schedule is applied. Otherwise, this
     * setting overrides the default of the schedule.
     */
    Boolean isDraggable();

    /**
     * @return Whether the event should be resizable. Returning {@code null}
     * means that the default of the schedule is applied. Otherwise, this
     * setting overrides the default of the schedule.
     */
    Boolean isResizable();

    boolean isOverlapAllowed();

    String getDescription();

    String getUrl();

    ScheduleRenderingMode getRenderingMode();

    Map<String, Object> getDynamicProperties();

    /**
     * @deprecated Use {@link #isResizable()} or {@link #isDraggable()} instead.
     */
    @Deprecated
    default boolean isEditable() {
        return isDraggable() != null && isResizable() != null && (isDraggable() || isResizable());
    }

}
