import { contentBox, cssPosition, Interaction, num, offsetOf, type Callback, type Coordinates, type InteractionOptions, pageX, pageY, scrollParent } from "./interaction.js";
import { DragDropManager, type DragSource } from "./droppable.js";
import { Sortable } from "./sortable.js";

/** The options of a draggable, see the jQuery UI draggable. */
export interface DraggableOptions extends InteractionOptions {
    /** Where the helper is appended while dragging, `parent` for the parent of the element. */
    appendTo: string | Element | JQuery;
    /** Restricts the movement to the `x` or `y` axis. */
    axis: 'x' | 'y' | false;
    /** Elements on which the drag does not start. */
    cancel: string;
    /** Sortables into which the element can be dragged. */
    connectToSortable: string | false;
    /** Area the helper is kept in: `parent`, `document`, `window`, a selector / element or `[x1, y1, x2, y2]`. */
    containment: false | 'parent' | 'document' | 'window' | string | Element | JQuery | number[];
    /** Cursor while dragging. */
    cursor: string | false;
    /** Snaps the movement to a grid `[x, y]`, `0` keeps the axis. */
    grid: [number, number] | false;
    /** Elements on which the drag starts, defaults to the whole element. */
    handle: string | false;
    /** `original`, `clone` or a function returning the element that is dragged. */
    helper: 'original' | 'clone' | ((this: HTMLElement, event: JQuery.TriggeredEvent) => Element | JQuery);
    /** Opacity of the helper while dragging. */
    opacity: number | false;
    /** Whether the helper moves back: `true`, `invalid` (not dropped), `valid` (dropped) or a function. */
    revert: boolean | 'invalid' | 'valid' | ((this: HTMLElement, dropped: boolean) => boolean);
    /** Duration of the revert animation in milliseconds. */
    revertDuration: number;
    /** Droppables with the same scope accept the element. */
    scope: string;
    /** Snaps to the edges of other elements: `true` for other draggables or a selector. */
    snap: boolean | string;
    /** `inner`, `outer` or `both` edges. */
    snapMode: 'inner' | 'outer' | 'both';
    /** Distance in pixels at which the helper snaps. */
    snapTolerance: number;
    /** Whether to scroll the scroll parent when dragging near its edge. */
    scroll: boolean;
    /** Group of draggables whose z-index is managed, the dragged one is brought to the front. */
    stack: string | false;
    /** z-index of the helper while dragging. */
    zIndex: number | false;
    /** Called when the drag starts, `false` cancels it. */
    start: Callback;
    /** Called on every move, `false` stops the drag, `ui.position` can be changed. */
    drag: Callback;
    /** Called when the drag stops, after the revert animation; `false` keeps the helper. */
    stop: Callback;
}

/** The `ui` argument of the draggable callbacks. */
export interface DraggableUi {
    /** The element that moves with the pointer. */
    helper: JQuery;
    /** The CSS position of the helper. */
    position: Coordinates;
    /** The CSS position of the helper when the drag started. */
    originalPosition: Coordinates;
    /** The offset of the helper. */
    offset: Coordinates;
}

/** The default options, the same as in jQuery UI. */
const DEFAULTS: DraggableOptions = {
    disabled: false,
    distance: 1,
    appendTo: 'parent',
    axis: false,
    cancel: 'input,textarea,button,select,option',
    connectToSortable: false,
    containment: false,
    cursor: 'auto',
    grid: false,
    handle: false,
    helper: 'original',
    opacity: false,
    revert: false,
    revertDuration: 500,
    scope: 'default',
    scroll: true,
    snap: false,
    snapMode: 'both',
    snapTolerance: 20,
    stack: false,
    zIndex: false,
    start: null,
    drag: null,
    stop: null
};

/** The bounds of an element the helper snaps to, in page coordinates. */
interface SnapElement {
    /** The left edge. */
    left: number;
    /** The top edge. */
    top: number;
    /** The right edge. */
    right: number;
    /** The bottom edge. */
    bottom: number;
}

/**
 * Makes an element draggable with the mouse, a pen or by touch.
 */
export class Draggable extends Interaction<DraggableOptions> implements DragSource {
    /** The element that moves with the pointer, from the start of the drag until the end of the revert animation. */
    helper: JQuery | null = null;
    /** The offset of the helper. */
    offset: Coordinates = { left: 0, top: 0 };
    /** The CSS position of the helper. */
    position: Coordinates = { left: 0, top: 0 };
    /** The CSS position of the helper when the drag started. */
    originalPosition: Coordinates = { left: 0, top: 0 };

