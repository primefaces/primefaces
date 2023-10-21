/**
 * __PrimeFaces Chart Widget__
 *
 * Chart.js based components are a modern replacement for the older `<p:chart>` component. Each chart component has its
 * own model api that defines the data and the options to customize the graph.
 *
 * You can also define an extender function. The extender function allows access to the underlying
 * [chart.js](https://www.chartjs.org/docs/latest/) API using the `setExtender` method of the model. You need to define
 * a global function and set it on the model, see the user guide for more details. The required typing of that function
 * is given by `PrimeFaces.widget.Chart.ChartExtender`.
 *
 * @typedef {<TWidget extends PrimeFaces.widget.Chart = PrimeFaces.widget.Chart>(this: TWidget) => void} PrimeFaces.widget.Chart.ChartExtender
 * The type of the chart extender. It is invoked when the chart is created and lets you modify the chart by using the
 * [chart.js](https://www.chartjs.org/docs/latest/) API. The current chart widget is passed as the this context. To
 * modify the chart configuration, mutate the `this.cfg.config` object.
 *
 * @prop {JQuery<HTMLCanvasElement>} canvas The canvas on which this chart is drawn.
 * @prop {CanvasRenderingContext2D} ctx The 2D rendering context of the canvas used by this chart.
 * @prop {import("chart.js")} chart The chart.js instance creates for this chart widget.
 *
 * @interface {PrimeFaces.widget.ChartCfg} cfg The configuration for the {@link  Chart|Base chart widget}. You
 * can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {import("chart.js").ChartConfiguration} cfg.config The configuration for the
 * [chart.js](https://www.chartjs.org/docs/latest/) chart. It can be modified within the extender function set for this
 * chart widget.
 * @prop {PrimeFaces.widget.Chart.ChartExtender} cfg.extender Extender function allows access to the underlying
 * [chart.js](https://www.chartjs.org/docs/latest/) API.
 */
PrimeFaces.widget.Chart = PrimeFaces.widget.DeferredWidget.extend({

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
