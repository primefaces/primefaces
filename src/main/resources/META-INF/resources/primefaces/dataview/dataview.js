/*
 * PrimeFaces DataView Widget
 */
PrimeFaces.widget.DataView = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.header = this.jq.children('.ui-dataview-header');
        this.content = this.jq.children('.ui-dataview-content');
        this.layoutOptions = this.header.children('.ui-dataview-layout-options');
        this.buttons = this.layoutOptions.children('div');
        this.cfg.formId = $(this.jqId).closest('form').attr('id');

        if(this.cfg.paginator) {
            this.setupPaginator();
        }

        this.bindEvents();
    },

    setupPaginator: function() {
        var $this = this;
        this.cfg.paginator.paginate = function(newState) {
            $this.handlePagination(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
    },

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

    select: function(button) {
        this.buttons.filter('.ui-state-active').removeClass('ui-state-active ui-state-hover').children(':radio').prop('checked', false);

        button.addClass('ui-state-active').children(':radio').prop('checked', true);

        this.loadLayoutContent(button.children(':radio').val());
    },

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

    handlePagination: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
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

    getPaginator: function() {
        return this.paginator;
    }
});