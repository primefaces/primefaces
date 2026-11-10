import { Chart as ChartJsChart, type ChartConfiguration } from "chart.js";

/**
 * The configuration for the {@link Chart} widget.
 */
export interface ChartCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
    /**
     * The configuration for the [chart.js](https://www.chartjs.org/docs/latest/) chart. It can be modified within the
     * extender function set for this chart widget.
     */
    config: ChartConfiguration;
    /** Extender function allows access to the underlying [chart.js](https://www.chartjs.org/docs/latest/) API. */
    extender: PrimeFacesTypes.widget.Chart.ChartExtender;
}

/**
 * __PrimeFaces Chart Widget__
 *
 * Chart.js based components are a modern replacement for the older `<p:chart>` component. Each chart component has its
 * own model api that defines the data and the options to customize the graph.
 *
 * You can also define an extender function. The extender function allows access to the underlying
 * [chart.js](https://www.chartjs.org/docs/latest/) API using the `setExtender` method of the model. You need to define
 * a global function and set it on the model, see the user guide for more details. The required typing of that function
 * is given by `PrimeFacesTypes.widget.Chart.ChartExtender`.
 */
export class Chart<Cfg extends ChartCfg = ChartCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** The canvas on which this chart is drawn. */
    canvas: JQuery<HTMLCanvasElement> = $();
    /** The 2D rendering context of the canvas used by this chart. */
    ctx: CanvasRenderingContext2D | null = null;
    /** The chart.js instance creates for this chart widget. */
    chart?: ChartJsChart;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.canvas = this.jq.children('canvas');
        this.ctx = this.canvas[0]!.getContext('2d');

        // user extension to configure chart
        const extender = this.cfg.extender;
        if (extender) {
            if (typeof extender === "function") {
                extender.call(this);
            } else {
                PrimeFaces.error("Extender value is not a javascript function!");
            }
        }

        this.renderDeferred();
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        if (this.chart) {
            this.chart.destroy();
        }

        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();

        if (this.chart) {
            this.chart.destroy();
        }
    }

    protected override _render(): void {
        this.chart = new ChartJsChart(this.ctx!, this.cfg.config!);

        this.bindItemSelect();
    }

    /**
     * Setups the event listeners required by this widget when an item (data point) in the chart is selected.
     */
    private bindItemSelect(): void {
        this.canvas.on('click', (evt) => {
            const activePoints = this.chart!.getElementsAtEventForMode(evt as unknown as Event, 'nearest', { intersect: true }, false);

            if (activePoints.length && this.cfg.behaviors) {
                const point = activePoints[0]!;
                const itemSelectCallback = this.cfg.behaviors['itemSelect'];
                if (itemSelectCallback) {
                    const ext: Partial<PrimeFacesTypes.ajax.ConfigurationExtender> = {
                        params: [
                            {name: 'itemIndex', value: point.index}
                            ,{name: 'dataSetIndex', value: point.datasetIndex}
                            ,{name: 'data', value: (point.element as typeof point.element & { $context: { raw: unknown } }).$context.raw}
                        ]
                    };

                    itemSelectCallback.call(this, ext);
                }
            }
        });
    }

    /**
     * Return this chart as an image with a data source URL (`<img src="data:url" />`)
     * @returns The content of this chart as an HTML IMAGE.
     */
    exportAsImage(): HTMLImageElement {
        const img = new Image();
        img.src = this.chart!.toBase64Image();
        return img;
    }

    /**
     * Send this chart to the printer.
     */
    print(): void {
        // Create a new image element
        const img = `<html><head><script>function s1(){setTimeout('s2()',10);}function s2(){window.print();window.close()}</script></head><body onload='s1()'><img src='${this.chart!.toBase64Image()}'/></body></html>`;

        const pwa = window.open("about:blank", "_new")!;
        pwa.document.open();
        pwa.document.write(img);
        pwa.document.close();
    }
}
