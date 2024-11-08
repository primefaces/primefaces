import "../../src/timeline/1-timeline.js";

// Expose some vis features to the global scope
// Not needed for our code, but may already be used by external code

// Their type declarations are wrong, timeline exists as a named export
declare module "vis-timeline" {
    export const timeline: unknown;
}

import moment from "moment";
import keycharm from "keycharm";
import Hammer from "@egjs/hammerjs";
import { Graph2d, Timeline, timeline } from "vis-timeline/esnext/esm/vis-timeline-graph2d.js";
import { DataSet, DataView, Queue } from "vis-data/esnext/esm/vis-data.js";
import * as VisUtil from "vis-util/esnext/esm/vis-util.js";

Object.assign(window, {
    vis: {
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
    },
});