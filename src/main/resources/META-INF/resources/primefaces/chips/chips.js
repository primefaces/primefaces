/**
 * __PrimeFaces Chips Widget__
 * 
 * Chips is used to enter multiple values on an inputfield.
 * 
 * @prop {JQuery} input DOM element of the visible INPUT field.
 * @prop {JQuery} hinput DOM element of the hidden INPUT field with the current value.
 * @prop {JQuery} itemContainer DOM element of the container of the items (chips).
 * @prop {JQuery} inputContainer DOM element of the container for the visible INPUT.
 * @prop {string} placeholder Placeholder for the input field.
 * 
 * @interface {PrimeFaces.widget.ChipsCfg} cfg The configuration for the {@link  Chips| Chips widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.addOnBlur Whether to add an item when the input loses focus.
 * @prop {boolean} cfg.unique Prevent duplicate entries from being added.
 * @prop {number} cfg.max Maximum number of entries allowed.
 */
PrimeFaces.widget.Chips = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.hinput = $(this.jqId + '_hinput');
        this.itemContainer = this.jq.children('ul');
        this.inputContainer = this.itemContainer.children('.ui-chips-input-token');

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hinput.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.placeholder = this.input.attr('placeholder');

        this.bindEvents();
    },

    /**
     * Sets up all event listeners required for this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.itemContainer.on("mouseenter", function() {
            $(this).addClass('ui-state-hover');
        }).on("mouseleave", function() {
            $(this).removeClass('ui-state-hover');
        }).on("click", function() {
            $this.input.trigger('focus');
        });


        this.input.on('focus.chips', function() {
            $this.itemContainer.addClass('ui-state-focus');
        }).on('blur.chips', function() {
            $this.itemContainer.removeClass('ui-state-focus');

            if ($this.cfg.addOnBlur) {
                $this.addItem($(this).val(), false);
            }
        }).on('keydown.chips', function(e) {
            var value = $(this).val();

            switch(e.which) {
                //backspace
                case 8:
                    if(value.length === 0 && $this.hinput.children('option') && $this.hinput.children('option').length > 0) {
                        var lastOption = $this.hinput.children('option:last'),
                            index = lastOption.index();
                        $this.removeItem($($this.itemContainer.children('li.ui-chips-token').get(index)));
                    }
                break;

                //enter
                case 13:
                    $this.addItem(value, true);
                    e.preventDefault();
                break;

                default:
                    if($this.cfg.max && $this.cfg.max === $this.hinput.children('option').length) {
                        e.preventDefault();
                    }
                break;
            }
        });

        var closeSelector = '> li.ui-chips-token > .ui-chips-token-icon';
        this.itemContainer.off('click', closeSelector).on('click', closeSelector, null, function(event) {
            $this.removeItem($(this).parent());
        });
    },

    /**
     * Adds a new item (chip) to the list of currently displayed items.
     * @param {string} value Value of the chip to add.
     * @param {boolean} [refocus] `true` to put focus back on the INPUT again after the chip was added, or `false`
     * otherwise. 
     */
    addItem: function(value, refocus) {
        var $this = this;
        if(value && value.trim().length && (!this.cfg.max||this.cfg.max > this.hinput.children('option').length)) {
            var escapedValue = PrimeFaces.escapeHTML(value);

            if (this.cfg.unique) {
                var duplicateFound = false;
                this.hinput.children('option').each(function() {
                    if (this.value === escapedValue) {
                        $this.refocus(refocus);
                        duplicateFound = true;
                        return false; // breaks
                    }
                });
                if (duplicateFound) {
                    return;
                }
            }

            var itemDisplayMarkup = '<li class="ui-chips-token ui-state-active ui-corner-all">';
            itemDisplayMarkup += '<span class="ui-chips-token-icon ui-icon ui-icon-close"></span>';
            itemDisplayMarkup += '<span class="ui-chips-token-label">' + escapedValue + '</span></li>';

            this.inputContainer.before(itemDisplayMarkup);
            this.refocus(refocus);

            this.hinput.append('<option value="' + escapedValue + '" selected="selected"></option>');
            this.invokeItemSelectBehavior(escapedValue);
        }
    },

    /**
     * Deletes the currently editing input value and refocus the input box if necessary.
     * @param {boolean} [refocus] `true` to put focus back on the INPUT again after the chip was added, or `false`
     * otherwise. 
     * @private
     */
    refocus: function(refocus) {
        this.input.val('');
        this.input.removeAttr('placeholder');

        if (refocus) {
            this.input.trigger('focus');
        }
    },

    /**
     * Removes an item (chip) from the list of currently displayed items.
     * @param {JQuery} item An item  (LI element) that should be removed.
     */
    removeItem: function(item) {
        var itemIndex = this.itemContainer.children('li.ui-chips-token').index(item);
        var itemValue = item.find('span.ui-chips-token-label').html()
        $this = this;

        //remove from options
        this.hinput.children('option').eq(itemIndex).remove();

        item.fadeOut('fast', function() {
            var token = $(this);

            token.remove();

            $this.invokeItemUnselectBehavior(itemValue);
        });

        // if empty return placeholder
        if (this.placeholder && this.hinput.children('option').length === 0) {
            this.input.attr('placeholder', this.placeholder);
        }
    },

    /**
     * Triggers the behaviors and event listeners for when an item (chip) was selected.
     * @param {string} itemValue Value of the selected item.
     * @private
     */
    invokeItemSelectBehavior: function(itemValue) {
        if(this.hasBehavior('itemSelect')) {
            var ext = {
                params : [
                    {name: this.id + '_itemSelect', value: itemValue}
                ]
            };

            this.callBehavior('itemSelect', ext);
        }
    },

    /**
     * Triggers the behaviors and event listeners for when an item (chip) was unselected.
     * @param {string} itemValue Value of the unselected item.
     * @private 
     */
    invokeItemUnselectBehavior: function(itemValue) {
        if(this.hasBehavior('itemUnselect')) {
            var ext = {
                params : [
                    {name: this.id + '_itemUnselect', value: itemValue}
                ]
            };

            this.callBehavior('itemUnselect', ext);
        }
    }
});