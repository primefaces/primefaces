/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.showcase.view.chartjs;

import org.primefaces.event.ItemSelectEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import software.xdev.chartjs.model.charts.BarChart;
import software.xdev.chartjs.model.charts.BubbleChart;
import software.xdev.chartjs.model.charts.DoughnutChart;
import software.xdev.chartjs.model.charts.LineChart;
import software.xdev.chartjs.model.charts.MixedChart;
import software.xdev.chartjs.model.charts.PieChart;
import software.xdev.chartjs.model.charts.PolarChart;
import software.xdev.chartjs.model.charts.RadarChart;
import software.xdev.chartjs.model.charts.ScatterChart;
import software.xdev.chartjs.model.color.RGBAColor;
import software.xdev.chartjs.model.data.BarData;
import software.xdev.chartjs.model.data.BubbleData;
import software.xdev.chartjs.model.data.DoughnutData;
import software.xdev.chartjs.model.data.LineData;
import software.xdev.chartjs.model.data.MixedData;
import software.xdev.chartjs.model.data.PieData;
import software.xdev.chartjs.model.data.PolarData;
import software.xdev.chartjs.model.data.RadarData;
import software.xdev.chartjs.model.data.ScatterData;
import software.xdev.chartjs.model.datapoint.BubbleDataPoint;
import software.xdev.chartjs.model.datapoint.ScatterDataPoint;
import software.xdev.chartjs.model.dataset.BarDataset;
import software.xdev.chartjs.model.dataset.BubbleDataset;
import software.xdev.chartjs.model.dataset.DoughnutDataset;
import software.xdev.chartjs.model.dataset.LineDataset;
import software.xdev.chartjs.model.dataset.PieDataset;
import software.xdev.chartjs.model.dataset.PolarDataset;
import software.xdev.chartjs.model.dataset.RadarDataset;
import software.xdev.chartjs.model.dataset.ScatterDataset;
import software.xdev.chartjs.model.enums.FontStyle;
import software.xdev.chartjs.model.enums.IndexAxis;
import software.xdev.chartjs.model.enums.ScalesPosition;
import software.xdev.chartjs.model.options.BarOptions;
import software.xdev.chartjs.model.options.DoughnutOptions;
import software.xdev.chartjs.model.options.Font;
import software.xdev.chartjs.model.options.LineOptions;
import software.xdev.chartjs.model.options.Options;
import software.xdev.chartjs.model.options.Plugins;
import software.xdev.chartjs.model.options.RadarOptions;
import software.xdev.chartjs.model.options.Title;
import software.xdev.chartjs.model.options.elements.Fill;
import software.xdev.chartjs.model.options.scale.Scales;
import software.xdev.chartjs.model.options.scale.cartesian.CartesianScaleOptions;
import software.xdev.chartjs.model.options.scale.cartesian.CartesianTickOptions;
import software.xdev.chartjs.model.options.scale.cartesian.linear.LinearScaleOptions;
import software.xdev.chartjs.model.options.scale.radial.AngleLines;
import software.xdev.chartjs.model.options.scale.radial.PointLabels;
import software.xdev.chartjs.model.options.scale.radial.RadialLinearScaleOptions;
import software.xdev.chartjs.model.options.scale.radial.RadialTickOptions;
import software.xdev.chartjs.model.options.tooltip.TooltipOptions;


@Named
@RequestScoped
public class ChartView implements Serializable {

    private static final long serialVersionUID = 1L;

    private String json;
    private String barModel;
    private String bubbleModel;
    private String cartesianLinerModel;
    private String donutModel;
    private String lineModel;
    private String pieModel;
    private String polarAreaModel;
    private String radarModel;
    private String scatterModel;
    private String stackedBarModel;
    private String mixedModel;

    @PostConstruct
    public void init() {
        createBarModel();
        createBubbleModel();
        createCartesianLinerModel();
        createDonutModel();
        createJsonModel();
        createLineModel();
        createMixedModel();
        createPieModel();
        createPolarAreaModel();
        createRadarModel();
        createScatterModel();
        createStackedBarModel();
    }

