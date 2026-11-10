import * as ChartJs from "chart.js";
import "chartjs-adapter-moment";
import zoomPlugin from "chartjs-plugin-zoom";
import Hammer from "hammerjs";

import { Chart as ChartWidget } from "./src/chart.js";
import { createChartJsGlobal, type ChartJsGlobal } from "./src/chart.global.js";

declare global {
    const Chart: ChartJsGlobal;
    interface Window {
        Chart: ChartJsGlobal;
        Hammer: typeof Hammer;
    }
}

PrimeFaces.widget.Chart = ChartWidget;

// Register all ChartJS components
const Chart = ChartJs.Chart
Chart.register(...ChartJs.registerables);

// Zoom plugin must be registered manually
// @ts-expect-error Their types are bad.
ChartJs.Chart.register(zoomPlugin);

Object.assign(window, { Chart: createChartJsGlobal(), Hammer });

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Chart: typeof ChartWidget;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type ChartCfg = import("./src/chart.js").ChartCfg;
    }

    namespace PrimeFacesTypes.widget.Chart {
        /**
         * The type of the chart extender. It is invoked when the chart is created and lets you modify the chart by
         * using the [chart.js](https://www.chartjs.org/docs/latest/) API. The current chart widget is passed as the
         * this context. To modify the chart configuration, mutate the `this.cfg.config` object.
         */
        export type ChartExtender = (this: ChartWidget) => void;
    }
}
