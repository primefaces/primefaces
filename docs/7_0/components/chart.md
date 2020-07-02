# Chart

Chart component is a generic graph component to create various types of charts using jqplot library.
Each chart type has its own subsection with code examples and section 3.12.10 documents the full
charting API.

## Info

| Name | Value |
| --- | --- |
| Tag | chart
| Component Class | org.primefaces.component.chart.Chart
| Component Type | org.primefaces.component.Chart
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ChartRenderer
| Renderer Class | org.primefaces.component.chart.ChartRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| type | null | String | Type of the chart.
| model | null | ChartModel | Model object of data and settings.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| widgetVar | null | String | Name of the client side widget.
| responsive | false | Boolean | In responsive mode, chart is redrawn when window is resized.

### PieChart

PieChart is created with PieChartModel.

#### Basic

```xhtml
<p:chart type="pie" model="#{bean.model}" />
```
```java
public class Bean {
    private PieChartModel model;

    public Bean() {
        model = new PieChartModel();
        model.set("Brand 1", 540);
        model.set("Brand 2", 325);
        model.set("Brand 3", 702);
        model.set("Brand 4", 421);
        model.setTitle("Simple Pie");
        model.setLegendPosition("w");
    }
    public PieChartModel getModel() {
        return model;
    }
}
```
#### Customized

```xhtml
<p:chart type="pie" model="#{bean.model}" />
```
``` java
public class Bean {
    private PieChartModel model;

    public Bean() {
        model = new PieChartModel();
        model.set("Brand 1", 540);
        model.set("Brand 2", 325);
        model.set("Brand 3", 702);
        model.set("Brand 4", 421);
        model.setTitle("Custom Pie");
        model.setLegendPosition("e");
        model.setFill(false);
        model.setShowDataLabels(true);
        model.setDiameter(150);
    }
    public PieChartModel getModel() {
        return model;
    }
}
```


### LineChart

LineChartModel is used to create a line chart.

#### Basic

```xhtml
<p:chart type="line" model="#{bean.model}" />
```
```java
public class Bean {
    private LineChartModel model;

    public Bean() {
        model = new LineChartModel();
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Series 1");
        series1.set(1, 2);
        series1.set(2, 1);
        series1.set(3, 3);
        series1.set(4, 6);
        series1.set(5, 8);
        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel("Series 2");
        series2.set(1, 6);
        series2.set(2, 3);
        series2.set(3, 2);
        series2.set(4, 7);
        series2.set(5, 9);
        model.addSeries(series1);
        model.addSeries(series2);
        model.setTitle("Linear Chart");
        model.setLegendPosition("e");
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(10);
    }
    public LineChartModel getModel() {
        return model;
    }
}
```

#### Customized

```xhtml
<p:chart type="line" model="#{bean.model}" />
```
```java
public class Bean {
    private LineChartModel model;

    public Bean() {
        model = new LineChartModel();
        ChartSeries boys = new ChartSeries();
        boys.setLabel("Boys");
        boys.set("2004", 120);
        boys.set("2005", 100);
        boys.set("2006", 44);
        boys.set("2007", 150);
        boys.set("2008", 25);
        ChartSeries girls = new ChartSeries();
        girls.setLabel("Girls");
        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 110);
        girls.set("2007", 90);
        girls.set("2008", 120);
        model.addSeries(boys);
        model.addSeries(girls);
        model.setTitle("Category Chart");
        model.setLegendPosition("e");
        model.setShowPointLabels(true);
        model.getAxes().put(AxisType.X, new CategoryAxis("Years"));
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Births");
        yAxis.setMin(0);
        yAxis.setMax(200);
    }
    public LineChartModel getModel() {
        return model;
    }
}
```

#### Area

