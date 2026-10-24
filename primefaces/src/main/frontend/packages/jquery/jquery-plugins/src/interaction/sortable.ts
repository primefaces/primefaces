import { cssPosition, Interaction, offsetOf, type Callback, type Coordinates, type InteractionOptions, pageX, pageY } from "./interaction.js";
import { DragDropManager, type DragSource } from "./droppable.js";

export interface SortableOptions extends InteractionOptions {
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
    /** Whether the helper moves to its new place, or the duration of that animation. */
    revert: boolean | number;
    /** z-index of the helper while sorting. */
    zIndex: number | false;
    activate: Callback;
    beforeStop: Callback;
    change: Callback;
    deactivate: Callback;
    out: Callback;
    over: Callback;
    receive: Callback;
    remove: Callback;
    sort: Callback;
    start: Callback;
    stop: Callback;
    update: Callback;
}

export interface SortableUi {
    helper: JQuery | null;
    placeholder: JQuery;
    position: Coordinates;
    originalPosition: Coordinates;
    offset: Coordinates;
    item: JQuery;
    sender: JQuery | null;
}

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
 * Makes the items of an element sortable by dragging them, also into connected sortables.
 */
export class Sortable extends Interaction<SortableOptions> implements DragSource {
    helper: JQuery | null = null;
    placeholder: JQuery = $();
    currentItem: JQuery = $();
    offset: Coordinates = { left: 0, top: 0 };
    position: Coordinates = { left: 0, top: 0 };
    originalPosition: Coordinates = { left: 0, top: 0 };

    private clickOffset: Coordinates = { left: 0, top: 0 };
    private delta: Coordinates = { left: 0, top: 0 };
    private domPosition: { prev: Element | undefined; parent: Element | undefined } = { prev: undefined, parent: undefined };
    private containers: Sortable[] = [];
    private currentContainer: Sortable = this;
    private storedStyle: string = '';
    private storedCursor: string | null = null;
    /** The draggable dragged over this sortable with `connectToSortable`. */
    private external: { source: DragSource; placeholder: JQuery } | null = null;

    constructor(element: HTMLElement, options?: Partial<SortableOptions>) {
        super(element, 'sortable', 'sort', DEFAULTS, options);
        this.element.addClass('ui-sortable');
        this.updateHandles();
        this.bindPointer();
    }

    override destroy(): void {
        this.getItems().removeClass('ui-sortable-handle').find('.ui-sortable-handle').removeClass('ui-sortable-handle');
        super.destroy();
    }

    /** The sortable items, without the placeholder and the helper. */
    getItems(): JQuery {
        return this.element.find(this.options.items).not('.ui-sortable-placeholder, .ui-sortable-helper');
    }

    private updateHandles(): void {
        const items = this.getItems();
        (this.options.handle ? items.find(this.options.handle) : items).addClass('ui-sortable-handle');
    }

    /** Updates the CSS classes of the items, e.g. after items were added. */
    refresh(): void {
        this.updateHandles();
    }

    item(): JQuery {
        return this.currentItem;
    }

    scope(): string {
        return 'default';
    }

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

    protected capture(event: JQuery.TriggeredEvent): boolean {
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

    private createHelper(event: JQuery.TriggeredEvent): JQuery {
        const o = this.options;
        const item = this.currentItem;
        const helper = typeof o.helper === 'function' ? $(o.helper.call(this.element[0]!, event, item))
            : o.helper === 'clone' ? item.clone() : item;

        if (!helper.parents('body').length) {
            helper.appendTo(o.appendTo === 'parent' ? item.parent() : $(o.appendTo as string));
        }
        if (helper[0] !== item[0]) {
            if (!(helper[0] as HTMLElement).style.width) {
                helper.width(item.width() ?? 0);
            }
            if (!(helper[0] as HTMLElement).style.height) {
                helper.height(item.height() ?? 0);
            }
        }
        return helper as JQuery;
    }

    private createPlaceholder(item: JQuery): JQuery {
        const o = this.options;
        const nodeName = item[0]!.nodeName.toLowerCase();
        const placeholder = $(document.createElement(nodeName)).addClass((o.placeholder || '') + ' ui-sortable-placeholder');

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
            placeholder.height((item.innerHeight() ?? 0) - (parseFloat(item.css('paddingTop')) || 0) - (parseFloat(item.css('paddingBottom')) || 0));
            if (nodeName !== 'tr') {
                placeholder.width((item.innerWidth() ?? 0) - (parseFloat(item.css('paddingLeft')) || 0) - (parseFloat(item.css('paddingRight')) || 0));
            }
        }
        return placeholder;
    }

