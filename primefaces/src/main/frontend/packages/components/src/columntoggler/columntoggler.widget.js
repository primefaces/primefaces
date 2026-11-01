/**
 * __PrimeFaces ColumnToggler Widget__
 *
 * ColumnToggler is a helper component for the data table to toggle visibility of columns.
 *
 * @prop {JQuery} [closer] DOM element of the close button for closing the overlay with the available columns.
 * @prop {JQuery} [columns] DOM elements for the `TH` columns of the data table.
 * @prop {JQuery} [itemContainer] DOM elements for the `UL` items in the overlay with the available columns.
 * @prop {JQuery} [selectAllCheckbox] DOM element for the select all checkbox.
 * @prop {number} [frozenColumnCount] The number of frozen column of table to which this column toggle is
 * attached.
 * @prop {boolean} hasFrozenColumn Whether the table to which this column toggle is attached has got any frozen columns.
 * @prop {boolean} [hasPriorityColumns] Whether any prioritized columns exist. Used for responsive mode. 
 * @prop {boolean} hasStickyHeader Whether the table to which this column toggle is attached has got a sticky header.
 * @prop {JQuery} [panel] Overlay column toggler panel with the available columns.
 * @prop {JQuery} table Table to which this column toggle is attached.
 * @prop {string} tableId ID of the table to which this column toggle is attached.
 * @prop {PrimeFaces.widget.BaseWidget} tableWidget Widget of the table to which this column toggle is attached.
 * @prop {JQuery} tbody The DOM element for the table body of the table to which this column toggle is attached.
 * @prop {JQuery} tfoot The DOM element for the table foot of the table to which this column toggle is attached.
 * @prop {JQuery} thead The DOM element for the table head of the table to which this column toggle is attached.
 * @prop {string[]} [togglerState] IDs of the columns that are selected.
 * @prop {JQuery} [togglerStateHolder] DOM element of the hidden input that contains the columns that are
 * selected. Used to preserve that state between AJAX updates.
 * @prop {JQuery} trigger Button that toggles this column toggler.
 * @prop {boolean} visible Whether this column toggler is currently displayed.
 * @prop {boolean} [widthAligned] Whether the width of the overlay panel with the available columns was
 * aligned with the width of the toggler.    
 *
 * @interface {PrimeFaces.widget.ColumnTogglerCfg} cfg The configuration for the
 * {@link  ColumnToggler| ColumnToggler widget}. You can access this configuration via
 * {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this configuration is usually meant to be
 * read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {string} cfg.trigger ID of the button that toggles this column toggler.
 * @prop {string} cfg.datasource ID of the component (table) to which this column toggler is attached.
 * @prop {boolean} [cfg.showSelectAll] Whether to show the select all checkbox. Defaults to `true`.
 */