```xhtml
<p:chart type="line" model="#{bean.model}" />
```
```java
public class Bean {
    private LineChartModel model;

    public Bean() {
        model = new LineChartModel();
        LineChartSeries boys = new LineChartSeries();
        boys.setFill(true);
        boys.setLabel("Boys");
        boys.set("2004", 120);
        boys.set("2005", 100);
        boys.set("2006", 44);
        boys.set("2007", 150);
        boys.set("2008", 25);
        LineChartSeries girls = new LineChartSeries();
        girls.setFill(true);
        girls.setLabel("Girls");
        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 110);
        girls.set("2007", 90);
        girls.set("2008", 120);
        model.addSeries(boys);
        model.addSeries(girls);
        model.setTitle("Area Chart");
        model.setLegendPosition("ne");
        model.setStacked(true);
        model.setShowPointLabels(true);
        model.getAxis(AxisType.X).setLabel("Years");
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Births");
        yAxis.setMin(0);
        yAxis.setMax(300);
    }
    public CartesianChartModel getModel() {
        return model;
    }
}
```

### BarChart

BarChartModel is used to created a BarChart.

#### Basic

```xhtml
<p:chart type="bar" model="#{bean.model}" />
```
```java
public class Bean {
    private BarChartModel model;

    public Bean() {
        model = new BarChartModel();
        ChartSeries boys = new ChartSeries();
        boys.setLabel("Boys");
        boys.set("2004", 120);
        boys.set("2005", 100);
        boys.set("2006", 44);
        boys.set("2007", 150);
        boys.set("2008", 25);
        ChartSeries girls = new ChartSeries();
        girls.setLabel("Girls");
        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 110);
        girls.set("2007", 135);
        girls.set("2008", 120);
        model.addSeries(boys);
        model.addSeries(girls);
        model.setTitle("Bar Chart");
        model.setLegendPosition("ne");
        Axis xAxis = model.getAxis(AxisType.X);
        xAxis.setLabel("Gender");
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Births");
        yAxis.setMin(0);
        yAxis.setMax(200);
    }
    public BarChartModel getModel() { 
        return model; 
    }
}
```

#### Horizontal and Stacked

```xhtml
<p:chart type="bar" model="#{bean.model}" />
```
```java
public class Bean {
    private HorizontalBarChartModel model;

    public Bean() {
        model = new HorizontalBarChartModel();
        ChartSeries boys = new ChartSeries();
        boys.setLabel("Boys");
        boys.set("2004", 50);
        boys.set("2005", 96);
        boys.set("2006", 44);
        boys.set("2007", 55);
        boys.set("2008", 25);
        ChartSeries girls = new ChartSeries();
        girls.setLabel("Girls");
        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 82);
        girls.set("2007", 35);
        girls.set("2008", 120);
        model.addSeries(boys);
        model.addSeries(girls);
        model.setTitle("Horizontal and Stacked");
        model.setLegendPosition("e");
        model.setStacked(true);
        Axis xAxis = model.getAxis(AxisType.X);
        xAxis.setLabel("Births");
        xAxis.setMin(0);
        xAxis.setMax(200);
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Gender");
    }
    public HorizontalBarChartModel getModel() { 
        return model; 
    }
}
```

### DonutChart

DonutChart is generated using DonutChartModel.

#### Basic

```xhtml
<p:chart type="donut" model="#{bean.model}" />
```
```java
public class Bean {
    private DonutChartModel model;
    
    public Bean() {
        model = new DonutChartModel();
        Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
        circle1.put("Brand 1", 150);
        circle1.put("Brand 2", 400);
        circle1.put("Brand 3", 200);
        circle1.put("Brand 4", 10);
        model.addCircle(circle1);
        Map<String, Number> circle2 = new LinkedHashMap<String, Number>();
        circle2.put("Brand 1", 540);
        circle2.put("Brand 2", 125);
        circle2.put("Brand 3", 702);
        circle2.put("Brand 4", 421);
        model.addCircle(circle2);
        Map<String, Number> circle3 = new LinkedHashMap<String, Number>();
        circle3.put("Brand 1", 40);
        circle3.put("Brand 2", 325);
        circle3.put("Brand 3", 402);
        circle3.put("Brand 4", 421);
        model.addCircle(circle3);
        model.setTitle("Donut Chart");
        model.setLegendPosition("w");
    }
    public DonutChartModel getModel() { 
        return model;
    }
}
```

#### Customized

