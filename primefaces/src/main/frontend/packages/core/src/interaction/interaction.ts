/*
 * Drag and drop, sorting and resizing with pointer events, replacing the jQuery UI interactions. The options, the
 * callbacks, the `ui` arguments and the events are compatible with jQuery UI as far as PrimeFaces uses them; the CSS
 * classes have the `p-` prefix (`p-draggable`, `p-sortable-helper`, ...).
 */

/** A position in pixels, e.g. an offset in page coordinates or a CSS `left` / `top`. */
export type Coordinates = { left: number; top: number };
/** A size in pixels. */
export type Size = { width: number; height: number };
/** A callback option like `start` or `stop`, called with the element as `this`, the event and the `ui` argument. */
export type Callback = ((this: HTMLElement, event: JQuery.TriggeredEvent, ui: any) => unknown) | null | undefined;

/** The options all interactions have. */
export interface InteractionOptions {
    /** Whether the interaction is disabled. */
    disabled: boolean;
    /** The options of the subclass. */
    [option: string]: unknown;
}

/** The options of the interactions started with the pointer: draggable, sortable and resizable. */
export interface PointerInteractionOptions extends InteractionOptions {
    /** Minimum distance in pixels the pointer has to move before the interaction starts. */
    distance: number;
}

/** Property set on a native pointerdown event once an (inner) interaction took it, so outer interactions ignore it. */
const HANDLED = '__pfInteractionHandled';

/** The events of the pointer that is down, listened to on the document. */
const POINTER_EVENTS = ['pointermove', 'pointerup', 'pointercancel'] as const;

/** The interaction whose pointer is down. Only one pointer interacts at a time, like with Touch Punch, which ignored multi-touch. */
let activeInteraction: PointerInteraction<PointerInteractionOptions> | null = null;

/**
 * Removes `undefined` values, so they don't override the defaults.
 * @typeParam T Type of the options.
 * @param options The options passed by the caller.
 * @returns A copy of the options without the `undefined` values.
 */
function defined<T extends object>(options: Partial<T> | undefined): Partial<T> {
    const result: Record<string, unknown> = {};
    const source = (options ?? {}) as Record<string, unknown>;
    for (const key of Object.keys(source)) {
        if (source[key] !== undefined) {
            result[key] = source[key];
        }
    }
    return result as Partial<T>;
}

/**
 * The key of the jQuery data of an element under which its interaction is stored.
 * @param name The name of the interaction, e.g. `sortable`.
 * @returns The key, e.g. `pf-sortable`.
 */
export function dataKey(name: string): string {
    return 'pf-' + name;
}

/**
 * The horizontal pointer position of an event in page coordinates.
 * @param event A pointer event.
 * @returns The page x coordinate.
 */
export function pageX(event: JQuery.TriggeredEvent): number {
    return (event.clientX ?? 0) + window.scrollX;
}

/**
 * The vertical pointer position of an event in page coordinates.
 * @param event A pointer event.
 * @returns The page y coordinate.
 */
export function pageY(event: JQuery.TriggeredEvent): number {
    return (event.clientY ?? 0) + window.scrollY;
}

/**
 * Parses a CSS pixel value like `12px`.
 * @param value The CSS value.
 * @returns The number, `0` if the value is not numeric (e.g. `auto`).
 */
export function num(value: string | undefined): number {
    return parseFloat(value ?? '') || 0;
}

/**
 * The offset of an element in page coordinates.
 * @param jq The element.
 * @returns Its offset, `0, 0` for an empty jQuery object.
 */
export function offsetOf(jq: JQuery): Coordinates {
    return jq.offset() ?? { left: 0, top: 0 };
}

/**
 * The current CSS `left` / `top` of an element, as numbers.
 * @param jq The element.
 * @returns Its CSS position.
 */
export function cssPosition(jq: JQuery): Coordinates {
    return { left: num(jq.css('left')), top: num(jq.css('top')) };
}

