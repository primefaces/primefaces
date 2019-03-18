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
package org.primefaces.model.charts.bar;

import java.io.IOException;
import java.util.List;

import org.primefaces.model.charts.ChartDataSet;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Used to provide DataSet objects to Bar chart component.
 */
public class BarChartDataSet extends ChartDataSet {

    private static final long serialVersionUID = 1L;

    private List<Number> data;
    private String label;
    private String xaxisID;
    private String yaxisID;
    private String stack;
    private Object backgroundColor;
    private Object borderColor;
    private Object borderWidth;
    private String borderSkipped;
    private Object hoverBackgroundColor;
    private Object hoverBorderColor;
    private Object hoverBorderWidth;

    /**
     * Gets the list of data in this dataSet
     *
     * @return List&#60;Number&#62; list of data
     */
    public List<Number> getData() {
        return data;
    }

    /**
     * Sets the list of data in this dataSet
     *
     * @param data List&#60;Number&#62; list of data
     */
    public void setData(List<Number> data) {
        this.data = data;
    }

    /**
     * Gets the label
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label
     *
     * @param label The label for the dataset which appears in the legend and tooltips
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets the xAxisID
     *
     * @return xAxisID
     */
    public String getXaxisID() {
        return xaxisID;
    }

    /**
     * Sets the xAxisID
     *
     * @param xaxisID The ID of the x axis to plot this dataset on.
     * If not specified, this defaults to the ID of the first found x axis
     */
    public void setXaxisID(String xaxisID) {
        this.xaxisID = xaxisID;
    }

    /**
     * Gets the yAxisID
     *
     * @return yAxisID
     */
    public String getYaxisID() {
        return yaxisID;
    }

    /**
     * Sets the yAxisID
     *
     * @param yaxisID The ID of the y axis to plot this dataset on.
     * If not specified, this defaults to the ID of the first found y axis.
     */
    public void setYaxisID(String yaxisID) {
        this.yaxisID = yaxisID;
    }

    /**
     * Gets the stack
     *
     * @return stack
     */
    public String getStack() {
        return stack;
    }

    /**
     * Sets the stack
     *
     * @param stack The ID of the group to which this dataset belongs to (when stacked, each group will be a separate stack)
     */
    public void setStack(String stack) {
        this.stack = stack;
    }

    /**
     * Gets the backgroundColor
     *
     * @return backgroundColor
     */
    public Object getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the backgroundColor
     *
     * @param backgroundColor The fill color of the bar.
     */
    public void setBackgroundColor(Object backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Gets the borderColor
     *
     * @return borderColor
     */
    public Object getBorderColor() {
        return borderColor;
    }

    /**
     * Sets the borderColor
     *
     * @param borderColor The color of the bar border.
     */
    public void setBorderColor(Object borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Gets the borderWidth
     *
     * @return borderWidth
     */
    public Object getBorderWidth() {
        return borderWidth;
    }

    /**
     * Sets the borderWidth
     *
     * @param borderWidth The stroke width of the bar in pixels.
     */
    public void setBorderWidth(Object borderWidth) {
        this.borderWidth = borderWidth;
    }

    /**
     * Gets the borderSkipped
     *
     * @return borderSkipped
     */
    public String getBorderSkipped() {
        return borderSkipped;
    }

    /**
     * Sets the borderSkipped
     *
     * @param borderSkipped Which edge to skip drawing the border for.
     */
    public void setBorderSkipped(String borderSkipped) {
        this.borderSkipped = borderSkipped;
    }

    /**
     * Gets the hoverBackgroundColor
     *
     * @return hoverBackgroundColor
     */
    public Object getHoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    /**
     * Sets the hoverBackgroundColor
     *
     * @param hoverBackgroundColor The fill colour of the bars when hovered.
     */
    public void setHoverBackgroundColor(Object hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    /**
     * Gets the hoverBorderColor
     *
     * @return hoverBorderColor
     */
    public Object getHoverBorderColor() {
        return hoverBorderColor;
    }

    /**
     * Sets the hoverBorderColor
     *
     * @param hoverBorderColor The stroke colour of the bars when hovered.
     */
    public void setHoverBorderColor(Object hoverBorderColor) {
        this.hoverBorderColor = hoverBorderColor;
    }

    /**
     * Gets the hoverBorderWidth
     *
     * @return hoverBorderWidth
     */
    public Object getHoverBorderWidth() {
        return hoverBorderWidth;
    }

    /**
     * Sets the hoverBorderWidth
     *
     * @param hoverBorderWidth The stroke width of the bars when hovered.
     */
    public void setHoverBorderWidth(Object hoverBorderWidth) {
        this.hoverBorderWidth = hoverBorderWidth;
    }

    /**
     * Gets the type
     *
     * @return type of current chart
     */
    public String getType() {
        return "bar";
    }

    /**
     * Write the options of this dataSet
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    @Override
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            fsw.write("{");

            ChartUtils.writeDataValue(fsw, "type", this.getType(), false);
            ChartUtils.writeDataValue(fsw, "data", this.data, true);
            ChartUtils.writeDataValue(fsw, "label", this.label, true);
            ChartUtils.writeDataValue(fsw, "hidden", this.isHidden(), true);
            ChartUtils.writeDataValue(fsw, "xAxisID", this.xaxisID, true);
            ChartUtils.writeDataValue(fsw, "yAxisID", this.yaxisID, true);
            ChartUtils.writeDataValue(fsw, "stack", this.stack, true);
            ChartUtils.writeDataValue(fsw, "backgroundColor", this.backgroundColor, true);
            ChartUtils.writeDataValue(fsw, "borderColor", this.borderColor, true);
            ChartUtils.writeDataValue(fsw, "borderWidth", this.borderWidth, true);
            ChartUtils.writeDataValue(fsw, "borderSkipped", this.borderSkipped, true);
            ChartUtils.writeDataValue(fsw, "hoverBackgroundColor", this.hoverBackgroundColor, true);
            ChartUtils.writeDataValue(fsw, "hoverBorderColor", this.hoverBorderColor, true);
            ChartUtils.writeDataValue(fsw, "hoverBorderWidth", this.hoverBorderWidth, true);

            fsw.write("}");
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
