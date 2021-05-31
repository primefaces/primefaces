/**
 * __PrimeFaces MultiSelectListbox Widget__
 * 
 * MultiSelectListbox is used to select an item from a collection of listboxes that are in parent-child relationship.
 * 
 * @prop {JQuery} root The DOM element for the root box with no children.
 * @prop {JQuery} items The DOM elements in all boxes that can be selected.
 * @prop {JQuery} input The hidden input field storing the selected value.
 * 
 * @interface {PrimeFaces.widget.MultiSelectListboxCfg} cfg The configuration for the {@link  MultiSelectListbox| MultiSelectListbox widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.disabled If true, disables the component.
 * @prop {boolean} cfg.showHeaders Displays label of a group at header section of the children items.
 * @prop {string} cfg.effect Effect to use when showing a group of items.
 */
PrimeFaces.widget.MultiSelectListbox = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
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

    /**
     * Removes some of the event listener that were registered by `bindEvents`. Called when this widget is disabled.
     * @private
     */
    unbindEvents: function() {
       this.items.off('mouseover.multiSelectListbox mouseout.multiSelectListbox click.multiSelectListbox');
    },

    /**
     * Shows the given box with a group of options.
     * @private
     * @param {JQuery} item The box to be shown.
     */
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

    /**
     * Enables this list box so that the user can select an item.
     */
    enable: function() {
       if(this.cfg.disabled) {
           this.cfg.disabled = false;
           PrimeFaces.utils.enableInputWidget(this.jq, this.input);
           this.bindEvents();
       }

    },

    /**
     * Disabled this list box so that the user cannot select items anymore.
     */
    disable: function() {
       if(!this.cfg.disabled) {
           this.cfg.disabled = true;
           PrimeFaces.utils.disableInputWidget(this.jq, this.input);
           this.unbindEvents();
           this.root.nextAll().remove();
       }
    },

    /**
     * Selects the item with the given value, expanding and showing all parent boxes as neccessary.
     * @param {string} value Value of the item to be shown. 
     */
    preselect: function(value) {
        var $this = this,
        item = this.items.filter('[data-value="' + $.escapeSelector(value)+'"]');

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
        clonedItem = lists.find(' > ul.ui-multiselectlistbox-list > li.ui-multiselectlistbox-item').filter('[data-value="' + $.escapeSelector(value) + '"]');
        clonedItem.addClass('ui-state-highlight');

        //highlight ancestors
        for(var i = 0; i < selectedIndexMap.length; i++) {
            lists.eq(i).find('> .ui-multiselectlistbox-list > li.ui-multiselectlistbox-item').eq(selectedIndexMap[i]).addClass('ui-state-highlight');
        }

        $this.jq.children('div.ui-multiselectlistbox-listcontainer:hidden').show();
    },

    /**
     * Triggers the change behavior, invoked after an item was selected or deselected.
     * @private
     */
    triggerChange: function () {
        this.callBehavior('change');
    }
});