    /** The pointer position relative to the element when the drag started. */
    private clickOffset: Coordinates = { left: 0, top: 0 };
    /** The offset of the element when the drag started. */
    private originalOffset: Coordinates = { left: 0, top: 0 };
    /** The offset of the helper minus its CSS position. */
    private delta: Coordinates = { left: 0, top: 0 };
    /** The bounds of the helper offset from the containment. */
    private bounds: [number, number, number, number] | null = null;
    /** The elements the helper snaps to. */
    private snapElements: SnapElement[] = [];
    /** The sortables of the `connectToSortable` option. */
    private sortables: Sortable[] = [];
    /** The inline z-index and opacity of the element, restored after the drag. */
    private storedCss: Record<string, string> = {};
    /** The inline cursor of the body, restored after the drag. */
    private storedCursor: string | null = null;
    /** The scroll parent of the helper when the drag started, `null` for the window. */
    private scrollContainer: HTMLElement | null = null;
    /** Whether the drag was aborted, it ends without a drop. */
    private cancelled: boolean = false;

    /**
     * Makes the element draggable.
     * @param element The element.
     * @param options The options.
     */
    constructor(element: HTMLElement, options?: Partial<DraggableOptions>) {
        super(element, 'draggable', 'drag', DEFAULTS, options);
        this.addClasses();
        this.updateHandles();
        this.bindPointer();
    }

    /** Removes the handle classes; a drag in progress ends without a drop and a running revert finishes. */
    override destroy(): void {
        if (this.isActive()) {
            this.cancel();
        }
        this.helper?.stop(true, true);
        if (this.helper) {
            // the stop callback returned false
            this.clear(null);
        }
        this.handles().removeClass('ui-draggable-handle');
        super.destroy();
    }

    /**
     * Sets an option, a new handle updates the handle classes.
     * @param key The name of the option.
     * @param value The new value.
     */
    protected override setOption(key: string, value: unknown): void {
        if (key === 'handle') {
            this.handles().removeClass('ui-draggable-handle');
        }
        super.setOption(key, value);
        if (key === 'handle') {
            this.updateHandles();
        }
    }

    /**
     * The elements on which the drag starts.
     * @returns The elements of the `handle` option, or the element itself.
     */
    private handles(): JQuery {
        return this.options.handle ? this.element.find(this.options.handle) : this.element;
    }

    /** Marks the handles, which disables touch scrolling on them (CSS). */
    private updateHandles(): void {
        this.handles().addClass('ui-draggable-handle');
    }

    /**
     * The dragged element, for the droppables.
     * @returns The element.
     */
    item(): JQuery {
        return this.element;
    }

    /**
     * The scope of the drag, only droppables with the same scope are activated.
     * @returns The `scope` option.
     */
    scope(): string {
        return this.options.scope;
    }

    /**
     * Creates the `ui` argument of the callbacks.
     * @returns The `ui` argument.
     */
    ui(): DraggableUi {
        return { helper: this.helper!, position: this.position, originalPosition: this.originalPosition, offset: this.offset };
    }

    /**
     * Starts on a handle, not on a `cancel` element, not on a resize handle and not during the revert animation.
     * @param event The pointerdown event.
     * @returns Whether the drag can start.
     */
    protected capture(event: JQuery.TriggeredEvent): boolean {
        if (this.helper || this.isCancel(event, this.options.cancel) || $(event.target).closest('.ui-resizable-handle').length) {
            return false;
        }
        return !this.options.handle || $(event.target).closest(this.element.find(this.options.handle)).length > 0;
    }

    /**
     * Creates the helper according to the `helper` and `appendTo` options.
     * @param event The pointerdown event.
     * @returns The element itself, a clone or the element of the helper function, positioned.
     */
    private createHelper(event: JQuery.TriggeredEvent): JQuery {
        const o = this.options;
        const helper = typeof o.helper === 'function' ? $(o.helper.call(this.element[0]!, event))
            : o.helper === 'clone' ? this.element.clone().removeAttr('id') : this.element;

        if (!helper.parents('body').length) {
            helper.appendTo(o.appendTo === 'parent' ? this.element.parent() : $(o.appendTo as string));
        }
        if (helper[0] !== this.element[0] && !/(fixed|absolute)/.test(helper.css('position'))) {
            helper.css('position', 'absolute');
        }
        return helper as JQuery;
    }

