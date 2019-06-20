# Charts

Chart.js based components are a modern replacement for the older p:chart component. Each chart component has its own model api that defines the data and the options to customize the graph.

## BarChart

A bar chart provides a way of showing data values represented as vertical bars. It is sometimes used to show trend data, and the comparison of multiple data sets side by side.

### Info

| Name | Value |
| --- | --- |
| Tag | barChart
| Component Class | org.primefaces.component.barchart.BarChart
| Component Type | org.primefaces.component.BarChart
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.BarChartRenderer
| Renderer Class | org.primefaces.component.barchart.BarChartRenderer

### Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| model | null | ChartModel | Model object of data and settings.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| widgetVar | null | String | Name of the client side widget.

### Getting Started with BarChart

```xhtml
<p:barChart model="#{bean.barModel}" />
```

```java
public class Bean {

    private BarChartModel barModel;

    @PostConstruct
    public void init() {
        barModel = new BarChartModel();
        ChartData data = new ChartData();
        
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("My First Dataset");
        
        List<Number> values = new ArrayList<>();
        values.add(65);
        values.add(59);
        values.add(80);
        values.add(81);
        values.add(56);
        values.add(55);
        values.add(40);
        barDataSet.setData(values);
        
        List<String> bgColor = new ArrayList<>();
        bgColor.add("rgba(255, 99, 132, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 205, 86, 0.2)");
        bgColor.add("rgba(75, 192, 192, 0.2)");
        bgColor.add("rgba(54, 162, 235, 0.2)");
        bgColor.add("rgba(153, 102, 255, 0.2)");
        bgColor.add("rgba(201, 203, 207, 0.2)");
        barDataSet.setBackgroundColor(bgColor);
        
        List<String> borderColor = new ArrayList<>();
        borderColor.add("rgb(255, 99, 132)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 205, 86)");
        borderColor.add("rgb(75, 192, 192)");
        borderColor.add("rgb(54, 162, 235)");
        borderColor.add("rgb(153, 102, 255)");
        borderColor.add("rgb(201, 203, 207)");
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);
        
        data.addChartDataSet(barDataSet);
        
        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        data.setLabels(labels);

        //Data
        barModel.setData(data);
        
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
        
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(24);
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        barModel.setOptions(options);
    }

    public BarChartModel getBarModel() {
        return barModel;
    }
}
```

## Horizontal Bar

```xhtml
<p:barChart model="#{bean.hbarModel}" />
```

```java
public class Bean {
    
    private HorizontalBarChartModel hbarModel;

    @PostConstruct
    public void init() {
        hbarModel = new HorizontalBarChartModel();
        ChartData data = new ChartData();
        
        HorizontalBarChartDataSet hbarDataSet = new HorizontalBarChartDataSet();
        hbarDataSet.setLabel("My First Dataset");
        
        List<Number> values = new ArrayList<>();
        values.add(65);
        values.add(59);
        values.add(80);
        values.add(81);
        values.add(56);
        values.add(55);
        values.add(40);
        hbarDataSet.setData(values);
        
        List<String> bgColor = new ArrayList<>();
        bgColor.add("rgba(255, 99, 132, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 205, 86, 0.2)");
        bgColor.add("rgba(75, 192, 192, 0.2)");
        bgColor.add("rgba(54, 162, 235, 0.2)");
        bgColor.add("rgba(153, 102, 255, 0.2)");
        bgColor.add("rgba(201, 203, 207, 0.2)");
        hbarDataSet.setBackgroundColor(bgColor);
        
        List<String> borderColor = new ArrayList<>();
        borderColor.add("rgb(255, 99, 132)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 205, 86)");
        borderColor.add("rgb(75, 192, 192)");
        borderColor.add("rgb(54, 162, 235)");
        borderColor.add("rgb(153, 102, 255)");
        borderColor.add("rgb(201, 203, 207)");
        hbarDataSet.setBorderColor(borderColor);
        hbarDataSet.setBorderWidth(1);
        
        data.addChartDataSet(hbarDataSet);
        
        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        data.setLabels(labels);
        hbarModel.setData(data);
        
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addXAxesData(linearAxes);
        options.setScales(cScales);
        
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Horizontal Bar Chart");
        options.setTitle(title);
        
        hbarModel.setOptions(options);
    }

    public HorizontalBarChartModel getHbarModel() {
        return hbarModel;
    }
}
```

