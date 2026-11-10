/**
 * The configuration for the {@link DataScroller} widget.
 */
export interface DataScrollerCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Buffer below the scroll position that triggers the load, sent in percent, converted to a fraction on init. */
    buffer: number;
    /** Number of items to load on each load. */
    chunkSize: number;
    /** When more items are loaded. */
    loadEvent: PrimeFacesTypes.widget.DataScroller.LoadEvent;
    /** Target of the scroll listener, defaults to `document`. */
    mode: PrimeFacesTypes.widget.DataScroller.Mode;
    /** Offset of the loaded items, set to `0` on init and updated on each load. */
    offset: number;
    /** Whether the items are loaded from the bottom and the scroll position starts at the bottom. */
    startAtBottom: boolean;
    /** Total number of items. */
    totalSize: number;
    /** Whether only the visible chunk of items is loaded while scrolling (inline mode). */
    virtualScroll: boolean;
}

/**
 * __PrimeFaces DataScroller Widget__
 *
 * DataScroller displays a collection of data with on demand loading using scrolling.
 */
export class DataScroller<Cfg extends DataScrollerCfg = DataScrollerCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The container of the list. */
    content: JQuery = $();
    /** The list of items. */
    list: JQuery = $();
    /** The container of the `more` button. */
    loaderContainer: JQuery = $();
    /** The status shown while loading. */
    loadStatus: JQuery = $();
    /** The `more` button, only with `loadEvent` `manual`. */
    loadTrigger?: JQuery;
    /** Whether a load request is running. */
    loading: boolean = false;
    /** Whether all items are loaded. */
    allLoaded: boolean = false;
    /** Height of an item in pixels, in inline mode. */
    itemHeight?: number;
    /** Whether a virtual scroll request is running. */
    virtualScrollActive?: boolean;
    /** The timer ID of the delayed virtual scroll. */
    scrollTimeout?: number;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.content = this.jq.children('div.p-datascroller-content');
        this.list = this.cfg.virtualScroll ? this.content.children('div').children('ul') : this.content.children('ul');
        this.loaderContainer = this.content.children('div.p-datascroller-loader');
        this.loadStatus = this.content.children('div.p-datascroller-loading');
        this.loadStatus.remove();
        this.loading = false;
        this.allLoaded = false;
        this.cfg.offset = 0;
        this.cfg.mode = this.cfg.mode || 'document';
        this.cfg.buffer = (100 - (this.cfg.buffer as number)) / 100;

        if (this.cfg.loadEvent === 'scroll') {
            this.bindScrollListener();
        }
        else {
            this.loadTrigger = this.loaderContainer.children();
            this.bindManualLoader();
        }
    }

    private bindScrollListener(): void {
        if (this.cfg.mode === 'document') {
            PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.id + '_align', () => {
                if (($(window).scrollTop() ?? 0) >= ((($(document).height() ?? 0) * (this.cfg.buffer as number)) - ($(window).height() ?? 0)) && this.shouldLoad()) {
                    this.load();
                }
            });
        }
        else {
            this.itemHeight = 0;

            if (this.cfg.virtualScroll) {
                const item = this.list.children('li.p-datascroller-item');
                if (item.length) {
                    this.itemHeight = item.outerHeight() as number;
                    this.content.children('div').css('min-height', parseFloat(((this.cfg.totalSize as number) * this.itemHeight) + 'px'));
                }

                if (this.cfg.startAtBottom) {
                    const pageHeight = this.itemHeight * (this.cfg.chunkSize as number);
                    const virtualListHeight = parseFloat(String((this.cfg.totalSize as number) * this.itemHeight));
                    const viewportHeight = this.content.height() ?? 0;
                    const pageCount = Math.floor(virtualListHeight / pageHeight) || 1;
                    const page = ((this.cfg.totalSize as number) % (this.cfg.chunkSize as number)) == 0 ? pageCount - 2 : pageCount - 1;
                    const top = (virtualListHeight < viewportHeight) ? (viewportHeight - virtualListHeight) : (Math.max(page, 0) * pageHeight);

                    this.list.css('top', top + 'px');
                    this.content.scrollTop(this.content[0]?.scrollHeight ?? 0);
                }
            }
            else if (this.cfg.startAtBottom) {
                this.content.scrollTop(this.content[0]?.scrollHeight ?? 0);
                this.cfg.offset = (this.cfg.totalSize as number) > (this.cfg.chunkSize as number) ? (this.cfg.totalSize as number) - (this.cfg.chunkSize as number) : (this.cfg.totalSize as number);

                let paddingTop: string | number = '0';
                if ((this.content.height() ?? 0) > (this.list.height() ?? 0)) {
                    paddingTop = (this.getInnerContentHeight() - (this.list.outerHeight() ?? 0) - (this.loaderContainer.outerHeight() ?? 0));
                }

                this.list.css('padding-top', paddingTop + 'px');
            }

            // a pending virtual scroll load must not run after the widget was updated or removed
            const clearScrollTimeout = () => {
                clearTimeout(this.scrollTimeout);
            };
            this.addRefreshListener(clearScrollTimeout);
            this.addDestroyListener(clearScrollTimeout);

            this.content.on('scroll.dataScroller', (e) => {
                const scrollContent = e.currentTarget as HTMLElement;
                if (this.cfg.virtualScroll) {
                    clearTimeout(this.scrollTimeout);
                    this.scrollTimeout = window.setTimeout(() => {
                        const itemHeight = this.itemHeight as number;
                        const viewportHeight = this.content.outerHeight() ?? 0;
                        const listHeight = (this.list.outerHeight() ?? 0) + Math.ceil(viewportHeight - (this.content.height() ?? 0));
                        const pageHeight = itemHeight * (this.cfg.chunkSize as number);
                        const virtualListHeight = parseFloat(String((this.cfg.totalSize as number) * itemHeight));
                        const pageCount = (virtualListHeight / pageHeight) || 1;

                        if (scrollContent.scrollTop + viewportHeight > parseFloat(this.list.css('top')) + listHeight
                                || scrollContent.scrollTop < parseFloat(this.list.css('top'))) {
                            const page = Math.floor((scrollContent.scrollTop * pageCount) / (scrollContent.scrollHeight)) + 1;
                            this.loadRowsWithVirtualScroll(page, () => {
                                this.list.css('top', ((page - 1) * pageHeight) + 'px');
                            });
                        }
                    }, 200);
                }
                else {
                    const scrollTop = scrollContent.scrollTop;
                    const scrollHeight = scrollContent.scrollHeight;
                    const viewportHeight = scrollContent.clientHeight;
                    const shouldLoad = this.shouldLoad() && (this.cfg.startAtBottom
                        ? (scrollTop <= (scrollHeight - (scrollHeight * (this.cfg.buffer as number)))) && ((this.cfg.totalSize as number) > (this.cfg.chunkSize as number))
                        : (scrollTop >= ((scrollHeight * (this.cfg.buffer as number)) - viewportHeight)));
                    if (shouldLoad) {
                        this.load();
                    }
                }
            });
        }
    }

    /**
     * Loads the given page of items for virtual scrolling.
     * @param page The 1-based page of `chunkSize` items to load.
     * @param callback Called when the items were inserted.
     */
    private loadRowsWithVirtualScroll(page: number, callback: () => void): void {
        if (this.virtualScrollActive) {
            return;
        }

        this.virtualScrollActive = true;

        const first = (page - 1) * (this.cfg.chunkSize as number);
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            formId: this.getParentFormId(),
            params: [{ name: this.getId() + '_virtualScrolling', value: true },
                { name: this.getId() + '_first', value: first }],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        // insert new rows
                        this.updateData(content);
                        callback();
                        this.virtualScrollActive = false;
                    }
                });

                return true;
            },
            oncomplete: (_xhr, _status, args) => {
                // also after a failed request, otherwise no page is loaded anymore
                this.virtualScrollActive = false;
                if (typeof args?.['totalSize'] !== 'undefined') {
                    this.cfg.totalSize = args['totalSize'] as number;
                }
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

    /**
     * Inserts loaded items.
     * @param data The HTML of the items.
     * @param clear `false` to keep the existing items, they are replaced by default.
     * @param pre `true` to prepend the items, they are appended by default.
     */
    private updateData(data: string, clear?: boolean, pre?: boolean): void {
        const empty = (clear === undefined) ? true : clear;

        if (empty) {
            this.list.html(data);
        }
        else if (pre) {
            this.list.prepend(data);
        }
        else {
            this.list.append(data);
        }
    }

    private bindManualLoader(): void {
        this.loadTrigger?.on('click.dataScroller', (e) => {
            this.load();
            e.preventDefault();
        });
    }

    /**
     * Loads the next chunk of items.
     */
    load(): void {
        const chunkSize = this.cfg.chunkSize as number;
        this.loading = true;
        this.cfg.offset = (this.cfg.offset as number) + (chunkSize * (this.cfg.startAtBottom ? -1 : 1));

        this.loadStatus.appendTo(this.loaderContainer);
        if (this.loadTrigger) {
            this.loadTrigger.hide();
        }

        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            global: false,
            ignoreAutoUpdate: true,
            params: [{ name: this.getId() + '_load', value: true }, { name: this.getId() + '_offset', value: this.cfg.offset }],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        this.updateData(content, false, this.cfg.startAtBottom);
                    }
                });

                return true;
            },
            oncomplete: () => {
                const offset = this.cfg.offset as number;
                if (offset < 0) {
                    this.cfg.offset = 0;
                }

                this.loading = false;
                this.allLoaded = (this.cfg.startAtBottom) ? this.cfg.offset == 0
                    : ((this.cfg.offset as number) + chunkSize) >= (this.cfg.totalSize as number);

                this.loadStatus.remove();

                if (this.loadTrigger && !this.allLoaded) {
                    this.loadTrigger.show();
                }
            }
        };

        if (this.hasBehavior('load')) {
            this.callBehavior('load', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * @returns `true` if more items can be loaded: no request is running and not all items are loaded.
     */
    shouldLoad(): boolean {
        return (!this.loading && !this.allLoaded);
    }

    /**
     * @returns The height of the content without its padding.
     */
    private getInnerContentHeight(): number {
        return ((this.content.innerHeight() ?? 0) - parseFloat(this.content.css('padding-top')) - parseFloat(this.content.css('padding-bottom')));
    }
}