```xhtml
<p:chart type="donut" model="#{bean.model}" />
```
```java
public class Bean {
    private DonutChartModel model;

    public Bean() {
        model = new DonutChartModel();
        Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
        circle1.put("Brand 1", 150);
        circle1.put("Brand 2", 400);
        circle1.put("Brand 3", 200);
        circle1.put("Brand 4", 10);
        model.addCircle(circle1);
        Map<String, Number> circle2 = new LinkedHashMap<String, Number>();
        circle2.put("Brand 1", 540);
        circle2.put("Brand 2", 125);
        circle2.put("Brand 3", 702);
        circle2.put("Brand 4", 421);
        model.addCircle(circle2);
        Map<String, Number> circle3 = new LinkedHashMap<String, Number>();
        circle3.put("Brand 1", 40);
        circle3.put("Brand 2", 325);
        circle3.put("Brand 3", 402);
        circle3.put("Brand 4", 421);
        model.addCircle(circle3);
        model.setTitle("Donut Chart");
        model.setLegendPosition("w");
        model.setTitle("Custom Options");
        model.setLegendPosition("e");
        model.setSliceMargin(5);
        model.setShowDataLabels(true);
        model.setDataFormat("value");
        model.setShadow(false);
    }
    public DonutChartModel getModel() {
        return model;
    }
}
```

### BubbleChart

BubbleChart is created with a BubbleChartModel.

#### Basic

```xhtml
<p:chart type="bubble" model="#{bean.model}" />
```
```java
public class Bean {
    private BubbleChartModel model;

    public Bean() {
        model = new BubbleChartModel();
        model.add(new BubbleChartSeries("Acura", 70, 183,55));
        model.add(new BubbleChartSeries("Alfa Romeo", 45, 92, 36));
        model.add(new BubbleChartSeries("AM General", 24, 104, 40));
        model.add(new BubbleChartSeries("Bugatti", 50, 123, 60));
        model.add(new BubbleChartSeries("BMW", 15, 89, 25));
        model.add(new BubbleChartSeries("Audi", 40, 180, 80));
        model.add(new BubbleChartSeries("Aston Martin", 70, 70, 48));
        model.setTitle("Bubble Chart");
        model.getAxis(AxisType.X).setLabel("Price");
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(250);
        yAxis.setLabel("Labels");
    }
    public BubbleChartModel getModel() { 
        return model; 
    }
}
```

#### Customized

```xhtml
<p:chart type="bubble" model="#{bean.model}" />
```
```java
public class Bean {
    private BubbleChartModel model;

    public Bean() {
        model = new BubbleChartModel();
        model.add(new BubbleChartSeries("Acura", 70, 183,55));
        model.add(new BubbleChartSeries("Alfa Romeo", 45, 92, 36));
        model.add(new BubbleChartSeries("AM General", 24, 104, 40));
        model.add(new BubbleChartSeries("Bugatti", 50, 123, 60));
        model.add(new BubbleChartSeries("BMW", 15, 89, 25));
        model.add(new BubbleChartSeries("Audi", 40, 180, 80));
        model.add(new BubbleChartSeries("Aston Martin", 70, 70, 48));
        model = initBubbleModel();
        model.setTitle("Custom Options");
        model.setShadow(false);
        model.setBubbleGradients(true);
        model.setBubbleAlpha(0.8);
        model.getAxis(AxisType.X).setTickAngle(-50);
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(250);
        yAxis.setTickAngle(50);
    }
    public BubbleChartModel getModel() { 
        return model; 
    }
}
```

### Ohlc Chart

OhlcChartModel is used to display Ohlc Charts.

#### Basic

```xhtml
<p:chart type="ohlc" model="#{bean.model}" />
```
```java
public class Bean {
    private OhlcChartModel model;

    public Bean() {
        ohlcModel = new OhlcChartModel();
        ohlcModel.add(new OhlcChartSeries(2007, 143.82, 144.56, 136.04, 136.97));
        ohlcModel.add(new OhlcChartSeries(2008, 138.7, 139.68, 135.18, 135.4));
        ohlcModel.add(new OhlcChartSeries(2009, 143.46, 144.66, 139.79, 140.02));
        ohlcModel.add(new OhlcChartSeries(2010, 140.67, 143.56, 132.88, 142.44));
        ohlcModel.add(new OhlcChartSeries(2011, 136.01, 139.5, 134.53, 139.48));
        ohlcModel.add(new OhlcChartSeries(2012, 124.76, 135.9, 124.55, 135.81));
        ohlcModel.add(new OhlcChartSeries(2012, 123.73, 129.31, 121.57, 122.5));
        ohlcModel.setTitle("OHLC Chart");
        ohlcModel.getAxis(AxisType.X).setLabel("Year");
        ohlcModel.getAxis(AxisType.Y).setLabel("Price Change $K/Unit");
    }
    public OhlcChartModel getModel() { 
        return model; 
    }
}
```

