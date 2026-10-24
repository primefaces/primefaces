import { contentBox, cssPosition, Interaction, offsetOf, type Callback, type Coordinates, type InteractionOptions, type Size, pageX, pageY } from "./interaction.js";

export interface ResizableOptions extends InteractionOptions {
    /** Other elements resized together with the element. */
    alsoResize: string | Element | JQuery | false;
    /** Whether the helper is animated to the new size. */
    animate: boolean;
    animateDuration: number | string;
    animateEasing: string;
    /** Keeps the aspect ratio: `true` for the current one, or a ratio (width / height). */
    aspectRatio: boolean | number;
    /** Element the element is kept in: `parent`, `document`, a selector or an element. */
    containment: false | 'parent' | 'document' | string | Element | JQuery;
    /** Whether a semi-transparent copy of the element is resized. */
    ghost: boolean;
    /** Snaps the size to a grid: a number or `[x, y]`. */
    grid: number | [number, number] | false;
    /** The resize handles, e.g. `n,e,s,w,ne,se,sw,nw` or `all`. */
    handles: string;
    /** Style class of a helper that is resized instead of the element. */
    helper: string | false;
    maxHeight: number | null;
    maxWidth: number | null;
    minHeight: number;
    minWidth: number;
    start: Callback;
    resize: Callback;
    stop: Callback;
}

export interface ResizableUi {
    element: JQuery;
    helper: JQuery;
    originalElement: JQuery;
    size: Size;
    originalSize: Size;
    position: Coordinates;
    originalPosition: Coordinates;
}

const DEFAULTS: ResizableOptions = {
    disabled: false,
    distance: 1,
    alsoResize: false,
    animate: false,
    animateDuration: 'slow',
    animateEasing: 'swing',
    aspectRatio: false,
    containment: false,
    ghost: false,
    grid: false,
    handles: 'e,s,se',
    helper: false,
    maxHeight: null,
    maxWidth: null,
    minHeight: 10,
    minWidth: 10,
    start: null,
    resize: null,
    stop: null
};

/** Elements that can't contain the handles and are wrapped. */
const WRAPPED = /^(canvas|textarea|input|select|button|img)$/i;

/**
 * Makes an element resizable with handles at its edges and corners.
 */
export class Resizable extends Interaction<ResizableOptions> {
    /** The element that is resized, a wrapper for elements that can't contain the handles. */
    readonly resizeElement: JQuery<HTMLElement>;
    size: Size = { width: 0, height: 0 };
    originalSize: Size = { width: 0, height: 0 };
    position: Coordinates = { left: 0, top: 0 };
    originalPosition: Coordinates = { left: 0, top: 0 };
    helper: JQuery | null = null;

    private axis: string = '';
    private startPointer: Coordinates = { left: 0, top: 0 };
    private originalOffset: Coordinates = { left: 0, top: 0 };
    private sizeDiff: Size = { width: 0, height: 0 };
    private ratio: number = 1;
    private alsoResizeSizes: { element: JQuery; size: Size }[] = [];
    private storedCursor: string = '';

    constructor(element: HTMLElement, options?: Partial<ResizableOptions>) {
        super(element, 'resizable', 'resize', DEFAULTS, options);
        this.resizeElement = WRAPPED.test(element.nodeName) ? this.wrap() : this.element;
        this.resizeElement.addClass('ui-resizable');
        this.element.addClass('ui-resizable');
        this.createHandles();
        this.bindPointer(this.resizeElement);
    }

    override destroy(): void {
        this.resizeElement.children('.ui-resizable-handle').remove();
        this.resizeElement.off(this.namespace).removeClass('ui-resizable ui-resizable-resizing');
        if (this.resizeElement[0] !== this.element[0]) {
            const wrapper = this.resizeElement;
            this.element.css({ position: wrapper.css('position'), width: wrapper.outerWidth() ?? '', height: wrapper.outerHeight() ?? '',
                top: wrapper.css('top'), left: wrapper.css('left'), margin: wrapper.css('margin') }).insertAfter(wrapper);
            wrapper.remove();
        }
        super.destroy();
    }

    /** Wraps elements like a textarea in a div, which gets the handles. */
    private wrap(): JQuery<HTMLElement> {
        const el = this.element;
        const wrapper = $('<div class="ui-wrapper" style="overflow: hidden;"></div>').css({
            position: el.css('position') === 'static' ? 'relative' : el.css('position'),
            width: el.outerWidth() ?? 0,
            height: el.outerHeight() ?? 0,
            top: el.css('top'),
            left: el.css('left'),
            marginLeft: el.css('marginLeft'),
            marginTop: el.css('marginTop'),
            marginRight: el.css('marginRight'),
            marginBottom: el.css('marginBottom')
        }) as JQuery<HTMLElement>;
        el.wrap(wrapper);
        const wrapped = el.parent() as JQuery<HTMLElement>;
        el.css({ position: 'static', display: 'block', margin: 0, resize: 'none' });
        this.syncWrapped(wrapped);
        return wrapped;
    }

