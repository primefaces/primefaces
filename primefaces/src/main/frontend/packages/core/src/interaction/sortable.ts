import { cssPosition, dataKey, offsetOf, type Callback, type Coordinates, pageX, pageY, PointerInteraction, type PointerInteractionOptions,
    positionDelta, scrollParent } from "./interaction.js";
import { DragDropManager, type DragSource } from "./droppable.js";

/** The options of a sortable, see the jQuery UI sortable. */
export interface SortableOptions extends PointerInteractionOptions {
    /** Where the helper is appended while sorting, `parent` for the parent of the item. */
    appendTo: string | Element | JQuery;
    /** Restricts the movement of the helper to the `x` or `y` axis. */
    axis: 'x' | 'y' | false;
    /** Elements on which sorting does not start. */
    cancel: string;
    /** Other sortables the items can be moved to. */
    connectWith: string | false;
    /** Cursor while sorting. */
    cursor: string | false;
    /** Whether the placeholder gets the size of the item. */
    forcePlaceholderSize: boolean;
    /** Elements on which sorting starts, defaults to the whole item. */
    handle: string | false;
    /** `original`, `clone` or a function returning the element that is dragged. */
    helper: 'original' | 'clone' | ((this: HTMLElement, event: JQuery.TriggeredEvent, item: JQuery) => Element | JQuery);
    /** Selector of the sortable items. */
    items: string;
    /** Opacity of the helper while sorting. */
    opacity: number | false;
    /** Style class of the placeholder. */
    placeholder: string | false;
    /** Whether to scroll the scroll parent when dragging near its edge. */
    scroll: boolean;
    /** Droppables with the same scope accept the dragged items. */
    scope: string;
    /** Whether the helper moves to its new place, or the duration of that animation. */
    revert: boolean | number;
    /** z-index of the helper while sorting. */
    zIndex: number | false;
    /** Called on this and the connected sortables when the sorting starts. */
    activate: Callback;
    /** Called when the sorting stops, while the placeholder and the helper still exist. */
    beforeStop: Callback;
    /** Called when the placeholder moved. */
    change: Callback;
    /** Called on this and the connected sortables when the sorting stops. */
    deactivate: Callback;
    /** Called when the item leaves a sortable. */
    out: Callback;
    /** Called when the item enters a sortable. */
    over: Callback;
    /** Called on the sortable that received an item from a connected sortable. */
    receive: Callback;
    /** Called on the sortable that lost an item to a connected sortable. */
    remove: Callback;
    /** Called on every move. */
    sort: Callback;
    /** Called when the sorting starts. */
    start: Callback;
    /** Called when the sorting stopped. */
    stop: Callback;
    /** Called when the order changed. */
    update: Callback;
}

/** The `ui` argument of the sortable callbacks. */
export interface SortableUi {
    /** The element that moves with the pointer. */
    helper: JQuery | null;
    /** The element that marks the new position of the item. */
    placeholder: JQuery;
    /** The CSS position of the helper. */
    position: Coordinates;
    /** The CSS position of the helper when the sorting started. */
    originalPosition: Coordinates;
    /** The offset of the helper. */
    offset: Coordinates;
    /** The sorted item. */
    item: JQuery;
    /** The sortable (or draggable) the item came from, for the events of connected sortables. */
    sender: JQuery | null;
}

/** The cached box of an item in page coordinates. */
interface ItemBox {
    /** The item. */
    el: HTMLElement;
    /** The left offset of the border box. */
    left: number;
    /** The top offset of the border box. */
    top: number;
    /** The outer width. */
    width: number;
    /** The outer height. */
    height: number;
}

/** The default options, the same as in jQuery UI. */
const DEFAULTS: SortableOptions = {
    disabled: false,
    distance: 1,
    appendTo: 'parent',
    axis: false,
    cancel: 'input,textarea,button,select,option',
    connectWith: false,
    cursor: 'auto',
    forcePlaceholderSize: false,
    handle: false,
    helper: 'original',
    items: '> *',
    opacity: false,
    placeholder: false,
    revert: false,
    scroll: true,
    scope: 'default',
    zIndex: 1000,
    activate: null,
    beforeStop: null,
    change: null,
    deactivate: null,
    out: null,
    over: null,
    receive: null,
    remove: null,
    sort: null,
    start: null,
    stop: null,
    update: null
};