#### Candlestick

```xhtml
<p:chart type="ohlc" model="#{bean.model}" />
```
```java
public class Bean {
    private OhlcChartModel model;

    public Bean() {
        model = new OhlcChartModel();
        for( int i=1 ; i < 41 ; i++) {
            ohlcModel2.add(new OhlcChartSeries(i, Math.random() * 80 + 80,
            Math.random() * 50 + 110, Math.random() * 20 + 80, Math.random() * 80 + 80));
        }
        model.setTitle("Candlestick");
        model.setCandleStick(true);
        model.getAxis(AxisType.X).setLabel("Sector");
        model.getAxis(AxisType.Y).setLabel("Index Value");
    }
    public OhlcChartModel getModel() { 
        return model; 
    }
}
```

### MeterGauge Chart

MeterGauge Chart is created using MeterGaugeChartModel.

#### Basic

```xhtml
<p:chart type="metergauge" model="#{bean.model}" />
```
```java
public class Bean {
    private MeterGaugeChartModel model;
    public Bean() {
        List<Number> intervals = new ArrayList<Number>(){{
            add(20);
            add(50);
            add(120);
            add(220);
        }};
        model = new MeterGaugeChartModel(140, intervals);
        model.setTitle("MeterGauge Chart");
        model.setGaugeLabel("km/h");
    }
    public MeterGaugeChartModel getModel() { 
        return model; 
    }
}
```
#### Customized

```xhtml
<p:chart type="metergauge" model="#{bean.model}" />
```
```java
public class Bean {
    private MeterGaugeChartModel model;

    public Bean() {
        List<Number> intervals = new ArrayList<Number>(){{
            add(20);
            add(50);
            add(120);
            add(220);
        }};
        model = new MeterGaugeChartModel(140, intervals);
        model.setTitle("Custom Options");
        model.setSeriesColors("66cc66,93b75f,E7E658,cc6666");
        model.setGaugeLabel("km/h");
        model.setGaugeLabelPosition("bottom");
        model.setShowTickLabels(false);
        model.setLabelHeightAdjust(110);
        model.setIntervalOuterRadius(130);
    }
    public MeterGaugeChartModel getModel() { 
        return model;
    }
}
```

### Combined Chart

On same graph, different series type can be displayed together.

#### Basic

```xhtml
<p:chart type="bar" model="#{bean.model}" />
```
```java
public class Bean {
    private BarChartModel model;

    public Bean() {
        combinedModel = new BarChartModel();
        BarChartSeries boys = new BarChartSeries();
        boys.setLabel("Boys");
        boys.set("2004", 120);
        boys.set("2005", 100);
        boys.set("2006", 44);
        boys.set("2007", 150);
        boys.set("2008", 25);
        LineChartSeries girls = new LineChartSeries();
        girls.setLabel("Girls");
        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 110);
        girls.set("2007", 135);
        girls.set("2008", 120);
        model.addSeries(boys);
        model.addSeries(girls);
        model.setTitle("Bar and Line");
        model.setLegendPosition("ne");
        model.setMouseoverHighlight(false);
        model.setShowDatatip(false);
        model.setShowPointLabels(true);
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(200);
    }
    public BarChartModel getModel() { 
        return model; 
    }
}
```

### Multiple Axis

Up to 9 axes (xaxis-x9axis, yaxis-y9axis) can be displayed on the same chart.

#### Basic