PrimeFaces.widget.ColumnToggler = class ColumnToggler extends PrimeFaces.widget.DeferredWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.table = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.datasource);
        this.trigger = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.trigger);
        this.tableId = this.table.attr('id');
        this.hasFrozenColumn = this.table.hasClass('p-datatable-frozencolumn');
        this.hasStickyHeader = this.table.hasClass('p-datatable-sticky');
        
        var clientId = PrimeFaces.escapeClientId(this.tableId);
        if (this.hasFrozenColumn) {
            this.thead = $(clientId + '_frozenThead,' + clientId + '_scrollableThead');
            this.tbody = $(clientId + '_frozenTbody,' + clientId + '_scrollableTbody');
            this.tfoot = $(clientId + '_frozenTfoot,' + clientId + '_scrollableTfoot');
            this.frozenColumnCount = this.thead.eq(0).find('th').length;
        }
        else {
            this.thead = $(clientId + '_head');
            this.tbody = $(clientId + '_data');
            this.tfoot = $(clientId + '_foot');
        }
        this.visible = false;

        this.render();
        this.bindEvents();
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        var jqs = $('[id=' + cfg.id.replace(/:/g, "\\:") + ']');
        if (jqs.length > 1) {
            $(document.body).children(this.jqId).remove();
        }

        this.widthAligned = false;

        super.refresh(cfg);
    }

    /**
     * @override
     * @inheritdoc
     */
    render() {
        this.jq.empty();
        this.columns = this.thead.find('> tr > th:not(.p-static-column)');
        this.panel = $(PrimeFaces.escapeClientId(this.cfg.id)).attr('role', 'dialog').addClass('p-columntoggler p-widget p-widget-content')
            .append('<ul class="p-columntoggler-items" role="group"></ul>').appendTo(document.body);
        this.itemContainer = this.panel.children('ul');

        var stateHolderId = this.tableId + "_columnTogglerState";
        this.togglerStateHolder = $(PrimeFaces.escapeClientId(stateHolderId));
        if (this.togglerStateHolder.length === 0) {
            this.togglerStateHolder = $('<input type="hidden" id="' + stateHolderId + '" name="' + stateHolderId + '" autocomplete="off"></input>');
            this.table.append(this.togglerStateHolder);
        }
        this.togglerState = [];

        // select all checkbox
        var showSelectAll = this.cfg.showSelectAll !== false; // default to true
        if (showSelectAll) {
            this.selectAllCheckbox = $('<li class="p-columntoggler-all">' +
                '<div class="p-chkbox p-widget">' +
                '<div role="checkbox" tabindex="0" aria-checked="true" aria-label="' + this.getAriaLabel('selectAll') + '" class="p-chkbox-box p-widget p-state-default p-state-active">' +
                '<span class="p-chkbox-icon p-icon p-checkbox-icon p-checkbox-checked-icon"></span></div></div>' +
                '</li>');
            this.selectAllCheckbox.appendTo(this.itemContainer);
            this.selectAllCheckbox = this.selectAllCheckbox.find('> .p-chkbox > .p-chkbox-box');
        } else {
            this.selectAllCheckbox = null;
        }

        //items
        for (var i = 0; i < this.columns.length; i++) {
            var column = this.columns.eq(i),
                hidden = column.hasClass('p-hidden'),
                boxClass = hidden ? 'p-chkbox-box p-widget p-state-default' : 'p-chkbox-box p-widget p-state-default p-state-active',
                iconClass = (hidden) ? 'p-chkbox-icon p-icon p-checkbox-icon' : 'p-chkbox-icon p-icon p-checkbox-icon p-checkbox-checked-icon',
                columnChildren = column.children('.p-column-title').clone(),
                columnTogglerCheckboxId = this.tableId + "_columnTogglerChkbx" + i;
            columnChildren.find('script').remove(); // GitHub #9647 remove scripts
            columnChildren.find('.p-tooltip-text').remove(); // GitHub #10740 remove tooltips
            var columnTitle = columnChildren.text();

            var label = columnChildren.find('label');
            if (label.length) {
                columnTitle = label.text();
            }

            this.hasPriorityColumns = column.is('[class*="p-column-p-"]');

            var item = $('<li class="p-columntoggler-item">' +
                '<div class="p-chkbox p-widget">' +
                '<div role="checkbox" tabindex="0" aria-checked="' + !hidden + '" aria-labelledby="' + columnTogglerCheckboxId + '" class="' + boxClass + '">' +
                '<span class="' + iconClass + '"></span></div></div>' +
                '<label id="' + columnTogglerCheckboxId + '">' + PrimeFaces.escapeHTML(columnTitle) + '</label></li>').data('column', column.attr('id'));

            if (this.hasPriorityColumns) {
                var columnClasses = column.attr('class').split(' ');
                for (const columnClass of columnClasses) {
                    var pindex = columnClass.indexOf('p-column-p-');
                    if (pindex !== -1) {
                        item.addClass(columnClass.substring(pindex, pindex + 12));
                    }
                }
            }

            if (hidden && this.selectAllCheckbox) {
                this.selectAllCheckbox
                    .removeClass('p-state-active').attr('aria-label', this.getAriaLabel('unselectAll')).attr('aria-checked', 'false')
                    .children('.p-chkbox-icon').removeClass('p-checkbox-checked-icon');
            }

            item.appendTo(this.itemContainer);

            this.togglerState.push(column.attr('id') + '_' + !hidden);
        }

        this.togglerStateHolder.val(this.togglerState.join(','));

        //close icon
        this.closer = PrimeFaces.skinCloseAction($('<a href="#" class="p-columntoggler-close"><span class="p-icon p-close-icon p-columntoggler-close-icon"></span></a>'))
            .prependTo(this.panel);

        if (this.panel.outerHeight() > 200) {
            this.panel.height(200);
        }
        this.hide();
    }

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents() {
        var $this = this;

        //trigger
        this.trigger.off('click.p-columntoggler').on('click.p-columntoggler', function(e) {
            if ($this.visible)
                $this.hide();
            else
                $this.show();
        });

        //checkboxes
        this.itemContainer.find('> .p-columntoggler-item > .p-chkbox > .p-chkbox-box').on('mouseenter.columnToggler', function() {
            $(this).addClass('p-state-hover');
        }).on('mouseleave.columnToggler', function() {
            $(this).removeClass('p-state-hover');
        }).on('click.columnToggler', function(e) {
            $this.toggle($(this));
            e.preventDefault();
        });

        //select all
        if (this.selectAllCheckbox) {
            this.selectAllCheckbox.on('mouseenter.columnToggler', function() {
                $(this).addClass('p-state-hover');
            }).on('mouseleave.columnToggler', function() {
                $(this).removeClass('p-state-hover');
            }).on('focus.columnToggler', function() {
                $(this).addClass('p-state-focus');
            }).on('blur.columnToggler', function(e) {
                $(this).removeClass('p-state-focus');
            }).on('click.columnToggler', function(e) {
                $this.toggleAll();
                e.preventDefault();
            }).on('keydown.columnToggler', function(e) {
                switch (e.code) {
                    case 'Tab':
                        var inputs = $this.itemContainer.find('> .p-columntoggler-item > .p-chkbox > .p-chkbox-box');
                        var index = $(this).closest('li').index() - 1;
                        var targetIndex = e.shiftKey ? index - 1 : index + 1;

                        if (e.shiftKey) {
                            if ($this.closer.is(':visible')) {
                                $this.closer.trigger('focus');
                            } else {
                                inputs.eq($this.columns.length - 1).trigger('focus');
                            }
                        } else {
                            inputs.eq(targetIndex).trigger('focus');
                        }
                        e.preventDefault();
                        break;
                    case 'Enter':
                    case 'NumpadEnter':
                    case 'Space':
                        $this.toggleAll();
                        e.preventDefault();
                        break;
                    case 'Escape':
                        $this.closer.trigger('click');
                        e.preventDefault();
                        break;
                }
            });
        }

        //labels
        this.itemContainer.find('> .p-columntoggler-item > label').on('click.selectCheckboxMenu', function(e) {
            $this.toggle($(this).prev().children('.p-chkbox-box'));
            PrimeFaces.clearSelection();
            e.preventDefault();
        });

        //closer
        this.closer.on('click', function(e) {
            $this.hide();
            $this.trigger.trigger('focus');
            e.preventDefault();
        });

        this.bindKeyEvents();

        PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $this.panel,
            function() { return $this.trigger; },
            function(e, eventTarget) {
                if (!($this.panel.is(eventTarget) || $this.panel.has(eventTarget).length > 0)) {
                    $this.hide();
                }
            });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.panel, function() {
            $this.alignPanel();
        });
    }

    /**
     * Sets up the event listners for keyboard interaction.
     * @private
     */
    bindKeyEvents() {
        var $this = this,
            inputs = this.itemContainer.find('> li:not(.p-columntoggler-all) > div.p-chkbox > div.p-chkbox-box');

        this.trigger.off('focus.columnToggler blur.columnToggler keydown.columnToggler').on('focus.columnToggler', function() {
            $(this).addClass('p-state-focus');
        }).on('blur.columnToggler', function() {
            $(this).removeClass('p-state-focus');
        }).on('keydown.columnToggler', function(e) {
            switch (e.code) {
                case 'Enter':
                case 'NumpadEnter':
                case 'Space':
                    if ($this.visible)
                        $this.hide();
                    else
                        $this.show();

                    e.preventDefault();
                    break;

                case 'Tab':
                    if ($this.visible) {
                        var firstItem = $this.selectAllCheckbox ? 
                            $this.itemContainer.children('li:not(.p-state-disabled):first').find('div.p-chkbox-box') :
                            $this.itemContainer.children('li.p-columntoggler-item:not(.p-state-disabled):first').find('div.p-chkbox-box');
                        firstItem.trigger('focus');
                        e.preventDefault();
                    }
                    break;
            };
        });

        inputs.on('focus.columnToggler', function() {
            $(this).addClass('p-state-focus');
        }).on('blur.columnToggler', function(e) {
            $(this).removeClass('p-state-focus');
        }).on('keydown.columnToggler', function(e) {
            switch (e.code) {
                case 'Tab':
                    // subtract 1 if select all checkbox exists because it's the first item
                    var liIndex = $(this).closest('li').index();
                    var index = $this.selectAllCheckbox ? liIndex - 1 : liIndex;
                    var targetIndex = e.shiftKey ? index - 1 : index + 1;

                    if ($this.selectAllCheckbox && (e.shiftKey && index === 0 || !e.shiftKey && index === $this.columns.length - 1)) {
                        $this.selectAllCheckbox.trigger('focus');
                    } else if (e.shiftKey && index === 0) {
                        // If no select all checkbox and at first item, focus closer or last item
                        if ($this.closer.is(':visible')) {
                            $this.closer.trigger('focus');
                        } else {
                            inputs.eq($this.columns.length - 1).trigger('focus');
                        }
                    } else if (!e.shiftKey && index === $this.columns.length - 1) {
                        // If no select all checkbox and at last item, focus closer
                        if ($this.closer.is(':visible')) {
                            $this.closer.trigger('focus');
                        } else {
                            inputs.eq(0).trigger('focus');
                        }
                    } else {
                        inputs.eq(targetIndex).trigger('focus');
                    }
                    e.preventDefault();
                    break;
                case 'Enter':
                case 'NumpadEnter':
                case 'Space':
                    $this.toggle($(this));
                    e.preventDefault();
                    break;
                case 'Escape':
                    $this.closer.trigger('click');
                    e.preventDefault();
                    break;
            }
        }).on('change.columnToggler', function(e) {
            if ($(this).attr('aria-checked') === "true") {
                $this.check(box);
                $(this).removeClass('p-state-active');
            }
            else {
                $this.uncheck(box);
            }
        });

        this.closer.on('keydown.columnToggler', function(e) {
            switch (e.code) {
                case 'Enter':
                case 'NumpadEnter':
                case 'Space':
                case 'Escape':
                    $this.hide();
                    $this.trigger.trigger('focus');
                    e.preventDefault();
                    break;

                case 'Tab':
                    if (e.shiftKey) {
                        inputs.eq($this.columns.length - 1).trigger('focus');
                    } else {
                        if ($this.selectAllCheckbox) {
                            $this.selectAllCheckbox.trigger('focus');
                        } else {
                            inputs.eq(0).trigger('focus');
                        }
                    }
                    e.preventDefault();
                    break;
            };
        });
    }

    /**
     * Checks or unchecks the given checkbox for a column, depending on whether it is currently selected. Also shows or
     * hides  the column of the table to which this column toggler is attached.
     * @param {JQuery} chkbox Checkbox (`.p-chkbox-box`) of a column of this column toggler.
     */
    toggle(chkbox) {
        if (chkbox.hasClass('p-state-active')) {
            this.uncheck(chkbox);
        }
        else {
            this.check(chkbox);
        }
    }

    /**
     * Toggles selecting or deselecting all columns.
     */
    toggleAll() {
        if (!this.selectAllCheckbox) {
            return;
        }
        if (this.selectAllCheckbox.hasClass('p-state-active')) {
            this.uncheckAll();
        }
        else {
            this.checkAll();
        }
    }

    /**
     * Checks all columns to enable all.
     */
    checkAll() {
        var $this = this;
        this.itemContainer.find('> .p-columntoggler-item > .p-chkbox > .p-chkbox-box').each(function() {
            $this.check($(this));
        });

        if (this.selectAllCheckbox) {
            this.selectAllCheckbox
                .addClass('p-state-active').attr('aria-label', this.getAriaLabel('selectAll')).attr('aria-checked', 'true')
                .children('.p-chkbox-icon').addClass('p-checkbox-checked-icon');
        }
    }

    /**
     * Unchecks all columns to disable all.
     */
    uncheckAll() {
        var $this = this;
        this.itemContainer.find('> .p-columntoggler-item > .p-chkbox > .p-chkbox-box').each(function() {
            $this.uncheck($(this));
        });
    }

    /**
     * Checks the given checkbox for a column, so that the column is now selected. Also display the column of the table
     * to which this column toggler is attached.
     * @param {JQuery} chkbox Checkbox (`.p-chkbox-box`) of a column of this column toggler.
     */
    check(chkbox) {
        if (chkbox.hasClass('p-state-active')) {
            return;
        }
        chkbox.addClass('p-state-active').children('.p-chkbox-icon').addClass('p-checkbox-checked-icon');

        var column = $(document.getElementById(chkbox.closest('li.p-columntoggler-item').data('column'))),
            index = column.index() + 1,
            isFrozenColumn = column.hasClass('p-frozen-column'),
            thead, tbody, tfoot;
        if (this.hasFrozenColumn) {
            thead = isFrozenColumn ? this.thead.eq(0) : this.thead.eq(1);
            tbody = isFrozenColumn ? this.tbody.eq(0) : this.tbody.eq(1);
            tfoot = isFrozenColumn ? this.tfoot.eq(0) : this.tfoot.eq(1);
        } else {
            thead = this.thead;
            tbody = this.tbody;
            tfoot = this.tfoot;
        }

        var rowSelector = 'tr:not(.p-expanded-row-content)';
        var rowHeader = thead.children(rowSelector),
            columnHeader = rowHeader.find('th:nth-child(' + index + ')');

        chkbox.attr('aria-checked', true);
        columnHeader.removeClass('p-hidden');
        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).removeClass('p-hidden');
        tbody.children(rowSelector).children(':nth-child(' + index + ')').removeClass('p-hidden');
        tfoot.children(rowSelector).children(':nth-child(' + index + ')').removeClass('p-hidden');

        if (this.hasFrozenColumn) {
            var headers = rowHeader.children('th');
            if (headers.length !== headers.filter('.p-hidden').length) {
                thead.closest('td').removeClass('p-hidden');
            }

            if (!column.hasClass('p-frozen-column')) {
                index += this.frozenColumnCount;
            }
        }

        if (this.hasStickyHeader) {
            $(PrimeFaces.escapeClientId(columnHeader.attr('id'))).removeClass('p-hidden');
        }

        this.changeTogglerState(column, true);
        this.fireToggleEvent(true, (index - 1));
        this.updateColspan();
    }

    /**
     * Unchecks the given checkbox for a column, so that the column is now not selected. Also hides the column of the
     * table to which this column toggler is attached.
     * @param {JQuery} chkbox Checkbox (`.p-chkbox-box`) of a column of this column toggler.
     */
    uncheck(chkbox) {
        if (!chkbox.hasClass('p-state-active')) {
            return;
        }
        chkbox.removeClass('p-state-active').children('.p-chkbox-icon').removeClass('p-checkbox-checked-icon');

        if (this.selectAllCheckbox) {
            this.selectAllCheckbox
                .removeClass('p-state-active').attr('aria-label', this.getAriaLabel('unselectAll')).attr('aria-checked', 'false')
                .children('.p-chkbox-icon').removeClass('p-checkbox-checked-icon');
        }

        var column = $(document.getElementById(chkbox.closest('li.p-columntoggler-item').data('column'))),
            index = column.index() + 1,
            isFrozenColumn = column.hasClass('p-frozen-column'),
            thead, tbody, tfoot;
        if (this.hasFrozenColumn) {
            thead = isFrozenColumn ? this.thead.eq(0) : this.thead.eq(1);
            tbody = isFrozenColumn ? this.tbody.eq(0) : this.tbody.eq(1);
            tfoot = isFrozenColumn ? this.tfoot.eq(0) : this.tfoot.eq(1);
        } else {
            thead = this.thead;
            tbody = this.tbody;
            tfoot = this.tfoot;
        }

        var rowSelector = 'tr:not(.p-expanded-row-content)';
        var rowHeader = thead.children(rowSelector),
            columnHeader = rowHeader.find('th:nth-child(' + index + ')');

        chkbox.attr('aria-checked', false);
        columnHeader.addClass('p-hidden');
        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).addClass('p-hidden');
        tbody.children(rowSelector).children(':nth-child(' + index + ')').addClass('p-hidden').removeAttr('tabindex');
        tfoot.children(rowSelector).children(':nth-child(' + index + ')').addClass('p-hidden').removeAttr('tabindex');

        if (this.hasFrozenColumn) {
            var headers = rowHeader.children('th');
            if (headers.length === headers.filter(':hidden').length) {
                thead.closest('td').addClass('p-hidden');
            }

            if (!column.hasClass('p-frozen-column')) {
                index += this.frozenColumnCount;
            }
        }

        if (this.hasStickyHeader) {
            $(PrimeFaces.escapeClientId(columnHeader.attr('id'))).addClass('p-hidden');
        }

        this.changeTogglerState(column, false);
        this.fireToggleEvent(false, (index - 1));
        this.updateColspan();
    }

    /**
     * Aligns the overlay panel of this column toggler according to the current widget configuration.
     */
    alignPanel() {
        PrimeFaces.utils.position(this.panel.css({ 'left': '', 'top': '', 'z-index': PrimeFaces.nextZindex() }), {
            my: 'left top'
            , at: 'left bottom'
            , of: this.trigger
        });

        if (this.hasPriorityColumns) {
            if (this.panel.outerWidth() <= this.trigger.outerWidth()) {
                this.panel.css('width', 'auto');
            }

            this.widthAligned = false;
        }

        if (!this.widthAligned && (this.panel.outerWidth() < this.trigger.outerWidth())) {
            this.panel.width(this.trigger.width());
            this.widthAligned = true;
        }
    }

    /**
     * Brings up this column toggler so that the user can which column to hide or show.
     */
    show() {
        this.subscribeToColumnOrderChange();
        this.alignPanel();
        this.panel.show();
        this.visible = true;
        this.trigger.attr('aria-expanded', true);
        if (this.closer.is(':visible')) {
            this.closer.trigger('focus');
        }
        else if (this.selectAllCheckbox && this.selectAllCheckbox.is(':visible')) {
            this.selectAllCheckbox.trigger('focus');
        }
        else {
            var firstItem = this.itemContainer.find('> .p-columntoggler-item > .p-chkbox > .p-chkbox-box').first();
            if (firstItem.length) {
                firstItem.trigger('focus');
            }
        }
    }

    /**
     * Hides this column toggler.
     */
    hide() {
        // only fire event if columnToggler was really shown
        if (this.visible) {
            this.fireCloseEvent();
        }
        this.panel.fadeOut('fast');
        this.visible = false;
        this.trigger.attr('aria-expanded', false);
    }

    /**
     * Triggers the events listeners and behaviors when a column was selected or unselected.
     * @param {boolean} visible `true` if the column was selected, `false` otherwise.
     * @param {number} index Index of the toggled column.
     * @private
     */
    fireToggleEvent(visible, index) {
        if (index >= 0 && this.hasBehavior('toggle')) {
            var ext = {
                params: [
                    { name: this.id + '_visibility', value: visible ? 'VISIBLE' : 'HIDDEN' },
                    { name: this.id + '_index', value: index }
                ]
            };

            this.callBehavior('toggle', ext);
        }
    }

    /**
     * Triggers the events listeners and behaviors when the popup is closed.
     * @private
     */
    fireCloseEvent() {
        if (this.hasBehavior('close')) {
            var columnIds = '';
            for (var i = 0; i < this.columns.length; i++) {
                var column = this.columns.eq(i);
                var parts = column.attr('id').split(':');
                var columnId = parts[parts.length - 1];
                var hidden = column.hasClass('p-hidden');

                if (!hidden) {
                    if (columnIds != '') {
                        columnIds = columnIds + ',';
                    }
                    columnIds = columnIds + columnId;
                }

            }
            var ext = {
                params: [
                    { name: this.id + '_visibleColumnIds', value: columnIds }
                ]
            };

            this.callBehavior('close', ext);
        }
    }

    /**
     * Computes the required `colspan` for the rows.
     * @private
     * @return {number} The calculated `colspan` for the rows.
     */
    calculateColspan() {
	    const visibleColumns = this.itemContainer
            .find('> .p-columntoggler-item > .p-chkbox > .p-chkbox-box.p-state-active')
            .length;

        const visibleStaticColumns = this.thead
            .find('> tr > th.p-static-column:not(.p-hidden)')
            .length;

        return visibleColumns + visibleStaticColumns;
    }

    /**
     * Updates the `colspan` attribute fo the columns of the given row.
     * @private
     * @param {JQuery} row A row to update.
     * @param {string} colspanValue New value for the `colspan` attribute.
     */
    updateRowColspan(row, colspanValue) {
        colspanValue = colspanValue || this.calculateColspan();
        if (colspanValue) {
            row.children('td').removeClass('p-hidden').attr('colspan', colspanValue);
        }
        else {
            row.children('td').addClass('p-hidden');
        }
    }

    /**
     * Updates the colspan attributes of the target table of this column toggler. Called after a column was selected or
     * unselected, which resulted in a column of the data table to be shown or hidden.
     * @private
     */
    updateColspan() {
        var emptyRow = this.tbody.children('tr:first');
        if (emptyRow && emptyRow.hasClass('p-datatable-empty-message')) {
            this.updateRowColspan(emptyRow);
        }
        else {
            var colspanValue = this.calculateColspan(),
                $this = this;
            this.tbody.children('.p-expanded-row-content').each(function() {
                $this.updateRowColspan($(this), colspanValue);
            });
        }
    }

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render() {
        throw new Error('Unsupported Operation');
    }

    /**
     * Selects or unselect a column of this column toggler. Also shows or hides the corresponding colum of the table
     * to which this column toggler is attached.
     * @param {JQuery} column A column element (`LI`) of this column toggler.
     * @param {boolean} isHidden `true` to unselect the column and hide the corresponding table column, or `true`
     * otherwise.
     * @private
     */
    changeTogglerState(column, isHidden) {
        if (column && column.length) {
            var stateVal = this.togglerStateHolder.val(),
                columnId = column.attr('id'),
                oldColState = columnId + "_" + !isHidden,
                newColState = columnId + "_" + isHidden;
            this.togglerStateHolder.val(stateVal.replace(oldColState, newColState));

            // #12195 must reset the navigable cells to keyboard accessibility of hidden/shown columns
            const tableWidget = this.getTableWidget();
            if (tableWidget) {
                tableWidget.setupNavigableCells();
            }
        }
    }

    /**
     * Gets the table widget.
     * @private
     * @return {PrimeFaces.widget.DataTable} The table widget.
     */
    getTableWidget() {
        if (!this.tableWidget) {
            this.tableWidget = PrimeFaces.getWidgetsByType(PrimeFaces.widget.DataTable).find(widget => widget.id === this.tableId);
        }
        return this.tableWidget;
    }

    /**
     * Gets the table widget and subscribes to its saveColumnOrder method to call render() when column order changes.
     * @private
     */
    subscribeToColumnOrderChange() {
        if (this.tableWidget) {
            return;
        }
        const tableWidget = this.getTableWidget();
        if (tableWidget && tableWidget.saveColumnOrder) {
            const originalSaveColumnOrder = tableWidget.saveColumnOrder.bind(tableWidget);
            const $this = this;
            tableWidget.saveColumnOrder = function() {
                originalSaveColumnOrder();
                $this.refresh($this.cfg);
            };
        }
    }

}