    protected start(event: JQuery.TriggeredEvent): boolean {
        const o = this.options;
        const item = this.currentItem;
        const itemOffset = offsetOf(item);

        this.domPosition = { prev: item.prev()[0], parent: item.parent()[0] };
        this.clickOffset = { left: pageX(event) - itemOffset.left, top: pageY(event) - itemOffset.top };
        this.placeholder = this.createPlaceholder(item).insertAfter(item);

        const helper = this.helper = this.createHelper(event);
        if (helper[0] === item[0]) {
            this.storedStyle = item.attr('style') ?? '';
            item.css({ width: item.width() ?? 0, height: item.height() ?? 0 });
        }
        else {
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
            this.storedCursor = document.body.style.cursor;
            document.body.style.cursor = o.cursor;
        }

        this.containers = [this];
        if (o.connectWith) {
            $(o.connectWith).each((_, el) => {
                const sortable = $.data(el, 'pf-sortable') as Sortable | undefined;
                if (sortable && sortable !== this && !sortable.options.disabled) {
                    this.containers.push(sortable);
                }
            });
        }
        this.currentContainer = this;
        DragDropManager.current = this;

        this.trigger('start', event, this.ui());
        for (const container of this.containers) {
            container.trigger('activate', event, this.ui(this));
        }
        return true;
    }

    protected move(event: JQuery.TriggeredEvent): void {
        const o = this.options;
        if (o.scroll) {
            // the sortable itself may scroll
            this.autoScroll(event, this.currentContainer.element[0]!);
        }
        let left = pageX(event) - this.clickOffset.left;
        let top = pageY(event) - this.clickOffset.top;
        const itemOffset = offsetOf(this.placeholder);
        if (o.axis === 'y') {
            left = itemOffset.left;
        }
        if (o.axis === 'x') {
            top = itemOffset.top;
        }
        this.offset = { left, top };
        this.position = { left: left - this.delta.left, top: top - this.delta.top };
        this.helper!.css({ left: this.position.left + 'px', top: this.position.top + 'px' });

        // the innermost container under the pointer
        const container = this.containers.filter((c) => c.contains(event)).sort((a, b) => $.contains(a.element[0]!, b.element[0]!) ? 1 : -1)[0];
        if (container && container !== this.currentContainer) {
            this.currentContainer.trigger('out', event, this.ui(this));
            this.currentContainer = container;
            container.trigger('over', event, this.ui(this));
        }

        if (this.currentContainer.movePlaceholder(this.placeholder, this.currentItem, event)) {
            this.currentContainer.trigger('change', event, this.ui(this));
        }
        this.trigger('sort', event, this.ui());
    }

    /** Whether the pointer is inside the element. */
    private contains(event: JQuery.TriggeredEvent): boolean {
        const offset = offsetOf(this.element);
        const x = pageX(event);
        const y = pageY(event);
        return this.element.is(':visible') && x >= offset.left && x <= offset.left + (this.element.outerWidth() ?? 0)
            && y >= offset.top && y <= offset.top + (this.element.outerHeight() ?? 0);
    }