```xhtml
<p:chart type="line" model="#{bean.model}" />
```
```java
public class Bean {
    private LineChartModel model;

    public Bean() {
        model = new LineChartModel();
        BarChartSeries boys = new BarChartSeries();
        boys.setLabel("Boys");
        boys.set("2004", 120);
        boys.set("2005", 100);
        boys.set("2006", 44);
        boys.set("2007", 150);
        boys.set("2008", 25);
        LineChartSeries girls = new LineChartSeries();
        girls.setLabel("Girls");
        girls.setXaxis(AxisType.X2);
        girls.setYaxis(AxisType.Y2);
        girls.set("A", 52);
        girls.set("B", 60);
        girls.set("C", 110);
        girls.set("D", 135);
        girls.set("E", 120);
        model.addSeries(boys);
        model.addSeries(girls);
        model.setTitle("Multi Axis Chart");
        model.setMouseoverHighlight(false);
        model.getAxes().put(AxisType.X, new CategoryAxis("Years"));
        model.getAxes().put(AxisType.X2, new CategoryAxis("Period"));
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Birth");
        yAxis.setMin(0);
        yAxis.setMax(200);
        Axis y2Axis = new LinearAxis("Number");
        y2Axis.setMin(0);
        y2Axis.setMax(200);
        model.getAxes().put(AxisType.Y2, y2Axis);
    }
    public LineChartModel getModel() { 
        return model; 
    }
}
```

### Date Axis

Use DateAxis if you are displaying dates in an axis.

#### Basic

```xhtml
<p:chart type="line" model="#{bean.model}" />
```
```java
public class Bean {
    private LineChartModel model;

    public Bean() {
        dateModel = new LineChartModel();
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Series 1");
        series1.set("2014-01-01", 51);
        series1.set("2014-01-06", 22);
        series1.set("2014-01-12", 65);
        series1.set("2014-01-18", 74);
        series1.set("2014-01-24", 24);
        series1.set("2014-01-30", 51);
        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel("Series 2");
        series2.set("2014-01-01", 32);
        series2.set("2014-01-06", 73);
        series2.set("2014-01-12", 24);
        series2.set("2014-01-18", 12);
        series2.set("2014-01-24", 74);
        series2.set("2014-01-30", 62);
        dateModel.addSeries(series1);
        dateModel.addSeries(series2);
        dateModel.setTitle("Zoom for Details");
        dateModel.setZoom(true);
        dateModel.getAxis(AxisType.Y).setLabel("Values");
        DateAxis axis = new DateAxis("Dates");
        axis.setTickAngle(-50);
        axis.setMax("2014-02-01");
        axis.setTickFormat("%b %#d, %y");
        dateModel.getAxes().put(AxisType.X, axis);
    }
    public LineChartModel getModel() { 
        return model;
    }
}
```

### Interactive Chart

Charts are interactive components, information about selected series and items can be passed via
ajax to a JSF backing bean using ItemSelectEvent.

#### Basic

```xhtml
<p:chart type="pie" model="#{bean.model}">
    <p:ajax event="itemSelect" listener="#{bean.itemSelect}" />
</p:chart>
```
```java
public class Bean {
    private PieChartModel model;
    public Bean() {
        model = new PieChartModel();
        model.set("Brand 1", 540);
        model.set("Brand 2", 325);
        model.set("Brand 3", 702);
        model.set("Brand 4", 421);
        model.setTitle("Simple Pie");
        model.setLegendPosition("w");
    }
    public PieChartModel getModel() { 
        return model; 
    }
    public void itemSelect(ItemSelectEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
        "Item selected", "Item Index: " + event.getItemIndex() +
        ", Series Index:" + event.getSeriesIndex());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
```

### Export

Chart component provides a client side method to convert the graph to an image. Example below
demonstrates how to use a button click to export the chart as an image and display it in a dialog so
that users can download it as a regular image.

```xhtml
<p:chart type="line" model="#{bean.model}" style="width:500px;height:300px" widgetVar="chart"/>
<p:commandButton type="button" value="Export" icon="ui-icon-extlink" onclick="exportChart()"/>
<p:dialog widgetVar="dlg" showEffect="fade" modal="true" header="Chart as an Image">
    <p:outputPanel id="output" layout="block" style="width:500px;height:300px"/>
</p:dialog>
```
```js
function exportChart() {
    //export image
    $('#output').empty().append(PF('chart').exportAsImage());
    //show the dialog
    PF('dlg').show();
}
```

