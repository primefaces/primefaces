import { Draggable, type DraggableOptions, type DraggableUi } from "./draggable.js";
import { DragDropManager, Droppable, type DroppableOptions, type DroppableUi } from "./droppable.js";
import { Resizable, type ResizableOptions, type ResizableUi } from "./resizable.js";
import { Sortable, type SortableOptions, type SortableUi } from "./sortable.js";

type InteractionClass = new (element: HTMLElement, options?: object) => { option(key?: unknown, value?: unknown): unknown; [method: string]: unknown };

/**
 * Registers a jQuery plugin like `$.fn.draggable`, called with options to create (or update) the interaction, or with
 * the name of a method like `enable`, `disable`, `destroy`, `option` or `instance`.
 */
function register(name: string, Constructor: InteractionClass): void {
    const key = 'pf-' + name;
    ($.fn as unknown as Record<string, unknown>)[name] = function(this: JQuery, arg?: string | object, ...args: unknown[]): unknown {
        if (typeof arg === 'string') {
            let result: unknown;
            this.each((_, el) => {
                const instance = $.data(el, key) as InstanceType<InteractionClass> | undefined;
                if (!instance || result !== undefined) {
                    return;
                }
                if (arg === 'instance') {
                    result = instance;
                }
                else if (arg === 'option') {
                    result = instance.option(...(args as [unknown, unknown]));
                }
                else if (typeof instance[arg] === 'function') {
                    result = (instance[arg] as (...a: unknown[]) => unknown).apply(instance, args);
                }
            });
            return result === undefined ? this : result;
        }

        return this.each((_, el) => {
            const instance = $.data(el, key) as InstanceType<InteractionClass> | undefined;
            if (instance) {
                instance.option(arg);
            }
            else {
                $.data(el, key, new Constructor(el, arg));
            }
        });
    };
}

/**
 * Triggers the `remove` event for removed elements, e.g. on `.remove()`, `.html()` or `.replaceWith()`. The widgets use
 * it to detect that they were removed by an AJAX update.
 */
function registerRemoveEvent(): void {
    const cleanData = $.cleanData;
    $.cleanData = function(elems: ArrayLike<Element | Document | Window>): void {
        for (let i = 0; i < elems.length; i++) {
            const elem = elems[i];
            const events = elem ? ($ as unknown as { _data(elem: unknown, key: string): Record<string, unknown> | undefined })._data(elem, 'events') : undefined;
            if (events?.['remove']) {
                $(elem as Element).triggerHandler('remove');
            }
        }
        cleanData(elems);
    };
}

export function registerInteractions(): void {
    register('draggable', Draggable as unknown as InteractionClass);
    register('droppable', Droppable as unknown as InteractionClass);
    register('sortable', Sortable as unknown as InteractionClass);
    register('resizable', Resizable as unknown as InteractionClass);
    registerRemoveEvent();
    Object.assign($, { pfDragDrop: DragDropManager });
}

declare global {
    namespace PrimeType.interaction {
        export type DraggableOptions = import("./draggable.js").DraggableOptions;
        export type DraggableUi = import("./draggable.js").DraggableUi;
        export type DroppableOptions = import("./droppable.js").DroppableOptions;
        export type DroppableUi = import("./droppable.js").DroppableUi;
        export type SortableOptions = import("./sortable.js").SortableOptions;
        export type SortableUi = import("./sortable.js").SortableUi;
        export type ResizableOptions = import("./resizable.js").ResizableOptions;
        export type ResizableUi = import("./resizable.js").ResizableUi;
    }

    interface JQueryStatic {
        /** The drag and drop manager, `current` is the interaction that is dragged right now. */
        pfDragDrop: typeof DragDropManager;
        cleanData(elems: ArrayLike<Element | Document | Window>): void;
    }

    interface JQuery {
        draggable(options?: Partial<DraggableOptions>): this;
        draggable(method: 'enable' | 'disable' | 'destroy'): this;
        draggable(method: 'option', key: string, value?: unknown): unknown;
        draggable(method: 'instance'): Draggable | undefined;

        droppable(options?: Partial<DroppableOptions>): this;
        droppable(method: 'enable' | 'disable' | 'destroy'): this;
        droppable(method: 'option', key: string, value?: unknown): unknown;
        droppable(method: 'instance'): Droppable | undefined;

        sortable(options?: Partial<SortableOptions>): this;
        sortable(method: 'enable' | 'disable' | 'destroy' | 'refresh' | 'cancel'): this;
        sortable(method: 'option', key: string, value?: unknown): unknown;
        sortable(method: 'instance'): Sortable | undefined;

        resizable(options?: Partial<ResizableOptions>): this;
        resizable(method: 'enable' | 'disable' | 'destroy'): this;
        resizable(method: 'option', key: string, value?: unknown): unknown;
        resizable(method: 'instance'): Resizable | undefined;
    }
}

export type { DraggableUi, DroppableUi, SortableUi, ResizableUi };
