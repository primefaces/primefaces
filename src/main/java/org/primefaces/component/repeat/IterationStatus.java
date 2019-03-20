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