    private void createPieModel() {
        pieModel = new PieChart()
                .setData(new PieData()
                .addDataset(new PieDataset()
                        .setData(BigDecimal.valueOf(300), BigDecimal.valueOf(50), BigDecimal.valueOf(100))
                        .setLabel("My First Dataset")
                        .addBackgroundColors(new RGBAColor(255, 99, 132), new RGBAColor(54, 162, 235), new RGBAColor(255, 205, 86))
                )
                .setLabels("Red", "Blue", "Yellow"))
                .toJson();
    }

    private void createPolarAreaModel() {
        polarAreaModel = new PolarChart()
                .setData(new PolarData()
                .addDataset(new PolarDataset()
                        .setData(BigDecimal.valueOf(11),
                                BigDecimal.valueOf(16),
                                BigDecimal.valueOf(7),
                                BigDecimal.valueOf(3),
                                BigDecimal.valueOf(14))
                        .setLabel("My First Dataset")
                        .addBackgroundColors(
                                new RGBAColor(255, 99, 132),
                                new RGBAColor(75, 192, 192),
                                new RGBAColor(255, 205, 86),
                                new RGBAColor(201, 203, 207),
                                new RGBAColor(54, 162, 235)
                        )
                )
                .setLabels("Red", "Green", "Yellow", "Grey", "Blue" ))
                .toJson();
    }

    public void createLineModel() {
        lineModel = new LineChart()
                .setData(new LineData()
                .addDataset(new LineDataset()
                        .setData(65, 59, 80, 81, 56, 55, 40)
                        .setLabel("My First Dataset")
                        .setBorderColor(new RGBAColor(75, 192, 192))
                        .setLineTension(0.1f)
                        .setFill(new Fill<Boolean>(false)))
                .setLabels("January", "February", "March", "April", "May", "June", "July"))
                .setOptions(new LineOptions()
                        .setResponsive(true)
                        .setMaintainAspectRatio(false)
                        .setPlugins(new Plugins()
                                .setTitle(new Title()
                                        .setDisplay(true)
                                        .setText("Line Chart Subtitle")))
                ).toJson();
    }

    public void createScatterModel() {
        scatterModel = new ScatterChart()
                .setData(new ScatterData()
                .addDataset(new ScatterDataset()
                        .addData(new ScatterDataPoint(-10, 0))
                        .addData(new ScatterDataPoint(0, 10))
                        .addData(new ScatterDataPoint(10, 5))
                        .addData(new ScatterDataPoint(8, 14))
                        .addData(new ScatterDataPoint(12, 2))
                        .addData(new ScatterDataPoint(13, 7))
                        .addData(new ScatterDataPoint(6, 9))
                        .setLabel("Red Dataset")
                        .setBorderColor(new RGBAColor(249, 24, 24))
                        .setShowLine(Boolean.FALSE)
                        .setFill(new Fill<Boolean>(true)))
                )
                .setOptions(new LineOptions()
                        .setResponsive(true)
                        .setShowLine(Boolean.FALSE)
                        .setScales(new Scales()
                                .addScale(Scales.ScaleAxis.X, new LinearScaleOptions().setPosition(ScalesPosition.BOTTOM)))
                        .setPlugins(new Plugins()
                                .setTitle(new Title()
                                        .setDisplay(true)
                                        .setText("Scatter Chart")))
                ).toJson();
    }

    public void createCartesianLinerModel() {
        cartesianLinerModel = new LineChart()
                .setData(new LineData()
                .addDataset(new LineDataset()
                        .setData(20, 50, 100, 75, 25, 0)
                        .setLabel("Left Dataset")
                        .setLineTension(0.5f)
                        .setYAxisID("left-y-axis")
                        .setFill(new Fill<Boolean>(true)))
                .addDataset(new LineDataset()
                        .setData(0.1, 0.5, 1.0, 2.0, 1.5, 0)
                        .setLabel("Right Dataset")
                        .setLineTension(0.5f)
                        .setYAxisID("right-y-axis")
                        .setFill(new Fill<Boolean>(true)))
                .setLabels("Jan", "Feb", "Mar", "Apr", "May", "Jun"))
                .setOptions(new LineOptions()
                        .setResponsive(true)
                        .setMaintainAspectRatio(false)
                        .setScales(new Scales()
                                .addScale("left-y-axis", new LinearScaleOptions().setPosition(ScalesPosition.LEFT))
                                .addScale("right-y-axis", new LinearScaleOptions().setPosition(ScalesPosition.RIGHT)))
                        .setPlugins(new Plugins()
                                .setTitle(new Title()
                                        .setDisplay(true)
                                        .setText("Cartesian Linear Chart")))
                ).toJson();
    }