## Stacked Bar

```xhtml
<p:barChart model="#{bean.stackedBarModel}" />
```

```java
public class Bean {
    
    private BarChartModel stackedBarModel;

    @PostConstruct
    public void init() {
        stackedBarModel = new BarChartModel();
        ChartData data = new ChartData();
        
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Dataset 1");
        barDataSet.setBackgroundColor("rgb(255, 99, 132)");
        List<Number> dataVal = new ArrayList<>();
        dataVal.add(62);
        dataVal.add(-58);
        dataVal.add(-49);
        dataVal.add(25);
        dataVal.add(4);
        dataVal.add(77);
        dataVal.add(-41);
        barDataSet.setData(dataVal);
        
        BarChartDataSet barDataSet2 = new BarChartDataSet();
        barDataSet2.setLabel("Dataset 2");
        barDataSet2.setBackgroundColor("rgb(54, 162, 235)");
        List<Number> dataVal2 = new ArrayList<>();
        dataVal2.add(-1);
        dataVal2.add(32);
        dataVal2.add(-52);
        dataVal2.add(11);
        dataVal2.add(97);
        dataVal2.add(76);
        dataVal2.add(-78);
        barDataSet2.setData(dataVal2);
        
        BarChartDataSet barDataSet3 = new BarChartDataSet();
        barDataSet3.setLabel("Dataset 3");
        barDataSet3.setBackgroundColor("rgb(75, 192, 192)");
        List<Number> dataVal3 = new ArrayList<>();
        dataVal3.add(-44);
        dataVal3.add(25);
        dataVal3.add(15);
        dataVal3.add(92);
        dataVal3.add(80);
        dataVal3.add(-25);
        dataVal3.add(-11);
        barDataSet3.setData(dataVal3);
        
        data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet2);
        data.addChartDataSet(barDataSet3);
        
        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        data.setLabels(labels);
        stackedBarModel.setData(data);
        
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setStacked(true);    
        cScales.addXAxesData(linearAxes);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
        
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart - Stacked");
        options.setTitle(title);
        
        Tooltip tooltip = new Tooltip();
        tooltip.setMode("index");
        tooltip.setIntersect(false);
        options.setTooltip(tooltip);  
        
        stackedBarModel.setOptions(options);
    }

    public BarChartModel getStackedBarModel() {
        return stackedBarModel;
    }
}
```

## Stacked Group Bar

```xhtml
<p:barChart model="#{bean.stackedBarModel}" />
```

```java
public class Bean {
    
    private BarChartModel stackedGroupBarModel;

    @PostConstruct
    public void init() {
        stackedGroupBarModel = new BarChartModel();
        ChartData data = new ChartData();
        
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Dataset 1");
        barDataSet.setBackgroundColor("rgb(255, 99, 132)");
        barDataSet.setStack("Stack 0");
        List<Number> dataVal = new ArrayList<>();
        dataVal.add(-32);
        dataVal.add(-70);
        dataVal.add(-33);
        dataVal.add(30);
        dataVal.add(-49);
        dataVal.add(23);
        dataVal.add(-92);
        barDataSet.setData(dataVal);
        
        BarChartDataSet barDataSet2 = new BarChartDataSet();
        barDataSet2.setLabel("Dataset 2");
        barDataSet2.setBackgroundColor("rgb(54, 162, 235)");
        barDataSet2.setStack("Stack 0");
        List<Number> dataVal2 = new ArrayList<>();
        dataVal2.add(83);
        dataVal2.add(18);
        dataVal2.add(86);
        dataVal2.add(8);
        dataVal2.add(34);
        dataVal2.add(46);
        dataVal2.add(11);
        barDataSet2.setData(dataVal2);
        
        BarChartDataSet barDataSet3 = new BarChartDataSet();
        barDataSet3.setLabel("Dataset 3");
        barDataSet3.setBackgroundColor("rgb(75, 192, 192)");
        barDataSet3.setStack("Stack 1");
        List<Number> dataVal3 = new ArrayList<>();
        dataVal3.add(-45);
        dataVal3.add(73);
        dataVal3.add(-25);
        dataVal3.add(65);
        dataVal3.add(49);
        dataVal3.add(-18);
        dataVal3.add(46);
        barDataSet3.setData(dataVal3);
        
        data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet2);
        data.addChartDataSet(barDataSet3);
        
        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        data.setLabels(labels);
        stackedGroupBarModel.setData(data);
        
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setStacked(true);    
        cScales.addXAxesData(linearAxes);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
        
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart - Stacked Group");
        options.setTitle(title);
        
        Tooltip tooltip = new Tooltip();
        tooltip.setMode("index");
        tooltip.setIntersect(false);
        options.setTooltip(tooltip);  
        
        stackedGroupBarModel.setOptions(options);
    }

    public BarChartModel getStackedGroupBarModel() {
        return stackedGroupBarModel;
    }
}
```

