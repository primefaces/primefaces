/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.component.api;

public class RepeatStatus {
    
    private static final long serialVersionUID = 1L;
    
    private final int count;
    private final int index;
    private final boolean first;
    private final boolean last;
    private final Integer begin;
    private final Integer end;
    private final Integer step;
    
    public RepeatStatus(boolean first, boolean last, int count, int index, Integer begin, Integer end, Integer step) {
        this.count = count;
        this.index = index;
        this.begin = begin;
        this.end = end;
        this.step = step;
        this.first = first;
        this.last = last;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }
    
    public boolean isEven() {
        return ((count % 2) == 0);
    }
    
    public boolean isOdd() {
        return !isEven();
    }
    
    public Integer getBegin() {
        return (begin == -1) ? null : begin;
    }
    
    public Integer getEnd() {
        return (end == -1) ? null : end;
    }

    public int getIndex() {
        return index;
    }

    public Integer getStep() {
        return (step == -1) ? null : step;
    }
}
