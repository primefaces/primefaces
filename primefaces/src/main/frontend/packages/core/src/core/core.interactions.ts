import { Draggable, type DraggableOptions } from "../interaction/draggable.js";
import { DragDropManager, Droppable, type DroppableOptions } from "../interaction/droppable.js";
import { Resizable, type ResizableOptions } from "../interaction/resizable.js";
import { Sortable, type SortableOptions } from "../interaction/sortable.js";

/** The name of an interaction, e.g. `draggable`. */
export type InteractionType = "draggable" | "droppable" | "sortable" | "resizable";

/** The class of each {@link InteractionType}. */
interface InteractionInstances {
    /** The class of the draggables. */
    draggable: Draggable;
    /** The class of the droppables. */
    droppable: Droppable;
    /** The class of the sortables. */
    sortable: Sortable;
    /** The class of the resizables. */
    resizable: Resizable;
}

/** The constructor of an interaction. */
type InteractionConstructor = new (element: HTMLElement, options?: object) => { option(options: object): unknown };

/**
 * The interactions of PrimeFaces: drag and drop, sorting and resizing with pointer events (mouse, pen and touch). They
 * replace the interactions of jQuery UI, their options, callbacks, `ui` arguments, events and CSS classes are
 * compatible with jQuery UI as far as PrimeFaces uses them.
 *
 * ```js
 * PrimeFaces.interactions.draggable($('#panel'), { handle: '.header', containment: 'parent' });
 * PrimeFaces.interactions.disable($('#panel'), 'draggable');
 * PrimeFaces.interactions.instance($('#panel'), 'draggable').option('axis', 'x');
 * ```
 *
 * The jQuery plugins `$.fn.draggable`, `$.fn.droppable`, `$.fn.sortable` and `$.fn.resizable` of jQuery UI do not exist
 * anymore, see the migration guide.
 * @since 17.0.0
 */
export class Interactions {
    /**
     * The drag and drop manager, `current` is the draggable or sortable that is dragged right now (formerly
     * `$.ui.ddmanager`).
     */
    readonly dragDrop: typeof DragDropManager = DragDropManager;

    /**
     * Makes the elements draggable, or updates the options of the elements that are draggable already.
     * @typeParam T Type of the elements.
     * @param element The elements to make draggable.
     * @param options The options, e.g. `handle`, `helper`, `containment` or the callbacks `start`, `drag` and `stop`.
     * @returns The given elements, for chaining.
     */
    draggable<T extends JQuery<any>>(element: T, options?: Partial<DraggableOptions>): T {
        return this.create(element, "draggable", Draggable, options);
    }

    /**
     * Makes the elements droppables, or updates the options of the elements that are droppables already.
     * @typeParam T Type of the elements.
     * @param element The elements to make droppables.
     * @param options The options, e.g. `accept`, `scope`, `tolerance` or the callbacks `over`, `out` and `drop`.
     * @returns The given elements, for chaining.
     */
    droppable<T extends JQuery<any>>(element: T, options?: Partial<DroppableOptions>): T {
        return this.create(element, "droppable", Droppable, options);
    }

    /**
     * Makes the items of the elements sortable, or updates the options of the elements that are sortable already.
     * @typeParam T Type of the elements.
     * @param element The containers of the items to sort.
     * @param options The options, e.g. `items`, `handle`, `connectWith` or the callbacks `start`, `update` and `stop`.
     * @returns The given elements, for chaining.
     */
    sortable<T extends JQuery<any>>(element: T, options?: Partial<SortableOptions>): T {
        return this.create(element, "sortable", Sortable, options);
    }

    /**
     * Makes the elements resizable, or updates the options of the elements that are resizable already.
     * @typeParam T Type of the elements.
     * @param element The elements to make resizable.
     * @param options The options, e.g. `handles`, `minWidth`, `aspectRatio` or the callbacks `start`, `resize` and
     * `stop`.
     * @returns The given elements, for chaining.
     */
    resizable<T extends JQuery<any>>(element: T, options?: Partial<ResizableOptions>): T {
        return this.create(element, "resizable", Resizable, options);
    }

    /**
     * Gets the interaction of the given type of the first element, e.g. to call its methods like `option` or `cancel`.
     * @typeParam K Type of the interaction.
     * @param element The element with the interaction.
     * @param type The type of the interaction, e.g. `draggable`.
     * @returns The interaction, `undefined` if the element does not have one of the given type.
     */
    instance<K extends InteractionType>(element: JQuery<any> | Element | null | undefined, type: K): InteractionInstances[K] | undefined {
        const el = element instanceof Element ? element : element?.[0];
        return el ? $.data(el, Interactions.key(type)) as InteractionInstances[K] | undefined : undefined;
    }

