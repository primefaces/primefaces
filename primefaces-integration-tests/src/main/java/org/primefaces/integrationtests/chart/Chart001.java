/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.integrationtests.chart;

import org.primefaces.event.ItemSelectEvent;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class Chart001 {

    /**
     * The chart.js configuration as raw JSON, animations are disabled so the bars can be clicked right away.
     *
     * @return the bar chart model
     */
    public String getBarModel() {
        return """
                {
                  "type": "bar",
                  "data": {
                    "labels": ["January", "February", "March"],
                    "datasets": [{
                      "label": "Sales",
                      "data": [10, 20, 30],
                      "backgroundColor": ["#3b82f6", "#22c55e", "#f59e0b"]
                    }]
                  },
                  "options": {
                    "animation": false,
                    "responsive": true,
                    "maintainAspectRatio": false,
                    "scales": {
                      "y": {
                        "beginAtZero": true
                      }
                    }
                  }
                }
                """;
    }

    public void onItemSelect(ItemSelectEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
                "dataSetIndex=" + event.getDataSetIndex() + ", itemIndex=" + event.getItemIndex() + ", data=" + event.getData());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
