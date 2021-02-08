/**
 * __PrimeFaces DataView Widget__
 * 
 * DataView displays data in grid or list layout.
 * 
 * @typedef {"grid" | "list"} PrimeFaces.widget.DataView.Layout The layout mode the data view. `grid` displays the
 * item in a grid with cards, `list` displays the items in a vertical list.
 * 
 * @prop {JQuery} buttons DOM elements of the buttons for switching the layout (grid or list).
 * @prop {JQuery} content DOM element of the content container for the data grid.
 * @prop {JQuery} header DOM element of the data view header. 
 * @prop {JQuery} layoutOptions DOM element of the container with the layout switch buttons.
 * @prop {PrimeFaces.widget.Paginator} paginator When pagination is enabled: The paginator widget instance used for
 * paging.
 * 
 * @interface {PrimeFaces.widget.DataViewCfg} cfg The configuration for the {@link  DataView| DataView widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.formId ID of the form to use for AJAX requests.
 * @prop {Partial<PrimeFaces.widget.PaginatorCfg>} cfg.paginator When pagination is enabled: The paginator configuration
 * for the paginator.
 */
PrimeFaces.widget.DataView = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.header = this.jq.children('.ui-dataview-header');
        this.content = this.jq.children('.ui-dataview-content');
        this.layoutOptions = this.header.children('.ui-dataview-layout-options');
        this.buttons = this.layoutOptions.children('div');

        if(this.cfg.paginator) {
            this.setupPaginator();
        }

        this.bindEvents();
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
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function () {
        var $this = this;

        this.buttons.on('mouseover', function() {
            var button = $(this);
            button.addClass('ui-state-hover');
        })
        .on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click', function() {
            var button = $(this),
            radio = button.children(':radio');

            if(!radio.prop('checked')) {
                $this.select(button);
            }
        });

        /* For keyboard accessibility */
        this.buttons.on('focus.dataview-button', function(){
            var button = $(this);
            button.addClass('ui-state-focus');
        })
        .on('blur.dataview-button', function(){
            var button = $(this);
            button.removeClass('ui-state-focus');
        })
        .on('keydown.dataview-button', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER) {
                var button = $(this),
                radio = button.children(':radio');

                if(!radio.prop('checked')) {
                    $this.select(button);
                }
                e.preventDefault();
            }
        });
    },

    /**
     * Switches this data view to the given layout (grid or list).
     * 
     * ```javascript
     * const widget = PF("MyDataView");
     * // Switch to grid layout
     * widget.select(widget.buttons.eq(1));
     * ```
     * @param {JQuery} button One of the layout switch buttons (`.ui-button`).
     */
    select: function(button) {
        this.buttons.filter('.ui-state-active').removeClass('ui-state-active ui-state-hover').children(':radio').prop('checked', false);

        button.addClass('ui-state-active').children(':radio').prop('checked', true);

        this.loadLayoutContent(button.children(':radio').val());
    },

    /**
     * Loads the content with the data items for the selected layout (grid or list).
     * @private
     * @param {PrimeFaces.widget.DataView.Layout} layout The current layout of this data view.
     */
    loadLayoutContent: function(layout) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_layout', value: layout}
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
                $this.jq.removeClass('ui-dataview-grid ui-dataview-list').addClass('ui-dataview-' + layout);
            }
        };

        PrimeFaces.ajax.Request.handle(options);
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