    /**
     * Enables the interaction of the given type of the elements.
     * @typeParam T Type of the elements.
     * @param element The elements with the interaction.
     * @param type The type of the interaction, e.g. `sortable`.
     * @returns The given elements, for chaining.
     */
    enable<T extends JQuery<any>>(element: T, type: InteractionType): T {
        return this.each(element, type, (instance) => instance.enable());
    }

    /**
     * Disables the interaction of the given type of the elements, the elements keep their options.
     * @typeParam T Type of the elements.
     * @param element The elements with the interaction.
     * @param type The type of the interaction, e.g. `sortable`.
     * @returns The given elements, for chaining.
     */
    disable<T extends JQuery<any>>(element: T, type: InteractionType): T {
        return this.each(element, type, (instance) => instance.disable());
    }

    /**
     * Removes the interaction of the given type from the elements, including its CSS classes and event listeners.
     * @typeParam T Type of the elements.
     * @param element The elements with the interaction.
     * @param type The type of the interaction, e.g. `draggable`.
     * @returns The given elements, for chaining.
     */
    destroy<T extends JQuery<any>>(element: T, type: InteractionType): T {
        return this.each(element, type, (instance) => instance.destroy());
    }

    /**
     * Creates the interaction for each element that doesn't have one yet, and updates the options of the others.
     * @typeParam T Type of the elements.
     * @param element The elements.
     * @param type The type of the interaction.
     * @param Constructor The class of the interaction.
     * @param options The options of the interaction.
     * @returns The given elements, for chaining.
     */
    private create<T extends JQuery<any>>(element: T, type: InteractionType, Constructor: InteractionConstructor, options?: object): T {
        const key = Interactions.key(type);
        element.each((_, el) => {
            const instance = $.data(el, key) as InstanceType<InteractionConstructor> | undefined;
            if (instance) {
                instance.option(options ?? {});
            }
            else {
                $.data(el, key, new Constructor(el, options));
            }
        });
        return element;
    }

    /**
     * Calls the callback with the interaction of the given type of each element that has one.
     * @typeParam T Type of the elements.
     * @param element The elements.
     * @param type The type of the interaction.
     * @param callback Called with each interaction.
     * @returns The given elements, for chaining.
     */
    private each<T extends JQuery<any>>(element: T, type: InteractionType, callback: (instance: InteractionInstances[InteractionType]) => void): T {
        element.each((_, el) => {
            const instance = this.instance(el, type);
            if (instance) {
                callback(instance);
            }
        });
        return element;
    }

    /**
     * The key of the jQuery data of the elements under which the interaction is stored.
     * @param type The type of the interaction.
     * @returns The key, e.g. `pf-draggable`.
     */
    private static key(type: InteractionType): string {
        return "pf-" + type;
    }
}

/**
 * The interactions module of PrimeFaces.
 */
export const interactions: Interactions = new Interactions();

declare global {
    namespace PrimeFacesTypes.interactions {
        /** The name of an interaction, e.g. `draggable`. */
        export type Type = import("./core.interactions.js").InteractionType;
        /** The options of a draggable, see {@link Interactions.draggable}. */
        export type DraggableOptions = import("../interaction/draggable.js").DraggableOptions;
        /** The `ui` argument of the draggable callbacks. */
        export type DraggableUi = import("../interaction/draggable.js").DraggableUi;
        /** The options of a droppable, see {@link Interactions.droppable}. */
        export type DroppableOptions = import("../interaction/droppable.js").DroppableOptions;
        /** The `ui` argument of the droppable callbacks. */
        export type DroppableUi = import("../interaction/droppable.js").DroppableUi;
        /** The options of a sortable, see {@link Interactions.sortable}. */
        export type SortableOptions = import("../interaction/sortable.js").SortableOptions;
        /** The `ui` argument of the sortable callbacks. */
        export type SortableUi = import("../interaction/sortable.js").SortableUi;
        /** The options of a resizable, see {@link Interactions.resizable}. */
        export type ResizableOptions = import("../interaction/resizable.js").ResizableOptions;
        /** The `ui` argument of the resizable callbacks. */
        export type ResizableUi = import("../interaction/resizable.js").ResizableUi;
        /** A draggable. */
        export type Draggable = import("../interaction/draggable.js").Draggable;
        /** A droppable. */
        export type Droppable = import("../interaction/droppable.js").Droppable;
        /** A sortable. */
        export type Sortable = import("../interaction/sortable.js").Sortable;
        /** A resizable. */
        export type Resizable = import("../interaction/resizable.js").Resizable;
    }
}
