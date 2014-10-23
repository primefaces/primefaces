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
        this.bindSortEvents();
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
    
    shouldSort: function(event) {
        if(this.isEmpty()) {
            return false;
        }
                
        return $(event.target).is('th,span');
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