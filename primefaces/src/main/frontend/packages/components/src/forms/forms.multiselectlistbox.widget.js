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
PrimeFaces.widget.MultiSelectListbox = class MultiSelectListbox extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

       this.root = this.jq.children('div.p-multiselectlistbox-listcontainer');
       this.items = this.jq.find('li.p-multiselectlistbox-item');
       this.input = $(this.jqId + '_input');
       this.cfg.disabled = this.jq.hasClass('p-state-disabled');

       if(!this.cfg.disabled) {
           this.bindEvents();
       }

       var value = this.input.val();
       if(value !== '') {
           this.preselect(value);
       }
    }

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents() {
       var $this = this;

       this.items.on('mouseover.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('p-state-highlight'))
               $(this).addClass('p-state-hover');
       })
       .on('mouseout.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('p-state-highlight'))
               $(this).removeClass('p-state-hover');
       })
       .on('click.multiSelectListbox', function() {
           var item = $(this);

           if(!item.hasClass('p-state-highlight')){
               $this.showOptionGroup(item);
           }
       });
    }

    /**
     * Removes some of the event listener that were registered by `bindEvents`. Called when this widget is disabled.
     * @private
     */
    unbindEvents() {
       this.items.off('mouseover.multiSelectListbox mouseout.multiSelectListbox click.multiSelectListbox');
    }

    /**
     * Shows the given box with a group of options.
     * @private
     * @param {JQuery} item The box to be shown.
     */
    showOptionGroup(item) {
       item.addClass('p-state-highlight').removeClass('p-state-hover').siblings().filter('.p-state-highlight').removeClass('p-state-highlight');
       item.closest('.p-multiselectlistbox-listcontainer').nextAll().remove();
       this.input.val(item.attr('data-value'));

       var childItemsContainer = item.children('ul');

       if(childItemsContainer.length) {
           var groupContainer = $('<div class="p-multiselectlistbox-listcontainer" style="display:none"></div>');
           childItemsContainer.clone(true).appendTo(groupContainer).addClass('p-multiselectlistbox-list p-inputfield p-widget-content').removeClass('p-hidden');

           if(this.cfg.showHeaders) {
               groupContainer.prepend('<div class="p-multiselectlistbox-header p-widget-header">' + PrimeFaces.escapeHTML(item.children('span').text()) + '</div>');
           }

           this.jq.append(groupContainer);

           if(this.cfg.effect)
               PrimeFaces.effects.show(groupContainer, this.cfg.effect);
           else
               groupContainer.show();
       }
       else {
           this.triggerChange();
       }
    }

    /**
     * Enables this list box so that the user can select an item.
     */
    enable() {
       if(this.cfg.disabled) {
           this.cfg.disabled = false;
           PrimeFaces.utils.enableInputWidget(this.jq, this.input);
           this.bindEvents();
       }

    }

    /**
     * Disabled this list box so that the user cannot select items anymore.
     */
    disable() {
       if(!this.cfg.disabled) {
           this.cfg.disabled = true;
           PrimeFaces.utils.disableInputWidget(this.jq, this.input);
           this.unbindEvents();
           this.root.nextAll().remove();
       }
    }

    /**
     * Selects the item with the given value, expanding and showing all parent boxes as neccessary.
     * @param {string} value Value of the item to be shown. 
     */
    preselect(value) {
        var $this = this,
        item = this.items.filter('[data-value="' + CSS.escape(value)+'"]');

        if(item.length === 0) {
            return;
        }

        var ancestors = item.parentsUntil('.p-multiselectlistbox-list'),
        selectedIndexMap = [];

        for(var i = (ancestors.length - 1); i >= 0; i--) {
            var ancestor = ancestors.eq(i);

            if(ancestor.is('li')) {
                selectedIndexMap.push(ancestor.index());
            }
            else if(ancestor.is('ul')) {
                var groupContainer = $('<div class="p-multiselectlistbox-listcontainer" style="display:none"></div>');
                ancestor.clone(true).appendTo(groupContainer).addClass('p-multiselectlistbox-list p-inputfield p-widget-content').removeClass('p-hidden');

                if(this.cfg.showHeaders) {
                   groupContainer.prepend('<div class="p-multiselectlistbox-header p-widget-header">' + PrimeFaces.escapeHTML(ancestor.prev('span').text()) + '</div>');
                }

                $this.jq.append(groupContainer);
            }
        }

        //highlight item
        var lists = this.jq.children('div.p-multiselectlistbox-listcontainer'),
        clonedItem = lists.find(' > ul.p-multiselectlistbox-list > li.p-multiselectlistbox-item').filter('[data-value="' + CSS.escape(value) + '"]');
        clonedItem.addClass('p-state-highlight');

        //highlight ancestors
        for(var j = 0; j < selectedIndexMap.length; j++) {
            lists.eq(j).find('> .p-multiselectlistbox-list > li.p-multiselectlistbox-item').eq(selectedIndexMap[j]).addClass('p-state-highlight');
        }

        $this.jq.children('div.p-multiselectlistbox-listcontainer:hidden').show();
    }

    /**
     * Triggers the change behavior, invoked after an item was selected or deselected.
     * @private
     */
    triggerChange() {
        this.callBehavior('change');
    }
}
