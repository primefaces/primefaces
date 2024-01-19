# Charts

Chart.js based components are a modern replacement for the older jQuery-based `p:chart` component. Each chart component has its own model api that defines the data and the options to customize the graph.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Chart.html)

## Chart

A bar chart provides a way of showing data values represented as vertical bars. It is sometimes used to show trend data, and the comparison of multiple data sets side by side.

### Info

| Name | Value |
| --- | --- |
| Tag | barChart
| Component Class | org.primefaces.component.chart.Chart
| Component Type | org.primefaces.component.Chart
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ChartRenderer
| Renderer Class | org.primefaces.component.barchart.ChartRenderer

### Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| ariaLabel | title | String | The aria-label attribute is used to define a string that labels the current element for accessibility. (default to chart title).
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| extender | null | String | Name of JavaScript function to extend the options of the underlying Chart.js plugin.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.
| value | null | String | XDEV Model object of data and settings or raw JSON string.
| widgetVar | null | String | Name of the client side widget.

### Raw JSON

You can pass any full Chart.js configuration using a raw JSON string of data and configuration. This allows you complete and total
control over the chart and how it is rendered as well as any new Chart.js features as they come along.

```xhtml
<p:chart value="#{chartView.json}">
```

```java
public class Bean {
    
    private String json;

    @PostConstruct
    public void init() {
        json = """
            {
               "type":"line",
               "data":{
                  "datasets":[
                     {
                        "backgroundColor":"rgba(40, 180, 99, 0.3)",
                        "borderColor":"rgb(40, 180, 99)",
                        "borderWidth":1,
                        "data":[
                           {
                              "x":1699457269877,
                              "y":20
                           },
                           {
                              "x":1700047109694,
                              "y":20
                           }
                        ],
                        "hidden":false,
                        "label":"Device Id: 524967 Register: A - total Wh ",
                        "minBarLength":3
                     },
                     {
                        "backgroundColor":"rgba(218, 117, 255, 0.3)",
                        "borderColor":"rgb(218, 117, 255)",
                        "borderWidth":1,
                        "data":[
                           {
                              "x":1699457267847,
                              "y":10
                           },
                           {
                              "x":1700047108397,
                              "y":234
                           }
                        ],
                        "hidden":false,
                        "label":"Device Id: 524967 Register: A+ total Wh ",
                        "minBarLength":3
                     }
                  ]
               },
               "options":{
                  "plugins":{
                     "legend":{
                        "display":true,
                        "fullWidth":true,
                        "position":"top",
                        "reverse":false,
                        "rtl":false
                     },
                     "title":{
                        "display":true,
                        "text":"Values from the meter"
                     },
                     "zoom":{
                        "pan":{
                           "enabled":true,
                           "mode":"xy",
                           "threshold":5
                        },
                        "zoom":{
                           "wheel":{
                              "enabled":true
                           },
                           "pinch":{
                              "enabled":true
                           },
                           "mode":"xy"
                        }
                     }
                  },
                  "scales":{
                     "x":{
                        "beginAtZero":false,
                        "offset":true,
                        "reverse":false,
                        "stacked":true,
                        "ticks":{
                           "autoSkip":true,
                           "maxRotation":0,
                           "minRotation":0,
                           "mirror":false,
                           "source":"data"
                        },
                        "time":{
                           "displayFormats":{
                              "minute":"dd.LL T"
                           },
                           "round":"minute",
                           "stepSize":"60",
                           "unit":"minute"
                        },
                        "type":"time"
                     },
                     "y":{
                        "beginAtZero":false,
                        "offset":false,
                        "reverse":false,
                        "stacked":true,
                        "ticks":{
                           "autoSkip":true,
                           "mirror":false
                        }
                     }
                  },
                  "showLine":true,
                  "spanGaps":false
               }
            }
            """;
    }

    public String getJson() {
        return json;
    }
}
```

### Facet

