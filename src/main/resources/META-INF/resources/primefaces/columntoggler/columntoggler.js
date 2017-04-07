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
        this.panel = $('<div></div>').attr('id', this.cfg.id).attr('role', 'dialog').addClass('ui-columntoggler ui-widget ui-widget-content ui-shadow ui-corner-all')
                .append('<ul class="ui-columntoggler-items" role="group"></ul>').appendTo(document.body);
        this.itemContainer = this.panel.children('ul');
          
        //items
        for(var i = 0; i < this.columns.length; i++) {
            var column = this.columns.eq(i),
            hidden = column.hasClass('ui-helper-hidden'),
            boxClass = hidden ? 'ui-chkbox-box ui-widget ui-corner-all ui-state-default' : 'ui-chkbox-box ui-widget ui-corner-all ui-state-default ui-state-active',
            iconClass = (hidden) ? 'ui-chkbox-icon ui-icon ui-icon-blank' : 'ui-chkbox-icon ui-icon ui-icon-check',
            columnTitle = column.children('.ui-column-title').text();
    
            this.hasPriorityColumns = column.is('[class*="ui-column-p-"]');
                    
            var item = $('<li class="ui-columntoggler-item">' + 
                    '<div class="ui-chkbox ui-widget">' +
                    '<div class="ui-helper-hidden-accessible"><input type="checkbox" role="checkbox"></div>' +
                    '<div class="' + boxClass + '"><span class="' + iconClass + '"></span></div>' + 
                    '</div>'
                    + '<label>' + columnTitle + '</label></li>').data('column', column.attr('id'));
            
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
            
            if(!hidden) {
                item.find('> .ui-chkbox > .ui-helper-hidden-accessible > input').prop('checked', true).attr('aria-checked', true);
            }
            
            item.appendTo(this.itemContainer);
        }
        
        //close icon
        this.closer = $('<a href="#" class="ui-columntoggler-close"><span class="ui-icon ui-icon-close"></span></a>')
                .attr('aria-label', PrimeFaces.getAriaLabel('columntoggler.CLOSE')).prependTo(this.panel);
                       
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
        
        //closer
        this.closer.on('click', function(e) {
            $this.hide();
            $this.trigger.focus();
            e.preventDefault();
        });
            
        this.bindKeyEvents();
        
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
    
    bindKeyEvents: function() {
        var $this = this,
        inputs = this.itemContainer.find('> li > div.ui-chkbox > div.ui-helper-hidden-accessible > input');

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
                case keyCode.NUMPAD_ENTER:
                    if($this.visible)
                        $this.hide();
                    else
                        $this.show();

                    e.preventDefault();
                break;
                
                case keyCode.TAB:
                    if($this.visible) {
                        $this.itemContainer.children('li:not(.ui-state-disabled):first').find('div.ui-helper-hidden-accessible > input').trigger('focus');
                        e.preventDefault();
                    }
                break;    
            };
        });
        
        inputs.on('focus.columnToggler', function() {
            var input = $(this),
            box = input.parent().next();

            if(input.prop('checked')) {
                box.removeClass('ui-state-active');
            }

            box.addClass('ui-state-focus');
            
            //PrimeFaces.scrollInView($this.panel, box);
        })
        .on('blur.columnToggler', function(e) {
            var input = $(this),
            box = input.parent().next();

            if(input.prop('checked')) {
                box.addClass('ui-state-active');
            }

            box.removeClass('ui-state-focus');
        })
        .on('keydown.columnToggler', function(e) {
            if(e.which === $.ui.keyCode.TAB) {
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
            }
        })
        .on('change.columnToggler', function(e) {
            var input = $(this),
            box = input.parent().next();

            if(input.prop('checked')) {
                $this.check(box);
                box.removeClass('ui-state-active');
            }
            else {                      
                $this.uncheck(box);
            }
        });
        
        this.closer.on('keydown.columnToggler', function(e) {
            var key = e.which,
            keyCode = $.ui.keyCode;

            if((key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER)) {
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
        
        var index = $(document.getElementById(chkbox.closest('li.ui-columntoggler-item').data('column'))).index() + 1,
        columnHeader = this.thead.children('tr').find('th:nth-child(' + index + ')'),
        checkedInput = chkbox.prev().children('input');
        
        checkedInput.prop('checked', true).attr('aria-checked', true);
        columnHeader.removeClass('ui-helper-hidden');
        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).removeClass('ui-helper-hidden');
        this.tbody.children('tr').find('td:nth-child(' + index + ')').removeClass('ui-helper-hidden');
        this.tfoot.children('tr').find('td:nth-child(' + index + ')').removeClass('ui-helper-hidden');
        
        this.fireToggleEvent(true, (index - 1));
        this.updateColspan();
    },
    
    uncheck: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
        
        var index = $(document.getElementById(chkbox.closest('li.ui-columntoggler-item').data('column'))).index() + 1,
        columnHeader = this.thead.children('tr').find('th:nth-child(' + index + ')'),
        uncheckedInput = chkbox.prev().children('input');
        
        uncheckedInput.prop('checked', false).attr('aria-checked', false);
        columnHeader.addClass('ui-helper-hidden');
        $(PrimeFaces.escapeClientId(columnHeader.attr('id') + '_clone')).addClass('ui-helper-hidden');
        this.tbody.children('tr').find('td:nth-child(' + index + ')').addClass('ui-helper-hidden');
        this.tfoot.children('tr').find('td:nth-child(' + index + ')').addClass('ui-helper-hidden');

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
    }

});
