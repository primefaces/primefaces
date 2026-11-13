import * as ChartJs from "chart.js";
import "chartjs-adapter-moment";
import zoomPlugin from "chartjs-plugin-zoom";
import Hammer from "hammerjs";

import { Chart as ChartComponent } from "./src/chart.js";
import { createChartJsGlobal, type ChartJsGlobal } from "./src/chart.global.js";

declare global {
    const Chart: ChartJsGlobal;
    interface Window {
        Chart: ChartJsGlobal;
        Hammer: typeof Hammer;
    }
}

PrimeFaces.component.Chart = ChartComponent;

// Register all ChartJS components
const Chart = ChartJs.Chart
Chart.register(...ChartJs.registerables);

// Zoom plugin must be registered manually
// @ts-expect-error Their types are bad.
ChartJs.Chart.register(zoomPlugin);

Object.assign(window, { Chart: createChartJsGlobal(), Hammer });

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Chart: typeof ChartComponent;
        }
    }
}