If you have a simple chart and do not even want any Java code or controller you can use the `value` facet.

```xhtml
<p:chart style="width: 100%; height: 500px;">
   <f:facet name="value">
   {
     type: 'bar',
     data: {
       labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
       datasets: [{
         label: '# of Votes',
         data: [12, 19, 3, 5, 2, 3],
         borderWidth: 1,
         backgroundColor: ['DarkRed', 'CornflowerBlue', 'Gold', 'Lime', 'BlueViolet', 'DarkOrange']
       }]
     },
     options: {
       scales: {
         y: {
           beginAtZero: true
         }
       }
     }
   }
   </f:facet>
</p:chart>

```


### Getting Started with XDEV Java Model

You can use [XDEV Chart.js Java Model](https://github.com/xdev-software/chartjs-java-model) to render a server side model of your Chart.
XDEV provides Java models for Chart.js so that e.g. a Java Server can build a chart and then instruct a JavaScript client what needs to be shown.

```java
public void createLineModel() {
    lineModel = new LineChart()
            .setData(new LineData()
            .addDataset(new LineDataset()
                    .setData(65, 59, 80, 81, 56, 55, 40)
                    .setLabel("My First Dataset")
                    .setBorderColor(new Color(75, 192, 192))
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
```

```xhtml
<p:chart value="#{chartView.cartesianLinerModel}" />
```


## Interactive Chart

When a dataset data is clicked, information can be passed to a backing bean using itemSelect ajax behavior.

```xhtml
<p:growl id="growl" showDetail="true" />
     
<p:chart value="#{chartView.barModel}" style="width: 100%; height: 500px;">
    <p:ajax event="itemSelect" listener="#{chartView.itemSelect}" update="growl"/>
</p:chart>
```

```java
public void itemSelect(ItemSelectEvent event) {
    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
            "Item Index: " + event.getItemIndex() + ", DataSet Index:" + event.getDataSetIndex());

    FacesContext.getCurrentInstance().addMessage(null, msg);
}
```

## Pinch and Zoom

The Pinch and Zoom plugin is automatically included with this component.  You simply need to enable it.

```js
options: {
    plugins: {
        zoom: {
            pan: {
                enabled: true,
                mode: 'xy',
                threshold: 5,
            },
            zoom: {
                wheel: {
                    enabled: true
                },
                pinch: {
                    enabled: true
                },
                mode: 'xy',
            },
        }
    }
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

<p:chart id="barChart" value="#{chartView.barModel}" style="width: 100%; height: 500px;" widgetVar="chart"/>

<p:commandButton type="button" value="Export" icon="pi pi-home" onclick="exportChart()" styleClass="mr-2 mb-2"/>
<p:commandButton type="button" value="Print"  icon="pi pi-print" styleClass="mr-2 mb-2">
   <p:printer target="barChart" title="Bar Chart" />
</p:commandButton>

<p:dialog widgetVar="dlg" showEffect="fade" modal="true" header="Chart as an Image" resizable="false">
    <p:outputPanel id="output" layout="block" style="width:500px;height:300px"/>
</p:dialog>
```

## Extender
Extender function allows access to the underlying chart.js api using the `extender` method of the component. The extender function needs to be defined before the chart component, otherwise it could happen that on the first model load, the script isn't found.

```xhtml
<script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0"></script>
<script>
    function chartExtender() {
        var options = {
                plugins: [ChartDataLabels],
                options: {
                   plugins: {
                      // Change options for ALL labels of THIS CHART
                      datalabels: {
                         color: 'HotPink'
                      }
                   }
                },
                data: {
                   datasets: [{
                      // Change options only for labels of THIS DATASET
                      datalabels: {
                         color: 'Indigo'
                      }
                   }]
                }
             };

             //merge all options into the main chart options
             $.extend(true, this.cfg.config, options);
    }
</script>

<p:chart value="#{chartView.pieModel}" extender="chartExtender"/>
```

