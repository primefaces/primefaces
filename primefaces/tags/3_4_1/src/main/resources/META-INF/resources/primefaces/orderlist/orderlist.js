/**
 * PrimeFaces OrderList Widget
 */
PrimeFaces.widget.OrderList = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.list = this.jq.find('.ui-orderlist-list'),
        this.items = this.list.children('.ui-orderlist-item');
        this.input = $(this.jqId + '_values');
        this.cfg.effect = this.cfg.effect||'fade';
        this.cfg.disabled = this.jq.hasClass('ui-state-disabled');
        var _self = this;

        if(!this.cfg.disabled) {
            this.generateItems();

            this.setupButtons();

            this.bindEvents();

            //Enable dnd
            this.list.sortable({
                revert: true,
                start: function(event, ui) {
                    PrimeFaces.clearSelection();
                } 
                ,update: function(event, ui) {
                    _self.onDragDrop(event, ui);
                }
            });
        }
    },
    
    generateItems: function() {
        var _self = this;

        this.list.children('.ui-orderlist-item').each(function() {
            var item = $(this),
            itemValue = item.data('item-value');

            _self.input.append('<option value="' + itemValue + '" selected="selected">' + itemValue + '</option>');
        });
    },
    
    bindEvents: function() {

        this.items.mouseover(function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight'))
                $(this).addClass('ui-state-hover');
        })
        .mouseout(function(e) {
            var element = $(this);

            if(!element.hasClass('ui-state-highlight'))
                $(this).removeClass('ui-state-hover');
        })
        .mousedown(function(e) {
            var element = $(this),
            metaKey = (e.metaKey||e.ctrlKey);

            if(!metaKey) {
                element.removeClass('ui-state-hover').addClass('ui-state-highlight')
                .siblings('.ui-state-highlight').removeClass('ui-state-highlight');
            }
            else {
                if(element.hasClass('ui-state-highlight'))
                    element.removeClass('ui-state-highlight');
                else
                    element.removeClass('ui-state-hover').addClass('ui-state-highlight');
            }
        });
    },
    
    setupButtons: function() {
        var _self = this;

        PrimeFaces.skinButton(this.jq.find('.ui-button'));

        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-up').click(function() {_self.moveUp(_self.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-top').click(function() {_self.moveTop(_self.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-down').click(function() {_self.moveDown(_self.sourceList);});
        this.jq.find(' .ui-orderlist-controls .ui-orderlist-button-move-bottom').click(function() {_self.moveBottom(_self.sourceList);});
    },
    
    onDragDrop: function(event, ui) {
        ui.item.removeClass('ui-state-highlight');

        this.saveState();
    },
    
    saveState: function() {
        this.input.children().remove();

        this.generateItems();
    },
    
    moveUp: function(list) {
        var _self = this;

        this.items.filter('.ui-state-highlight').each(function() {
            var item = $(this);

            if(!item.is(':first-child')) {
                item.hide(_self.cfg.effect, {}, 'fast', function() {
                    item.insertBefore(item.prev()).show(_self.cfg.effect, {}, 'fast', function() {
                        _self.saveState();
                    });
                });
            }
        });
    },
    
    moveTop: function(list) {
        var _self = this;

        this.items.filter('.ui-state-highlight').each(function() {
            var item = $(this);

            if(!item.is(':first-child')) {
                item.hide(_self.cfg.effect, {}, 'fast', function() {
                    item.prependTo(item.parent()).show(_self.cfg.effect, {}, 'fast', function(){
                        _self.saveState();
                    });
                });
            }

        });
    },
    
    moveDown: function(list) {
        var _self = this;

        $(this.items.filter('.ui-state-highlight').get().reverse()).each(function() {
            var item = $(this);

            if(!item.is(':last-child')) {
                item.hide(_self.cfg.effect, {}, 'fast', function() {
                    item.insertAfter(item.next()).show(_self.cfg.effect, {}, 'fast', function() {
                        _self.saveState();
                    });
                });
            }

        });
    },
    
    moveBottom: function(list) {
        var _self = this;

        this.items.filter('.ui-state-highlight').each(function() {
            var item = $(this);

            if(!item.is(':last-child')) {
                item.hide(_self.cfg.effect, {}, 'fast', function() {
                    item.appendTo(item.parent()).show(_self.cfg.effect, {}, 'fast', function() {
                        _self.saveState();
                    });
                });
            }
        });
    }

});