/**
 * The offset of an element minus its CSS position, which changes when a container of the element scrolls.
 * @param jq The element, e.g. the helper of a drag.
 * @returns The difference.
 */
export function positionDelta(jq: JQuery): Coordinates {
    const offset = offsetOf(jq);
    const css = cssPosition(jq);
    return { left: offset.left - css.left, top: offset.top - css.top };
}

/**
 * The bounds of the content box of an element in page coordinates.
 * @param jq The element.
 * @returns The bounds as `[left, top, right, bottom]`.
 */
export function contentBox(jq: JQuery): [number, number, number, number] {
    const offset = offsetOf(jq);
    const left = offset.left + num(jq.css('borderLeftWidth')) + num(jq.css('paddingLeft'));
    const top = offset.top + num(jq.css('borderTopWidth')) + num(jq.css('paddingTop'));
    return [left, top, left + (jq.width() ?? 0), top + (jq.height() ?? 0)];
}

/**
 * The closest scrollable parent of an element, like `scrollParent()` of jQuery UI: an absolute element skips its
 * static parents.
 * @param jq The element, e.g. the helper of a drag.
 * @returns The scrollable parent, `null` if there is none or the element is fixed (it scrolls with the window).
 */
export function scrollParent(jq: JQuery): HTMLElement | null {
    const position = jq.css('position');
    if (position === 'fixed') {
        return null;
    }
    return jq.parents().filter((_, el) => {
        const p = $(el);
        return !(position === 'absolute' && p.css('position') === 'static')
            && /(auto|scroll)/.test(p.css('overflow') + p.css('overflow-y') + p.css('overflow-x'));
    })[0] ?? null;
}

/**
 * Base of the interactions: options, `enable` / `disable` / `destroy`, the callbacks and the events.
 * @typeParam O Type of the options of the interaction.
 */
export abstract class Interaction<O extends InteractionOptions> {
    /** The element the interaction was created for. */
    readonly element: JQuery<HTMLElement>;
    /** The current options. */
    options: O;

    /** Name of the interaction, e.g. `draggable`. */
    readonly name: string;
    /** Prefix of the event names, e.g. `drag` for `dragstart`. */
    protected readonly eventPrefix: string;
    /** Unique event namespace of this instance. */
    protected readonly namespace: string;

    /**
     * Sets up the options and destroys the interaction when the element is removed.
     * @param element The element of the interaction.
     * @param name Name of the interaction, e.g. `draggable`.
     * @param eventPrefix Prefix of the event names, e.g. `drag`.
     * @param defaults The default options.
     * @param options The options passed by the caller.
     */
    protected constructor(element: HTMLElement, name: string, eventPrefix: string, defaults: O, options?: Partial<O>) {
        this.element = $(element);
        this.name = name;
        this.eventPrefix = eventPrefix;
        this.options = { ...defaults, ...defined(options) };
        this.namespace = '.pf' + name + Math.random().toString(36).slice(2);
        // destroyed with the element, like a jQuery UI widget
        this.element.on('remove' + this.namespace, () => this.destroy());
    }

    /**
     * The element that gets the state classes like `p-resizable-disabled`, triggers the events and is `this` in the
     * callbacks, like the widget element of jQuery UI.
     * @returns The element of the interaction.
     */
    protected widget(): JQuery<HTMLElement> {
        return this.element;
    }

    /** Adds the classes of the interaction, called by the subclass once its elements are set up. */
    protected addClasses(): void {
        this.element.addClass('p-' + this.name);
        this.widget().toggleClass('p-' + this.name + '-disabled', !!this.options.disabled);
    }

    /**
     * Gets all options, gets an option or sets one or more options.
     * @param key The name of an option or an object with the options to set, omitted to get all options.
     * @param value The value to set, omitted to get the option.
     * @returns The options or the value of the option (`null` if it is not set), `undefined` when setting.
     */
    option(key?: string | Partial<O>, value?: unknown): unknown {
        if (key === undefined) {
            return this.options;
        }
        if (typeof key === 'string') {
            if (value === undefined) {
                return this.options[key] ?? null;
            }
            this.setOption(key, value);
            return undefined;
        }
        const values = defined(key) as Record<string, unknown>;
        for (const k of Object.keys(values)) {
            this.setOption(k, values[k]);
        }
        return undefined;
    }

