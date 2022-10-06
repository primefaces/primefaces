/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
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
package org.primefaces.model.charts.axes.cartesian;

import java.io.IOException;
import java.io.Serializable;

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Common time options for cartesian types
 * From ChartJs version 3.8.0
 * @see <a href="https://www.chartjs.org/docs/3.8.0/axes/cartesian/time.html#time-units">ChartJS Time</a>
 */
public abstract class CartesianTime implements Serializable {

    private static final long serialVersionUID = 1L;

    private Number isoWeekday = 1; // default to Monday
    private Number stepSize;
    private String displayFormats;
    private String parser;
    private String round;
    private String tooltipFormat;
    private String unit;
    private String minUnit;

    public String getDisplayFormats() {
        return displayFormats;
    }

    public void setDisplayFormats(String displayFormats) {
        this.displayFormats = displayFormats;
    }

    public Number getIsoWeekday() {
        return isoWeekday;
    }

    public void setIsoWeekday(Number isoWeekday) {
        this.isoWeekday = isoWeekday;
    }

    public String getParser() {
        return parser;
    }

    public void setParser(String parser) {
        this.parser = parser;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getTooltipFormat() {
        return tooltipFormat;
    }

    public void setTooltipFormat(String tooltipFormat) {
        this.tooltipFormat = tooltipFormat;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Number getStepSize() {
        return stepSize;
    }

    public void setStepSize(Number stepSize) {
        this.stepSize = stepSize;
    }

    public String getMinUnit() {
        return minUnit;
    }

    public void setMinUnit(String minUnit) {
        this.minUnit = minUnit;
    }

    /**
     * Write the common ticks options
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        try (FastStringWriter fsw = new FastStringWriter()) {
            ChartUtils.writeDataValue(fsw, "isoWeekday", this.isoWeekday, false);
            ChartUtils.writeDataValue(fsw, "stepSize", this.stepSize, true);
            ChartUtils.writeDataValue(fsw, "displayFormats", this.displayFormats, true);
            ChartUtils.writeDataValue(fsw, "parser", parser, true);
            ChartUtils.writeDataValue(fsw, "round", this.round, true);
            ChartUtils.writeDataValue(fsw, "tooltipFormat", this.tooltipFormat, true);
            ChartUtils.writeDataValue(fsw, "unit", this.unit, true);
            ChartUtils.writeDataValue(fsw, "minUnit", this.minUnit, true);

            return fsw.toString();
        }
    }
}