/**
 * Whether the items are laid out horizontally, like `_isFloating` of jQuery UI, plus the items of a flex row.
 * @param item An item.
 * @returns `true` if the items are compared by their left / right halves.
 */
function isFloating(item: JQuery): boolean {
    const parent = item.parent();
    return /left|right/.test(item.css('float')) || /inline|table-cell/.test(item.css('display'))
        || (/flex/.test(parent.css('display')) && /row/.test(parent.css('flexDirection')));
}

/**
 * Makes the items of an element sortable by dragging them, also into connected sortables.
 */
export class Sortable extends PointerInteraction<SortableOptions> implements DragSource {
    /** The element that moves with the pointer, from the start of the sorting until the end of the revert animation. */
    helper: JQuery | null = null;
    /** The element that marks the new position of the item. */
    placeholder: JQuery = $();
    /** The sorted item. */
    currentItem: JQuery = $();
    /** The offset of the helper. */
    offset: Coordinates = { left: 0, top: 0 };
    /** The CSS position of the helper. */
    position: Coordinates = { left: 0, top: 0 };
    /** The CSS position of the helper when the sorting started. */
    originalPosition: Coordinates = { left: 0, top: 0 };

    /** The pointer position relative to the item when the sorting started. */
    private clickOffset: Coordinates = { left: 0, top: 0 };
    /** The offset of the helper minus its CSS position. */
    private delta: Coordinates = { left: 0, top: 0 };
    /** The position of the item before the sorting, to detect a change and to restore it on cancel. */
    private domPosition: { prev: Element | undefined; parent: Element | undefined } = { prev: undefined, parent: undefined };
    /** This and the connected sortables the item can be moved to. */
    private containers: Sortable[] = [];
    /** The sortable the item is sorted in right now. */
    private currentContainer: Sortable = this;
    /** The inline style of the item, restored after the sorting. */
    private storedStyle: string = '';
    /** The scroll parent of the placeholder, `null` for the window. */
    private scrollContainer: HTMLElement | null = null;
    /** Watches for added items, so they get the handle class. */
    private readonly observer: MutationObserver;
    /** Whether the sorting is finishing right now, the final events are triggered. */
    private clearing: boolean = false;
    /** Whether the dragged item is over this sortable (as a container of a sorting). */
    private isOver: boolean = false;
    /** The boxes of the items while an item is dragged over this sortable, measured again when the layout changes. */
    private itemCache: ItemBox[] | null = null;
    /** Whether the items are laid out horizontally, determined with the item boxes. */
    private floating: boolean = false;
    /** The draggable dragged over this sortable with `connectToSortable`. */
    private external: { source: DragSource; placeholder: JQuery } | null = null;

    /**
     * Makes the items of the element sortable.
     * @param element The element.
     * @param options The options.
     */
    constructor(element: HTMLElement, options?: Partial<SortableOptions>) {
        super(element, 'sortable', 'sort', DEFAULTS, options);
        this.addClasses();
        this.updateHandles();
        this.bindPointer();
        // items added later (also by a sorting or a connectToSortable drop) get the handle class too, its
        // touch-action: none is needed before a touch starts
        this.observer = new MutationObserver(() => {
            if (!this.isActive() && !this.helper) {
                this.updateHandles();
            }
        });
        this.observer.observe(element, { childList: true });
    }

    /** Removes the handle classes; a sorting in progress ends at the original position and a running revert finishes. */
    override destroy(): void {
        this.observer.disconnect();
        // only a sorting in progress or a revert is cancelled, not the last sorting; destroyed in a final callback
        // (e.g. by an AJAX update in stop) the sorting finishes as it is
        if (!this.clearing && (this.isActive() || this.helper)) {
            this.cancel();
        }
        // the revert started by the cancel
        this.helper?.stop(true, true);
        this.externalOut();
        this.handleElements().removeClass('ui-sortable-handle');
        super.destroy();
    }