    public void createBarModel() {
        barModel = new BarChart()
                .setData(new BarData()
                .addDataset(new BarDataset()
                        .setData(65, 59, 80, 81, 56, 55, 40)
                        .setLabel("My First Dataset")
                        .setBackgroundColor(new RGBAColor(255, 99, 132, 0.2))
                        .setBorderColor(new RGBAColor(255, 99, 132))
                        .setBorderWidth(1))
                .addDataset(new BarDataset()
                        .setData(85, 69, 20, 51, 76, 75, 10)
                        .setLabel("My Second Dataset")
                        .setBackgroundColor(new RGBAColor(255, 159, 64, 0.2))
                        .setBorderColor(new RGBAColor(255, 159, 64))
                        .setBorderWidth(1)
                )
                .setLabels("January", "February", "March", "April", "May", "June", "July"))
                .setOptions(new BarOptions()
                        .setResponsive(true)
                        .setMaintainAspectRatio(false)
                        .setIndexAxis(IndexAxis.X)
                        .setScales(new Scales().addScale(Scales.ScaleAxis.Y, new CartesianScaleOptions()
                                .setStacked(false)
                                .setTicks(new CartesianTickOptions()
                                        .setAutoSkip(true)
                                        .setMirror(true)))
                        )
                        .setPlugins(new Plugins()
                                .setTitle(new Title()
                                        .setDisplay(true)
                                        .setText("Bar Chart using XDEV java model")))
                ).toJson();
    }

    public void createStackedBarModel() {
        stackedBarModel = new BarChart()
                .setData(new BarData()
                .addDataset(new BarDataset()
                        .setData(62, -58, -49, 25, 4, 77, -41)
                        .setLabel("Dataset 1")
                        .setBackgroundColor(new RGBAColor(255, 99, 132)))
                .addDataset(new BarDataset()
                        .setData(-1, 32, -52, 11, 97, 76, -78)
                        .setLabel("Dataset 2")
                        .setBackgroundColor(new RGBAColor(54, 162, 235)))
                .addDataset(new BarDataset()
                        .setData(-44, 25, 15, 92, 80, -25, -11)
                        .setLabel("Dataset 3")
                        .setBackgroundColor(new RGBAColor(75, 192, 192)))
                .setLabels("January", "February", "March", "April", "May", "June", "July"))
                .setOptions(new BarOptions()
                        .setResponsive(true)
                        .setMaintainAspectRatio(false)
                        .setScales(new Scales()
                                .addScale(Scales.ScaleAxis.X, new CartesianScaleOptions()
                                        .setStacked(true)
                                        .setTicks(new CartesianTickOptions()))
                                .addScale(Scales.ScaleAxis.Y, new CartesianScaleOptions()
                                        .setStacked(true)
                                        .setTicks(new CartesianTickOptions()))
                        )
                        .setPlugins(new Plugins()
                                .setTooltip(new TooltipOptions().setMode("index"))
                                .setTitle(new Title()
                                        .setDisplay(true)
                                        .setText("Bar Chart - Stacked")))
                ).toJson();
    }