    /** Enables the interaction. */
    enable(): void {
        this.setOption('disabled', false);
    }

    /** Disables the interaction, one in progress continues. */
    disable(): void {
        this.setOption('disabled', true);
    }

    /** Removes the interaction from the element. */
    destroy(): void {
        this.element.off(this.namespace).removeClass('p-' + this.name);
        this.widget().removeClass('p-' + this.name + '-disabled');
        this.element.removeData(dataKey(this.name));
    }

    /**
     * Sets an option and updates the `p-*-disabled` class.
     * @param key The name of the option.
     * @param value The new value.
     */
    protected setOption(key: string, value: unknown): void {
        (this.options as Record<string, unknown>)[key] = value;
        if (key === 'disabled') {
            this.widget().toggleClass('p-' + this.name + '-disabled', !!value);
        }
    }

    /**
     * Calls the callback of the option and triggers the event (e.g. `dragstart`) on the element, both with the
     * `ui` argument. The event bubbles like with jQuery UI (also `resize`, up to the window) and has the properties of
     * the native event, e.g. `pageX`.
     * @param type The name of the callback, e.g. `start`.
     * @param event The pointer event that caused it, if any.
     * @param ui The `ui` argument.
     * @returns `false` if the callback returned `false` or the default of the event was prevented.
     */
    trigger(type: string, event: JQuery.TriggeredEvent | null, ui: unknown): boolean {
        const name = (type === this.eventPrefix ? type : this.eventPrefix + type).toLowerCase();
        const widget = this.widget();
        const jqEvent = $.Event(name) as JQuery.TriggeredEvent;
        if (event?.originalEvent) {
            // jQuery reads pageX, clientX, ... from the original event
            jqEvent.originalEvent = event.originalEvent;
        }
        widget.trigger(jqEvent, [ui]);

        const callback = this.options[type] as Callback;
        const result = typeof callback === 'function' ? callback.call(widget[0]!, jqEvent, ui) : undefined;
        return result !== false && !jqEvent.isDefaultPrevented();
    }
}

/**
 * Base of the interactions started with the pointer (draggable, sortable, resizable): starts the interaction after
 * the pointer moved the minimum distance and follows that pointer until it is released.
 * @typeParam O Type of the options of the interaction.
 */
export abstract class PointerInteraction<O extends PointerInteractionOptions> extends Interaction<O> {
    /** Whether the interaction was aborted (pointercancel, cancelled or destroyed by code), it ends without a drop. */
    protected aborted: boolean = false;
    /** The pointerdown event of the pointer that is down. */
    private startEvent: JQuery.TriggeredEvent | null = null;
    /** The last event of the pointer that is down, for the position of an interaction cancelled by code. */
    private lastEvent: JQuery.TriggeredEvent | null = null;
    /** The id of the pointer that is down. */
    private pointerId: number | null = null;
    /** The element that captured the pointer while the interaction is in progress. */
    private captureElement: Element | null = null;
    /** The inline cursor of the body while the interaction sets its own, restored afterwards. */
    private storedCursor: string | null = null;
    /**
     * Prevents the native drag of images and links, which would cancel the pointer events.
     * @param e The native `dragstart` event.
     */
    private readonly preventNativeDrag = (e: Event): void => e.preventDefault();