    /**
     * Sets an option, new items or a new handle update the handle classes, e.g. when a widget initializes the sortable
     * again after an AJAX update.
     * @param key The name of the option.
     * @param value The new value.
     */
    protected override setOption(key: string, value: unknown): void {
        const handles = key === 'handle' || key === 'items';
        if (handles) {
            this.handleElements().removeClass('ui-sortable-handle');
        }
        super.setOption(key, value);
        if (handles) {
            this.updateHandles();
        }
    }

    /**
     * The sortable items.
     * @returns The items, without the placeholder and the helper.
     */
    getItems(): JQuery {
        return this.element.find(this.options.items).not('.ui-sortable-placeholder, .ui-sortable-helper');
    }

    /**
     * The elements on which the sorting starts.
     * @returns The elements of the `handle` option in the items, or the items.
     */
    private handleElements(): JQuery {
        const items = this.getItems();
        return this.options.handle ? items.find(this.options.handle) : items;
    }

    /** Marks the handles, which disables touch scrolling on them (CSS). */
    private updateHandles(): void {
        this.handleElements().addClass('ui-sortable-handle');
    }

    /** Updates the CSS classes of the items, e.g. after items were added. */
    refresh(): void {
        this.updateHandles();
    }

    /**
     * The dragged element, for the droppables.
     * @returns The current item.
     */
    item(): JQuery {
        return this.currentItem;
    }

    /**
     * The scope of the sorting, only droppables with the same scope are activated.
     * @returns The `scope` option.
     */
    scope(): string {
        return this.options.scope;
    }

    /**
     * Creates the `ui` argument of the callbacks.
     * @param sender The sortable the item comes from, for the events of the connected sortables.
     * @returns The `ui` argument.
     */
    ui(sender?: Sortable): SortableUi {
        return {
            helper: this.helper,
            placeholder: this.placeholder,
            position: this.position,
            originalPosition: this.originalPosition,
            offset: this.offset,
            item: this.currentItem,
            sender: sender ? sender.element : null
        };
    }

    /**
     * Starts on an item (or its handle), not on a `cancel` element and not during the revert animation.
     * @param event The pointerdown event.
     * @returns Whether the sorting can start.
     */
    protected capture(event: JQuery.TriggeredEvent): boolean {
        if (this.helper) {
            return false;
        }
        const item = $(event.target).closest(this.getItems());
        if (!item.length || this.isCancel(event, this.options.cancel)) {
            return false;
        }
        if (this.options.handle && !$(event.target).closest(item.find(this.options.handle)).length) {
            return false;
        }
        this.currentItem = item;
        return true;
    }

    /**
     * Creates the placeholder: an element of the same type with the `placeholder` class (like jQuery UI the classes of
     * the item without that option), with cells for a table row.
     * @param item The sorted item.
     * @returns The placeholder, not inserted yet.
     */
    private createPlaceholder(item: JQuery): JQuery {
        const o = this.options;
        const nodeName = item[0]!.nodeName.toLowerCase();
        const placeholder = $(document.createElement(nodeName)).addClass((o.placeholder || item[0]!.className) + ' ui-sortable-placeholder')
            .removeClass('ui-sortable-helper');

        if (nodeName === 'tr') {
            item.children().each((_, cell) => {
                $('<td>&#160;</td>').attr('colspan', $(cell).attr('colspan') || '1').appendTo(placeholder);
            });
        }
        else if (nodeName === 'img') {
            placeholder.attr('src', item.attr('src') ?? '');
        }
        if (!o.placeholder) {
            placeholder.css('visibility', 'hidden');
        }
        if (!o.placeholder || o.forcePlaceholderSize) {
            // the border box of the item: the placeholder is not inserted yet, jQuery can't detect its box-sizing there
            placeholder.css({ boxSizing: 'border-box', height: item.outerHeight() ?? 0 });
            // like jQuery UI only a placeholder without a width of its own (floating, inline, flex row) gets the width
            // of the item, a block placeholder fills the container it is moved to
            if (nodeName !== 'tr' && isFloating(item)) {
                placeholder.css('width', item.outerWidth() ?? 0);
            }
        }
        return placeholder;
    }