    private syncWrapped(wrapper: JQuery = this.resizeElement): void {
        if (wrapper[0] !== this.element[0]) {
            this.element.outerWidth(wrapper.width() ?? 0).outerHeight(wrapper.height() ?? 0);
        }
    }

    private createHandles(): void {
        const handles = this.options.handles === 'all' ? 'n,e,s,w,se,sw,ne,nw' : this.options.handles;
        this.resizeElement.children('.ui-resizable-handle').remove();
        for (const dir of handles.split(',').map((h) => h.trim()).filter((h) => h)) {
            $('<div class="ui-resizable-handle"></div>').addClass('ui-resizable-' + dir).toggleClass('ui-icon ui-icon-gripsmall-diagonal-se', dir === 'se')
                .attr('data-axis', dir).css('z-index', 90).appendTo(this.resizeElement);
        }
    }

    ui(): ResizableUi {
        return {
            element: this.resizeElement,
            helper: this.helper ?? this.resizeElement,
            originalElement: this.element,
            size: this.size,
            originalSize: this.originalSize,
            position: this.position,
            originalPosition: this.originalPosition
        };
    }

    protected capture(event: JQuery.TriggeredEvent): boolean {
        const handle = $(event.target).closest('.ui-resizable-handle');
        if (!handle.length || handle.parent()[0] !== this.resizeElement[0]) {
            return false;
        }
        this.axis = handle.attr('data-axis') ?? 'se';
        return true;
    }

    protected start(event: JQuery.TriggeredEvent): boolean {
        const o = this.options;
        const el = this.resizeElement;

        if (el.css('position') === 'static') {
            el.css('position', 'relative');
        }
        this.originalOffset = offsetOf(el);
        this.position = cssPosition(el);
        this.originalPosition = { ...this.position };
        this.size = { width: el.width() ?? 0, height: el.height() ?? 0 };
        this.originalSize = { ...this.size };
        this.sizeDiff = { width: (el.outerWidth() ?? 0) - this.size.width, height: (el.outerHeight() ?? 0) - this.size.height };
        this.startPointer = { left: pageX(event), top: pageY(event) };
        this.ratio = typeof o.aspectRatio === 'number' ? o.aspectRatio : (this.originalSize.width / this.originalSize.height) || 1;

        if (o.helper || o.ghost || o.animate) {
            this.helper = $('<div style="overflow: hidden;"></div>').addClass(o.helper || 'ui-resizable-helper').css({
                position: 'absolute',
                left: this.originalOffset.left,
                top: this.originalOffset.top,
                width: (el.outerWidth() ?? 0) - 1,
                height: (el.outerHeight() ?? 0) - 1,
                zIndex: 1000
            }).appendTo(document.body);
            if (o.ghost) {
                el.clone().removeAttr('id').addClass('ui-resizable-ghost')
                    .css({ display: 'block', position: 'relative', margin: 0, left: 0, top: 0, width: '100%', height: '100%' })
                    .appendTo(this.helper);
            }
        }

        this.alsoResizeSizes = o.alsoResize ? $(o.alsoResize as string).map((_, element) => {
            const jq = $(element);
            return { element: jq, size: { width: jq.width() ?? 0, height: jq.height() ?? 0 } };
        }).get() : [];

        this.storedCursor = document.body.style.cursor;
        document.body.style.cursor = $(event.target).css('cursor');
        el.addClass('ui-resizable-resizing');

        this.trigger('start', event, this.ui());
        return true;
    }

