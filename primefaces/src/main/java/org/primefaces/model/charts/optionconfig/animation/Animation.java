/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.model.charts.optionconfig.animation;

import java.io.IOException;
import java.io.Serializable;

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * The animaton configuration is passed into the options.animation namespace.
 */
public class Animation implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean loop;
    private Number duration;
    private String easing;
    private Number delay;

    public boolean isLoop() {
        return loop;
    }
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    public Number getDuration() {
        return duration;
    }
    public void setDuration(Number duration) {
        this.duration = duration;
    }
    public String getEasing() {
        return easing;
    }
    public void setEasing(String easing) {
        this.easing = easing;
    }
    public Number getDelay() {
        return delay;
    }
    public void setDelay(Number delay) {
        this.delay = delay;
    }

    /**
     * Write the options of Title
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        try (FastStringWriter fsw = new FastStringWriter()) {
            ChartUtils.writeDataValue(fsw, "loop", this.loop, false);
            ChartUtils.writeDataValue(fsw, "duration", this.duration, true);
            ChartUtils.writeDataValue(fsw, "easing", this.easing, true);
            ChartUtils.writeDataValue(fsw, "delay", this.delay, true);
            return fsw.toString();
        }
    }


}
