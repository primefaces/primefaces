import { Interaction, offsetOf, type Callback, type Coordinates, type InteractionOptions, pageX, pageY } from "./interaction.js";

/** The options of a droppable, see the jQuery UI droppable. */
export interface DroppableOptions extends InteractionOptions {
    /** Selector or function deciding which draggables are accepted. */
    accept: string | ((this: HTMLElement, draggable: JQuery) => boolean);
    /** Class added while an accepted draggable is dragged. */
    activeClass: string | false;
    /** Additional classes for `p-droppable-active` / `p-droppable-hover`. */
    classes: Record<string, string>;
    /** Whether a drop on this droppable is not propagated to parent droppables. */
    greedy: boolean;
    /** Class added while an accepted draggable is over this droppable. */
    hoverClass: string | false;
    /** Only draggables with the same scope are accepted. */
    scope: string;
    /** `intersect`, `fit`, `pointer` or `touch`. */
    tolerance: 'intersect' | 'fit' | 'pointer' | 'touch';
    /** Zoom factor of the droppable (CSS zoom / transform). */
    zoomFactor: (() => number) | null;
    /** Called when an accepted draggable starts dragging. */
    activate: Callback;
    /** Called when an accepted draggable stops dragging. */
    deactivate: Callback;
    /** Called when an accepted draggable is dragged over this droppable. */
    over: Callback;
    /** Called when an accepted draggable is dragged out of this droppable. */
    out: Callback;
    /** Called when an accepted draggable is dropped on this droppable. */
    drop: Callback;
}

/** The interaction (draggable or sortable) that is dragged over droppables. */
export interface DragSource {
    /**
     * The dragged element.
     * @returns The element of a draggable, the current item of a sortable.
     */
    item(): JQuery;
    /** The element that moves with the pointer. */
    helper: JQuery | null;
    /**
     * The scope of the drag, only droppables with the same scope are activated.
     * @returns The `scope` option.
     */
    scope(): string;
    /** The offset of the helper. */
    offset: Coordinates;
    /** The CSS position of the helper. */
    position: Coordinates;
    /** Aborts the drag. */
    cancel(): void;
}

/** The `ui` argument of the droppable callbacks. */
export interface DroppableUi {
    /** The dragged element. */
    draggable: JQuery;
    /** The element that moves with the pointer. */
    helper: JQuery | null;
    /** The CSS position of the helper. */
    position: Coordinates;
    /** The offset of the helper. */
    offset: Coordinates;
}

/** The default options, the same as in jQuery UI. */
const DEFAULTS: DroppableOptions = {
    disabled: false,
    accept: '*',
    activeClass: false,
    classes: {},
    greedy: false,
    hoverClass: false,
    scope: 'default',
    tolerance: 'intersect',
    zoomFactor: null,
    activate: null,
    deactivate: null,
    over: null,
    out: null,
    drop: null
};

/**
 * An element on which draggables can be dropped.
 */
export class Droppable extends Interaction<DroppableOptions> {
    /** The box in page coordinates while a draggable is dragged. */
    box: [number, number, number, number] = [0, 0, 0, 0];
    /** Whether the dragged helper is over this droppable. */
    isOver: boolean = false;
    /** The scope under which this droppable is registered at the manager. */
    private currentScope: string;

    /**
     * Makes the element a droppable and registers it at the manager.
     * @param element The element.
     * @param options The options.
     */
    constructor(element: HTMLElement, options?: Partial<DroppableOptions>) {
        super(element, 'droppable', 'drop', DEFAULTS, options);
        this.addClasses();
        this.currentScope = this.options.scope;
        DragDropManager.add(this);
    }

    /** Unregisters the droppable, during a drag it also leaves it and removes its state classes. */
    override destroy(): void {
        const active = DragDropManager.active.includes(this);
        DragDropManager.remove(this, this.currentScope);
        if (active) {
            this.toggleClasses('active', false);
            this.toggleClasses('hover', false);
        }
        super.destroy();
    }

    /**
     * Sets an option, a changed scope registers the droppable under that scope.
     * @param key The name of the option.
     * @param value The new value.
     */
    protected override setOption(key: string, value: unknown): void {
        // the same scope again (e.g. the options applied again in a drop callback) keeps the droppable in the drag
        if (key === 'scope' && String(value) !== this.currentScope) {
            DragDropManager.remove(this, this.currentScope);
            super.setOption(key, value);
            this.currentScope = String(value);
            DragDropManager.add(this);
            return;
        }
        super.setOption(key, value);
    }

