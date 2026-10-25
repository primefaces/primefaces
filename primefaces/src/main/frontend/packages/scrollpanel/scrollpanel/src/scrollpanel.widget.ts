/**
 * The configuration for the {@link ScrollPanel} widget.
 */
export interface ScrollPanelCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
}

/** An axis of the scroll panel. */
type Axis = "x" | "y";

/** The minimum length of a bar in pixels, so that it can still be grabbed in a long content. */
const MIN_BAR_LENGTH = 20;

/**
 * __PrimeFaces ScrollPanel Widget__
 *
 * ScrollPanel displays scrollable content with theme aware scrollbars. The content scrolls natively (mouse wheel,
 * touch, keyboard), only the scrollbars of the browser are hidden and replaced by bars that follow the scroll
 * position and can be dragged; a click on a track scrolls by a page.
 */
export class ScrollPanel<Cfg extends ScrollPanelCfg = ScrollPanelCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The element that scrolls, it contains the children of the scroll panel. */
    content: JQuery = $();

    /** The tracks of the horizontal and the vertical bar. */
    private tracks: Record<Axis, JQuery> = { x: $(), y: $() };
    /** The bars of the horizontal and the vertical axis, they can be dragged. */
    private bars: Record<Axis, JQuery> = { x: $(), y: $() };
    /** Observes the size of the panel and of the children of the content. */
    private resizeObserver: ResizeObserver | null = null;
    /** Observes added and removed children of the content, e.g. after an AJAX update. */
    private mutationObserver: MutationObserver | null = null;
    /** The pending animation frame of an update of the bars. */
    private frame: number = 0;

    /**
     * Initializes the scroll panel: finds the content and the bars rendered by the server, binds the events and
     * observes the size of the content.
     * @param cfg The configuration of the scroll panel.
     */
    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.content = this.jq.children('.ui-scrollpanel-content');
        for (const axis of ['x', 'y'] as Axis[]) {
            this.tracks[axis] = this.jq.children('.ui-scrollpanel-track-' + axis);
            this.bars[axis] = this.tracks[axis].children('.ui-scrollpanel-bar');
        }

        this.bindEvents();
        this.observe();
        this.update();
    }

    /**
     * Binds the scroll event of the content, dragging the bars and clicks on the tracks.
     */
    private bindEvents(): void {
        this.content.on('scroll.scrollpanel', () => this.requestUpdate());
        this.jq.on('pointerenter.scrollpanel', () => this.requestUpdate());

        for (const axis of ['x', 'y'] as Axis[]) {
            this.bars[axis].on('pointerdown.scrollpanel', (e) => this.onBarPointerDown(e, axis));
            this.tracks[axis]
                // no text selection and no focus change; not on pointerdown, that would suppress the mousedown event
                // the overlays of other widgets listen to on the document to hide
                .on('mousedown.scrollpanel', (e) => e.preventDefault())
                .on('pointerdown.scrollpanel', (e) => this.onTrackPointerDown(e, axis));
        }
    }

    /**
     * Observes the size of the panel and the children of the content, so the bars follow a changed content (e.g. an
     * AJAX update or a panel that becomes visible). The observers are disconnected when the widget is destroyed.
     */
    private observe(): void {
        this.resizeObserver?.disconnect();
        this.mutationObserver?.disconnect();

        const content = this.content[0];
        if (!content) {
            return;
        }
        this.resizeObserver = new ResizeObserver(() => this.requestUpdate());
        this.resizeObserver.observe(content);
        const observeChildren = () => {
            for (const child of Array.from(content.children)) {
                this.resizeObserver?.observe(child);
            }
        };
        observeChildren();

        this.mutationObserver = new MutationObserver(() => {
            observeChildren();
            this.requestUpdate();
        });
        this.mutationObserver.observe(content, { childList: true });

        this.addDestroyListener(() => {
            this.resizeObserver?.disconnect();
            this.mutationObserver?.disconnect();
            cancelAnimationFrame(this.frame);
        });
    }

    /**
     * Starts dragging a bar: the content scrolls along with the pointer until it is released.
     * @param e The pointerdown event of the bar.
     * @param axis The axis of the bar.
     */
    private onBarPointerDown(e: JQuery.TriggeredEvent, axis: Axis): void {
        const event = e.originalEvent as PointerEvent | undefined;
        const content = this.content[0];
        const bar = this.bars[axis][0];
        if (!event || !content || !bar || (event.pointerType === 'mouse' && event.button !== 0)) {
            return;
        }
        e.stopPropagation();
        bar.setPointerCapture(event.pointerId);
        this.bars[axis].addClass('ui-scrollpanel-bar-active');

        const startPointer = axis === 'x' ? event.clientX : event.clientY;
        const startScroll = axis === 'x' ? content.scrollLeft : content.scrollTop;
        // the content moves by its scrollable size while the bar moves through the free length of the track
        const { scrollSize, clientSize } = this.sizes(axis);
        const free = this.trackLength(axis) - this.barLength(axis);
        const ratio = free > 0 ? (scrollSize - clientSize) / free : 0;

        this.bars[axis]
            .on('pointermove.scrollpanel-drag', (ev) => {
                const pointer = (ev.originalEvent as PointerEvent);
                const scroll = startScroll + ((axis === 'x' ? pointer.clientX : pointer.clientY) - startPointer) * ratio;
                if (axis === 'x') {
                    content.scrollLeft = scroll;
                }
                else {
                    content.scrollTop = scroll;
                }
            })
            .on('pointerup.scrollpanel-drag pointercancel.scrollpanel-drag', () => {
                this.bars[axis].off('.scrollpanel-drag').removeClass('ui-scrollpanel-bar-active');
            });
    }

    /**
     * Scrolls by a page towards the pointer when a track is pressed beside its bar.
     * @param e The pointerdown event of the track.
     * @param axis The axis of the track.
     */
    private onTrackPointerDown(e: JQuery.TriggeredEvent, axis: Axis): void {
        const event = e.originalEvent as PointerEvent | undefined;
        const content = this.content[0];
        const bar = this.bars[axis][0];
        if (!event || !content || !bar || (event.pointerType === 'mouse' && event.button !== 0)) {
            return;
        }
        const rect = bar.getBoundingClientRect();
        const before = axis === 'x' ? event.clientX < rect.left : event.clientY < rect.top;
        const page = axis === 'x' ? content.clientWidth : content.clientHeight;
        // in a right-to-left content the start of the horizontal scroll range is on the right
        const sign = (before ? -1 : 1) * (axis === 'x' && this.isRtl() ? -1 : 1);
        content.scrollBy(axis === 'x' ? { left: sign * page } : { top: sign * page });
    }

    /**
     * Updates the bars in the next animation frame, several changes in a frame update them once.
     */
    private requestUpdate(): void {
        if (!this.frame) {
            this.frame = requestAnimationFrame(() => {
                this.frame = 0;
                this.update();
            });
        }
    }

    /**
     * Shows the track of each axis whose content overflows, sizes its bar by the visible part of the content and
     * positions it at the scroll position.
     */
    private update(): void {
        const content = this.content[0];
        if (!content) {
            return;
        }
        for (const axis of ['x', 'y'] as Axis[]) {
            const { scrollSize, clientSize } = this.sizes(axis);
            const scrollable = scrollSize > clientSize + 1;
            this.tracks[axis].toggleClass('p-hidden', !scrollable);
            this.jq.toggleClass('ui-scrollpanel-scrollable-' + axis, scrollable);
        }
        for (const axis of ['x', 'y'] as Axis[]) {
            if (this.jq.hasClass('ui-scrollpanel-scrollable-' + axis)) {
                this.positionBar(axis);
            }
        }
    }

    /**
     * Sizes and positions the bar of an axis.
     * @param axis The axis of the bar.
     */
    private positionBar(axis: Axis): void {
        const content = this.content[0]!;
        const { scrollSize, clientSize } = this.sizes(axis);
        const trackLength = this.trackLength(axis);
        const barLength = Math.min(trackLength, Math.max(MIN_BAR_LENGTH, trackLength * clientSize / scrollSize));
        // the scroll position as a fraction, a right-to-left content scrolls from 0 to the negative maximum
        let fraction = Math.abs(axis === 'x' ? content.scrollLeft : content.scrollTop) / (scrollSize - clientSize);
        fraction = Math.min(1, Math.max(0, fraction));
        if (axis === 'x' && this.isRtl()) {
            fraction = 1 - fraction;
        }
        const offset = (trackLength - barLength) * fraction;

        this.bars[axis].css(axis === 'x'
            ? { width: barLength + 'px', left: offset + 'px' }
            : { height: barLength + 'px', top: offset + 'px' });
    }

    /**
     * The scrollable and the visible size of the content on an axis.
     * @param axis The axis.
     * @returns The size of the whole content and of its visible part, in pixels.
     */
    private sizes(axis: Axis): { scrollSize: number, clientSize: number } {
        const content = this.content[0]!;
        return axis === 'x'
            ? { scrollSize: content.scrollWidth, clientSize: content.clientWidth }
            : { scrollSize: content.scrollHeight, clientSize: content.clientHeight };
    }

    /**
     * The length of the track of an axis.
     * @param axis The axis.
     * @returns The length in pixels.
     */
    private trackLength(axis: Axis): number {
        const track = this.tracks[axis][0];
        return track ? (axis === 'x' ? track.clientWidth : track.clientHeight) : 0;
    }

    /**
     * The length of the bar of an axis.
     * @param axis The axis.
     * @returns The length in pixels.
     */
    private barLength(axis: Axis): number {
        const bar = this.bars[axis][0];
        return bar ? (axis === 'x' ? bar.offsetWidth : bar.offsetHeight) : 0;
    }

    /**
     * Whether the content is right-to-left.
     * @returns `true` for a right-to-left content.
     */
    private isRtl(): boolean {
        return this.content.css('direction') === 'rtl';
    }

    /**
     * Scrolls to the given scroll position.
     * @param x Horizontal scroll position in pixels.
     * @param y Vertical scroll position in pixels.
     */
    scrollTo(x: number, y: number): void {
        this.content[0]?.scrollTo(x, y);
    }

    /**
     * Scrolls to the given horizontal position.
     * @param x Horizontal scroll position in pixels.
     */
    scrollX(x: number): void {
        this.content[0]?.scrollTo({ left: x });
    }

    /**
     * Scrolls to the given vertical position.
     * @param y Vertical scroll position in pixels.
     */
    scrollY(y: number): void {
        this.content[0]?.scrollTo({ top: y });
    }

    /**
     * Updates the bars, e.g. after the size of the content changed in a way the scroll panel does not notice. Changed
     * children and sizes are noticed automatically.
     */
    redraw(): void {
        this.update();
    }
}
