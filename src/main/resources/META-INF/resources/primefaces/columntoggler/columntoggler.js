/**
 * PrimeFaces ColumnToggler Widget
 */
PrimeFaces.widget.ColumnToggler = PrimeFaces.widget.DeferredWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.table = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.datasource);
        this.trigger = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.trigger);
        this.tableId = this.table.attr('id');
        this.hasFrozenColumn = this.table.hasClass('ui-datatable-frozencolumn');
        this.hasStickyHeader = this.table.hasClass('ui-datatable-sticky');
        var clientId = PrimeFaces.escapeClientId(this.tableId);

        if(this.hasFrozenColumn) {
            this.thead = $(clientId + '_frozenThead,' + clientId + '_scrollableThead');
            this.tbody = $(clientId + '_frozenTbody,' + clientId + '_scrollableTbody');
            this.tfoot = $(clientId + '_frozenTfoot,' + clientId + '_scrollableTfoot');
            this.frozenColumnCount = this.thead.eq(0).children('tr').length;
        }
        else {
            this.thead = $(clientId + '_head');
            this.tbody = $(clientId + '_data');
            this.tfoot = $(clientId + '_foot');
        }
        this.visible = false;

        this.render();
        this.bindEvents();
    },

    //@override
    refresh: function(cfg) {
        var jqs = $('[id=' + cfg.id.replace(/:/g,"\\:") + ']');
        if(jqs.length > 1) {
            $(document.body).children(this.jqId).remove();
        }

        this.widthAligned = false;

        this._super(cfg);
    },

    render: function() {
        this.columns = this.thead.find('> tr > th:not(.ui-static-column)');
        this.panel = $(PrimeFaces.escapeClientId(this.cfg.id)).attr('role', 'dialog').addClass('ui-columntoggler ui-widget ui-widget-content ui-shadow ui-corner-all')
                .append('<ul class="ui-columntoggler-items" role="group"></ul>').appendTo(document.body);
        this.itemContainer = this.panel.children('ul');

        var stateHolderId = this.tableId + "_columnTogglerState";
        this.togglerStateHolder = $('<input type="hidden" id="' + stateHolderId + '" name="' + stateHolderId + '" autocomplete="off" />');
        this.table.append(this.togglerStateHolder);
        this.togglerState = [];

        //items
        for(var i = 0; i < this.columns.length; i++) {
            var column = this.columns.eq(i),
            hidden = column.hasClass('ui-helper-hidden'),
            boxClass = hidden ? 'ui-chkbox-box ui-widget ui-corner-all ui-state-default' : 'ui-chkbox-box ui-widget ui-corner-all ui-state-default ui-state-active',
            iconClass = (hidden) ? 'ui-chkbox-icon ui-icon ui-icon-blank' : 'ui-chkbox-icon ui-icon ui-icon-check',
            columnChildren = column.children('.ui-column-title'),
            columnTitle = columnChildren.text(),
            columnTogglerCheckboxId = this.tableId + "_columnTogglerChkbx" + i;
            
            var label = columnChildren.find('label');
            if (label.length) {
                columnTitle = label.text();
            }

            this.hasPriorityColumns = column.is('[class*="ui-column-p-"]');

            var item = $('<li class="ui-columntoggler-item">' +
                    '<div class="ui-chkbox ui-widget">' +
                    '<div role="checkbox" tabindex="0" aria-checked="'+ !hidden + '" aria-labelledby="'+ columnTogglerCheckboxId + '" class="' + boxClass + '">' +
                    '<span class="' + iconClass + '"></span></div></div>' +
                    '<label id="' + columnTogglerCheckboxId + '">' + PrimeFaces.escapeHTML(columnTitle) + '</label></li>').data('column', column.attr('id'));

            if(this.hasPriorityColumns) {
                var columnClasses = column.attr('class').split(' ');
                for(var j = 0; j < columnClasses.length; j++) {
                    var columnClass = columnClasses[j],
                    pindex = columnClass.indexOf('ui-column-p-');
                    if(pindex !== -1) {
                        item.addClass(columnClass.substring(pindex , pindex + 13));
                    }
                }
            }

            item.appendTo(this.itemContainer);

            this.togglerState.push(column.attr('id') + '_' + !hidden);
        }

        this.togglerStateHolder.val(this.togglerState.join(','));

        //close icon
        this.closer = $('<a href="#" class="ui-columntoggler-close"><span class="ui-icon ui-icon-close"></span></a>')
                .attr('aria-label', PrimeFaces.getAriaLabel('columntoggler.CLOSE')).prependTo(this.panel);

        if(this.panel.outerHeight() > 200) {
            this.panel.height(200);
        }
        this.hide();
    },

    bindEvents: function() {
        var $this = this;

        //trigger
        this.trigger.off('click.ui-columntoggler').on('click.ui-columntoggler', function(e) {
            if($this.visible)
                $this.hide();
            else
                $this.show();
        });

        //checkboxes
        this.itemContainer.find('> .ui-columntoggler-item > .ui-chkbox > .ui-chkbox-box').on('mouseover.columnToggler', function() {
                var item = $(this);
                if(!item.hasClass('ui-state-active')) {
                    item.addClass('ui-state-hover');
                }
            })
            .on('mouseout.columnToggler', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.columnToggler', function(e) {
                $this.toggle($(this));
                e.preventDefault();
            });

        //labels
        this.itemContainer.find('> .ui-columntoggler-item > label').on('click.selectCheckboxMenu', function(e) {
            $this.toggle($(this).prev().children('.ui-chkbox-box'));
            PrimeFaces.clearSelection();
            e.preventDefault();
        });

        //closer
        this.closer.on('click', function(e) {
            $this.hide();
            $this.trigger.focus();
            e.preventDefault();
        });

        this.bindKeyEvents();

        PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $this.panel, null,
            function(e, eventTarget) {
                if(!($this.panel.is(eventTarget) || $this.panel.has(eventTarget).length > 0)) {
                    $this.hide();
                }
            });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.panel, function() {
            $this.alignPanel();
        });
    },

    bindKeyEvents: function() {
        var $this = this,
        inputs = this.itemContainer.find('> li > div.ui-chkbox > div.ui-chkbox-box');

        this.trigger.on('focus.columnToggler', function() {
            $(this).addClass('ui-state-focus');
        })
        .on('blur.columnToggler', function() {
            $(this).removeClass('ui-state-focus');
        })
        .on('keydown.columnToggler', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.ENTER:
                    if($this.visible)
                        $this.hide();
                    else
                        $this.show();

                    e.preventDefault();
                break;

                case keyCode.TAB:
                    if($this.visible) {
                        $this.itemContainer.children('li:not(.ui-state-disabled):first').find('div.ui-chkbox-box').trigger('focus');
                        e.preventDefault();
                    }
                break;
            };
        });

        inputs.on('focus.columnToggler', function() {
            $(this).addClass('ui-state-focus');
            //PrimeFaces.scrollInView($this.panel, box);
        })
        .on('blur.columnToggler', function(e) {
            $(this).removeClass('ui-state-focus');
        })
        .on('keydown.columnToggler', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.TAB:
                    var index = $(this).closest('li').index();
                    if(e.shiftKey) {
                        if(index === 0)
                            $this.closer.focus();
                        else
                            inputs.eq(index - 1).focus();
                    }
                    else {
                        if(index === ($this.columns.length - 1) && !e.shiftKey)
                            $this.closer.focus();
                        else
                            inputs.eq(index + 1).focus();
                    }

                    e.preventDefault();
                    break;
                case keyCode.ENTER:
                case keyCode.SPACE:
                    $this.toggle($(this));
                    e.preventDefault();
                    break;
            }
        })
        .on('change.columnToggler', function(e) {
            if($(this).attr('aria-checked')) {
                $this.check(box);
                $(this).removeClass('ui-state-active');
            }
            else {
                $this.uncheck(box);
            }
        });

        this.closer.on('keydown.columnToggler', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER)) {
                $this.hide();
                $this.trigger.focus();
                e.preventDefault();
            }
            else if(key === keyCode.TAB) {
                if(e.shiftKey)
                    inputs.eq($this.columns.length - 1).focus();
                else
                    inputs.eq(0).focus();

                e.preventDefault();
            }
        });
    },

    toggle: function(chkbox) {
        if(chkbox.hasClass('ui-state-active')) {
            this.uncheck(chkbox);
        }
        else {
            this.check(chkbox);
        }
    },

    check: function(chkbox) {
        chkbox.addClass('ui-state-active').removeClass('ui-state-hover').children('.ui-chkbox-icon').addClass('ui-icon-check').removeClass('ui-icon-blank');

        var column = $(document.getElementById(chkbox.closest('li.ui-columntoggler-item').data('column'))),
        index = column.index() + 1,
        thead = this.hasFrozenColumn ? (column.hasClass('ui-frozen-column') ? this.thead.eq(0) : this.thead.eq(1)) : this.thead,
        tbody = this.hasFrozenColumn ? (column.hasClass('ui-frozen-column') ? this.tbody.eq(0) : this.tbody.eq(1)) : this.tbody,
        tfoot = this.hasFrozenColumn ? (column.hasClass('ui-frozen-column') ? this.tfoot.eq(0) : this.tfoot.eq(1)) : this.tfoot;

        var rowHeader = thead.children('tr'),
        columnHeader = rowHeader.find('th:nth-child(' + index + ')');

        chkbox.attr('aria-checked', true);
        columnHeader.removeClass('ui-helper-hidden');
        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).removeClass('ui-helper-hidden');
        tbody.children('tr').find('td:nth-child(' + index + ')').removeClass('ui-helper-hidden');
        tfoot.children('tr').find('td:nth-child(' + index + ')').removeClass('ui-helper-hidden');

        if(this.hasFrozenColumn) {
            var headers = rowHeader.children('th');
            if(headers.length !== headers.filter('.ui-helper-hidden').length) {
                thead.closest('td').removeClass('ui-helper-hidden');
            }

            if(!column.hasClass('ui-frozen-column')) {
                index += this.frozenColumnCount;
            }
        }

        if(this.hasStickyHeader) {
            $(PrimeFaces.escapeClientId(columnHeader.attr('id'))).removeClass('ui-helper-hidden');
        }

        this.changeTogglerState(column, true);
        this.fireToggleEvent(true, (index - 1));
        this.updateColspan();
    },

    uncheck: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');

        var column = $(document.getElementById(chkbox.closest('li.ui-columntoggler-item').data('column'))),
        index = column.index() + 1,
        thead = this.hasFrozenColumn ? (column.hasClass('ui-frozen-column') ? this.thead.eq(0) : this.thead.eq(1)) : this.thead,
        tbody = this.hasFrozenColumn ? (column.hasClass('ui-frozen-column') ? this.tbody.eq(0) : this.tbody.eq(1)) : this.tbody,
        tfoot = this.hasFrozenColumn ? (column.hasClass('ui-frozen-column') ? this.tfoot.eq(0) : this.tfoot.eq(1)) : this.tfoot;

        var rowHeader = thead.children('tr'),
        columnHeader = rowHeader.find('th:nth-child(' + index + ')');

        chkbox.attr('aria-checked', false);
        columnHeader.addClass('ui-helper-hidden');
        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).addClass('ui-helper-hidden');
        tbody.children('tr').find('td:nth-child(' + index + ')').addClass('ui-helper-hidden');
        tfoot.children('tr').find('td:nth-child(' + index + ')').addClass('ui-helper-hidden');

        if(this.hasFrozenColumn) {
            var headers = rowHeader.children('th');
            if(headers.length === headers.filter(':hidden').length) {
                thead.closest('td').addClass('ui-helper-hidden');
            }

            if(!column.hasClass('ui-frozen-column')) {
                index += this.frozenColumnCount;
            }
        }

        if(this.hasStickyHeader) {
            $(PrimeFaces.escapeClientId(columnHeader.attr('id'))).addClass('ui-helper-hidden');
        }

        this.changeTogglerState(column, false);
        this.fireToggleEvent(false, (index - 1));
        this.updateColspan();
    },

    alignPanel: function() {
        this.panel.css({'left':'', 'top':'', 'z-index': ++PrimeFaces.zindex}).position({
                            my: 'left top'
                            ,at: 'left bottom'
                            ,of: this.trigger
                        });

        if(this.hasPriorityColumns) {
            if(this.panel.outerWidth() <= this.trigger.outerWidth()) {
                this.panel.css('width','auto');
            }

            this.widthAligned = false;
        }

        if(!this.widthAligned && (this.panel.outerWidth() < this.trigger.outerWidth())) {
            this.panel.width(this.trigger.width());
            this.widthAligned = true;
        }
    },

    show: function() {
        this.alignPanel();
        this.panel.show();
        this.visible = true;
        this.trigger.attr('aria-expanded', true);
        this.closer.trigger('focus');
    },

    hide: function() {
        this.panel.fadeOut('fast');
        this.visible = false;
        this.trigger.attr('aria-expanded', false);
    },

    fireToggleEvent: function(visible, index) {
        if(this.hasBehavior('toggle')) {
            var ext = {
                params: [
                    {name: this.id + '_visibility', value: visible ? 'VISIBLE' : 'HIDDEN'},
                    {name: this.id + '_index', value: index}
                ]
            };

            this.callBehavior('toggle', ext);
        }
    },

    updateColspan: function() {
        var emptyRow = this.tbody.children('tr:first');
        if(emptyRow && emptyRow.hasClass('ui-datatable-empty-message')) {
            var activeboxes = this.itemContainer.find('> .ui-columntoggler-item > .ui-chkbox > .ui-chkbox-box.ui-state-active');
            if(activeboxes.length) {
                emptyRow.children('td').removeClass('ui-helper-hidden').attr('colspan', activeboxes.length);
            }
            else {
                emptyRow.children('td').addClass('ui-helper-hidden');
            }
        }
    },

    changeTogglerState: function(column, isHidden) {
        if(column && column.length) {
            var stateVal = this.togglerStateHolder.val(),
            columnId = column.attr('id'),
            oldColState = columnId + "_" + !isHidden,
            newColState = columnId + "_" + isHidden;
            this.togglerStateHolder.val(stateVal.replace(oldColState, newColState));
        }
    }

});