    /**
     * Whether the `accept` option accepts the dragged element.
     * @param item The dragged element.
     * @returns `true` if it can be dropped here.
     */
    accepts(item: JQuery): boolean {
        const accept = this.options.accept;
        return typeof accept === 'function' ? !!accept.call(this.element[0]!, item) : item.is(accept);
    }

    /**
     * Creates the `ui` argument of the callbacks.
     * @param source The dragged interaction.
     * @returns The `ui` argument.
     */
    ui(source: DragSource): DroppableUi {
        return { draggable: source.item(), helper: source.helper, position: source.position, offset: source.offset };
    }

    /** Caches the box in page coordinates. */
    measure(): void {
        const el = this.element[0]!;
        const zoom = this.options.zoomFactor ? this.options.zoomFactor() : 1;
        const offset = offsetOf(this.element);
        this.box = [offset.left, offset.top, offset.left + el.offsetWidth * zoom, offset.top + el.offsetHeight * zoom];
    }

    /**
     * Adds or removes the classes of a state, including the `classes`, `activeClass` and `hoverClass` options.
     * @param state The state.
     * @param on Whether to add the classes.
     */
    private toggleClasses(state: 'active' | 'hover', on: boolean): void {
        const key = 'p-droppable-' + state;
        const custom = state === 'active' ? this.options.activeClass : this.options.hoverClass;
        this.element.toggleClass([key, this.options.classes[key] ?? '', custom || ''].join(' ').trim(), on);
    }

    /**
     * An accepted draggable started dragging.
     * @param event The event that started the drag.
     * @param source The dragged interaction.
     */
    activate(event: JQuery.TriggeredEvent | null, source: DragSource): void {
        this.toggleClasses('active', true);
        this.trigger('activate', event, this.ui(source));
    }

    /**
     * The accepted draggable stopped dragging.
     * @param event The event that ended the drag.
     * @param source The dragged interaction.
     */
    deactivate(event: JQuery.TriggeredEvent | null, source: DragSource): void {
        this.toggleClasses('active', false);
        this.toggleClasses('hover', false);
        this.isOver = false;
        this.trigger('deactivate', event, this.ui(source));
    }

    /**
     * The helper moved over this droppable.
     * @param event The pointermove event.
     * @param source The dragged interaction.
     */
    over(event: JQuery.TriggeredEvent, source: DragSource): void {
        this.isOver = true;
        this.toggleClasses('hover', true);
        this.trigger('over', event, this.ui(source));
    }

    /**
     * The helper moved out of this droppable.
     * @param event The pointermove event.
     * @param source The dragged interaction.
     */
    out(event: JQuery.TriggeredEvent, source: DragSource): void {
        this.isOver = false;
        this.toggleClasses('hover', false);
        this.trigger('out', event, this.ui(source));
    }

    /**
     * The helper was dropped on this droppable.
     * @param event The pointerup event.
     * @param source The dragged interaction.
     */
    dropItem(event: JQuery.TriggeredEvent, source: DragSource): void {
        this.toggleClasses('hover', false);
        this.isOver = false;
        this.trigger('drop', event, this.ui(source));
    }

    /**
     * Whether the helper of the source intersects this droppable according to the tolerance.
     * @param source The dragged interaction.
     * @param event The pointer event, for the `pointer` tolerance.
     * @returns `true` if the helper is over this droppable.
     */
    intersects(source: DragSource, event: JQuery.TriggeredEvent): boolean {
        const helper = source.helper;
        if (!helper) {
            return false;
        }
        const [l, t, r, b] = this.box;
        const x1 = source.offset.left;
        const y1 = source.offset.top;
        const width = helper.outerWidth() ?? 0;
        const height = helper.outerHeight() ?? 0;
        const x2 = x1 + width;
        const y2 = y1 + height;

        switch (this.options.tolerance) {
            case 'fit':
                return l <= x1 && x2 <= r && t <= y1 && y2 <= b;
            case 'pointer': {
                const x = pageX(event);
                const y = pageY(event);
                return x >= l && x < r && y >= t && y < b;
            }
            case 'touch':
                return y1 < b && y2 > t && x1 < r && x2 > l;
            default:
                // at least half of the helper is inside
                return l < x1 + width / 2 && x2 - width / 2 < r && t < y1 + height / 2 && y2 - height / 2 < b;
        }
    }
}

