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
PrimeFaces.widget.Chart = class Chart extends PrimeFaces.widget.DeferredWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.canvas = this.jq.children('canvas');
        this.ctx = this.canvas[0].getContext('2d');

        // user extension to configure chart
        var extender = this.cfg.extender;
        if (extender) {
            if (typeof extender === "function") {
                extender.call(this);
            } else {
                PrimeFaces.error("Extender value is not a javascript function!");
            }
        }

        this.renderDeferred();
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        if (this.chart) {
            this.chart.destroy();
        }

        super.refresh(cfg);
    }

    /**
     * @override
     * @inheritdoc
     */
    destroy() {
        super.destroy();

        if (this.chart) {
            this.chart.destroy();
        }
    }

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render() {
        this.chart = new Chart(this.ctx, this.cfg.config);

        this.bindItemSelect();
    }

    /**
     * Setups the event listeners required by this widget when an item (data point) in the chart is selected.
     * @private
     */
    bindItemSelect() {
        var $this = this;

        this.canvas.on('click', function(evt){   
            var activePoints = $this.chart.getElementsAtEventForMode(evt, 'nearest', { intersect: true }, false);
            
            if(activePoints.length && $this.cfg.behaviors) {
                var point = activePoints[0];
                var itemSelectCallback = $this.cfg.behaviors['itemSelect'];
                if(itemSelectCallback) {
                    var ext = {
                        params: [
                            {name: 'itemIndex', value: point.index}
                            ,{name: 'dataSetIndex', value: point.datasetIndex}
                            ,{name: 'data', value: point.element.$context.raw}
                        ]
                    };

                    itemSelectCallback.call($this, ext);
                }
            }
        });
    }

    /**
     * Return this chart as an image with a data source URL (`<img src="data:url" />`)
     * @return {HTMLImageElement} The content of this chart as an HTML IMAGE.
     */
    exportAsImage() {
        var img = new Image();
        img.src = this.chart.toBase64Image();
        return img;
    }

    /**
     * Send this chart to the printer.
     */
    print() {
        // Create a new image element
        var img = `<html><head><script>function s1(){setTimeout('s2()',10);}function s2(){window.print();window.close()}</script></head><body onload='s1()'><img src='${this.chart.toBase64Image()}'/></body></html>`;

        var pwa = window.open("about:blank", "_new");
        pwa.document.open();
        pwa.document.write(img);
        pwa.document.close();
    }
}
