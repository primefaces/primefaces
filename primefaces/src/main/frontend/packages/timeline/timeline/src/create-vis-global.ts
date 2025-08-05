import keycharm from "keycharm";
import Hammer from "@egjs/hammerjs";
import { Graph2d, Timeline, timeline } from "vis-timeline/esnext";
import { DataSet, DataView, Queue } from "vis-data/esnext";
import * as VisUtil from "vis-util/esnext";

/**
 * The global object available via the global `window.vis`, when the timeline widget
 * is used.
 */
export interface VisGlobal {
    /** The vis DataSet class. */
    DataSet: typeof DataSet;
    /** The vis DataView class. */
    DataView: typeof DataView;
    /** The vis Graph2d class. */
    Graph2d: typeof Graph2d;
    /** The Hammer library. */
    Hammer: typeof Hammer;
    /** The vis Queue class. */
    Queue: typeof Queue;
    /** The vis Timeline class. */
    Timeline: typeof Timeline;
    /** The keycharm library. */
    keycharm: typeof keycharm;
    /** The moment library. */
    moment: typeof moment;
    /** The vis timeline module. */
    timeline: typeof timeline;
    /** The vis util module. */
    util: typeof VisUtil;
}

/**
 * Creates the object that will be exposed to the global scope as `window.vis`.
 * @returns The object that will be exposed to the global scope as `window.vis`.
 */
export function createVisGlobal(): VisGlobal {
    return {
            DataSet,
            DataView,
            Graph2d,
            Hammer,
            Queue,
            Timeline,
            keycharm,
            moment,
            timeline,
            util: VisUtil,
    };   
}
