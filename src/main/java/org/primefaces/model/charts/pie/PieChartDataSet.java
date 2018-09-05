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
package org.primefaces.model.charts.pie;

import java.io.IOException;
import java.util.List;
import org.primefaces.model.charts.ChartDataSet;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Used to provide DataSet objects to Pie chart component.
 */
public class PieChartDataSet extends ChartDataSet {

    private List<Number> data;
    private List<String> backgroundColor;
    private List<String> borderColor;
    private List<Number> borderWidth;
    private List<String> hoverBackgroundColor;
    private List<String> hoverBorderColor;
    private List<Number> hoverBorderWidth;

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
     * Gets the backgroundColor
     *
     * @return backgroundColor
     */
    public List<String> getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the backgroundColor
     *
     * @param backgroundColor The fill color of the arcs in the dataset.
     */
    public void setBackgroundColor(List<String> backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Gets the borderColor
     *
     * @return borderColor
     */
    public List<String> getBorderColor() {
        return borderColor;
    }

    /**
     * Sets the borderColor
     *
     * @param borderColor The border color of the arcs in the dataset.
     */
    public void setBorderColor(List<String> borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Gets the borderWidth
     *
     * @return borderWidth
     */
    public List<Number> getBorderWidth() {
        return borderWidth;
    }

    /**
     * Sets the borderWidth
     *
     * @param borderWidth The border width of the arcs in the dataset.
     */
    public void setBorderWidth(List<Number> borderWidth) {
        this.borderWidth = borderWidth;
    }

    /**
     * Gets the hoverBackgroundColor
     *
     * @return hoverBackgroundColor
     */
    public List<String> getHoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    /**
     * Sets the hoverBackgroundColor
     *
     * @param hoverBackgroundColor The fill colour of the arcs when hovered.
     */
    public void setHoverBackgroundColor(List<String> hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    /**
     * Gets the hoverBorderColor
     *
     * @return hoverBorderColor
     */
    public List<String> getHoverBorderColor() {
        return hoverBorderColor;
    }

    /**
     * Sets the hoverBorderColor
     *
     * @param hoverBorderColor The stroke colour of the arcs when hovered.
     */
    public void setHoverBorderColor(List<String> hoverBorderColor) {
        this.hoverBorderColor = hoverBorderColor;
    }

    /**
     * Gets the hoverBorderWidth
     *
     * @return hoverBorderWidth
     */
    public List<Number> getHoverBorderWidth() {
        return hoverBorderWidth;
    }

    /**
     * Sets the hoverBorderWidth
     *
     * @param hoverBorderWidth The stroke width of the arcs when hovered.
     */
    public void setHoverBorderWidth(List<Number> hoverBorderWidth) {
        this.hoverBorderWidth = hoverBorderWidth;
    }

    /**
     * Gets the type
     *
     * @return type of current chart
     */
    public String getType() {
        return "pie";
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
            ChartUtils.writeDataValue(fsw, "backgroundColor", this.backgroundColor, true);
            ChartUtils.writeDataValue(fsw, "borderColor", this.borderColor, true);
            ChartUtils.writeDataValue(fsw, "borderWidth", this.borderWidth, true);
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
