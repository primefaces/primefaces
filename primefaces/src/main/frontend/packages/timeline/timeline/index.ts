import { Timeline } from "./src/timeline.js";
import { createVisGlobal, type VisGlobal } from "./src/create-vis-global.js";

import "moment/locale/de.js";
import "moment/locale/es.js";
import "moment/locale/fr.js";
import "moment/locale/it.js";
import "moment/locale/ja.js";
import "moment/locale/nl.js";
import "moment/locale/pl.js";
import "moment/locale/ru.js";
import "moment/locale/uk.js";

PrimeFaces.widget.Timeline = Timeline;

declare global {
    const vis: VisGlobal;
    interface Window {
        vis: VisGlobal;
    }
}

// Their type declarations are wrong, timeline exists as a named export
declare module "vis-timeline" {
    export const timeline: unknown;
}

// Expose some vis features to the global scope
Object.assign(window, { vis: createVisGlobal(), });

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Timeline: typeof Timeline;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type TimelineCfg = import("./src/timeline.js").TimelineCfg;
    }

    namespace PrimeFacesTypes.widget.Timeline {
        /**
         * Callback that is invoked when an event was added to this timeline, with the added item, or `null` to cancel.
         * See also {@link Timeline.addCallback}.
         */
        export type AddCallbackCallback = (item: import("vis-timeline/esnext").TimelineItem | null) => void;

        /**
         * Callback for when an item of the timeline has changed or was moved, with the changed item, or `null` to
         * cancel. See also {@link Timeline.changedCallback}.
         */
        export type ChangedCallbackCallback = (item: import("vis-timeline/esnext").TimelineItem | null) => void;

        /**
         * Callback for when an item of the timeline was deleted, with the deleted item, or `null` to cancel. See also
         * {@link Timeline.deleteCallback}.
         */
        export type DeleteCallbackCallback = (item: import("vis-timeline/esnext").TimelineItem | null) => void;

        /**
         * An extender function that may be used modify the settings of the configuration object. The
         * {@link TimelineCfg.opts} are passed directly to vis-timeline. See also {@link TimelineCfg.extender}.
         */
        export type TimelineExtender = (this: Timeline) => void;

        /**
         * The options for the vis timeline, as rendered by the server: `editable` is always an object, `min` and `max`
         * are dates.
         */
        export type TimelineOptions = import("vis-timeline/esnext").TimelineOptions & {
            /** Which items can be added, changed and removed. */
            editable: import("vis-timeline/esnext").TimelineEditableOption;
            /** The upper bound of the timeline. */
            max?: Date;
            /** The lower bound of the timeline. */
            min?: Date;
        };

        /**
         * The vis-timeline instance, with the internals used by the widget which are not part of the type declarations
         * of vis-timeline.
         */
        export type VisTimelineInstance = import("vis-timeline/esnext").Timeline & {
            /** The items: a wrapper of the data set that converts `start` and `end` to dates. */
            itemsData: Pick<import("vis-data/esnext").DataSet<import("vis-timeline/esnext").DataItem, "id">, "add" | "clear" | "get" | "length" | "remove" | "update">;
            /** The groups, `null` when the timeline has no groups. */
            groupsData: import("vis-data/esnext").DataView<import("vis-timeline/esnext").DataGroup, "id"> | null;
            /** The DOM elements of the timeline. */
            dom: {
                /** The center panel with the items. */
                center: HTMLElement;
                /** The container of the timeline. */
                container: HTMLElement;
            };
            /** The item set of the timeline. */
            itemSet: {
                /** The options of the items. */
                options: {
                    /** Snaps dates of items to nice values. */
                    snap?: ((date: Date, scale: string, step: number) => Date | number) | null;
                };
                /** Finds the group at the position of the event. */
                groupFromTarget(event: unknown): { groupId: import("vis-timeline/esnext").IdType } | null | undefined;
                /** Finds the item with the given ID. */
                getItemById(id: import("vis-timeline/esnext").IdType): { editable?: import("vis-timeline/esnext").TimelineItemEditableType } | undefined;
            };
            /** Internals of the timeline. */
            body: {
                /** Utilities of the timeline. */
                util: {
                    /** The current scale of the time axis, e.g. `day`. */
                    getScale(): string;
                    /** The current step of the time axis. */
                    getStep(): number;
                };
            };
            /**
             * Converts a horizontal position to a time.
             * @param x The position in pixels, relative to the center panel.
             * @returns The time at the position.
             */
            _toTime(x: number): Date;
        };

        /**
         * The `ext` passed to the behaviors: besides the AJAX options also the item and callback of vis-timeline, or
         * the event and `ui` of the drop. They can be accessed in `onstart` via `cfg.ext`.
         */
        export type BehaviorExtender = Partial<PrimeFacesTypes.ajax.ConfigurationExtender> & {
            /** The item that is added, changed or deleted. */
            item?: import("vis-timeline/esnext").TimelineItem;
            /** The callback of vis-timeline for the item, `null` cancels the change. */
            callback?: (item: import("vis-timeline/esnext").TimelineItem | null) => void;
            /** The drop event. */
            event?: JQuery.TriggeredEvent;
            /** The `ui` of the drop, with the dropped `draggable`. */
            ui?: { draggable: JQuery };
        };

        /**
         * The properties of the `rangechange` and `rangechanged` events of vis-timeline.
         */
        export interface RangeChangeProperties {
            /** Start of the visible range. */
            start: Date;
            /** End of the visible range. */
            end: Date;
            /** Whether the change was initiated by the user. */
            byUser: boolean;
            /** The original event. */
            event?: Event;
        }

        /**
         * Represents a time range between two points in time.
         */
        export interface TimeRange {
            /** Lower bound of the time range. */
            start: number | null;
            /** Upper bound of the time range. */
            end: number | null;
        }

        /**
         * Represents one or two time ranges.
         */
        export interface TimelineBiRange {
            /** Start of the first time range. */
            startFirst: number;
            /** End of the first time range. */
            endFirst: number;
            /** Start of the second time range. */
            startSecond: number | null;
            /** End of the second time range. */
            endSecond: number | null;
        }
    }
}