## BubbleChart

A bubble chart is used to display three dimensions of data at the same time. The location of the bubble is determined by the first two dimensions and the corresponding horizontal and vertical axes. The third dimension is represented by the size of the individual bubbles.

### Info

| Name | Value |
| --- | --- |
| Tag | bubbleChart
| Component Class | org.primefaces.component.bubblechart.BubbleChart
| Component Type | org.primefaces.component.BubbleChart
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.BubbleChartRenderer
| Renderer Class | org.primefaces.component.bubblechart.BubbleChartRenderer

### Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| model | null | ChartModel | Model object of data and settings.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| widgetVar | null | String | Name of the client side widget.

### Getting Started with BubbleChart

```xhtml
<p:bubbleChart model="#{bean.bubbleModel}" />
```

```java
public class Bean {

    private BubbleChartModel bubbleModel;

    @PostConstruct
    public void init() {
        bubbleModel = new BubbleChartModel();
        ChartData data = new ChartData();
        
        BubbleChartDataSet dataSet = new BubbleChartDataSet();
        List<BubblePoint> values = new ArrayList<>();
        values.add(new BubblePoint(20, 30, 15));
        values.add(new BubblePoint(40, 10, 10));
        dataSet.setData(values);
        dataSet.setBackgroundColor("rgb(255, 99, 132)");
        dataSet.setLabel("First Dataset");
        data.addChartDataSet(dataSet);
        bubbleModel.setData(data);
    }

    public BubbleChartModel getBubbleModel() {
        return bubbleModel;
    }
}
```

## DonutChart

A Donut Chart is a variation of a Pie Chart but with a space in the center.

### Info

| Name | Value |
| --- | --- |
| Tag | donutChart
| Component Class | org.primefaces.component.donutchart.DonutChart
| Component Type | org.primefaces.component.DonutChart
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DonutChartRenderer
| Renderer Class | org.primefaces.component.donutchart.DonutChartRenderer

### Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| model | null | ChartModel | Model object of data and settings.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| widgetVar | null | String | Name of the client side widget.

### Getting Started with DonutChart

```xhtml
<p:donutChart model="#{bean.donutModel}" />
```

```java
public class Bean {

    private DonutChartModel donutModel;

    @PostConstruct
    public void init() {
        donutModel = new DonutChartModel();
        ChartData data = new ChartData();
        
        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(300);
        values.add(50);
        values.add(100);
        dataSet.setData(values);
        
        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(255, 99, 132)");
        bgColors.add("rgb(54, 162, 235)");
        bgColors.add("rgb(255, 205, 86)");
        dataSet.setBackgroundColor(bgColors);
        
        data.addChartDataSet(dataSet);
        List<String> labels = new ArrayList<>();
        labels.add("Red");
        labels.add("Blue");
        labels.add("Yellow");
        data.setLabels(labels);
        
        donutModel.setData(data);
    }

    public DonutChartModel getDonutModel() {
        return donutModel;
    }
}
```

## LineChart

