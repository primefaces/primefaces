          /**
 * PrimeFaces ColumnToggler Widget
 */
PrimeFaces.widget.ColumnToggler = PrimeFaces.widget.DeferredWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.table = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.datasource);
        this.trigger = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.trigger);
        this.tableId = this.table.attr('id');
        this.thead = $(PrimeFaces.escapeClientId(this.tableId) + '_head');
        this.tbody = $(PrimeFaces.escapeClientId(this.tableId) + '_data');
        this.tfoot = $(PrimeFaces.escapeClientId(this.tableId) + '_foot');
        this.visible = false;
        
        this.render();
        this.bindEvents();
    },
    
    render: function() {
        this.columns = this.thead.find('> tr > th:not(.ui-static-column)');
        this.panel = $('<div></div>').attr('id', this.cfg.id).addClass('ui-columntoggler ui-widget ui-widget-content ui-shadow ui-corner-all')
                .append('<ul class="ui-columntoggler-items"></ul').appendTo(document.body);
        this.itemContainer = this.panel.children('ul');
                
        for(var i = 0; i < this.columns.length; i++) {
            var column = this.columns.eq(i),
            hidden = column.hasClass('ui-helper-hidden'),
            boxClass = hidden ? 'ui-chkbox-box ui-widget ui-corner-all ui-state-default' : 'ui-chkbox-box ui-widget ui-corner-all ui-state-default ui-state-active',
            iconClass = (hidden) ? 'ui-chkbox-icon ui-icon ui-icon-blank' : 'ui-chkbox-icon ui-icon ui-icon-check';
                    
            $('<li class="ui-columntoggler-item">' + 
                    '<div class="ui-chkbox ui-widget">' +
                    '<div class="' + boxClass + '"><span class="' + iconClass + '"></span></div>' + 
                    '</div>'
                    + '<label>' + column.children('.ui-column-title').text() + '</label></li>').data('column', column.attr('id')).appendTo(this.itemContainer);
        }
        
        if(this.panel.outerHeight() > 200) {
            this.panel.height(200);
        }
        this.hide();
    },
    
    bindEvents: function() {
        var $this = this,
        hideNS = 'mousedown.' + this.id,
        resizeNS = 'resize.' + this.id;
        
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
            
        //hide overlay when outside is clicked
        $(document.body).off(hideNS).on(hideNS, function (e) {        
            if(!$this.visible) {
                return;
            }

            //do nothing on trigger mousedown
            var target = $(e.target);
            if($this.trigger.is(target)||$this.trigger.has(target).length) {
                return;
            }

            //hide the panel and remove focus from label
            var offset = $this.panel.offset();
            if(e.pageX < offset.left ||
                e.pageX > offset.left + $this.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.panel.height()) {

                $this.hide();
            }
        });

        //Realign overlay on resize
        $(window).off(resizeNS).on(resizeNS, function() {
            if($this.visible) {
                $this.alignPanel();
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
        
        var index = $(document.getElementById(chkbox.closest('li.ui-columntoggler-item').data('column'))).index() + 1,
        columnHeader = this.thead.children('tr').find('th:nth-child(' + index + ')');

        columnHeader.removeClass('ui-helper-hidden');
        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).removeClass('ui-helper-hidden');
        this.tbody.children('tr').find('td:nth-child(' + index + ')').removeClass('ui-helper-hidden');
        this.tfoot.children('tr').find('td:nth-child(' + index + ')').removeClass('ui-helper-hidden');
        
        this.fireToggleEvent(true, (index - 1));
    },
    
    uncheck: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
        
        var index = $(document.getElementById(chkbox.closest('li.ui-columntoggler-item').data('column'))).index() + 1,
        columnHeader = this.thead.children('tr').find('th:nth-child(' + index + ')');
        
        columnHeader.addClass('ui-helper-hidden');
        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).addClass('ui-helper-hidden');
        this.tbody.children('tr').find('td:nth-child(' + index + ')').addClass('ui-helper-hidden');
        this.tfoot.children('tr').find('td:nth-child(' + index + ')').addClass('ui-helper-hidden');

        this.fireToggleEvent(false, (index - 1));
    },
    
    alignPanel: function() {
        this.panel.css({'left':'', 'top':'', 'z-index': ++PrimeFaces.zindex}).position({
                            my: 'left top'
                            ,at: 'left bottom'
                            ,of: this.trigger
                        });
                        
        if(!this.widthAligned && (this.panel.width() < this.trigger.width())) {
            this.panel.width(this.trigger.width());
            this.widthAligned = true;
        }
    },
    
    show: function() {
        this.alignPanel();
        this.panel.show();
        this.visible = true;
    },
    
    hide: function() {
		this.panel.fadeOut('fast');
        this.visible = false;
    },
    
    fireToggleEvent: function(visible, index) {
        if(this.cfg.behaviors) {
            var toggleBehavior = this.cfg.behaviors['toggle'];

            if(toggleBehavior) {
                var visibility = visible ? 'VISIBLE' : 'HIDDEN',
                ext = {
                    params: [
                        {name: this.id + '_visibility', value: visibility},
                        {name: this.id + '_index', value: index}
                    ]
                };

                toggleBehavior.call(this, ext);
            }
        }
    }

});  