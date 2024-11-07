import * as ChartJs from "chart.js";
import * as ChartJsHelpers from "chart.js/helpers";
import "chartjs-adapter-moment";
import zoomPlugin from "chartjs-plugin-zoom";
import Hammer from "hammerjs";
import "../../src/chart/9-chartjs-widget.js";

// Register all ChartJS components
const Chart = ChartJs.Chart
Chart.register(...ChartJs.registerables);

// Zoom plugin must be registered manually
ChartJs.Chart.register(zoomPlugin);

// Expose some more globals
// Not needed for our code, but may already be used by external code
const platforms = Object.freeze({
    __proto__: null,
    BasePlatform: ChartJs.BasePlatform,
    BasicPlatform: ChartJs.BasicPlatform,
    DomPlatform: ChartJs.DomPlatform,
    _detectPlatform: ChartJs._detectPlatform,
});
Object.assign(Chart, {
    helpers: ChartJsHelpers,
    _adapters: ChartJs._adapters,
    Animation: ChartJs.Animation,
    Animations: ChartJs.Animations,
    animator: ChartJs.animator,
    // @ts-expect-error items is a private field, but we need to expose it for backward compatibility
    controllers: ChartJs.registry.controllers.items,
    DatasetController: ChartJs.DatasetController,
    Element: ChartJs.Element,
    elements: ChartJs.elements,
    Interaction: ChartJs.Interaction,
    layouts: ChartJs.layouts,
    platforms: platforms,
    Scale: ChartJs.Scale,
    Ticks: ChartJs.Ticks,
});
Object.assign(Chart, ChartJs.controllers, ChartJs.scales, ChartJs.elements, ChartJs.plugins, platforms);
Object.assign(Chart, { Chart });
Object.assign(window, { Chart, Hammer });