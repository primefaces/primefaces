/**
 * __PrimeFaces DataGrid Widget__
 * 
 * DataGrid displays a collection of data in a grid layout.
 *
 * __DataGrid is deprecated, use DataView instead.__
 * 
 * @deprecated
 * 
 * @prop {JQuery} content DOM element of the content container for the data grid.
 * @prop {PrimeFaces.widget.Paginator} paginator When pagination is enabled: The paginator widget instance used for
 * paging.
 * 
 * @interface {PrimeFaces.widget.DataGridCfg} cfg The configuration for the {@link  DataGrid| DataGrid widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.formId ID of the form to use for AJAX requests.
 * @prop {Partial<PrimeFaces.widget.PaginatorCfg>} cfg.paginator When pagination is enabled: The paginator configuration
 * for the paginator.
 */
PrimeFaces.widget.DataGrid = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.content = $(this.jqId + '_content');

        if(this.cfg.paginator) {
            this.setupPaginator();
        }
    },

    /**
     * Initializes the paginator, called during widget initialization.
     * @private
     */
    setupPaginator: function() {
        var $this = this;
        this.cfg.paginator.paginate = function(newState) {
            $this.handlePagination(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
        this.paginator.bindSwipeEvents(this.jq, this.cfg);
    },

    /**
     * Handles a pagination event by updating the data grid and invoking the appropriate behaviors.
     * @private
     * @param {PrimeFaces.widget.Paginator.PaginationState} newState The new pagination state to apply. 
     */
    handlePagination: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.getParentFormId(),
            params: [
                {name: this.id + '_pagination', value: true},
                {name: this.id + '_skipChildren', value: true},
                {name: this.id + '_first', value: newState.first},
                {name: this.id + '_rows', value: newState.rows}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.paginator.cfg.page = newState.page;
                $this.paginator.updateUI();
            }
        };

        if(this.hasBehavior('page')) {
            this.callBehavior('page', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Retrieves the paginator widget used by this data grid for pagination. You can use this widget to switch to a
     * different page programatically.
     * @return {PrimeFaces.widget.Paginator | undefined} The paginator widget, or `undefined` when pagination is not
     * enabled.
     */
    getPaginator: function() {
        return this.paginator;
    }

});