    public void createRadarModel() {
        radarModel = new RadarChart()
                .setData(new RadarData()
                .addDataset(new RadarDataset()
                        .setData(BigDecimal.valueOf(2.2),
                                BigDecimal.valueOf(3),
                                BigDecimal.valueOf(2.4),
                                BigDecimal.valueOf(1.1),
                                BigDecimal.valueOf(3))
                        .setLabel("P.Practitioner")
                        .setLineTension(0.1f)
                        .setBackgroundColor(new RGBAColor(102, 153, 204, 0.2))
                        .setBorderColor(new RGBAColor(102, 153, 204, 1))
                        .setPointBackgroundColor(List.of(new RGBAColor(102, 153, 204, 1)))
                        .setPointBorderColor(List.of(RGBAColor.WHITE))
                        .setPointHoverRadius(List.of(5))
                        .setPointHoverBackgroundColor(List.of(RGBAColor.WHITE))
                        .setPointHoverBorderColor(List.of(new RGBAColor(102, 153, 204, 1))))
                .addDataset(new RadarDataset()
                        .setData(BigDecimal.valueOf(2.1),
                                BigDecimal.valueOf(3),
                                BigDecimal.valueOf(3),
                                BigDecimal.valueOf(2.7),
                                BigDecimal.valueOf(3))
                        .setLabel("P.Manager")
                        .setLineTension(0.1f)
                        .setBackgroundColor(new RGBAColor(255, 204, 102, 0.2))
                        .setBorderColor(new RGBAColor(255, 204, 102, 1))
                        .setPointBackgroundColor(List.of(new RGBAColor(255, 204, 102, 1)))
                        .setPointBorderColor(List.of(RGBAColor.WHITE))
                        .setPointHoverRadius(List.of(5))
                        .setPointHoverBackgroundColor(List.of(RGBAColor.WHITE))
                        .setPointHoverBorderColor(List.of(new RGBAColor(255, 204, 102, 1))))
                .setLabels("Process Excellence", "Problem Solving", "Facilitation", "Project Mgmt", "Change Mgmt"))
                .setOptions(new RadarOptions()
                        .setResponsive(true)
                        .setMaintainAspectRatio(false)
                        .setScales(new Scales().addScale(Scales.ScaleAxis.Y, new RadialLinearScaleOptions()
                                .setAngleLines(new AngleLines()
                                        .setDisplay(Boolean.TRUE)
                                        .setLineWidth(BigDecimal.valueOf(0.5))
                                        .setColor(new RGBAColor(128, 128, 128, 0.2)))
                                .setPointLabels(new PointLabels().setFont(new Font()
                                        .setSize(BigDecimal.valueOf(14))
                                        .setStyle(FontStyle.NORMAL)
                                        .setFamily("Lato, sans-serif"))
                                        .setColor(new RGBAColor(204, 204, 204, 1)))
                                .setTicks(new RadialTickOptions()
                                        .setDisplay(false)
                                        .setStepSize(BigDecimal.valueOf(0.2))
                                        .setMaxTickLimits(BigDecimal.valueOf(3))))
                        )
                        .setPlugins(new Plugins()
                                .setTitle(new Title()
                                        .setDisplay(true)
                                        .setText("Radar Chart")))
                ).toJson();

    }

    public void createBubbleModel() {
        bubbleModel = new BubbleChart()
                .setData(new BubbleData()
                .addDataset(new BubbleDataset()
                        .addData(new BubbleDataPoint(BigDecimal.valueOf(20), BigDecimal.valueOf(30), BigDecimal.valueOf(15)))
                        .addData(new BubbleDataPoint(BigDecimal.valueOf(40), BigDecimal.valueOf(10), BigDecimal.valueOf(10)))
                        .setLabel("My First Dataset")
                        .setBackgroundColor(new RGBAColor(255, 99, 132))
                        .setBorderColor(new RGBAColor(255, 99, 132))
                )).toJson();
    }

    public void createDonutModel() {
        donutModel = new DoughnutChart()
                .setData(new DoughnutData()
                .addDataset(new DoughnutDataset()
                        .setData(BigDecimal.valueOf(300),
                                BigDecimal.valueOf(50),
                                BigDecimal.valueOf(100))
                        .addBackgroundColors(
                                new RGBAColor(255, 99, 132),
                                new RGBAColor(54, 162, 235),
                                new RGBAColor(255, 205, 86))
                )
                .setLabels("Red", "Blue", "Yellow"))
                .setOptions(new DoughnutOptions().setMaintainAspectRatio(Boolean.FALSE))
                .toJson();
    }