    /**
     * Handles the pointermove, pointerup and pointercancel events on the document while the pointer is down. Events of
     * other pointers (e.g. a second finger) are ignored and keep their default.
     * @param nativeEvent The native pointer event.
     */
    private readonly onDocumentPointer = (nativeEvent: Event): void => {
        // a jQuery event, like the handlers of the interactions get from jQuery (clientX, pageX, ... of the native event)
        const e = ($.event as unknown as { fix(event: Event): JQuery.TriggeredEvent }).fix(nativeEvent);
        if (!this.isOwnPointer(e)) {
            return;
        }
        if (nativeEvent.type === 'pointermove') {
            this.onPointerMove(e);
        }
        else if (nativeEvent.type === 'pointerup' || !this.started) {
            this.end(e);
        }
        else {
            this.abort(e);
        }
    };
    /** Whether the pointer moved far enough and the interaction started. */
    private started: boolean = false;

    /** Removes the interaction from the element, one in progress ends without a drop. */
    override destroy(): void {
        if (this.started) {
            this.abort(this.cancelEvent());
        }
        this.cleanupPointer();
        super.destroy();
    }

    /**
     * Starts listening for pointerdown on the element (or another target).
     * @param target The element on which the interaction starts.
     */
    protected bindPointer(target: JQuery = this.element): void {
        target.on('pointerdown' + this.namespace, (e) => this.onPointerDown(e));
    }

    /**
     * Whether the interaction can start for this pointerdown.
     * @param event The pointerdown event.
     * @returns `true` to start listening for the pointer moves.
     */
    protected abstract capture(event: JQuery.TriggeredEvent): boolean;

    /**
     * Starts the interaction, after the pointer moved the minimum distance.
     * @param event The pointerdown event.
     * @returns `false` to cancel.
     */
    protected abstract start(event: JQuery.TriggeredEvent): boolean;

    /**
     * Moves the interaction to the pointer.
     * @param event The pointermove event.
     */
    protected abstract move(event: JQuery.TriggeredEvent): void;

    /**
     * Finishes the interaction.
     * @param event The pointerup or pointercancel event.
     */
    protected abstract stop(event: JQuery.TriggeredEvent): void;

    /**
     * Whether the target of the event matches the `cancel` option.
     * @param event The pointerdown event.
     * @param cancel The selector of the `cancel` option.
     * @returns `true` if the interaction must not start.
     */
    protected isCancel(event: JQuery.TriggeredEvent, cancel: unknown): boolean {
        return typeof cancel === 'string' && !!cancel && $(event.target).closest(cancel).length > 0;
    }

    /**
     * Prepares the interaction when the pointer goes down on the element and listens for the moves of that pointer.
     * @param e The pointerdown event.
     */
    private onPointerDown(e: JQuery.TriggeredEvent): void {
        const event = e.originalEvent as (PointerEvent & Record<string, unknown>) | undefined;
        if (!event || event[HANDLED] || (event.pointerType === 'mouse' && event.button !== 0) || this.options.disabled) {
            return;
        }
        if (activeInteraction) {
            // another pointer, e.g. a second finger, is ignored; the same pointer again means its pointerup got lost
            if (event.pointerId !== activeInteraction.pointerId) {
                return;
            }
            activeInteraction.end(e);
        }
        if (!this.capture(e)) {
            return;
        }
        // an inner interaction takes precedence over an outer one
        event[HANDLED] = true;

        this.startEvent = e;
        this.lastEvent = e;
        this.pointerId = event.pointerId;
        activeInteraction = this as unknown as PointerInteraction<PointerInteractionOptions>;
        // in the capture phase, so an event handler that stops the propagation can't swallow the end of the drag
        for (const type of POINTER_EVENTS) {
            document.addEventListener(type, this.onDocumentPointer, true);
        }
        // no text selection while dragging
        $(document).on('selectstart' + this.namespace, (ev) => ev.preventDefault());
        // no native drag of images and links, it would cancel the pointer events (native listener, the jQuery
        // "dragstart" event of the draggable bubbles to the document too)
        document.addEventListener('dragstart', this.preventNativeDrag, true);
    }

