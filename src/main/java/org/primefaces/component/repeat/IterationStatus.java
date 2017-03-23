/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2005-2007 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
        this.even = ((index - iBegin) / iStep) % 2 == 0;
        this.iterationCount = iterationCount;
    }


    // ---------------------------------------------- Methods from LoopTagStatus


    public boolean isFirst() {
        return this.first;
    }

    public boolean isLast() {
        return this.last;
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
        return "IterationStatus{" +
               "index=" + index +
               ", first=" + first +
               ", last=" + last +
               ", begin=" + begin +
               ", end=" + end +
               ", step=" + step +
               ", even=" + even +
               ", current=" + current +
               ", iterationCount=" + iterationCount +
               '}';
    }
}
