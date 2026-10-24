import { contentBox, cssPosition, Interaction, offsetOf, type Callback, type Coordinates, type InteractionOptions, pageX, pageY } from "./interaction.js";
import { DragDropManager, type DragSource } from "./droppable.js";
import { Sortable } from "./sortable.js";

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
    /** Snaps the movement to a grid `[x, y]`. */
    grid: [number, number] | false;
    /** Elements on which the drag starts, defaults to the whole element. */
    handle: string | false;
    /** `original`, `clone` or a function returning the element that is dragged. */
    helper: 'original' | 'clone' | ((this: HTMLElement, event: JQuery.TriggeredEvent) => Element | JQuery);
    /** Opacity of the helper while dragging. */
    opacity: number | false;
    /** Whether the helper moves back: `true`, `invalid` (not dropped), `valid` (dropped) or a function. */
    revert: boolean | 'invalid' | 'valid' | ((this: HTMLElement, dropped: boolean) => boolean);
    revertDuration: number;
    /** Droppables with the same scope accept the element. */
    scope: string;
    /** Snaps to the edges of other elements: `true` for other draggables or a selector. */
    snap: boolean | string;
    /** `inner`, `outer` or `both` edges. */
    snapMode: 'inner' | 'outer' | 'both';
    snapTolerance: number;
    /** Whether to scroll the scroll parent when dragging near its edge. */
    scroll: boolean;
    /** Group of draggables whose z-index is managed, the dragged one is brought to the front. */
    stack: string | false;
    /** z-index of the helper while dragging. */
    zIndex: number | false;
    start: Callback;
    drag: Callback;
    stop: Callback;
}

export interface DraggableUi {
    helper: JQuery;
    position: Coordinates;
    originalPosition: Coordinates;
    offset: Coordinates;
}

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

interface SnapElement {
    left: number;
    top: number;
    right: number;
    bottom: number;
}

/**
 * Makes an element draggable with the mouse, a pen or by touch.
 */
export class Draggable extends Interaction<DraggableOptions> implements DragSource {
    helper: JQuery | null = null;
    offset: Coordinates = { left: 0, top: 0 };
    position: Coordinates = { left: 0, top: 0 };
    originalPosition: Coordinates = { left: 0, top: 0 };

    private clickOffset: Coordinates = { left: 0, top: 0 };
    private originalOffset: Coordinates = { left: 0, top: 0 };
    private delta: Coordinates = { left: 0, top: 0 };
    private bounds: [number, number, number, number] | null = null;
    private snapElements: SnapElement[] = [];
    private sortables: Sortable[] = [];
    private storedCss: Record<string, string> = {};
    private storedCursor: string | null = null;
    private cancelled: boolean = false;

    constructor(element: HTMLElement, options?: Partial<DraggableOptions>) {
        super(element, 'draggable', 'drag', DEFAULTS, options);
        this.element.addClass('ui-draggable');
        this.updateHandles();
        this.bindPointer();
    }

    override destroy(): void {
        this.handles().removeClass('ui-draggable-handle');
        super.destroy();
    }

    protected override setOption(key: string, value: unknown): void {
        if (key === 'handle') {
            this.handles().removeClass('ui-draggable-handle');
        }
        super.setOption(key, value);
        if (key === 'handle') {
            this.updateHandles();
        }
    }

    private handles(): JQuery {
        return this.options.handle ? this.element.find(this.options.handle) : this.element;
    }

    private updateHandles(): void {
        this.handles().addClass('ui-draggable-handle');
    }

    item(): JQuery {
        return this.element;
    }

    scope(): string {
        return this.options.scope;
    }

    ui(): DraggableUi {
        return { helper: this.helper!, position: this.position, originalPosition: this.originalPosition, offset: this.offset };
    }

    protected capture(event: JQuery.TriggeredEvent): boolean {
        if (this.isCancel(event, this.options.cancel)) {
            return false;
        }
        return !this.options.handle || $(event.target).closest(this.element.find(this.options.handle)).length > 0;
    }

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

        DragDropManager.start(this, event);

        if (!this.trigger('start', event, this.ui())) {
            this.clear(event);
            return false;
        }

        // the callback may have changed the helper
        this.bounds = this.containment();
        this.snapElements = o.snap ? this.findSnapElements() : [];
        this.sortables = o.connectToSortable ? $(o.connectToSortable).map((_, el) => $.data(el, 'pf-sortable') as Sortable | undefined).get() : [];
        return true;
    }

    protected move(event: JQuery.TriggeredEvent): void {
        const o = this.options;
        if (o.scroll) {
            this.autoScroll(event);
        }
        let left = pageX(event) - this.clickOffset.left;
        let top = pageY(event) - this.clickOffset.top;

        if (o.grid) {
            left = this.originalOffset.left + Math.round((left - this.originalOffset.left) / o.grid[0]) * o.grid[0];
            top = this.originalOffset.top + Math.round((top - this.originalOffset.top) / o.grid[1]) * o.grid[1];
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

    protected override abort(e: JQuery.TriggeredEvent): void {
        this.cancelled = true;
        this.end(e);
    }

    /** Aborts the drag. */
    cancel(): void {
        if (this.isActive()) {
            this.cancelled = true;
            this.end($.Event('pointercancel') as JQuery.TriggeredEvent);
        }
        else {
            this.clear(null);
        }
    }

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

    /** The bounds of the helper offset: [minLeft, minTop, maxLeft, maxTop]. */
    private containment(): [number, number, number, number] | null {
        const o = this.options;
        const helper = this.helper!;
        const width = (helper.outerWidth() ?? 0) + (parseFloat(helper.css('marginLeft')) || 0) + (parseFloat(helper.css('marginRight')) || 0);
        const height = (helper.outerHeight() ?? 0) + (parseFloat(helper.css('marginTop')) || 0) + (parseFloat(helper.css('marginBottom')) || 0);
        const fit = (box: [number, number, number, number]): [number, number, number, number] =>
            [box[0], box[1], Math.max(box[0], box[2] - width), Math.max(box[1], box[3] - height)];

        if (!o.containment) {
            return null;
        }
        if (Array.isArray(o.containment)) {
            return o.containment as [number, number, number, number];
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
        const container = o.containment === 'parent' ? this.element.parent() : $(o.containment as string);
        return container.length ? fit(contentBox(container.first())) : null;
    }

    /** Brings the element to the front of the stack group. */
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

    private findSnapElements(): SnapElement[] {
        const snap = this.options.snap;
        return $(snap === true ? '.ui-draggable' : snap as string).not(this.element).not(this.helper!).filter(':visible').map((_, el) => {
            const jq = $(el);
            const offset = offsetOf(jq);
            return { left: offset.left, top: offset.top, right: offset.left + (jq.outerWidth() ?? 0), bottom: offset.top + (jq.outerHeight() ?? 0) };
        }).get();
    }

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
