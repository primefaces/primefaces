/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.repeat;

import java.io.Serializable;

/**
 * @author Jacob Hookom
 * @version $Id: IterationStatus.java 8641 2010-10-04 20:54:50Z edburns $
 */
public class IterationStatus implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final int index;
    private final boolean first;
    private final boolean last;
    private final Integer begin;
    private final Integer end;
    private final Integer step;
    private final boolean even;
    private final Object current;
    private final int iterationCount;

    // ------------------------------------------------------------ Constructors

    /**
     * Constructor used for ui:repeat.
     */
    public IterationStatus(boolean first,
                           boolean last,
                           int index,
                           Integer begin,
                           Integer end,
                           Integer step) {
        this(first, last, index, begin, end, step, null, 0);
    }

    /**
     * Constructor used for c:forEach varStatus
     */
    public IterationStatus(boolean first,
                           boolean last,
                           int index,
                           Integer begin,
                           Integer end,
                           Integer step,
                           Object current,
                           int iterationCount) {
        this.index = index;
        this.begin = begin;
        this.end = end;
        this.step = step;
        this.first = first;
        this.last = last;
        this.current = current;
        int iBegin = ((begin != null) ? begin : 0);
        int iStep = ((step != null) ? step : 1);
        even = ((index - iBegin) / iStep) % 2 == 0;
        this.iterationCount = iterationCount;
    }

    // ---------------------------------------------- Methods from LoopTagStatus
    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public Integer getBegin() {
        return begin;
    }

    public Integer getEnd() {
        return end;
    }

    public int getIndex() {
        return index;
    }

    public Integer getStep() {
        return step;
    }

    public Object getCurrent() {
        return current;
    }

    public int getCount() {
        return iterationCount;
    }

    // ---------------------------------------------------------- Public Methods
    public boolean isEven() {
        return even;
    }

    public boolean isOdd() {
        return !even;
    }

    @Override
    public String toString() {
        return "IterationStatus{"
                + "index=" + index
                + ", first=" + first
                + ", last=" + last
                + ", begin=" + begin
                + ", end=" + end
                + ", step=" + step
                + ", even=" + even
                + ", current=" + current
                + ", iterationCount=" + iterationCount
                + '}';
    }
}
