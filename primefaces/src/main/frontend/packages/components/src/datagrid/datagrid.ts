import type { PaginationState, PaginatorCfg } from "../paginator/paginator.js";

/**
 * The configuration for the {@link DataGrid} component.
 */
export interface DataGridCfg extends PrimeFaces.component.BaseComponentCfg {
    /** When pagination is enabled: The paginator configuration for the paginator. */
    paginator: PrimeFaces.component.PartialComponentCfg<PaginatorCfg>;
}

/**
 * __PrimeFaces DataGrid Component__
 *
 * DataGrid displays a collection of data in a grid layout.
 *
 * __DataGrid is deprecated, use DataView instead.__
 *
 * @deprecated
 */
export class DataGrid<Cfg extends DataGridCfg = DataGridCfg> extends PrimeFaces.component.BaseComponent<Cfg> {
    /** DOM element of the content container for the data grid. */
    content: JQuery = $();
    /** When pagination is enabled: The paginator component instance used for paging. */
    paginator?: PrimeFaces.Component<"Paginator">;

    override init(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);

        this.content = $(this.jqId + '_content');

        if (this.cfg.paginator) {
            this.setupPaginator();
        }
    }

    /**
     * Initializes the paginator, called during component initialization.
     */
    private setupPaginator(): void {
        this.cfg.paginator!.paginate = (newState: PaginationState) => {
            this.handlePagination(newState);
        };

        this.paginator = new PrimeFaces.component.Paginator();
        this.paginator.init(this.cfg.paginator!);
        this.paginator.bindSwipeEvents(this.jq, this.cfg);
    }

    /**
     * Handles a pagination event by updating the data grid and invoking the appropriate behaviors.
     * @param newState The new pagination state to apply.
     */
    private handlePagination(newState: PaginationState): void {
        const options: PrimeFaces.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            formId: this.getParentFormId(),
            params: [
                { name: this.getId() + '_pagination', value: true },
                { name: this.getId() + '_skipChildren', value: true },
                { name: this.getId() + '_first', value: newState.first },
                { name: this.getId() + '_rows', value: newState.rows }
            ],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    component: this,
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
     * Retrieves the paginator component used by this data grid for pagination. You can use this component to switch to a
     * different page programmatically.
     * @returns The paginator component, or `undefined` when pagination is not enabled.
     */
    getPaginator(): PrimeFaces.Component<"Paginator"> | undefined {
        return this.paginator;
    }
}