### Static Images

JFreeChart with GraphicImage component is an alternative to the chart component.

#### Basic

```xhtml
<p:graphicImage value="#{bean.chart}" />
```
```java
public class Bean {
    private StreamedContent chart;

    public Bean() {
        JFreeChart jfreechart = ChartFactory.createPieChart("Cities",
        createDataset(), true, true, false);
        File chartFile = new File("dynamichart");
        ChartUtilities.saveChartAsPNG(chartFile, jfreechart, 375, 300);
        chart = new DefaultStreamedContent(new FileInputStream(chartFile),
        "image/png");
    }
    public StreamedContent getChart() {
        return model;
    }
    private PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("New York", new Double(45.0));
        dataset.setValue("London", new Double(15.0));
        dataset.setValue("Paris", new Double(25.2));
        dataset.setValue("Berlin", new Double(14.8));
        return dataset;
    }
}
```

## Skinning

Charts can be styled using regular css. Following is the list of style classes;

| Class | Applies | 
| --- | --- | 
| .jqplot-target | Plot target container.
| .jqplot-axis | Axes.
| .jqplot-xaxis | Primary x-axis.
| .jqplot-yaxis | Primary y-axis.
| .jqplot-x2axis, .jqplot-x3axis | ... 2nd, 3rd ... x-axis.
| .jqplot-y2axis, .jqplot-y3axis | ... 2nd, 3rd ... y-axis.
| .jqplot-axis-tick | Axis ticks.
| .jqplot-xaxis-tick | Primary x-axis ticks.
| .jqplot-x2axis-tick | Secondary x-axis ticks.
| .jqplot-yaxis-tick | Primary y-axis-ticks.
| .jqplot-y2axis-tick | Seconday y-axis-ticks.
| table.jqplot-table-legend | Legend table.
| .jqplot-title | Title of the chart.
| .jqplot-cursor-tooltip | Cursor tooltip.
| .jqplot-highlighter-tooltip | Highlighter tooltip.
| div.jqplot-table-legend-swatch | Colors swatch of the legend.

Additionally _style_ and _styleClass_ options of chart component apply to the container element of
charts, use these attribute to specify the dimensions of a chart.

```xhtml
<p:chart type="pie" model="#{bean.model}" style=”width:320px;height:200px” />
```
In case you’d like to change the colors of series, use the _seriesColors_ option in ChartModel API.


## Extender

Chart API provide high level access to commonly used jqplot options however there are many more
customization options available in jqplot. Extender feature provide access to low level apis to do
advanced customization by enhancing the configuration object, here is an example to increase
shadow depth of the line series where model's extender property is set to "ext".

```xhtml
<p:chart type="line" model="#{bean.model}" />
```
```js
function ext() {
    //this = chart widget instance
    //this.cfg = options
    this.cfg.seriesDefaults = {
    shadowDepth: 5
    };
}
```
Refer to jqPlot docs for available options.


## Chart API

### Axis
_org.primefaces.model.chart.Axis_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| label | null | String | Title of the axis.
| min | null | Object | Minimum boundary value.
| max | null | Object | Maximum boundary value.
| tickAngle | null | Integer | Angle of text, measured clockwise.
| tickFormat | null | String | Format string to use with the axis tick formatter
| tickInterval | null | String | Number of units between ticks.
| tickCount | null | Integer | Desired number of ticks.

### AxisType
_org.primefaces.model.chart.AxisType_

AxisType is an enum to define the type of the axis from X-Y to X9-Y9.

### BarChartModel
_org.primefaces.model.chart.BarChartModel_ extends _org.primefaces.model.chart.ChartModel_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| barPadding | 8 | Integer | Padding between bars.
| barMargin | 10 | Integer | Margin between bars.
| stacked | false | Boolean | Displays series in stacked format.

### BarChartSeries
_org.primefaces.model.chart.BarChartSeries_ extends _org.primefaces.model.chart.ChartSeries_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| disableStack | false | Boolean | When true, series data is not included in a stacked chart.

