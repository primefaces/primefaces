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
package org.primefaces.showcase.view.app;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import software.xdev.chartjs.model.charts.DoughnutChart;
import software.xdev.chartjs.model.color.RGBAColor;
import software.xdev.chartjs.model.data.DoughnutData;
import software.xdev.chartjs.model.dataset.DoughnutDataset;
import software.xdev.chartjs.model.options.DoughnutOptions;

@Named("landingData")
@ApplicationScoped
public class LandingData implements Serializable {

    private String componentChart;

    public static class ShowcaseComponent implements Serializable {
        private final String name;
        private final String category;
        private final String status;
        private final String since;
        private final String description;

        public ShowcaseComponent(String name, String category, String status, String since, String description) {
            this.name = name;
            this.category = category;
            this.status = status;
            this.since = since;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public String getStatus() {
            return status;
        }

        public String getSince() {
            return since;
        }

        public String getDescription() {
            return description;
        }

        public String getStatusSeverity() {
            switch (status) {
                case "New":
                    return "info";
                case "Updated":
                    return "warn";
                default:
                    return "success";
            }
        }
    }

    private final List<ShowcaseComponent> components = Arrays.asList(
        new ShowcaseComponent("DataTable",    "Data",    "Stable",  "3.0",  "Feature-rich table with sort, filter and pagination"),
        new ShowcaseComponent("Dialog",       "Overlay", "Stable",  "3.0",  "Modal dialog with configurable header and footer"),
        new ShowcaseComponent("AutoComplete", "Input",   "Updated", "14.0", "Text input with auto-complete suggestions"),
        new ShowcaseComponent("Chart",        "Chart",   "Stable",  "5.0",  "Chart.js integration for multiple chart types"),
        new ShowcaseComponent("FileUpload",   "File",    "Stable",  "3.0",  "Advanced file upload with drag-and-drop support"),
        new ShowcaseComponent("DatePicker",   "Input",   "New",     "14.0", "Modern calendar with range and time selection")
    );

    public List<ShowcaseComponent> getComponents() {
        return components;
    }

    @PostConstruct
    public void init() {
        componentChart = new DoughnutChart()
            .setData(new DoughnutData()
                .addDataset(new DoughnutDataset()
                    .setData(
                        BigDecimal.valueOf(27),
                        BigDecimal.valueOf(18),
                        BigDecimal.valueOf(12),
                        BigDecimal.valueOf(9),
                        BigDecimal.valueOf(8),
                        BigDecimal.valueOf(6),
                        BigDecimal.valueOf(5))
                    .addBackgroundColors(
                        new RGBAColor(99, 102, 241, 0.85f),
                        new RGBAColor(34, 197, 94, 0.85f),
                        new RGBAColor(244, 114, 182, 0.85f),
                        new RGBAColor(251, 191, 36, 0.85f),
                        new RGBAColor(56, 189, 248, 0.85f),
                        new RGBAColor(167, 139, 250, 0.85f),
                        new RGBAColor(74, 222, 128, 0.85f))
                    .setBorderWidth(0))
                .setLabels("Input", "Data", "Overlay", "Navigation", "Charts", "Button", "Misc"))
            .setOptions(new DoughnutOptions()
                .setMaintainAspectRatio(Boolean.FALSE))
            .toJson();
    }

    public String getComponentChart() {
        return componentChart;
    }

    public void sendInvite() {
        FacesContext.getCurrentInstance().addMessage("landing-deck-form:invite-growl",
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Invite sent!", "The invitation has been sent successfully."));
    }
}