A line chart is a way of plotting data points on a line. Often, it is used to show trend data, or the comparison of two data sets.

### Info

| Name | Value |
| --- | --- |
| Tag | lineChart
| Component Class | org.primefaces.component.linechart.LineChart
| Component Type | org.primefaces.component.LineChart
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.LineChartRenderer
| Renderer Class | org.primefaces.component.linechart.LineChartRenderer

### Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| model | null | ChartModel | Model object of data and settings.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| widgetVar | null | String | Name of the client side widget.

### Getting Started with LineChart

```xhtml
<p:lineChart model="#{bean.lineModel}" />
```

```java
public class Bean {

    private LineChartModel lineModel;

    @PostConstruct
    public void init() {
        lineModel = new LineChartModel();
        ChartData data = new ChartData();
        
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(65);
        values.add(59);
        values.add(80);
        values.add(81);
        values.add(56);
        values.add(55);
        values.add(40);
        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel("My First Dataset");
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setLineTension(0.1);
        data.addChartDataSet(dataSet);
        
        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        data.setLabels(labels);
        
        //Options
        LineChartOptions options = new LineChartOptions();        
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Line Chart");
        options.setTitle(title);
        
        lineModel.setOptions(options);
        lineModel.setData(data);
    }

    public LineChartModel getLineModel() {
        return lineModel;
    }
}
```

## PieChart

Pie chart is divided into segments, the arc of each segment shows the proportional value of each piece of data.

### Info

| Name | Value |
| --- | --- |
| Tag | pieChart
| Component Class | org.primefaces.component.piechart.PieChart
| Component Type | org.primefaces.component.PieChart
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.PieChartRenderer
| Renderer Class | org.primefaces.component.piechart.PieChartRenderer

### Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| model | null | ChartModel | Model object of data and settings.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| widgetVar | null | String | Name of the client side widget.

### Getting Started with PieChart

```xhtml
<p:pieChart model="#{bean.pieModel}" />
```

```java
public class Bean {

    private PieChartModel pieModel;

    @PostConstruct
    public void init() {
        pieModel = new PieChartModel();
        ChartData data = new ChartData();
        
        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(300);
        values.add(50);
        values.add(100);
        dataSet.setData(values);
        
        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(255, 99, 132)");
        bgColors.add("rgb(54, 162, 235)");
        bgColors.add("rgb(255, 205, 86)");
        dataSet.setBackgroundColor(bgColors);
        
        data.addChartDataSet(dataSet);
        List<String> labels = new ArrayList<>();
        labels.add("Red");
        labels.add("Blue");
        labels.add("Yellow");
        data.setLabels(labels);
        
        pieModel.setData(data);
    }

    public PieChartModel getPieModel() {
        return pieModel;
    }
}
```

## PolarAreaChart

Polar area charts are similar to pie charts, but each segment has the same angle - the radius of the segment differs depending on the value.

### Info

| Name | Value |
| --- | --- |
| Tag | polarAreaChart
| Component Class | org.primefaces.component.polarareachart.PolarAreaChart
| Component Type | org.primefaces.component.PolarAreaChart
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.PolarAreaChartRenderer
| Renderer Class | org.primefaces.component.polarareachart.PolarAreaChartRenderer

### Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| model | null | ChartModel | Model object of data and settings.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| widgetVar | null | String | Name of the client side widget.

### Getting Started with PolarAreaChart

```xhtml
<p:polarAreaChart model="#{bean.polarAreaModel}" />
```

```java
public class Bean {

    private PieChartModel pieModel;

    @PostConstruct
    public void init() {
        polarAreaModel = new PolarAreaChartModel();
        ChartData data = new ChartData();
        
        PolarAreaChartDataSet dataSet = new PolarAreaChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(11);
        values.add(16);
        values.add(7);
        values.add(3);
        values.add(14);
        dataSet.setData(values);
        
        List<String> bgColors = new ArrayList<>();
        bgColors.add("rgb(255, 99, 132)");
        bgColors.add("rgb(75, 192, 192)");
        bgColors.add("rgb(255, 205, 86)");
        bgColors.add("rgb(201, 203, 207)");
        bgColors.add("rgb(54, 162, 235)");
        dataSet.setBackgroundColor(bgColors);
        
        data.addChartDataSet(dataSet);
        List<String> labels = new ArrayList<>();
        labels.add("Red");
        labels.add("Green");
        labels.add("Yellow");
        labels.add("Grey");
        labels.add("Blue");
        data.setLabels(labels);
        
        polarAreaModel.setData(data);
    }

    public PolarAreaModel getPolarAreaModel() {
        return polarAreaModel;
    }
}
```

