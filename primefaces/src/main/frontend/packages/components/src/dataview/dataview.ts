/**
 * The configuration for the {@link DataView} widget.
 */
export interface DataViewCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** When pagination is enabled: The paginator configuration for the paginator. */
    paginator: PrimeFacesTypes.widget.PartialWidgetCfg<PrimeFacesTypes.widget.PaginatorCfg>;
}

/**
 * __PrimeFaces DataView Widget__
 *
 * DataView displays data in grid or list layout.
 */
export class DataView<Cfg extends DataViewCfg = DataViewCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** DOM element of the data view header. */
    header: JQuery = $();
    /** DOM element of the content container for the data grid. */
    content: JQuery = $();
    /** DOM element of the container with the layout switch buttons. */
    layoutOptions: JQuery = $();
    /** DOM elements of the buttons for switching the layout (grid or list). */
    buttons: JQuery = $();
    /** When pagination is enabled: The paginator widget instance used for paging. */
    paginator?: PrimeFacesTypes.Widget<"Paginator">;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.header = this.jq.children('.p-dataview-header');
        this.content = this.jq.children('.p-dataview-content');
        this.layoutOptions = this.header.children('.p-dataview-layout-options');
        this.buttons = this.layoutOptions.children('div');

        if (this.cfg.paginator) {
            this.setupPaginator();
        }

        this.bindEvents();
    }

    /**
     * Initializes the paginator, called during widget initialization.
     */
    private setupPaginator(): void {
        this.cfg.paginator!.paginate = (newState: PrimeFacesTypes.widget.Paginator.PaginationState) => {
            this.handlePagination(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator();
        this.paginator.init(this.cfg.paginator!);
        this.paginator.bindSwipeEvents(this.jq, this.cfg);
    }

    /**
     * Sets up all event listeners required by this widget.
     */
    private bindEvents(): void {
        this.buttons.on('mouseover', (e) => {
            const button = $(e.currentTarget);
            button.addClass('p-state-hover');
        })
        .on('mouseout', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('click', (e) => {
            const button = $(e.currentTarget),
            radio = button.children(':radio');

            if (!radio.prop('checked')) {
                this.select(button);
            }
        });

        /* For keyboard accessibility */
        this.buttons.on('focus.dataview-button', (e) => {
            const button = $(e.currentTarget);
            button.addClass('p-state-focus');
        })
        .on('blur.dataview-button', (e) => {
            const button = $(e.currentTarget);
            button.removeClass('p-state-focus');
        })
        .on('keydown.dataview-button', (e) => {
            if (PrimeFaces.utils.isActionKey(e)) {
                const button = $(e.currentTarget),
                radio = button.children(':radio');

                if (!radio.prop('checked')) {
                    this.select(button);
                }
                e.preventDefault();
            }
        });
    }

    /**
     * Switches this data view to the given layout (grid or list).
     *
     * ```javascript
     * const widget = PF("MyDataView");
     * // Switch to grid layout
     * widget.select(widget.buttons.eq(1));
     * ```
     * @param button One of the layout switch buttons (`.p-button`).
     */
    select(button: JQuery): void {
        this.buttons.filter('.p-state-active').removeClass('p-state-active p-state-hover').children(':radio').prop('checked', false);

        button.addClass('p-state-active').children(':radio').prop('checked', true);

        this.loadLayoutContent(button.children(':radio').val() as PrimeFacesTypes.widget.DataView.Layout);
    }

    /**
     * Loads the content with the data items for the selected layout (grid or list).
     * @param layout The current layout of this data view.
     */
    private loadLayoutContent(layout: PrimeFacesTypes.widget.DataView.Layout): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            params: [
                { name: this.getId() + '_layout', value: layout }
            ],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        this.content.html(content);
                    }
                });

                return true;
            },
            oncomplete: () => {
                this.jq.removeClass('p-dataview-grid p-dataview-list').addClass('p-dataview-' + layout);
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

    /**
     * Handles a pagination event by updating the data grid and invoking the appropriate behaviors.
     * @param newState The new pagination state to apply.
     */
    private handlePagination(newState: PrimeFacesTypes.widget.Paginator.PaginationState): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            formId: this.getParentFormId(),
            params: [
                { name: this.getId() + '_pagination', value: true },
                { name: this.getId() + '_first', value: newState.first },
                { name: this.getId() + '_rows', value: newState.rows }
            ],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        this.content.html(content);
                    }
                });

                return true;
            },
            oncomplete: () => {
                this.paginator!.cfg.page = newState.page;
                this.paginator!.updateUI();
            }
        };

        if (this.hasBehavior('page')) {
            this.callBehavior('page', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Retrieves the paginator widget used by this data grid for pagination. You can use this widget to switch to a
     * different page programmatically.
     * @returns The paginator widget, or `undefined` when pagination is not enabled.
     */
    getPaginator(): PrimeFacesTypes.Widget<"Paginator"> | undefined {
        return this.paginator;
    }
}