    /**
     * Creates the helper and calls the `start` callback, then activates the droppables, like jQuery UI.
     * @param event The pointerdown event.
     * @returns `false` if the `start` callback cancelled the drag.
     */
    protected start(event: JQuery.TriggeredEvent): boolean {
        const o = this.options;
        const helper = this.helper = this.createHelper(event);
        const original = helper[0] === this.element[0];

        if (original) {
            this.storedCss = { zIndex: this.element[0]!.style.zIndex, opacity: this.element[0]!.style.opacity };
            if (this.element.css('position') === 'static') {
                this.element.css('position', 'relative');
            }
        }

        const elementOffset = offsetOf(this.element);
        this.clickOffset = { left: pageX(event) - elementOffset.left, top: pageY(event) - elementOffset.top };
        helper.offset(elementOffset);
        this.originalOffset = elementOffset;
        this.offset = { ...elementOffset };
        this.position = cssPosition(helper);
        this.originalPosition = { ...this.position };
        this.delta = { left: elementOffset.left - this.position.left, top: elementOffset.top - this.position.top };

        helper.addClass('ui-draggable-dragging');
        if (o.zIndex !== false) {
            helper.css('z-index', o.zIndex);
        }
        if (o.opacity !== false) {
            helper.css('opacity', o.opacity);
        }
        if (o.stack) {
            this.stack();
        }
        if (o.cursor && o.cursor !== 'auto') {
            this.storedCursor = document.body.style.cursor;
            document.body.style.cursor = o.cursor;
        }
        // before the callback, which may e.g. hide the overflow of the container
        this.scrollContainer = scrollParent(helper);
        DragDropManager.current = this;

        if (!this.trigger('start', event, this.ui())) {
            this.clear(event);
            return false;
        }

        // the callback may have changed the helper
        this.bounds = this.containment();
        this.snapElements = o.snap ? this.findSnapElements() : [];
        this.sortables = o.connectToSortable ? $(o.connectToSortable).map((_, el) => $.data(el, 'pf-sortable') as Sortable | undefined).get() : [];
        DragDropManager.start(this, event);
        return true;
    }

    /**
     * Moves the helper to the pointer, applying grid, containment, axis and snapping, and informs the droppables and
     * the connected sortables.
     * @param event The pointermove event.
     */
    protected move(event: JQuery.TriggeredEvent): void {
        const o = this.options;
        if (o.scroll) {
            this.autoScroll(event, this.scrollContainer, o.axis);
        }
        // the helper moves when its container scrolls, a fixed one stays when the window scrolls
        this.delta = this.helperDelta();
        let left = pageX(event) - this.clickOffset.left;
        let top = pageY(event) - this.clickOffset.top;

        if (o.grid) {
            // a grid value of 0 keeps the axis, like jQuery UI
            left = o.grid[0] ? this.originalOffset.left + Math.round((left - this.originalOffset.left) / o.grid[0]) * o.grid[0] : this.originalOffset.left;
            top = o.grid[1] ? this.originalOffset.top + Math.round((top - this.originalOffset.top) / o.grid[1]) * o.grid[1] : this.originalOffset.top;
        }
        if (this.bounds && this.hasElementContainment()) {
            // like jQuery UI, an element containment is measured again, it may have moved (e.g. scrolled)
            this.bounds = this.containment();
        }
        if (this.bounds) {
            left = Math.min(Math.max(left, this.bounds[0]), this.bounds[2]);
            top = Math.min(Math.max(top, this.bounds[1]), this.bounds[3]);
        }
        if (o.axis === 'y') {
            left = this.originalOffset.left;
        }
        if (o.axis === 'x') {
            top = this.originalOffset.top;
        }
        if (this.snapElements.length) {
            ({ left, top } = this.snapTo(left, top));
        }

        this.offset = { left, top };
        this.position = { left: left - this.delta.left, top: top - this.delta.top };

        const ui = this.ui();
        if (!this.trigger('drag', event, ui)) {
            this.end(event);
            return;
        }

        // the callback may change the position
        this.position = ui.position;
        const css: Record<string, string> = {};
        if (o.axis !== 'y') {
            css['left'] = this.position.left + 'px';
        }
        if (o.axis !== 'x') {
            css['top'] = this.position.top + 'px';
        }
        this.helper!.css(css);
        this.offset = { left: this.position.left + this.delta.left, top: this.position.top + this.delta.top };

        DragDropManager.drag(this, event);
        for (const sortable of this.sortables) {
            sortable.externalDrag(this, event);
        }
    }