    public void createMixedModel() {
        MixedData mixedData = new MixedData();

        BarDataset barDataset = new BarDataset()
                .setType("bar")
                .setData(120, 113, 175, 143, 118, 159, 110)
                .setLabel("Bar data")
                .setBorderColor(new RGBAColor(255, 99, 132, 1.0))
                .setBackgroundColor(new RGBAColor(255, 99, 132, 0.5))
                .setBorderWidth(1);

        LineDataset lineDataset = new LineDataset()
                .setType("line")
                .setData(119, 144, 179, 165, 195, 170, 135)
                .setLabel("Line data")
                .setStepped(true)
                .setBorderColor(new RGBAColor(75, 192, 192, 1.0))
                .setBackgroundColor(new RGBAColor(75, 192, 192, 0.5))
                .setLineTension(0.1f)
                .setFill(new Fill<Boolean>(false));

        mixedData.addDataset(barDataset);
        mixedData.addDataset(lineDataset);

        mixedData.setLabels("January", "February", "March", "April", "May", "June", "July");

        mixedModel = new MixedChart()
                .setData(mixedData)
                .setOptions(new Options<>()
                        .setResponsive(true)
                        .setMaintainAspectRatio(false)
                        .setPlugins(new Plugins()
                                .setTooltip(new TooltipOptions().setMode("index"))
                                .setTitle(new Title()
                                        .setDisplay(true)
                                        .setText("Mixed Chart")
                                )
                        )
                ).toJson();
    }

    public void createJsonModel() {
        json = "{\r\n"
                + "   \"type\":\"line\",\r\n"
                + "   \"data\":{\r\n"
                + "      \"datasets\":[\r\n"
                + "         {\r\n"
                + "            \"backgroundColor\":\"rgba(40, 180, 99, 0.3)\",\r\n"
                + "            \"borderColor\":\"rgb(40, 180, 99)\",\r\n"
                + "            \"borderWidth\":1,\r\n"
                + "            \"data\":[\r\n"
                + "               {\r\n"
                + "                  \"x\":1699457269877,\r\n"
                + "                  \"y\":20\r\n"
                + "               },\r\n"
                + "               {\r\n"
                + "                  \"x\":1700047109694,\r\n"
                + "                  \"y\":20\r\n"
                + "               }\r\n"
                + "            ],\r\n"
                + "            \"hidden\":false,\r\n"
                + "            \"label\":\"Device Id: 524967 Register: A - total Wh \",\r\n"
                + "            \"minBarLength\":3\r\n"
                + "         },\r\n"
                + "         {\r\n"
                + "            \"backgroundColor\":\"rgba(218, 117, 255, 0.3)\",\r\n"
                + "            \"borderColor\":\"rgb(218, 117, 255)\",\r\n"
                + "            \"borderWidth\":1,\r\n"
                + "            \"data\":[\r\n"
                + "               {\r\n"
                + "                  \"x\":1699457267847,\r\n"
                + "                  \"y\":10\r\n"
                + "               },\r\n"
                + "               {\r\n"
                + "                  \"x\":1700047108397,\r\n"
                + "                  \"y\":234\r\n"
                + "               }\r\n"
                + "            ],\r\n"
                + "            \"hidden\":false,\r\n"
                + "            \"label\":\"Device Id: 524967 Register: A+ total Wh \",\r\n"
                + "            \"minBarLength\":3\r\n"
                + "         }\r\n"
                + "      ]\r\n"
                + "   },\r\n"
                + "   \"options\":{\r\n"
                + "      \"plugins\":{\r\n"
                + "         \"legend\":{\r\n"
                + "            \"display\":true,\r\n"
                + "            \"fullWidth\":true,\r\n"
                + "            \"position\":\"top\",\r\n"
                + "            \"reverse\":false,\r\n"
                + "            \"rtl\":false\r\n"
                + "         },\r\n"
                + "         \"title\":{\r\n"
                + "            \"display\":true,\r\n"
                + "            \"text\":\"Values from the meter\"\r\n"
                + "         },\r\n"
                + "         \"zoom\":{\r\n"
                + "            \"pan\":{\r\n"
                + "               \"enabled\":true,\r\n"
                + "               \"mode\":\"xy\",\r\n"
                + "               \"threshold\":5\r\n"
                + "            },\r\n"
                + "            \"zoom\":{\r\n"
                + "               \"wheel\":{\r\n"
                + "                  \"enabled\":true\r\n"
                + "               },\r\n"
                + "               \"pinch\":{\r\n"
                + "                  \"enabled\":true\r\n"
                + "               },\r\n"
                + "               \"mode\":\"xy\"\r\n"
                + "            }\r\n"
                + "         }\r\n"
                + "      },\r\n"
                + "      \"scales\":{\r\n"
                + "         \"x\":{\r\n"
                + "            \"beginAtZero\":false,\r\n"
                + "            \"offset\":true,\r\n"
                + "            \"reverse\":false,\r\n"
                + "            \"stacked\":true,\r\n"
                + "            \"ticks\":{\r\n"
                + "               \"autoSkip\":true,\r\n"
                + "               \"maxRotation\":0,\r\n"
                + "               \"minRotation\":0,\r\n"
                + "               \"mirror\":false,\r\n"
                + "               \"source\":\"data\"\r\n"
                + "            },\r\n"
                + "            \"time\":{\r\n"
                + "               \"displayFormats\":{\r\n"
                + "                  \"minute\":\"dd.LL T\"\r\n"
                + "               },\r\n"
                + "               \"round\":\"minute\",\r\n"
                + "               \"stepSize\":\"60\",\r\n"
                + "               \"unit\":\"minute\"\r\n"
                + "            },\r\n"
                + "            \"type\":\"time\"\r\n"
                + "         },\r\n"
                + "         \"y\":{\r\n"
                + "            \"beginAtZero\":false,\r\n"
                + "            \"offset\":false,\r\n"
                + "            \"reverse\":false,\r\n"
                + "            \"stacked\":true,\r\n"
                + "            \"ticks\":{\r\n"
                + "               \"autoSkip\":true,\r\n"
                + "               \"mirror\":false\r\n"
                + "            }\r\n"
                + "         }\r\n"
                + "      },\r\n"
                + "      \"showLine\":true,\r\n"
                + "      \"spanGaps\":false\r\n"
                + "   }\r\n"
                + "}";
    }

