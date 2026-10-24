import { Interaction, offsetOf, type Callback, type Coordinates, type InteractionOptions, pageX, pageY } from "./interaction.js";

export interface DroppableOptions extends InteractionOptions {
    /** Selector or function deciding which draggables are accepted. */
    accept: string | ((this: HTMLElement, draggable: JQuery) => boolean);
    /** Class added while an accepted draggable is dragged. */
    activeClass: string | false;
    /** Additional classes for `ui-droppable-active` / `ui-droppable-hover`. */
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
    activate: Callback;
    deactivate: Callback;
    over: Callback;
    out: Callback;
    drop: Callback;
}

/** The interaction (draggable or sortable) that is dragged over droppables. */
export interface DragSource {
    /** The dragged element. */
    item(): JQuery;
    helper: JQuery | null;
    scope(): string;
    /** The offset of the helper. */
    offset: Coordinates;
    /** The CSS position of the helper. */
    position: Coordinates;
    cancel(): void;
}

export interface DroppableUi {
    draggable: JQuery;
    helper: JQuery | null;
    position: Coordinates;
    offset: Coordinates;
}

const DEFAULTS: DroppableOptions = {
    disabled: false,
    distance: 0,
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
    isOver: boolean = false;
    private currentScope: string;

    constructor(element: HTMLElement, options?: Partial<DroppableOptions>) {
        super(element, 'droppable', 'drop', DEFAULTS, options);
        this.element.addClass('ui-droppable');
        this.currentScope = this.options.scope;
        DragDropManager.add(this);
    }

    override destroy(): void {
        DragDropManager.remove(this, this.currentScope);
        super.destroy();
    }

    protected override setOption(key: string, value: unknown): void {
        if (key === 'scope') {
            DragDropManager.remove(this, this.currentScope);
            super.setOption(key, value);
            this.currentScope = String(value);
            DragDropManager.add(this);
            return;
        }
        super.setOption(key, value);
    }

    protected capture(): boolean {
        return false;
    }

    protected start(): boolean {
        return false;
    }

    protected move(): void {
        // droppables don't move
    }

    protected stop(): void {
        // droppables don't move
    }

    accepts(item: JQuery): boolean {
        const accept = this.options.accept;
        return typeof accept === 'function' ? !!accept.call(this.element[0]!, item) : item.is(accept);
    }

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

    private toggleClasses(state: 'active' | 'hover', on: boolean): void {
        const key = 'ui-droppable-' + state;
        const custom = state === 'active' ? this.options.activeClass : this.options.hoverClass;
        this.element.toggleClass([key, this.options.classes[key] ?? '', custom || ''].join(' ').trim(), on);
    }

    activate(event: JQuery.TriggeredEvent | null, source: DragSource): void {
        this.toggleClasses('active', true);
        this.trigger('activate', event, this.ui(source));
    }

    deactivate(event: JQuery.TriggeredEvent | null, source: DragSource): void {
        this.toggleClasses('active', false);
        this.toggleClasses('hover', false);
        this.isOver = false;
        this.trigger('deactivate', event, this.ui(source));
    }

    over(event: JQuery.TriggeredEvent, source: DragSource): void {
        this.isOver = true;
        this.toggleClasses('hover', true);
        this.trigger('over', event, this.ui(source));
    }

    out(event: JQuery.TriggeredEvent, source: DragSource): void {
        this.isOver = false;
        this.toggleClasses('hover', false);
        this.trigger('out', event, this.ui(source));
    }

    /** @return `true` if the drop was accepted. */
    dropItem(event: JQuery.TriggeredEvent, source: DragSource): boolean {
        this.toggleClasses('hover', false);
        this.isOver = false;
        this.trigger('drop', event, this.ui(source));
        return true;
    }

    /** Whether the helper of the source intersects this droppable according to the tolerance. */
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

    droppables: {} as Record<string, Droppable[]>,

    active: [] as Droppable[],

    scrollListener: null as (() => void) | null,

    add(droppable: Droppable): void {
        (this.droppables[droppable.options.scope] ??= []).push(droppable);
    },

    remove(droppable: Droppable, scope: string): void {
        this.droppables[scope] = (this.droppables[scope] ?? []).filter((d) => d !== droppable);
    },

    /** Activates the droppables accepting the source, when the drag starts. */
    start(source: DragSource, event: JQuery.TriggeredEvent): void {
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
        this.scrollListener = () => this.active.forEach((d) => d.measure());
        document.addEventListener('scroll', this.scrollListener, true);
    },

    /** Calls over / out of the droppables. */
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

    /** Drops the source on the droppables it is over. @return whether it was dropped. */
    drop(source: DragSource, event: JQuery.TriggeredEvent): boolean {
        let dropped = false;
        for (const droppable of this.active) {
            if (droppable.isOver && droppable.intersects(source, event) && droppable.accepts(source.item())) {
                dropped = droppable.dropItem(event, source) || dropped;
            }
        }
        return dropped;
    },

    /** Deactivates the droppables when the drag ended. */
    stop(source: DragSource, event: JQuery.TriggeredEvent | null): void {
        for (const droppable of this.active) {
            droppable.deactivate(event, source);
        }
        this.active = [];
        if (this.scrollListener) {
            document.removeEventListener('scroll', this.scrollListener, true);
            this.scrollListener = null;
        }
        if (this.current === source) {
            this.current = null;
        }
    }
};