/**
 * Keeps track of the droppables and of the current drag source.
 */
export const DragDropManager = {
    /** The interaction that is dragged right now. */
    current: null as DragSource | null,

    /** The droppables by scope. */
    droppables: {} as Record<string, Droppable[]>,

    /** The droppables activated for the current drag. */
    active: [] as Droppable[],

    /** Measures the active droppables again when a container scrolls during the drag. */
    scrollListener: null as (() => void) | null,

    /**
     * Registers a droppable under its scope.
     * @param droppable The droppable.
     */
    add(droppable: Droppable): void {
        (this.droppables[droppable.options.scope] ??= []).push(droppable);
    },

    /**
     * Unregisters a droppable, also from the current drag.
     * @param droppable The droppable.
     * @param scope The scope it was registered under.
     */
    remove(droppable: Droppable, scope: string): void {
        this.droppables[scope] = (this.droppables[scope] ?? []).filter((d) => d !== droppable);
        this.active = this.active.filter((d) => d !== droppable);
    },

    /**
     * Activates the droppables accepting the source, when the drag starts.
     * @param source The dragged interaction.
     * @param event The event that started the drag.
     */
    start(source: DragSource, event: JQuery.TriggeredEvent): void {
        // droppables a previous drag left active (e.g. a sortable that still reverts) are deactivated first
        if (this.current && this.active.length) {
            this.deactivate(this.current, null);
        }
        this.current = source;
        const item = source.item();
        this.active = (this.droppables[source.scope()] ?? []).filter((d) => {
            // droppables inside the dragged item can't be targets
            return !d.options.disabled && d.element[0]!.isConnected && !$.contains(item[0]!, d.element[0]!) && d.element[0] !== item[0]
                && d.element.is(':visible') && d.accepts(item);
        });
        for (const droppable of this.active) {
            droppable.measure();
            droppable.activate(event, source);
        }

        // the boxes change when a container scrolls
        if (this.scrollListener) {
            document.removeEventListener('scroll', this.scrollListener, true);
        }
        this.scrollListener = () => this.active.forEach((d) => d.measure());
        document.addEventListener('scroll', this.scrollListener, true);
    },

    /**
     * Calls over / out of the droppables.
     * @param source The dragged interaction.
     * @param event The pointermove event.
     */
    drag(source: DragSource, event: JQuery.TriggeredEvent): void {
        let intersecting = this.active.filter((d) => d.intersects(source, event));
        // a greedy droppable hides its parent droppables
        const greedy = intersecting.filter((d) => d.options.greedy);
        intersecting = intersecting.filter((d) => !greedy.some((g) => g !== d && $.contains(d.element[0]!, g.element[0]!)));

        for (const droppable of this.active) {
            const over = intersecting.includes(droppable);
            if (over && !droppable.isOver) {
                droppable.over(event, source);
            }
            else if (!over && droppable.isOver) {
                droppable.out(event, source);
            }
        }
    },

    /**
     * Drops the source on the droppables it is over.
     * @param source The dragged interaction.
     * @param event The pointerup event.
     * @returns Whether it was dropped.
     */
    drop(source: DragSource, event: JQuery.TriggeredEvent): boolean {
        // over / out at the final position: the pointer may not have moved since a container scrolled under it
        this.drag(source, event);
        let dropped = false;
        for (const droppable of this.active) {
            if (droppable.isOver) {
                droppable.dropItem(event, source);
                dropped = true;
            }
        }
        return dropped;
    },

    /**
     * Deactivates the droppables of the drag, e.g. at the drop.
     * @param source The dragged interaction.
     * @param event The event that ended the drag, `null` if it was cancelled or destroyed.
     */
    deactivate(source: DragSource, event: JQuery.TriggeredEvent | null): void {
        for (const droppable of this.active) {
            droppable.deactivate(event, source);
        }
        this.active = [];
        if (this.scrollListener) {
            document.removeEventListener('scroll', this.scrollListener, true);
            this.scrollListener = null;
        }
    },

    /**
     * Deactivates the droppables when the drag ended, the drag is not current anymore.
     * @param source The dragged interaction.
     * @param event The event that ended the drag, `null` if it was cancelled or destroyed.
     */
    stop(source: DragSource, event: JQuery.TriggeredEvent | null): void {
        this.deactivate(source, event);
        if (this.current === source) {
            this.current = null;
        }
    }
};
