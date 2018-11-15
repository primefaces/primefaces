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
package org.primefaces.model.charts.axes.cartesian.category;

import java.io.IOException;
import java.util.List;

import org.primefaces.model.charts.axes.cartesian.CartesianTicks;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Used to provide cartesian category ticks. CartesianCategoryTicks extends {@link CartesianTicks}
 */
public class CartesianCategoryTicks extends CartesianTicks {

    private static final long serialVersionUID = 1L;

    private List<String> labels;
    private String min;
    private String max;

    /**
     * Gets the labels
     *
     * @return labels
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * Sets the labels
     *
     * @param labels List&#60;String&#62; list of labels to display.
     */
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    /**
     * Gets the min
     *
     * @return min
     */
    public String getMin() {
        return min;
    }

    /**
     * Sets the min
     *
     * @param min The minimum item to display.
     */
    public void setMin(String min) {
        this.min = min;
    }

    /**
     * Gets the max
     *
     * @return max
     */
    public String getMax() {
        return max;
    }

    /**
     * Sets the max
     *
     * @param max The maximum item to display.
     */
    public void setMax(String max) {
        this.max = max;
    }

    /**
     * Write the options of cartesian category ticks
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    @Override
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            fsw.write(super.encode());
            ChartUtils.writeDataValue(fsw, "labels", this.labels, true);
            ChartUtils.writeDataValue(fsw, "min", this.min, true);
            ChartUtils.writeDataValue(fsw, "max", this.max, true);
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