    /**
     * Inserts the placeholder, creates the helper, calls `start` and activates the connected sortables and the
     * droppables, like jQuery UI.
     * @param event The pointerdown event.
     * @returns Always `true`.
     */
    protected start(event: JQuery.TriggeredEvent): boolean {
        const o = this.options;
        const item = this.currentItem;
        const itemOffset = offsetOf(item);

        this.domPosition = { prev: item.prev()[0], parent: item.parent()[0] };
        this.clickOffset = { left: pageX(event) - itemOffset.left, top: pageY(event) - itemOffset.top };
        // measured before the placeholder is inserted, it may add a scrollbar to the container
        const itemWidth = item.width() ?? 0;
        const itemHeight = item.height() ?? 0;
        this.placeholder = this.createPlaceholder(item).insertAfter(item);

        const helper = this.helper = this.createHelper(event, item, item);
        if (helper[0] === item[0]) {
            this.storedStyle = item.attr('style') ?? '';
            // the setters of jQuery respect the box-sizing, unlike css()
            item.width(itemWidth).height(itemHeight);
        }
        else {
            // a helper without an inline size gets the size of the item
            if (!(helper[0] as HTMLElement).style.width) {
                helper.width(itemWidth);
            }
            if (!(helper[0] as HTMLElement).style.height) {
                helper.height(itemHeight);
            }
            item.hide();
        }
        helper.css('position', 'absolute').addClass('ui-sortable-helper');
        helper.offset(itemOffset);
        this.offset = itemOffset;
        this.position = cssPosition(helper);
        this.originalPosition = { ...this.position };
        this.delta = { left: itemOffset.left - this.position.left, top: itemOffset.top - this.position.top };

        if (o.zIndex !== false) {
            helper.css('zIndex', o.zIndex);
        }
        if (o.opacity !== false) {
            helper.css('opacity', o.opacity);
        }
        if (o.cursor && o.cursor !== 'auto') {
            this.setCursor(o.cursor);
        }

        this.containers = [this];
        if (o.connectWith) {
            $(o.connectWith).each((_, el) => {
                const sortable = $.data(el, dataKey('sortable')) as Sortable | undefined;
                // a sortable inside the item can't take the item
                if (sortable && sortable !== this && !sortable.options.disabled && !$.contains(item[0]!, el)) {
                    this.containers.push(sortable);
                }
            });
        }
        this.currentContainer = this;
        this.invalidate();
        this.scrollContainer = scrollParent(this.placeholder);
        // the item boxes change when a container scrolls, e.g. by the mouse wheel
        document.addEventListener('scroll', this.onScroll, true);

        this.trigger('start', event, this.ui());
        for (const container of this.containers) {
            container.trigger('activate', event, this.ui(this));
        }
        DragDropManager.start(this, event);
        return true;
    }

    /** Discards the cached item boxes when the document or a container scrolls during the sorting. */
    private readonly onScroll = (): void => this.invalidate();

    /** Discards the cached item boxes of all containers, e.g. after the placeholder moved or a container scrolled. */
    private invalidate(): void {
        for (const container of this.containers) {
            container.itemCache = null;
        }
    }