    /**
     * Whether the event comes from the pointer that started the interaction.
     * @param e A pointer event.
     * @returns `false` for other pointers, e.g. a second finger.
     */
    private isOwnPointer(e: JQuery.TriggeredEvent): boolean {
        return (e.originalEvent as PointerEvent | undefined)?.pointerId === this.pointerId;
    }

    /**
     * The browser took over the pointer (e.g. for scrolling), or the interaction is cancelled or destroyed by code: it
     * ends without a drop.
     * @param e The pointercancel event.
     */
    protected abort(e: JQuery.TriggeredEvent): void {
        this.aborted = true;
        this.end(e);
    }

    /**
     * Starts the interaction once the pointer moved the minimum distance, then moves it.
     * @param e The pointermove event.
     */
    private onPointerMove(e: JQuery.TriggeredEvent): void {
        this.lastEvent = e;
        const pointer = e.originalEvent as PointerEvent | undefined;
        // the pointerup got lost, e.g. the button was released over an iframe
        if (pointer?.pointerType === 'mouse' && pointer.buttons === 0) {
            this.end(e);
            return;
        }
        if (!this.started) {
            const start = this.startEvent!;
            const distance = Math.max(Math.abs((start.clientX ?? 0) - (e.clientX ?? 0)), Math.abs((start.clientY ?? 0) - (e.clientY ?? 0)));
            if (distance < this.options.distance) {
                return;
            }
            window.getSelection()?.removeAllRanges();
            this.started = this.start(start);
            if (!this.started) {
                this.cleanupPointer();
                return;
            }
            // the pointer events keep coming while the pointer is over an iframe or outside of the window; the drop
            // targets are found by their boxes, not by the event target
            const target = start.target;
            if (target instanceof Element && target.isConnected) {
                try {
                    target.setPointerCapture(this.pointerId!);
                    this.captureElement = target;
                }
                catch {
                    // the pointer is not active anymore
                }
            }
        }
        e.preventDefault();
        this.move(e);
    }

    /**
     * Ends the interaction, e.g. on pointerup, and prevents the click after a drag.
     * @param e The event that ends it.
     */
    protected end(e: JQuery.TriggeredEvent): void {
        const started = this.started;
        const target = this.startEvent?.target;
        this.cleanupPointer();
        if (started) {
            this.stop(e);
            // no click after a drag: the click of a tap comes in a later task than pointerup, so the click is blocked
            // until it comes, the next pointer is pressed or a short time passed
            if (target instanceof Element) {
                let timer = 0;
                const release = () => {
                    clearTimeout(timer);
                    target.removeEventListener('click', preventClick, { capture: true });
                    document.removeEventListener('pointerdown', release, { capture: true });
                };
                const preventClick = (ev: Event) => {
                    ev.stopImmediatePropagation();
                    ev.preventDefault();
                    release();
                };
                target.addEventListener('click', preventClick, { capture: true });
                document.addEventListener('pointerdown', release, { capture: true });
                timer = window.setTimeout(release, 500);
            }
        }
        this.aborted = false;
    }

    /** Stops listening for the pointer and releases it. */
    private cleanupPointer(): void {
        $(document).off(this.namespace);
        for (const type of POINTER_EVENTS) {
            document.removeEventListener(type, this.onDocumentPointer, true);
        }
        document.removeEventListener('dragstart', this.preventNativeDrag, true);
        if (this.captureElement?.hasPointerCapture(this.pointerId!)) {
            this.captureElement.releasePointerCapture(this.pointerId!);
        }
        if (activeInteraction === (this as unknown as PointerInteraction<PointerInteractionOptions>)) {
            activeInteraction = null;
        }
        this.captureElement = null;
        this.pointerId = null;
        this.started = false;
        this.startEvent = null;
        this.lastEvent = null;
    }