    /**
     * Moves the placeholder before or after the item under the pointer.
     * @return Whether the placeholder moved.
     */
    private movePlaceholder(placeholder: JQuery, current: JQuery, event: JQuery.TriggeredEvent): boolean {
        const x = pageX(event);
        const y = pageY(event);
        const items = this.getItems().not(current).filter(':visible');

        for (const el of items.get()) {
            const item = $(el);
            const offset = offsetOf(item);
            const width = item.outerWidth() ?? 0;
            const height = item.outerHeight() ?? 0;
            if (x < offset.left || x > offset.left + width || y < offset.top || y > offset.top + height) {
                continue;
            }
            const horizontal = this.options.axis === 'x';
            const before = horizontal ? x < offset.left + width / 2 : y < offset.top + height / 2;
            if (before ? item.prev()[0] === placeholder[0] : item.next()[0] === placeholder[0]) {
                return false;
            }
            if (before) {
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
        const closest = items.get().map((el) => ({ el, distance: this.distance(el, x, y) })).sort((a, b) => a.distance - b.distance)[0];
        if (closest) {
            const item = $(closest.el);
            const offset = offsetOf(item);
            const after = this.options.axis === 'x' ? x > offset.left + (item.outerWidth() ?? 0) / 2 : y > offset.top + (item.outerHeight() ?? 0) / 2;
            if (after) {
                placeholder.insertAfter(item);
            }
            else {
                placeholder.insertBefore(item);
            }
        }
        else {
            this.element.append(placeholder);
        }
        return true;
    }

    private distance(el: HTMLElement, x: number, y: number): number {
        const r = el.getBoundingClientRect();
        const dx = Math.max(r.left + window.scrollX - x, 0, x - r.right - window.scrollX);
        const dy = Math.max(r.top + window.scrollY - y, 0, y - r.bottom - window.scrollY);
        return dx * dx + dy * dy;
    }

    protected stop(event: JQuery.TriggeredEvent): void {
        const revert = this.options.revert;
        if (revert && this.helper) {
            const target = offsetOf(this.placeholder);
            this.helper.animate({ left: target.left - this.delta.left, top: target.top - this.delta.top },
                typeof revert === 'number' ? revert : 500, () => this.clear(event));
        }
        else {
            this.clear(event);
        }
    }

    protected override abort(): void {
        this.cancel();
    }

    /** Aborts the sorting, the item stays at its original position. */
    cancel(): void {
        if (!this.isActive()) {
            return;
        }
        const { prev, parent } = this.domPosition;
        if (prev) {
            this.placeholder.insertAfter(prev);
        }
        else if (parent) {
            $(parent).prepend(this.placeholder);
        }
        this.currentContainer = this;
        this.end($.Event('pointercancel') as JQuery.TriggeredEvent);
    }

    private clear(event: JQuery.TriggeredEvent | null): void {
        const item = this.currentItem;
        const sender = this;
        // the innermost container holding the placeholder
        const receiver = this.containers.filter((c) => c.element[0]!.contains(this.placeholder[0]!))
            .sort((a, b) => $.contains(a.element[0]!, b.element[0]!) ? 1 : -1)[0] ?? this;

        item.insertBefore(this.placeholder);
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

        if (this.storedCursor !== null) {
            document.body.style.cursor = this.storedCursor;
            this.storedCursor = null;
        }
        if (DragDropManager.current === this) {
            DragDropManager.current = null;
        }

        if (this.domPosition.prev !== item.prev()[0] || this.domPosition.parent !== item.parent()[0]) {
            this.trigger('update', event, this.ui());
        }
        if (receiver !== sender) {
            this.trigger('remove', event, this.ui());
            receiver.trigger('receive', event, this.ui(sender));
            receiver.trigger('update', event, this.ui(sender));
        }
        for (const container of this.containers) {
            container.trigger('deactivate', event, this.ui(this));
        }
        this.trigger('stop', event, this.ui());

        this.containers = [];
        this.currentContainer = this;
        this.updateHandles();
        receiver.updateHandles();
    }

    // ==========================
    // === connectToSortable ===
    // ==========================

    /** A draggable with `connectToSortable` moves over this sortable. */
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
            this.trigger('over', event, { item: source.item(), helper: source.helper, sender: source.item() });
        }
        if (this.movePlaceholder(this.external.placeholder, source.item(), event)) {
            this.trigger('change', event, { item: source.item(), placeholder: this.external.placeholder });
        }
    }

    /** The draggable left this sortable or the drag ended. */
    externalOut(): void {
        if (this.external) {
            this.external.placeholder.remove();
            this.external = null;
        }
    }

    /**
     * A draggable with `connectToSortable` is dropped, the element (helper `original`) or a clone of it is inserted at
     * the placeholder.
     * @return Whether it was dropped on this sortable.
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

        const ui = { helper: null, placeholder: $(), position: source.position, originalPosition: source.position, offset: source.offset, item, sender: source.item() };
        this.trigger('receive', event, ui);
        this.trigger('update', event, ui);
        this.updateHandles();
        return true;
    }
}