    /**
     * Moves the helper to the pointer and the placeholder to the item under the pointer, and triggers `over`, `out`,
     * `change` and `sort` like jQuery UI.
     * @param event The pointermove event.
     */
    protected move(event: JQuery.TriggeredEvent): void {
        const o = this.options;
        if (o.scroll && this.autoScroll(event, this.scrollContainer)) {
            this.invalidate();
        }
        // the helper moves when its container scrolls
        this.delta = positionDelta(this.helper!);
        let left = pageX(event) - this.clickOffset.left;
        let top = pageY(event) - this.clickOffset.top;
        // like jQuery UI the helper keeps its position on the other axis, also in a connected container
        if (o.axis === 'y') {
            left = this.originalPosition.left + this.delta.left;
        }
        if (o.axis === 'x') {
            top = this.originalPosition.top + this.delta.top;
        }
        this.offset = { left, top };
        this.position = { left: left - this.delta.left, top: top - this.delta.top };
        this.helper!.css({ left: this.position.left + 'px', top: this.position.top + 'px' });

        // the containers under the pointer, the innermost one takes the placeholder
        const containing = this.containers.filter((c) => c.contains(event));
        const container = containing.sort((a, b) => $.contains(a.element[0]!, b.element[0]!) ? 1 : -1)[0];
        for (const c of this.containers) {
            if (c.isOver && !containing.includes(c)) {
                c.isOver = false;
                c.trigger('out', event, this.ui(this));
            }
        }
        const entering = !!container && container !== this.currentContainer;
        if (container) {
            this.currentContainer = container;
        }
        if (this.currentContainer.movePlaceholder(this.placeholder, this.currentItem, event)) {
            this.invalidate();
            this.trigger('change', event, this.ui());
            if (entering) {
                this.currentContainer.trigger('change', event, this.ui(this));
                this.scrollContainer = scrollParent(this.placeholder);
            }
        }
        if (container && !container.isOver) {
            container.isOver = true;
            container.trigger('over', event, this.ui(this));
        }

        DragDropManager.drag(this, event);
        this.trigger('sort', event, this.ui());
    }

    /**
     * Whether the pointer is inside the element; with the `axis` option only that coordinate counts, like jQuery UI.
     * @param event A pointer event.
     * @returns `true` if the element is visible and the pointer is inside its border box.
     */
    private contains(event: JQuery.TriggeredEvent): boolean {
        const offset = offsetOf(this.element);
        const x = pageX(event);
        const y = pageY(event);
        const insideX = this.options.axis === 'y' || (x >= offset.left && x <= offset.left + (this.element.outerWidth() ?? 0));
        const insideY = this.options.axis === 'x' || (y >= offset.top && y <= offset.top + (this.element.outerHeight() ?? 0));
        return this.element.is(':visible') && insideX && insideY;
    }

    /**
     * The boxes of the visible items, measured once and cached until the layout changes.
     * @param current The dragged item, which is skipped together with the items inside it.
     * @returns The boxes in page coordinates.
     */
    private itemBoxes(current: JQuery): ItemBox[] {
        if (!this.itemCache) {
            const items = this.getItems().filter((_, el) => el !== current[0] && !$.contains(current[0]!, el)).filter(':visible');
            this.floating = this.options.axis === 'x' || isFloating(items.length ? items.first() : current);
            this.itemCache = items.get().map((el) => {
                const jq = $(el);
                const offset = offsetOf(jq);
                return { el, left: offset.left, top: offset.top, width: jq.outerWidth() ?? 0, height: jq.outerHeight() ?? 0 };
            });
        }
        return this.itemCache;
    }

    /**
     * Moves the placeholder before or after the item under the pointer (by the left / right halves for horizontal
     * items, else by the top / bottom halves); with the `axis` option only that coordinate counts, like jQuery UI.
     * @param placeholder The placeholder.
     * @param current The dragged item.
     * @param event The pointermove event.
     * @returns Whether the placeholder moved.
     */
    private movePlaceholder(placeholder: JQuery, current: JQuery, event: JQuery.TriggeredEvent): boolean {
        const x = pageX(event);
        const y = pageY(event);
        const boxes = this.itemBoxes(current);
        const before = (box: ItemBox) => this.floating ? x < box.left + box.width / 2 : y < box.top + box.height / 2;

        const axis = this.options.axis;
        for (const box of boxes) {
            if ((axis !== 'y' && (x < box.left || x > box.left + box.width))
                    || (axis !== 'x' && (y < box.top || y > box.top + box.height))) {
                continue;
            }
            const item = $(box.el);
            if (before(box) ? item.prev()[0] === placeholder[0] : item.next()[0] === placeholder[0]) {
                return false;
            }
            if (before(box)) {
                placeholder.insertBefore(item);
            }
            else {
                placeholder.insertAfter(item);
            }
            return true;
        }

        if (this.element[0]!.contains(placeholder[0]!)) {
            return false;
        }
        // entering this container: next to the closest item, an empty container takes the placeholder
        const closest = boxes.map((box) => ({ box, distance: this.distance(box, x, y) })).sort((a, b) => a.distance - b.distance)[0];
        if (closest) {
            if (before(closest.box)) {
                placeholder.insertBefore(closest.box.el);
            }
            else {
                placeholder.insertAfter(closest.box.el);
            }
        }
        else {
            this.element.append(placeholder);
        }
        return true;
    }