### BubbleChartModel
_org.primefaces.model.chart.BubbleChartModel_ extends _org.primefaces.model.chart.CartesianChartModel_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| data | null | List<BubbleChartSeries> | Data as a list of BubbleChartSeries.
| bubbleGradients | false | Boolean | Displays bubbles with gradients.
| bubbleAlpha | 1.0 | Double | Opacity of bubbles.
| showLabels | true | Boolean | Displays label of a series inside a bubble.


### BubbleChartSeries
_org.primefaces.model.chart.BubbleChartSeries_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| x | null | Integer | X-Axis value of the bubble.
| y | null | Integer | Y-Axis value of the bubble.
| radius | null | Integer | Radius of the bubble.
| label | null | String | Label text of the bubble.

### CartesianChartModel
_org.primefaces.model.chart.CartesianChartModel_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| series | null | List<ChartSeries> | List of series.
| axes | HashMap | Map<AxisType,Axis> | Map of chart axis.
| zoom | false | Boolean | Adds zoom feature when enabled.
| animate | false | Boolean | When enabled, series are drawn with an effect.
| showDatatip | true | Boolean | Displays a tooltip on hover.
| datatipFormat | null | String | Format of the data tooltip.
| showPointLabels | false | Boolean | Displays data inline in plot.
| datatipEditor | function | String | Javascript callback to customize the datatips.

### CategoryAxis
_org.primefaces.model.chart.CategoryAxis_ extends _org.primefaces.model.chart.Axis_

CategoryAxis is used when data on the axis does not consists of numbers.

### ChartModel
_org.primefaces.model.chart.ChartModel_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| title | null | String | Title text for the plot
| shadow | true | Boolean | To show shadow or not on series.
| seriesColors | null | String | Comma separated list of series colors e.g. "#4BB2C5","CCCCC"
| negativeSeriesColors | null | String | Similar to seriesColors but for negative values.
| legendPosition | null | String | Position of the legend like "n" or "ne".
| legendCols | 0 | Integer | Maximum number of columns in the legend.
| legendRows | 0 | Integer | Maximum number of rows in the legend.
| legendPlacement | null | Enum | Defines the location of the legend.
| mouseoverHighlight | true | Boolean | Highlights series on hover.
| extender | null | String | Name of javascript function to extend chart with.
| resetAxesOnResize | true | Boolean | Resets axes on resize for responsive charts.


### ChartSeries
_org.primefaces.model.chart.ChartSeries_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| label | null | String | Title text of the series.
| data | null | Map<Object,Number> | Data of the series as a map.
| xaxis | null | AxisType | X-Axis of the series.
| yaxis | null | AxisType | Y-Axis of the series.

### DateAxis
_org.primefaces.model.chart.DateAxis_ extends _org.primefaces.model.chart.Axis_

DateAxis is used when data on the axis consists of string representations of date values.

### DonutChartModel
_org.primefaces.model.chart.DonutChartModel_ extends _org.primefaces.model.chart.ChartModel_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| data | null | List<Map<String,Object>> | Data as a list of map instances.
| sliceMargin | 0 | Integer | Angular spacing between pie slices in degrees.
| fill | true | Boolean | True or False to fill the slices.
| showDataLabels | false | Boolean | True to False show data labels on slices.
| dataFormat | percent | String | Either ‘label’, ‘value’, ‘percent’ or an array of labels to place on the pie slices.
| dataLabelFormat | null | String | Format string for data labels. If none, ‘%s’ is used for “label” and for arrays, ‘%d’ for value and ‘%d%%’ for percentage.
| dataLabelThreshold | 3 | Integer | Threshhold in percentage (0-100) of pie area, below which no label will be displayed. This applies to all label types, not just to percentage labels.
| showDatatip | true | Boolean | Displays tooltip when enabled.
| datatipFormat | %s-%d | String | Format string for datatip.
| datatipEditor | null | String | Name of client side function that returns html to provide custom content in datatip.

### HorizontalBarChartModel
_org.primefaces.model.chart.HorizontalBarChartModel_ extends _org.primefaces.model.chart.BarChartModel_