## RadarChart

A radar chart is a way of showing multiple data points and the variation between them.

### Info

| Name | Value |
| --- | --- |
| Tag | radarChart
| Component Class | org.primefaces.component.radarchart.RadarChart
| Component Type | org.primefaces.component.RadarChart
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.RadarChartRenderer
| Renderer Class | org.primefaces.component.radarchart.RadarChartRenderer

### Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| model | null | ChartModel | Model object of data and settings.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| widgetVar | null | String | Name of the client side widget.

### Getting Started with RadarChart

```xhtml
<p:radarChart model="#{bean.radarModel}" />
```

```java
public class Bean {

    private RadarChartModel radarModel;

    @PostConstruct
    public void init() {
        radarModel = new RadarChartModel();
        ChartData data = new ChartData();
        
        RadarChartDataSet radarDataSet = new RadarChartDataSet();
        radarDataSet.setLabel("My First Dataset");
        radarDataSet.setFill(true);
        radarDataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        radarDataSet.setBorderColor("rgb(255, 99, 132)");
        radarDataSet.setPointBackgroundColor("rgb(255, 99, 132)");
        radarDataSet.setPointBorderColor("#fff");
        radarDataSet.setPointHoverBackgroundColor("#fff");
        radarDataSet.setPointHoverBorderColor("rgb(255, 99, 132)");
        List<Number> dataVal = new ArrayList<>();
        dataVal.add(65);
        dataVal.add(59);
        dataVal.add(90);
        dataVal.add(81);
        dataVal.add(56);
        dataVal.add(55);
        dataVal.add(40);
        radarDataSet.setData(dataVal);
        
        RadarChartDataSet radarDataSet2 = new RadarChartDataSet();
        radarDataSet2.setLabel("My Second Dataset");
        radarDataSet2.setFill(true);
        radarDataSet2.setBackgroundColor("rgba(54, 162, 235, 0.2)");
        radarDataSet2.setBorderColor("rgb(54, 162, 235)");
        radarDataSet2.setPointBackgroundColor("rgb(54, 162, 235)");
        radarDataSet2.setPointBorderColor("#fff");
        radarDataSet2.setPointHoverBackgroundColor("#fff");
        radarDataSet2.setPointHoverBorderColor("rgb(54, 162, 235)");
        List<Number> dataVal2 = new ArrayList<>();
        dataVal2.add(28);
        dataVal2.add(48);
        dataVal2.add(40);
        dataVal2.add(19);
        dataVal2.add(96);
        dataVal2.add(27);
        dataVal2.add(100);
        radarDataSet2.setData(dataVal2);
        
        data.addChartDataSet(radarDataSet);
        data.addChartDataSet(radarDataSet2);
        
        List<String> labels = new ArrayList<>();
        labels.add("Eating");
        labels.add("Drinking");
        labels.add("Sleeping");
        labels.add("Designing");
        labels.add("Coding");
        labels.add("Cycling");
        labels.add("Running");
        data.setLabels(labels);
        
        /* Options */
        RadarChartOptions options = new RadarChartOptions();
        Elements elements = new Elements();
        ElementsLine elementsLine = new ElementsLine();
        elementsLine.setTension(0);
        elementsLine.setBorderWidth(3);
        elements.setLine(elementsLine);
        options.setElements(elements);
        
        radarModel.setOptions(options);
        radarModel.setData(data);
    }

    public RadarChartModel getRadarModel() {
        return radarModel;
    }
}
```

## Mixed Chart

Different types of dataSet can be displayed on the same graph. Following example uses a bar and a line series.

