/**
 * PrimeFaces ColumnToggler Widget
 */
PrimeFaces.widget.ColumnToggler = PrimeFaces.widget.DeferredWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.table = $(PrimeFaces.escapeClientId(this.cfg.datasource));
        this.thead = $(PrimeFaces.escapeClientId(this.cfg.datasource) + '_head');
        this.tbody = $(PrimeFaces.escapeClientId(this.cfg.datasource) + '_data');
        this.trigger = $(PrimeFaces.escapeClientId(this.cfg.trigger));
        
        this.render();
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        this.trigger.off('click.ui-columntoggler').on('click.ui-columntoggler', function(e) {
            if($this.panel.is(':visible'))
                $this.hide();
            else
                $this.show();
        });
        
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
    },
    
    render: function() {
        this.columns = this.thead.find('> tr > th:visible');
        this.panel = $('<div></div>').attr('id', this.cfg.id).addClass('ui-columntoggler ui-widget ui-widget-content ui-shadow ui-corner-all')
                .append('<ul class="ui-columntoggler-items"></ul').appendTo(document.body);
        this.itemContainer = this.panel.children('ul');
        
        for(var i = 0; i < this.columns.length; i++) {
            this.itemContainer.append('<li class="ui-columntoggler-item">' + 
                    '<div class="ui-chkbox ui-widget">' +
                    '<div class="ui-chkbox-box ui-widget ui-corner-all ui-state-default ui-state-active"><span class="ui-chkbox-icon ui-icon ui-icon-check"></span></div>' + 
                    '</div>'
                    + '<label>' + this.columns.eq(i).children('.ui-column-title').text() + '</label></li>');
        }
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
        chkbox.addClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon ui-icon-check');
        
        var index = chkbox.closest('li.ui-columntoggler-item').index() + 1;
        
        this.thead.children('tr').find('th:nth-child(' + index + ')').show();
        this.tbody.children('tr').find('td:nth-child(' + index + ')').show();
    },
    
    uncheck: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon ui-icon-check');
        
        var index = chkbox.closest('li.ui-columntoggler-item').index() + 1;
        
        this.thead.children('tr').find('th:nth-child(' + index + ')').hide();
        this.tbody.children('tr').find('td:nth-child(' + index + ')').hide();
    },
    
    alignPanel: function() {
        this.panel.css({left:'', top:''}).position({
                            my: 'left top'
                            ,at: 'left bottom'
                            ,of: this.trigger
                        });
    },
    
    show: function() {
        this.alignPanel();
        this.panel.show();
    },
    
    hide: function() {
        this.panel.hide();
    }

});