    /**
     * The offset of the helper minus its CSS position, which changes when a container of the helper scrolls.
     * @returns The difference.
     */
    private helperDelta(): Coordinates {
        const offset = offsetOf(this.helper!);
        const css = cssPosition(this.helper!);
        return { left: offset.left - css.left, top: offset.top - css.top };
    }

    /**
     * Drops the helper on the droppables and connected sortables, then reverts it if needed and calls `stop`.
     * @param event The pointerup or pointercancel event.
     */
    protected stop(event: JQuery.TriggeredEvent): void {
        const o = this.options;
        let dropped = !this.cancelled && DragDropManager.drop(this, event);
        for (const sortable of this.cancelled ? [] : this.sortables) {
            dropped = sortable.externalDrop(this, event) || dropped;
        }
        this.cancelled = false;

        const revert = typeof o.revert === 'function' ? o.revert.call(this.element[0]!, dropped)
            : o.revert === true || (o.revert === 'invalid' && !dropped) || (o.revert === 'valid' && dropped);

        DragDropManager.stop(this, event);

        if (revert && this.helper) {
            this.helper.animate({ left: this.originalPosition.left, top: this.originalPosition.top }, o.revertDuration, () => {
                if (this.trigger('stop', event, this.ui())) {
                    this.clear(event);
                }
            });
        }
        else if (this.trigger('stop', event, this.ui())) {
            this.clear(event);
        }
    }

    /**
     * The browser took over the pointer, the drag ends without a drop.
     * @param e The pointercancel event.
     */
    protected override abort(e: JQuery.TriggeredEvent): void {
        this.cancelled = true;
        this.end(e);
    }

    /** Aborts the drag without a drop; outside of a drag (e.g. during the revert animation) it removes the helper. */
    cancel(): void {
        if (this.isActive()) {
            this.cancelled = true;
            this.end($.Event('pointercancel') as JQuery.TriggeredEvent);
        }
        else {
            this.helper?.stop(true);
            this.clear(null);
        }
    }

    /**
     * Removes the helper and restores the element, the cursor, the droppables and the connected sortables.
     * @param event The event that ended the drag, `null` if it was cancelled or destroyed.
     */
    private clear(event: JQuery.TriggeredEvent | null): void {
        if (DragDropManager.current === this) {
            DragDropManager.stop(this, event);
        }
        if (this.storedCursor !== null) {
            document.body.style.cursor = this.storedCursor;
            this.storedCursor = null;
        }
        if (this.helper) {
            this.helper.removeClass('ui-draggable-dragging');
            if (this.helper[0] !== this.element[0]) {
                this.helper.remove();
            }
            else {
                if (this.options.zIndex !== false) {
                    this.element[0]!.style.zIndex = this.storedCss['zIndex'] ?? '';
                }
                if (this.options.opacity !== false) {
                    this.element[0]!.style.opacity = this.storedCss['opacity'] ?? '';
                }
            }
        }
        for (const sortable of this.sortables) {
            sortable.externalOut();
        }
        this.helper = null;
        this.sortables = [];
    }

    /**
     * The bounds of the helper offset from the `containment` option. Like with jQuery UI the containment applies to
     * the margin box of the helper, so the bounds are shifted by its left / top margin.
     * @returns The bounds as `[minLeft, minTop, maxLeft, maxTop]`, `null` without containment.
     */
    private containment(): [number, number, number, number] | null {
        const o = this.options;
        const helper = this.helper!;
        const marginLeft = num(helper.css('marginLeft'));
        const marginTop = num(helper.css('marginTop'));
        const width = (helper.outerWidth() ?? 0) + marginLeft + num(helper.css('marginRight'));
        const height = (helper.outerHeight() ?? 0) + marginTop + num(helper.css('marginBottom'));
        const shift = (box: number[]): [number, number, number, number] =>
            [(box[0] ?? 0) + marginLeft, (box[1] ?? 0) + marginTop, (box[2] ?? 0) + marginLeft, (box[3] ?? 0) + marginTop];
        const fit = (box: [number, number, number, number]): [number, number, number, number] =>
            shift([box[0], box[1], Math.max(box[0], box[2] - width), Math.max(box[1], box[3] - height)]);

        if (!o.containment) {
            return null;
        }
        if (Array.isArray(o.containment)) {
            return shift(o.containment as number[]);
        }
        if (o.containment === 'document') {
            return fit([0, 0, $(document).width() ?? 0, $(document).height() ?? 0]);
        }
        if (o.containment === 'window') {
            const win = $(window);
            const left = win.scrollLeft() ?? 0;
            const top = win.scrollTop() ?? 0;
            return fit([left, top, left + (win.width() ?? 0), top + (win.height() ?? 0)]);
        }
        // like jQuery UI, "parent" is the parent of the helper
        const container = o.containment === 'parent' ? helper.parent() : $(o.containment as string);
        return container.length ? fit(contentBox(container.first())) : null;
    }