HorizontalBarChartModel is an extension to BarChartModel with y-axis used for the categories and x-axis for the data values.

### LineChartSeries
_org.primefaces.model.chart.LineChartSeries_ extends _org.primefaces.model.chart.ChartSeries_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| markerStyle | filledCircle | String | Style of the markers, valid values are _diamond_ , _circle_ , _square_ , x , plus , dash , filledDiamond , filledCircle , filledSquare.
| showLine | true | Boolean | Whether to actually draw the line or not.
| showMarker | true | Boolean | Displays markes at data points.
| fill | false | Boolean | Fills the area between lines.
| fillAlpha | 1 | Double | Opacity of the filled area.
| smoothLine | false | Boolean | Enables smooth renderer.
| disableStack | false | Boolean | When true, series data is not included in a stacked chart.

### LinearAxis
_org.primefaces.model.chart.LinearAxis_ extends _org.primefaces.model.chart.Axis_

LinearAxis is the Axis implementation used to display numbers.

### LineChartModel
_org.primefaces.model.chart.LineChartModel_ extends _org.primefaces.model.chart.CartesianChartModel_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| stacked | null | List< ChartSeries > | Displays series in stacked format.
| breakOnNull | HashMap | Map<AxisType,Axis> | Discontinues line plot for | null | values.

### MeterGaugeChartModel
_org.primefaces.model.chart.MeterGaugeChartModel_ extends _org.primefaces.model.chart.ChartModel_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| value | null | Number | Value of the gauge.
| intervals | null | List<Number> | List of ranges to be drawn around the gauge.
| ticks | 0 | List<Number> | List of tick values.
| gaugeLabel | null | String | Label text of the gauge.
| gaugeLabelPosition | null | String | Where to position the label, either ‘inside’ or ‘bottom’.
| min | null | Double | Minimum value on the gauge.
| max | null | Double | Minimum value on the gauge.
| showTickLabels | true | Boolean | Displays tick labels next to ticks.
| intervalInnerRadius | null | Integer | Radius of the inner circle of the interval ring.
| intervalOuterRadius | 85 | Integer | Radius of the outer circle of the interval ring.
| labelHeightAdjust | -25 | Integer | Number of Pixels to offset the label up (-) or down (+) from its default position.

### OhlcChartModel
_org.primefaces.model.chart.OhlcChartModel_ extends _org.primefaces.model.chart.ChartModel_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| data | null | List<OhlcChartSeries> | Data as a list of OhlChartSeries.
| candleStick | false | Boolean | Displays series as candlestick.

### OhlcChartSeries
_org.primefaces.model.chart.OhlcChartSeries_ extends _org.primefaces.model.chart.ChartSeries_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| value | null | List<OhlcChartSeries> | Data as a list of OhlChartSeries.
| open | null | Double | Open value.
| high | null | Double | High value.
| low | null | Double | Low value.
| close | null | Double | Close value.

### PieChartModel
_org.primefaces.model.chart.PieChartModel_ extends _org.primefaces.model.chart.ChartModel_

| Property | Default | Type | Description |
| --- | --- | --- | --- |
| data | null | Map<String,Object> | Data as a Map instance.
| diameter | null | Integer | Outer diameter of the pie, auto computed by default
| sliceMargin | 0 | Integer | Angular spacing between pie slices in degrees.
| fill | true | Boolean | True or False to fill the slices.
| showDataLabels | false | Boolean | True to False show data labels on slices.
| dataFormat | percent | String | Either ‘label’, ‘value’, ‘percent’ or an array of labels to place on the pie slices.
| dataLabelFormat String | null | String | Format string for data labels. If none, ‘%s’ is used for “label” and for arrays, ‘%d’ for value and ‘%d% %’ for percentage.
| dataLabelThreshold | 3 | Integer | Threshhold in percentage (0-100) of pie area, below which no label will be displayed. This applies to all label types, not just to percentage labels.
| showDatatip | true | Boolean | Displays tooltip when enabled.
| datatipFormat | %s-%d | String | Format string for datatip.
| datatipEditor | null | String | Name of client side function that returns html to provide custom content in datatip.