    protected move(event: JQuery.TriggeredEvent): void {
        const o = this.options;
        const dx = pageX(event) - this.startPointer.left;
        const dy = pageY(event) - this.startPointer.top;
        const os = this.originalSize;
        const op = this.originalPosition;
        const axis = this.axis;
        let width = os.width;
        let height = os.height;
        let left = op.left;
        let top = op.top;

        if (axis.includes('e')) {
            width = os.width + dx;
        }
        if (axis.includes('w')) {
            width = os.width - dx;
        }
        if (axis.includes('s')) {
            height = os.height + dy;
        }
        if (axis.includes('n')) {
            height = os.height - dy;
        }

        if (o.grid) {
            const [gx, gy] = Array.isArray(o.grid) ? o.grid : [o.grid, o.grid];
            width = os.width + Math.round((width - os.width) / gx) * gx;
            height = os.height + Math.round((height - os.height) / gy) * gy;
        }

        if (o.aspectRatio || (event.originalEvent as PointerEvent | undefined)?.shiftKey) {
            if (axis === 'n' || axis === 's') {
                width = height * this.ratio;
            }
            else {
                height = width / this.ratio;
            }
        }

        // min / max, keeping the ratio
        const clamp = (value: number, min: number, max: number | null) => Math.max(min, max === null ? value : Math.min(max, value));
        width = clamp(width, o.minWidth ?? 0, o.maxWidth);
        height = clamp(height, o.minHeight ?? 0, o.maxHeight);
        if (o.aspectRatio) {
            if (width / height > this.ratio) {
                width = height * this.ratio;
            }
            else {
                height = width / this.ratio;
            }
        }

        // the opposite edges stay in place
        if (axis.includes('w')) {
            left = op.left + os.width - width;
        }
        if (axis.includes('n')) {
            top = op.top + os.height - height;
        }

        ({ width, height, left, top } = this.contain(width, height, left, top));

        this.size = { width, height };
        this.position = { left, top };

        if (this.helper) {
            this.helper.css({
                left: this.originalOffset.left + left - op.left,
                top: this.originalOffset.top + top - op.top,
                width: width + this.sizeDiff.width - 1,
                height: height + this.sizeDiff.height - 1
            });
        }
        else {
            this.apply();
        }

        this.trigger('resize', event, this.ui());
    }

    /** Keeps the element in the containment. */
    private contain(width: number, height: number, left: number, top: number): { width: number; height: number; left: number; top: number } {
        const containment = this.options.containment;
        if (!containment) {
            return { width, height, left, top };
        }
        const container = containment === 'parent' ? this.resizeElement.parent() : containment === 'document' ? $(document.body) : $(containment as string);
        if (!container.length) {
            return { width, height, left, top };
        }

        const [cl, ct, cr, cb] = contentBox(container.first());
        let pageLeft = this.originalOffset.left + left - this.originalPosition.left;
        let pageTop = this.originalOffset.top + top - this.originalPosition.top;

        if (pageLeft < cl) {
            width -= cl - pageLeft;
            left += cl - pageLeft;
            pageLeft = cl;
        }
        if (pageTop < ct) {
            height -= ct - pageTop;
            top += ct - pageTop;
            pageTop = ct;
        }
        if (pageLeft + width + this.sizeDiff.width > cr) {
            width = cr - pageLeft - this.sizeDiff.width;
        }
        if (pageTop + height + this.sizeDiff.height > cb) {
            height = cb - pageTop - this.sizeDiff.height;
        }
        return { width, height, left, top };
    }

    /** Applies the size and position to the element. */
    private apply(): void {
        const el = this.resizeElement;
        el.width(this.size.width).height(this.size.height);
        if (this.axis.includes('w')) {
            el.css('left', this.position.left);
        }
        if (this.axis.includes('n')) {
            el.css('top', this.position.top);
        }
        this.syncWrapped();
        for (const { element, size } of this.alsoResizeSizes) {
            element.width(size.width + this.size.width - this.originalSize.width).height(size.height + this.size.height - this.originalSize.height);
        }
    }

    protected stop(event: JQuery.TriggeredEvent): void {
        const o = this.options;
        const helper = this.helper;

        if (helper && !o.animate) {
            this.apply();
        }

        this.resizeElement.removeClass('ui-resizable-resizing');
        document.body.style.cursor = this.storedCursor;
        this.trigger('stop', event, this.ui());

        if (helper) {
            helper.remove();
            this.helper = null;
            if (o.animate) {
                const target: Record<string, number> = {
                    width: this.size.width + (this.resizeElement.css('box-sizing') === 'border-box' ? this.sizeDiff.width : 0),
                    height: this.size.height + (this.resizeElement.css('box-sizing') === 'border-box' ? this.sizeDiff.height : 0)
                };
                if (this.axis.includes('w')) {
                    target['left'] = this.position.left;
                }
                if (this.axis.includes('n')) {
                    target['top'] = this.position.top;
                }
                this.resizeElement.animate(target, {
                    duration: o.animateDuration as JQuery.Duration,
                    easing: o.animateEasing,
                    step: () => this.syncWrapped(),
                    complete: () => this.apply()
                });
            }
        }
    }
}
