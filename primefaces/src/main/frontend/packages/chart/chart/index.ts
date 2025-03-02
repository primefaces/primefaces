import * as ChartJs from "chart.js";
import "chartjs-adapter-moment";
import zoomPlugin from "chartjs-plugin-zoom";
import Hammer from "hammerjs";

import "./src/chart.widget.js";
import { createChartJsGlobal, type ChartJsGlobal } from "./src/chart.global.js";

declare global {
    const Chart: ChartJsGlobal;
    interface Window {
        Chart: ChartJsGlobal;
        Hammer: typeof Hammer;
    }
}

// Register all ChartJS components
const Chart = ChartJs.Chart
Chart.register(...ChartJs.registerables);

// Zoom plugin must be registered manually
// @ts-expect-error Their types are bad.
ChartJs.Chart.register(zoomPlugin);

Object.assign(window, { Chart: createChartJsGlobal(), Hammer });