    /**
     * The squared distance of the pointer to a box.
     * @param box The box of an item.
     * @param x The page x coordinate of the pointer.
     * @param y The page y coordinate of the pointer.
     * @returns `0` if the pointer is inside the box.
     */
    private distance(box: ItemBox, x: number, y: number): number {
        const dx = Math.max(box.left - x, 0, x - box.left - box.width);
        const dy = Math.max(box.top - y, 0, y - box.top - box.height);
        return dx * dx + dy * dy;
    }

    /**
     * Drops the item on the droppables, then animates the helper to the placeholder if `revert` is set.
     * @param event The pointerup or pointercancel event.
     */
    protected stop(event: JQuery.TriggeredEvent): void {
        if (!this.aborted) {
            DragDropManager.drop(this, event);
        }
        // like jQuery UI the droppables are deactivated at the drop, before the revert; the drag stays current until
        // the sorting is cleared (e.g. for the cancel of an AJAX update)
        if (DragDropManager.current === this) {
            DragDropManager.deactivate(this, event);
        }
        const revert = this.options.revert;
        if (revert && this.helper) {
            this.delta = positionDelta(this.helper);
            const target = offsetOf(this.placeholder);
            this.helper.animate({ left: target.left - this.delta.left, top: target.top - this.delta.top },
                typeof revert === 'number' ? revert : 500, () => this.clear(event));
        }
        else {
            this.clear(event);
        }
    }

    /**
     * The browser took over the pointer, or the sorting is cancelled or destroyed: the item goes back to its original
     * position.
     * @param e The pointercancel event.
     */
    protected override abort(e: JQuery.TriggeredEvent): void {
        this.restore(this.placeholder);
        this.currentContainer = this;
        super.abort(e);
    }

    /**
     * Aborts the sorting, the item goes back to its original position. Like jQuery UI, this also works during the
     * revert animation, in the callbacks at the end of the sorting (e.g. `receive`, `update` or `stop`) and after
     * the sorting, e.g. to undo a reorder the server rejected; then no events are triggered.
     */
    cancel(): void {
        if (this.clearing) {
            this.restore(this.currentItem);
            return;
        }
        if (!this.isActive()) {
            if (this.helper) {
                // reverting: the item goes back instead of to the placeholder when the animation finishes
                this.restore(this.placeholder);
                this.helper.stop(true, true);
            }
            else if (this.currentItem[0]?.isConnected) {
                // after the sorting: the last sorted item goes back
                this.restore(this.currentItem);
            }
            return;
        }
        this.abort(this.cancelEvent());
    }

    /**
     * Moves the given element (the item or the placeholder) back to the original position of the item.
     * @param element The element to move.
     */
    private restore(element: JQuery): void {
        const { prev, parent } = this.domPosition;
        if (prev) {
            element.insertAfter(prev);
        }
        else if (parent) {
            $(parent).prepend(element);
        }
    }

    /**
     * Moves the item to the placeholder, removes the helper and the placeholder and triggers the final events like
     * jQuery UI: `beforeStop`, `update`, `remove` / `receive` / `update` of the receiver, `deactivate` / `out` of the
     * containers and `stop`.
     * @param event The event that ended the sorting.
     */
    private clear(event: JQuery.TriggeredEvent | null): void {
        this.clearing = true;
        try {
            this.clearItem(event);
        }
        finally {
            this.clearing = false;
        }
    }

