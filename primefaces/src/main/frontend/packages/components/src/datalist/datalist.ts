/**
 * The configuration for the {@link DataList} widget.
 */
export interface DataListCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** When pagination is enabled: The paginator configuration for the paginator. */
    paginator: PrimeFacesTypes.widget.PartialWidgetCfg<PrimeFacesTypes.widget.PaginatorCfg>;
}

/**
 * __PrimeFaces DataList Widget__
 *
 * DataList presents a collection of data in list layout with several display types.
 *
 * __DataList is deprecated, use DataView instead.__
 *
 * @deprecated
 */
export class DataList<Cfg extends DataListCfg = DataListCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** DOM element of the content container for the data grid. */
    content: JQuery = $();
    /** When pagination is enabled: The paginator widget instance used for paging. */
    paginator?: PrimeFacesTypes.Widget<"Paginator">;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.cfg.formId = this.jq.parents('form').first().attr('id');
        this.content = $(this.jqId + '_content');

        if (this.cfg.paginator) {
            this.setupPaginator();
        }
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
     * Handles a pagination event by updating the data grid and invoking the appropriate behaviors.
     * @param newState The new pagination state to apply.
     */
    private handlePagination(newState: PrimeFacesTypes.widget.Paginator.PaginationState): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            formId: this.cfg.formId,
            params: [
                { name: this.getId() + '_pagination', value: true },
                { name: this.getId() + '_skipChildren', value: true },
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