    /**
     * A `pointercancel` event for an interaction cancelled or destroyed by code, with the position of the last pointer
     * event, so the callbacks get a position (e.g. `event.pageX` in the `stop` callback).
     * @returns The event.
     */
    protected cancelEvent(): JQuery.TriggeredEvent {
        const last = this.lastEvent;
        const position = last ? {
            pageX: last.pageX, pageY: last.pageY, clientX: last.clientX, clientY: last.clientY,
            target: last.target, originalEvent: last.originalEvent
        } : {};
        return $.Event('pointercancel', position) as unknown as JQuery.TriggeredEvent;
    }

    /**
     * Creates the helper of a draggable or sortable from the `helper` and `appendTo` options: the dragged element, a
     * clone of it without its id or the element of the helper function, appended if it is not in the document.
     * @param event The pointerdown event.
     * @param dragged The dragged element.
     * @param args More arguments of the helper function after the event, e.g. the item of a sortable.
     * @returns The helper.
     */
    protected createHelper(event: JQuery.TriggeredEvent, dragged: JQuery, ...args: unknown[]): JQuery {
        const helperOption = this.options['helper'];
        const appendTo = this.options['appendTo'];
        const helper = typeof helperOption === 'function' ? $(helperOption.call(this.element[0], event, ...args) as Element)
            : helperOption === 'clone' ? dragged.clone().removeAttr('id') : dragged;
        if (!helper.parents('body').length) {
            helper.appendTo(appendTo === 'parent' ? dragged.parent() : $(appendTo as string));
        }
        return helper as JQuery;
    }

    /**
     * Sets the cursor of the body until {@link restoreCursor} is called, e.g. for the `cursor` option.
     * @param cursor The CSS cursor.
     */
    protected setCursor(cursor: string): void {
        this.storedCursor ??= document.body.style.cursor;
        document.body.style.cursor = cursor;
    }

    /** Restores the inline cursor of the body that {@link setCursor} replaced. */
    protected restoreCursor(): void {
        if (this.storedCursor !== null) {
            document.body.style.cursor = this.storedCursor;
            this.storedCursor = null;
        }
    }

    /**
     * Scrolls the container (or the window) when the pointer is near its edge, like the `scroll` option of jQuery UI:
     * an element scrolls when the pointer is closer than the sensitivity to an edge or beyond it, the window when the
     * pointer is close to the edge of the viewport.
     * @param event The pointermove event.
     * @param container The scroll parent determined when the interaction started, `null` for the window (also the
     * body and the document element scroll with the window).
     * @param axis Only scrolls along the `x` or `y` axis.
     * @param sensitivity Distance in pixels from the edge at which the scrolling starts.
     * @param speed Pixels scrolled per pointer move.
     * @returns Whether it scrolled.
     */
    protected autoScroll(event: JQuery.TriggeredEvent, container: HTMLElement | null, axis: 'x' | 'y' | false = false,
            sensitivity: number = 20, speed: number = 20): boolean {
        if (container === document.body || container === document.documentElement) {
            container = null;
        }
        let dx = 0;
        let dy = 0;
        if (container) {
            const x = pageX(event);
            const y = pageY(event);
            const offset = offsetOf($(container));
            dy = offset.top + container.offsetHeight - y < sensitivity ? speed : y - offset.top < sensitivity ? -speed : 0;
            dx = offset.left + container.offsetWidth - x < sensitivity ? speed : x - offset.left < sensitivity ? -speed : 0;
        }
        else {
            const x = event.clientX ?? 0;
            const y = event.clientY ?? 0;
            dy = y < sensitivity ? -speed : window.innerHeight - y < sensitivity ? speed : 0;
            dx = x < sensitivity ? -speed : window.innerWidth - x < sensitivity ? speed : 0;
        }
        if (axis === 'x') {
            dy = 0;
        }
        if (axis === 'y') {
            dx = 0;
        }
        if (!dx && !dy) {
            return false;
        }
        if (container) {
            container.scrollBy(dx, dy);
        }
        else {
            window.scrollBy(dx, dy);
        }
        return true;
    }

    /**
     * Whether the interaction is in progress.
     * @returns `true` between the start and the end of the interaction.
     */
    isActive(): boolean {
        return this.started;
    }
}