    /**
     * The work of {@link clear}, during which {@link cancel} moves the item back.
     * @param event The event that ended the sorting.
     */
    private clearItem(event: JQuery.TriggeredEvent | null): void {
        const item = this.currentItem;
        const sender = this;
        // the innermost container holding the placeholder
        const receiver = this.containers.filter((c) => c.element[0]!.contains(this.placeholder[0]!))
            .sort((a, b) => $.contains(a.element[0]!, b.element[0]!) ? 1 : -1)[0] ?? this;

        // like jQuery UI, not if a callback (e.g. drop of a droppable) removed the item
        if (item.parent().length) {
            item.insertBefore(this.placeholder);
        }
        // like jQuery UI, the final events are determined before beforeStop, which may cancel the sorting
        const updated = this.domPosition.prev !== item.prev()[0] || this.domPosition.parent !== item.parent()[0];
        this.trigger('beforeStop', event, this.ui());

        if (this.helper && this.helper[0] === item[0]) {
            item.attr('style', this.storedStyle).removeClass('ui-sortable-helper');
            if (!this.storedStyle) {
                item.removeAttr('style');
            }
        }
        else {
            this.helper?.remove();
            item.show();
        }
        this.placeholder.remove();
        this.helper = null;

        this.restoreCursor();
        if (DragDropManager.current === this) {
            DragDropManager.stop(this, event);
        }

        if (updated) {
            this.trigger('update', event, this.ui());
        }
        if (receiver !== sender) {
            this.trigger('remove', event, this.ui());
            receiver.trigger('receive', event, this.ui(sender));
            receiver.trigger('update', event, this.ui(sender));
        }
        for (const container of this.containers) {
            container.trigger('deactivate', event, this.ui(this));
            if (container.isOver) {
                container.isOver = false;
                container.trigger('out', event, this.ui(this));
            }
        }
        this.trigger('stop', event, this.ui());

        document.removeEventListener('scroll', this.onScroll, true);
        this.invalidate();
        this.containers = [];
        this.currentContainer = this;
    }

    // ==========================
    // === connectToSortable ===
    // ==========================

    /**
     * A draggable with `connectToSortable` moves over this sortable: a placeholder shows where it would be inserted.
     * @param source The draggable.
     * @param event The pointermove event.
     */
    externalDrag(source: DragSource, event: JQuery.TriggeredEvent): void {
        if (this.options.disabled) {
            return;
        }
        if (!this.contains(event)) {
            this.externalOut();
            return;
        }
        if (!this.external) {
            this.external = { source, placeholder: this.createPlaceholder(source.item()) };
            this.itemCache = null;
            this.trigger('over', event, { item: source.item(), helper: source.helper, sender: source.item() });
        }
        if (this.movePlaceholder(this.external.placeholder, source.item(), event)) {
            this.itemCache = null;
            this.trigger('change', event, { item: source.item(), placeholder: this.external.placeholder });
        }
    }

    /** The draggable left this sortable or the drag ended, the placeholder is removed. */
    externalOut(): void {
        if (this.external) {
            this.external.placeholder.remove();
            this.external = null;
            this.itemCache = null;
        }
    }

    /**
     * A draggable with `connectToSortable` is dropped, the element (helper `original`) or a clone of it is inserted at
     * the placeholder. Like jQuery UI, only `receive` gets the draggable as `sender`.
     * @param source The draggable.
     * @param event The pointerup event.
     * @returns Whether it was dropped on this sortable.
     */
    externalDrop(source: DragSource, event: JQuery.TriggeredEvent): boolean {
        if (!this.external || !this.external.placeholder.parent().length) {
            this.externalOut();
            return false;
        }
        const original = source.helper?.[0] === source.item()[0];
        const item = (original ? source.item() : source.item().clone().removeAttr('id').removeClass('ui-draggable'))
            .removeClass('ui-draggable-dragging').css({ left: '', top: '', position: '' });
        item.insertBefore(this.external.placeholder);
        this.externalOut();

        const ui = { helper: null, placeholder: $(), position: source.position, originalPosition: source.position, offset: source.offset, item, sender: null };
        this.trigger('receive', event, { ...ui, sender: source.item() });
        this.trigger('update', event, ui);
        return true;
    }
}