    public void itemSelect(ItemSelectEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
                "Value: " + event.getData()
                + ", Item Index: " + event.getItemIndex()
                + ", DataSet Index:" + event.getDataSetIndex());

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String getPieModel() {
        return pieModel;
    }

    public void setPieModel(String pieModel) {
        this.pieModel = pieModel;
    }

    public String getPolarAreaModel() {
        return polarAreaModel;
    }

    public void setPolarAreaModel(String polarAreaModel) {
        this.polarAreaModel = polarAreaModel;
    }

    public String getLineModel() {
        return lineModel;
    }

    public void setLineModel(String lineModel) {
        this.lineModel = lineModel;
    }

    public String getCartesianLinerModel() {
        return cartesianLinerModel;
    }

    public void setCartesianLinerModel(String cartesianLinerModel) {
        this.cartesianLinerModel = cartesianLinerModel;
    }

    public String getBarModel() {
        return barModel;
    }

    public void setBarModel(String barModel) {
        this.barModel = barModel;
    }

    public String getStackedBarModel() {
        return stackedBarModel;
    }

    public void setStackedBarModel(String stackedBarModel) {
        this.stackedBarModel = stackedBarModel;
    }

    public String getRadarModel() {
        return radarModel;
    }

    public void setRadarModel(String radarModel) {
        this.radarModel = radarModel;
    }

    public String getBubbleModel() {
        return bubbleModel;
    }

    public void setBubbleModel(String bubbleModel) {
        this.bubbleModel = bubbleModel;
    }

    public String getDonutModel() {
        return donutModel;
    }

    public void setDonutModel(String donutModel) {
        this.donutModel = donutModel;
    }

    public String getScatterModel() {
        return scatterModel;
    }

    public void setScatterModel(String scatterModel) {
        this.scatterModel = scatterModel;
    }

    public String getMixedModel() {
        return mixedModel;
    }

    public void setMixedModel(String mixedModel) {
        this.mixedModel = mixedModel;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
