/**
 * PrimeFaces Mobile DataTable Widget
 */
PrimeFaces.widget.DataTable = PrimeFaces.widget.BaseWidget.extend({
    
    SORT_ORDER: {
        ASCENDING: 1,
        DESCENDING: -1,
        UNSORTED: 0
    },
    
    init: function(cfg) {
        this._super(cfg);
        this.thead = $(this.jqId + '_head');
        this.tbody = $(this.jqId + '_data');
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        if(this.cfg.paginator) {
            this.bindPaginator();
        }
        
        this.bindSortEvents();
        
        if(this.cfg.selectionMode) {
            this.bindSelection();
        }
        
        this.bindMobileEvents();
    },
    
    bindPaginator: function() {
        var $this = this;
        this.cfg.paginator.paginate = function(newState) {
            $this.paginate(newState);
        };

        this.paginator = new PrimeFaces.widget.Paginator(this.cfg.paginator);
    },
    
    bindSortEvents: function() {
        var $this = this;
        this.sortableColumns = this.thead.find('> tr > th.ui-sortable-column');

        for(var i = 0; i < this.sortableColumns.length; i++) {
            var columnHeader = this.sortableColumns.eq(i),
            sortIcon = columnHeader.children('span.ui-sortable-column-icon'),
            sortOrder = null;
    
            if(sortIcon.hasClass('ui-column-sorted')) {
                if(sortIcon.hasClass('ui-icon-arrow-u'))
                    sortOrder = this.SORT_ORDER.ASCENDING;
                else
                    sortOrder = this.SORT_ORDER.DESCENDING;
            }
            else {
                sortOrder = this.SORT_ORDER.UNSORTED;
            }
            
            columnHeader.data('sortorder', sortOrder);
        }
        
        this.sortableColumns.on('click.dataTable', function(e) {
            if(!$this.shouldSort(e, this)) {
                return;
            }

            PrimeFaces.clearSelection();
                            
            var columnHeader = $(this),
            sortOrderData = columnHeader.data('sortorder'),
            sortOrder = (sortOrderData === $this.SORT_ORDER.UNSORTED) ? $this.SORT_ORDER.ASCENDING : -1 * sortOrderData;
            
            $this.sort(columnHeader, sortOrder);
        });
    },
    
    bindMobileEvents: function() {
        if(this.cfg.behaviors) {
            var $this = this,
            rowSelector = '> tr:not(.ui-datatable-empty-message)';
            
            $.each(this.cfg.behaviors, function(eventName, fn) {
                $this.tbody.off(eventName, rowSelector).on(eventName, rowSelector, null, function() {
                    var rowMeta = $this.getRowMeta($(this));
        
                    var ext = {
                        params: [{name: $this.id + '_rowkey', value: rowMeta.key}]
                    };
                            
                    fn.call($this, ext);
                });
            });
        }
    },
    
    shouldSort: function(event) {
        if(this.isEmpty()) {
            return false;
        }
                
        return $(event.target).is('th,span');
    },
    
    paginate: function(newState) {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId,
            params: [{name: this.id + '_pagination', value: true},
                    {name: this.id + '_first', value: newState.first},
                    {name: this.id + '_rows', value: newState.rows},
                    {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.updateData(content);
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
            var pageBehavior = this.cfg.behaviors['page'];

            pageBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.Request.handle(options); 
        }
    },
    
    sort: function(columnHeader, order) {  
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            params: [{name: this.id + '_sorting', value: true},
                     {name: this.id + '_skipChildren', value: true},
                     {name: this.id + '_encodeFeature', value: true},
                     {name: this.id + '_sortKey', value: columnHeader.attr('id')},
                     {name: this.id + '_sortDir', value: order}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.updateData(content);

                            if(this.paginator) {
                                this.paginator.setPage(0, true);
                            }
                                                        
                            this.sortableColumns.filter('.ui-column-sorted').data('sortorder', this.SORT_ORDER.UNSORTED).removeClass('ui-column-sorted')
                                            .find('.ui-sortable-column-icon').removeClass('ui-icon-arrow-d ui-icon-arrow-u').addClass('ui-icon-bars');

                            columnHeader.data('sortorder', order).addClass('ui-column-sorted');
                            
                            var sortIcon = columnHeader.find('.ui-sortable-column-icon');
                            if(order === this.SORT_ORDER.DESCENDING) {
                                sortIcon.removeClass('ui-icon-bars ui-icon-arrow-u').addClass('ui-icon-arrow-d');
                            } else if(order === this.SORT_ORDER.ASCENDING) {
                                sortIcon.removeClass('ui-icon-bars ui-icon-arrow-d').addClass('ui-icon-arrow-u');
                            }
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args) {           
                if($this.paginator && args && $this.paginator.cfg.rowCount !== args.totalRecords) {
                    $this.paginator.setTotalRecords(args.totalRecords);
                }
            }
        };
        
        options.params.push({name: this.id + '_sortKey', value: columnHeader.attr('id')});
        options.params.push();

        if(this.hasBehavior('sort')) {
            var sortBehavior = this.cfg.behaviors['sort'];

            sortBehavior.call(this, options);
        } 
        else {
            PrimeFaces.ajax.Request.handle(options); 
        }
    },
    
    bindSelection: function() {
        var $this = this;
        this.selectionHolder = $(this.jqId + '_selection');
        this.rowSelector = '> tr.ui-datatable-selectable';

        var preselection = this.selectionHolder.val();
        this.selection = (preselection === "") ? [] : preselection.split(',');

        this.tbody.off('click.dataTable', this.rowSelector).on('click.dataTable', this.rowSelector, null, function(e) {
            $this.onRowClick(e, this);
        });
    },
    
    onRowClick: function(event, rowElement) { 
        //Check if rowclick triggered this event not a clickable element in row content
        if($(event.target).is('td,span:not(.ui-c)')) {
            var row = $(rowElement),
            selected = row.hasClass('ui-bar-b');
    
            if(selected) {
                this.unselectRow(row);
            }
            else {
                if(this.cfg.selectionMode === 'single') {
                    this.unselectAllRows();
                }
                
                this.selectRow(row);
            }
        }
    },
    
    selectRow: function(r, silent) {
        var row = this.findRow(r),
        rowMeta = this.getRowMeta(row);

        row.addClass('ui-bar-b');
        
        this.addSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowSelectEvent(rowMeta.key, 'rowSelect');
        }
    },
    
    unselectRow: function(r, silent) {
        var row = this.findRow(r),
        rowMeta = this.getRowMeta(row);

        row.removeClass('ui-bar-b');
        
        this.removeSelection(rowMeta.key);

        this.writeSelections();

        if(!silent) {
            this.fireRowUnselectEvent(rowMeta.key, "rowUnselect");
        }
    },
    
    unselectAllRows: function() {
        this.tbody.children('tr.ui-bar-b').removeClass('ui-bar-b');
        
        this.selection = [];
        this.writeSelections();
    },
    
    /**
     * Sends a rowSelectEvent on server side to invoke a rowSelectListener if defined
     */
    fireRowSelectEvent: function(rowKey, behaviorEvent) {
        if(this.cfg.behaviors) {
            var selectBehavior = this.cfg.behaviors[behaviorEvent];

            if(selectBehavior) {
                var ext = {
                        params: [{name: this.id + '_instantSelectedRowKey', value: rowKey}
                    ]
                };

                selectBehavior.call(this, ext);
            }
        }
    },
    
    /**
     * Sends a rowUnselectEvent on server side to invoke a rowUnselectListener if defined
     */
    fireRowUnselectEvent: function(rowKey, behaviorEvent) {
        if(this.cfg.behaviors) {
            var unselectBehavior = this.cfg.behaviors[behaviorEvent];

            if(unselectBehavior) {
                var ext = {
                    params: [
                    {
                        name: this.id + '_instantUnselectedRowKey', 
                        value: rowKey
                    }
                    ]
                };

                unselectBehavior.call(this, ext);
            }
        }
    },
    
    writeSelections: function() {
        this.selectionHolder.val(this.selection.join(','));
    },
    
    findRow: function(r) {
        var row = r;

        if(PrimeFaces.isNumber(r)) {
            row = this.tbody.children('tr:eq(' + r + ')');
        }

        return row;
    },
    
    /**
     * Remove given rowIndex from selection
     */
    removeSelection: function(rowIndex) {        
        this.selection = $.grep(this.selection, function(value) {
            return value != rowIndex;
        });
    },
    
    /**
     * Adds given rowIndex to selection if it doesn't exist already
     */
    addSelection: function(rowIndex) {
        if(!this.isSelected(rowIndex)) {
            this.selection.push(rowIndex);
        }
    },
    
    getRowMeta: function(row) {
        var meta = {
            index: row.data('ri'),
            key:  row.attr('data-rk')
        };

        return meta;
    },
    
    /**
     * Finds if given rowIndex is in selection
     */
    isSelected: function(rowIndex) {
        return PrimeFaces.inArray(this.selection, rowIndex);
    },
        
    isEmpty: function() {
        return this.tbody.children('tr.ui-datatable-empty-message').length === 1;
    },
    
    updateData: function(data) {
        this.tbody.html(data);
        
        this.postUpdateData();
    },
    
    postUpdateData: function() {
        if(this.cfg.draggableRows) {
            this.makeRowsDraggable();
        } 
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }
    
        return false;
    }
    
});