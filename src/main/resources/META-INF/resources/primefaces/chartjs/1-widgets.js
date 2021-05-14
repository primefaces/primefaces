/**
 * __PrimeFaces BaseChart Widget__
 *
 * Chart.js based components are a modern replacement for the older `<p:chart>` component. Each chart component has its
 * own model api that defines the data and the options to customize the graph.
 *
 * You can also define an extender function. The extender function allows access to the underlying
 * [chart.js](https://www.chartjs.org/docs/latest/) API using the `setExtender` method of the model. You need to define
 * a global function and set it on the model, see the user guide for more details. The required typing of that function
 * is given by `PrimeFaces.widget.BaseChart.ChartExtender`.
 *
 * @typedef {<TWidget extends PrimeFaces.widget.BaseChart = PrimeFaces.widget.BaseChart>(this: TWidget) => void} PrimeFaces.widget.BaseChart.ChartExtender
 * The type of the chart extender. It is invoked when the chart is created and lets you modify the chart by using the
 * [chart.js](https://www.chartjs.org/docs/latest/) API. The current chart widget is passed as the this context. To
 * modify the chart configuration, mutate the `this.cfg.config` object.
 *
 * @prop {JQuery<HTMLCanvasElement>} canvas The canvas on which this chart is drawn.
 * @prop {CanvasRenderingContext2D} ctx The 2D rendering context of the canvas used by this chart.
 * @prop {import("chart.js")} chart The chart.js instance creates for this chart widget.
 *
 * @interface {PrimeFaces.widget.BaseChartCfg} cfg
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {import("chart.js").ChartConfiguration} cfg.config The configuration for the
 * [chart.js](https://www.chartjs.org/docs/latest/) chart. It can be modified within the extender function set for this
 * chart widget.
 * @prop {PrimeFaces.widget.BaseChart.ChartExtender} cfg.extender Extender function allows access to the underlying
 * [chart.js](https://www.chartjs.org/docs/latest/) API.
 */
PrimeFaces.widget.BaseChart = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.canvas = this.jq.children('canvas');
        this.ctx = this.canvas[0].getContext('2d');

        if(this.cfg.extender) {
            this.cfg.extender.call(this);
        }

        this.renderDeferred();
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        if (this.chart) {
            this.chart.destroy();
        }

        this._super(cfg);
    },

    /**
     * @override
     * @inheritdoc
     */
    destroy: function() {
        this._super();

        if (this.chart) {
            this.chart.destroy();
        }
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        this.chart = new Chart(this.ctx, this.cfg.config);

        this.bindItemSelect();
    },

    /**
     * Setups the event listeners required by this widget when an item (data point) in the chart is selected.
     * @private
     */
    bindItemSelect: function() {
        var $this = this;

        this.canvas.on('click', function(evt){   
            var activePoints = $this.chart.getElementsAtEventForMode(evt, 'nearest', { intersect: true }, false)

            if(activePoints.length && $this.cfg.behaviors) {
                var itemSelectCallback = $this.cfg.behaviors['itemSelect'];
                if(itemSelectCallback) {
                    var ext = {
                        params: [
                            {name: 'itemIndex', value: activePoints[0].index}
                            ,{name: 'dataSetIndex', value: activePoints[0].datasetIndex}
                        ]
                    };

                    itemSelectCallback.call($this, ext);
                }
            }
        });
    },

    /**
     * Return this chart as an image with a data source URL (`<img src="data:url" />`)
     * @return {HTMLImageElement} The content of this chart as an HTML IMAGE.
     */
    exportAsImage: function() {
        var img = new Image();
        img.src = this.chart.toBase64Image();
        return img;
    }
});

/**
 * __PrimeFaces LineChart Widget__
 *
 * A line chart is a way of plotting data points on a line. Often, it is used to show trend data, or the comparison of
 * two data sets.
 *
 * @interface {PrimeFaces.widget.LineChartCfg} cfg The configuration for the {@link  LineChart| LineChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseChartCfg} cfg
 */
PrimeFaces.widget.LineChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * __PrimeFaces BarChart Widget__
 *
 * A bar chart provides a way of showing data values represented as vertical
 * bars. It is sometimes used to show trend data, and the comparison of multiple
 * data sets side by side.
 *
 * @interface {PrimeFaces.widget.BarChartCfg} cfg The configuration for the {@link  BarChart| BarChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseChartCfg} cfg
 */
PrimeFaces.widget.BarChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * __PrimeFaces PieChart Widget__
 *
 * Pie chart is divided into segments, the arc of each segment shows the proportional value of each piece of data.
 *
 * @interface {PrimeFaces.widget.PieChartCfg} cfg The configuration for the {@link  PieChart| PieChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseChartCfg} cfg
 */
PrimeFaces.widget.PieChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * __PrimeFaces DonutChart Widget__
 *
 * A Donut Chart is a variation of a Pie Chart but with a space in the center.
 *
 * @interface {PrimeFaces.widget.DonutChartCfg} cfg The configuration for the {@link  DonutChart| DonutChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseChartCfg} cfg
 */
PrimeFaces.widget.DonutChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * __PrimeFaces PolarAreaChart Widget__
 *
 * Polar area charts are similar to pie charts, but each segment has the same angle - the radius of the segment differs
 * depending on the value.
 *
 * @interface {PrimeFaces.widget.PolarAreaChartCfg} cfg The configuration for the {@link  PolarAreaChart| PolarAreaChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseChartCfg} cfg
 */
PrimeFaces.widget.PolarAreaChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * __PrimeFaces RadarChart Widget__
 *
 * A radar chart is a way of showing multiple data points and the variation between them.
 *
 * @interface {PrimeFaces.widget.RadarChartCfg} cfg The configuration for the {@link  RadarChart| RadarChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseChartCfg} cfg
 */
PrimeFaces.widget.RadarChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * __PrimeFaces BubbleChart Widget__
 *
 * A bubble chart is used to display three dimensions of data at the same time. The location of the bubble is determined
 * by the first two dimensions and the corresponding horizontal and vertical axes. The third dimension is represented by
 * the size of the individual bubbles.
 *
 * @interface {PrimeFaces.widget.BubbleChartCfg} cfg The configuration for the {@link  BubbleChart| BubbleChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseChartCfg} cfg
 */
PrimeFaces.widget.BubbleChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * __PrimeFaces ScatterChart Widget__
 *
 * @interface {PrimeFaces.widget.ScatterChartCfg} cfg The configuration for the {@link  ScatterChart| ScatterChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseChartCfg} cfg
 */
PrimeFaces.widget.ScatterChart = PrimeFaces.widget.BaseChart.extend({});