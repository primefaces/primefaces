/**
 * PrimeFaces Chips Widget
 */
PrimeFaces.widget.Chips = PrimeFaces.widget.BaseWidget.extend({

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

    bindEvents: function() {
        var $this = this;

        this.itemContainer.hover(function() {
                $(this).addClass('ui-state-hover');
            },
            function() {
                $(this).removeClass('ui-state-hover');
            }
        ).click(function() {
            $this.input.focus();
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

    addItem : function(value, refocus) {
        if(value && value.trim().length && (!this.cfg.max||this.cfg.max > this.hinput.children('option').length)) {
            var escapedValue = PrimeFaces.escapeHTML(value);
            var itemDisplayMarkup = '<li class="ui-chips-token ui-state-active ui-corner-all">';
            itemDisplayMarkup += '<span class="ui-chips-token-icon ui-icon ui-icon-close" />';
            itemDisplayMarkup += '<span class="ui-chips-token-label">' + escapedValue + '</span></li>';

            this.inputContainer.before(itemDisplayMarkup);
            this.input.val('');
            this.input.removeAttr('placeholder');
            
            if (refocus) {
                this.input.focus();
            }

            this.hinput.append('<option value="' + escapedValue + '" selected="selected"></option>');
            this.invokeItemSelectBehavior(escapedValue);
        }
    },

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