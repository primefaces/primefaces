/*
 * PrimeFaces MultiSelectListbox Widget
 */
PrimeFaces.widget.MultiSelectListbox = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
       this._super(cfg);

       this.root = this.jq.children('div.ui-multiselectlistbox-listcontainer');
       this.items = this.jq.find('li.ui-multiselectlistbox-item');
       this.input = $(this.jqId + '_input');
       this.cfg.disabled = this.jq.hasClass('ui-state-disabled');

       if(!this.cfg.disabled) {
           this.bindEvents();
       }

       var value = this.input.val();
       if(value !== '') {
           this.preselect(value);
       }
    },

    bindEvents: function() {
       var $this = this;

       this.items.on('mouseover.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('ui-state-highlight'))
               $(this).addClass('ui-state-hover');
       })
       .on('mouseout.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('ui-state-highlight'))
               $(this).removeClass('ui-state-hover');
       })
       .on('click.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('ui-state-highlight')){
               $this.showOptionGroup(item);
           }
       });
    },

    unbindEvents: function() {
       this.items.off('mouseover.multiSelectListbox mouseout.multiSelectListbox click.multiSelectListbox');
    },

    showOptionGroup: function(item) {
       item.addClass('ui-state-highlight').removeClass('ui-state-hover').siblings().filter('.ui-state-highlight').removeClass('ui-state-highlight');
       item.closest('.ui-multiselectlistbox-listcontainer').nextAll().remove();
       this.input.val(item.attr('data-value'));

       var childItemsContainer = item.children('ul');

       if(childItemsContainer.length) {
           var groupContainer = $('<div class="ui-multiselectlistbox-listcontainer" style="display:none"></div>');
           childItemsContainer.clone(true).appendTo(groupContainer).addClass('ui-multiselectlistbox-list ui-inputfield ui-widget-content').removeClass('ui-helper-hidden');

           if(this.cfg.showHeaders) {
               groupContainer.prepend('<div class="ui-multiselectlistbox-header ui-widget-header ui-corner-top">' + PrimeFaces.escapeHTML(item.children('span').text()) + '</div>')
                       .children('.ui-multiselectlistbox-list').addClass('ui-corner-bottom');
           } else {
               groupContainer.children().addClass('ui-corner-all');
           }

           this.jq.append(groupContainer);

           if(this.cfg.effect)
               groupContainer.show(this.cfg.effect);
           else
               groupContainer.show();
       }
       else {
           this.triggerChange();
       }
    },

    enable: function() {
       if(this.cfg.disabled) {
           this.cfg.disabled = false;
           this.jq.removeClass('ui-state-disabled');
           this.bindEvents();
       }

    },

    disable: function() {
       if(!this.cfg.disabled) {
           this.cfg.disabled = true;
           this.jq.addClass('ui-state-disabled');
           this.unbindEvents();
           this.root.nextAll().remove();
       }
    },

    preselect: function(value) {
        var $this = this,
        item = this.items.filter('[data-value="' + value + '"]');

        if(item.length === 0) {
            return;
        }

        var ancestors = item.parentsUntil('.ui-multiselectlistbox-list'),
        selectedIndexMap = [];

        for(var i = (ancestors.length - 1); i >= 0; i--) {
            var ancestor = ancestors.eq(i);

            if(ancestor.is('li')) {
                selectedIndexMap.push(ancestor.index());
            }
            else if(ancestor.is('ul')) {
                var groupContainer = $('<div class="ui-multiselectlistbox-listcontainer" style="display:none"></div>');
                ancestor.clone(true).appendTo(groupContainer).addClass('ui-multiselectlistbox-list ui-inputfield ui-widget-content ui-corner-all').removeClass('ui-helper-hidden');

                if(this.cfg.showHeaders) {
                   groupContainer.prepend('<div class="ui-multiselectlistbox-header ui-widget-header ui-corner-top">' + PrimeFaces.escapeHTML(ancestor.prev('span').text()) + '</div>')
                           .children('.ui-multiselectlistbox-list').addClass('ui-corner-bottom').removeClass('ui-corner-all');
                }

                $this.jq.append(groupContainer);
            }
        }

        //highlight item
        var lists = this.jq.children('div.ui-multiselectlistbox-listcontainer'),
        clonedItem = lists.find(' > ul.ui-multiselectlistbox-list > li.ui-multiselectlistbox-item').filter('[data-value="' + value + '"]');
        clonedItem.addClass('ui-state-highlight');

        //highlight ancestors
        for(var i = 0; i < selectedIndexMap.length; i++) {
            lists.eq(i).find('> .ui-multiselectlistbox-list > li.ui-multiselectlistbox-item').eq(selectedIndexMap[i]).addClass('ui-state-highlight');
        }

        $this.jq.children('div.ui-multiselectlistbox-listcontainer:hidden').show();
    },

    triggerChange: function () {
        this.callBehavior('change');
    }
});
