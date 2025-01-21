import * as ChartJs from "chart.js";
import * as ChartJsHelpers from "chart.js/helpers";

export interface ChartJsGlobalPlatforms {
    BasePlatform: typeof ChartJs.BasePlatform,
    BasicPlatform: typeof ChartJs.BasicPlatform,
    DomPlatform: typeof ChartJs.DomPlatform,
    _detectPlatform: typeof ChartJs._detectPlatform,
}

export interface ChartJsControllerTypes {
    bar: typeof ChartJs.BarController,
    bubble: typeof ChartJs.BubbleController,
    doughnut: typeof ChartJs.DoughnutController,
    line: typeof ChartJs.LineController,
    pie: typeof ChartJs.PieController,
    polarArea: typeof ChartJs.PolarAreaController,
    radar: typeof ChartJs.RadarController,
    scatter: typeof ChartJs.ScatterController,
}

export type ChartJsControllers = typeof ChartJs.controllers;
export type ChartJsElements = typeof ChartJs.elements;
export type ChartJsPlugins = typeof ChartJs.plugins;
export type ChartJsScales = typeof ChartJs.scales;

export interface ChartJsGlobalExtensions extends ChartJsControllers, ChartJsElements, ChartJsGlobalPlatforms, ChartJsPlugins, ChartJsScales {
    helpers: typeof ChartJsHelpers,
    _adapters: typeof ChartJs._adapters,
    Animation: typeof ChartJs.Animation,
    Animations: typeof ChartJs.Animations,
    animator: typeof ChartJs.animator,
    controllers: ChartJsControllerTypes,
    Chart: typeof ChartJs.Chart,
    DatasetController: typeof ChartJs.DatasetController,
    Element: typeof ChartJs.Element,
    elements: typeof ChartJs.elements,
    Interaction: typeof ChartJs.Interaction,
    layouts: typeof ChartJs.layouts,
    platforms: ChartJsGlobalPlatforms,
    Scale: typeof ChartJs.Scale,
    Ticks: typeof ChartJs.Ticks,
}

export type ChartJsGlobal = typeof ChartJs.Chart & ChartJsGlobalExtensions;

/**
 * Creates the object that will be exposed as window.Chart.
 * @returns The object that will be exposed as window.Chart.
 */
export function createChartJsGlobal(): ChartJsGlobal {
    const Chart = ChartJs.Chart;

    const platforms: ChartJsGlobalPlatforms = Object.freeze({
        __proto__: null,
        BasePlatform: ChartJs.BasePlatform,
        BasicPlatform: ChartJs.BasicPlatform,
        DomPlatform: ChartJs.DomPlatform,
        _detectPlatform: ChartJs._detectPlatform,
    });

    return Object.assign(Chart, {
        ...ChartJs.controllers,
        ...ChartJs.scales,
        ...ChartJs.elements,
        ...ChartJs.plugins,
        ...platforms,
        Chart,
        helpers: ChartJsHelpers,
        _adapters: ChartJs._adapters,
        Animation: ChartJs.Animation,
        Animations: ChartJs.Animations,
        animator: ChartJs.animator,
        controllers: {
            bar: ChartJs.BarController,
            bubble: ChartJs.BubbleController,
            doughnut: ChartJs.DoughnutController,
            line: ChartJs.LineController,
            pie: ChartJs.PieController,
            polarArea: ChartJs.PolarAreaController,
            radar: ChartJs.RadarController,
            scatter: ChartJs.ScatterController,
        },
        DatasetController: ChartJs.DatasetController,
        Element: ChartJs.Element,
        elements: ChartJs.elements,
        Interaction: ChartJs.Interaction,
        layouts: ChartJs.layouts,
        platforms: platforms,
        Scale: ChartJs.Scale,
        Ticks: ChartJs.Ticks,
    });
}