    /**
     * Whether the `containment` option is an element (`parent`, a selector, an element or a jQuery instance), not
     * fixed coordinates, the document or the window.
     * @returns `true` for an element containment.
     */
    private hasElementContainment(): boolean {
        const containment = this.options.containment;
        return !!containment && !Array.isArray(containment) && containment !== 'document' && containment !== 'window';
    }

    /** Brings the element to the front of the `stack` group. */
    private stack(): void {
        const group = $(this.options.stack as string).get()
            .sort((a, b) => (parseInt($(a).css('zIndex'), 10) || 0) - (parseInt($(b).css('zIndex'), 10) || 0));
        if (!group.length) {
            return;
        }
        const min = parseInt($(group[0]!).css('zIndex'), 10) || 0;
        group.forEach((el, i) => {
            el.style.zIndex = String(min + i);
        });
        this.element.css('zIndex', min + group.length);
    }

    /**
     * Measures the visible elements of the `snap` option.
     * @returns Their bounds.
     */
    private findSnapElements(): SnapElement[] {
        const snap = this.options.snap;
        return $(snap === true ? '.ui-draggable' : snap as string).not(this.element).not(this.helper!).filter(':visible').map((_, el) => {
            const jq = $(el);
            const offset = offsetOf(jq);
            return { left: offset.left, top: offset.top, right: offset.left + (jq.outerWidth() ?? 0), bottom: offset.top + (jq.outerHeight() ?? 0) };
        }).get();
    }

    /**
     * Snaps the helper offset to the edges of the snap elements within the tolerance, according to the `snapMode`.
     * @param left The left offset of the helper.
     * @param top The top offset of the helper.
     * @returns The snapped offset.
     */
    private snapTo(left: number, top: number): Coordinates {
        const o = this.options;
        const d = o.snapTolerance;
        const width = this.helper!.outerWidth() ?? 0;
        const height = this.helper!.outerHeight() ?? 0;

        for (const s of this.snapElements) {
            const x1 = left;
            const x2 = left + width;
            const y1 = top;
            const y2 = top + height;
            if (!((s.left - d < x1 && x1 < s.right + d && s.top - d < y1 && y1 < s.bottom + d)
                    || (s.left - d < x1 && x1 < s.right + d && s.top - d < y2 && y2 < s.bottom + d)
                    || (s.left - d < x2 && x2 < s.right + d && s.top - d < y1 && y1 < s.bottom + d)
                    || (s.left - d < x2 && x2 < s.right + d && s.top - d < y2 && y2 < s.bottom + d))) {
                continue;
            }

            if (o.snapMode !== 'inner') {
                if (Math.abs(s.top - y2) <= d) {
                    top = s.top - height;
                }
                if (Math.abs(s.bottom - y1) <= d) {
                    top = s.bottom;
                }
                if (Math.abs(s.left - x2) <= d) {
                    left = s.left - width;
                }
                if (Math.abs(s.right - x1) <= d) {
                    left = s.right;
                }
            }
            if (o.snapMode !== 'outer') {
                if (Math.abs(s.top - y1) <= d) {
                    top = s.top;
                }
                if (Math.abs(s.bottom - y2) <= d) {
                    top = s.bottom - height;
                }
                if (Math.abs(s.left - x1) <= d) {
                    left = s.left;
                }
                if (Math.abs(s.right - x2) <= d) {
                    left = s.right - width;
                }
            }
        }
        return { left, top };
    }
}