```xhtml
<p:barChart model="#{bean.mixedModel}" />
```

```java
public class Bean {

    private BarChartModel mixedModel;

    @PostConstruct
    public void init() {
        mixedModel = new BarChartModel();
        ChartData data = new ChartData();
        
        BarChartDataSet dataSet = new BarChartDataSet();
        List<Number> values = new ArrayList<>();
        values.add(10);
        values.add(20);
        values.add(30);
        values.add(40);
        dataSet.setData(values);
        dataSet.setLabel("Bar Dataset");
        dataSet.setBorderColor("rgb(255, 99, 132)");
        dataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        
        LineChartDataSet dataSet2 = new LineChartDataSet();
        List<Number> values2 = new ArrayList<>();
        values2.add(50);
        values2.add(50);
        values2.add(50);
        values2.add(50);
        dataSet2.setData(values2);
        dataSet2.setLabel("Line Dataset");
        dataSet2.setFill(false);
        dataSet2.setBorderColor("rgb(54, 162, 235)");
        
        data.addChartDataSet(dataSet);
        data.addChartDataSet(dataSet2);
        
        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        data.setLabels(labels);
        
        mixedModel.setData(data);
        
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
        mixedModel.setOptions(options);
    }

    public BarChartModel getMixedModel() {
        return mixedModel;
    }
}
```

## Interactive Chart

When a dataset data is clicked, information can be passed to a backing bean using itemSelect ajax behavior.

```xhtml
<p:growl id="growl" showDetail="true" />
     
<p:barChart model="#{bean.barModel}">
    <p:ajax event="itemSelect" listener="#{bean.itemSelect}" update="growl"/>
</p:barChart>
```

```java
public void itemSelect(ItemSelectEvent event) {
    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
            "Item Index: " + event.getItemIndex() + ", DataSet Index:" + event.getDataSetIndex());

    FacesContext.getCurrentInstance().addMessage(null, msg);
}
```

## Export

Charts are canvas based and can be exported as static images with client side api.

```xhtml
<script type="text/javascript">
//<![CDATA[
    function exportChart() {
        //export image
        $('#output').empty().append(PF('chart').exportAsImage());

        //show the dialog
        PF('dlg').show();
    }
//]]>
</script>

<p:barChart model="#{bean.barModel}"  style="width: 100%; height: 500px;" widgetVar="chart" />
 
<p:commandButton type="button" value="Export" icon="pi pi-home" onclick="exportChart()"/>
 
<p:dialog widgetVar="dlg" showEffect="fade" modal="true" header="Chart as an Image" resizable="false">
    <p:outputPanel id="output" layout="block" style="width:500px;height:300px"/>
</p:dialog>
```

## Extender
Extender function allows access to the underlying chart.js api using the setExtender method of the model.

```xhtml
<p:radarChart model="#{bean.radarModel2}" />

<h:outputScript>
        function chartExtender() {
           //copy the config options into a variable
           var options = $.extend(true, {}, this.cfg.config);
        
           options = {
              //remove the legend
              legend: {
                 display: false
              },
              scales: {
                 xAxes: [{
                    display: true,
                    type: "time",
                    time: {
                       parser: 'h:mm:ss a',
                       tooltipFormat: 'h:mm:ss a',
                       unit: 'hour',
                       displayFormats: {
                          'hour': 'h:mm:ss a'
                       }
                    }
                 }],
                 yAxes: [{
                    display: true,
                    scaleLabel: {
                       display: true,
                       labelString: 'Your Y Axis',
                       fontSize: 13,
                    }
                 }]
              }
           };
        
           //merge all options into the main chart options
           $.extend(true, this.cfg.config, options);
        };
</h:outputScript>
```

```java
public class Bean {

    private RadarChartModel radarModel2;

    @PostConstruct
    public void init() {
        // create the model
        radarModel2 = new RadarChartModel();

        // REMOVED FOR BREVITY

        // pass Javascript function name to be called 
        radarModel2.setExtender("chartExtender");
    }

    public RadarChartModel getRadarModel() {
        return radarModel;
    }
}
```