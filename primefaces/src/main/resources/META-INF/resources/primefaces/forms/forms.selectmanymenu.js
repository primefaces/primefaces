/**
 * __PrimeFaces SelectManyMenu Widget__
 * 
 * SelectManyMenu is an extended version of the standard SelectManyMenu.
 * 
 * @interface {PrimeFaces.widget.SelectManyMenuCfg} cfg The configuration for the {@link  SelectManyMenu| SelectManyMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.SelectListboxCfg} cfg
 * 
 * @prop {JQuery} [cursorItem] The last clicked item, used for selecting a range by holding down the shift
 * key. 
 * @prop {boolean} [checkboxClick] Whether a checkbox of the select menu was clicked. Reset after the list
 * box was clicked.
 * @prop {JQuery} [checkboxes] DOM elements of the checkboxes, if checkboxes are enabled.
 * 
 * @prop {boolean} cfg.disabled Whether the select many menu is initially disabled.
 * @prop {boolean} cfg.metaKeySelection Whether the meta key (`SHIFT` or `CTRL`) must be held down to select multiple
 * items.
 * @prop {boolean} cfg.showCheckbox When set to `true`, a checkbox is displayed next to each item.
 */
PrimeFaces.widget.SelectManyMenu = PrimeFaces.widget.SelectListbox.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.cfg.metaKeySelection = this.cfg.metaKeySelection != undefined ? this.cfg.metaKeySelection : true;

        this.allItems.filter('.ui-state-highlight').find('> .ui-chkbox > .ui-chkbox-box').addClass('ui-state-active');
        // Checkbox is inside TD element when using custom content
        this.allItems.filter('.ui-state-highlight').find('> td > .ui-chkbox > .ui-chkbox-box').addClass('ui-state-active');
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    bindEvents: function() {
        this._super();
        var $this = this;

        if (!this.cfg.disabled) {
            this.items.off().on('click.selectListbox', function(e) {
                //stop propagation
                if ($this.checkboxClick) {
                    $this.checkboxClick = false;
                    return;
                }

                var item = $(this),
                    metaKey = $this.cfg.metaKeySelection && (e.metaKey || e.ctrlKey);

                if (!e.shiftKey) {
                    if (!metaKey && !$this.cfg.showCheckbox) {
                        $this.unselectAll();
                    }

                    if ((metaKey || $this.cfg.showCheckbox) && item.hasClass('ui-state-highlight')) {
                        $this.unselectItem(item);
                    }
                    else {
                        $this.selectItem(item);
                        $this.cursorItem = item;
                    }
                }
                else {
                    //range selection
                    if ($this.cursorItem) {
                        $this.unselectAll();

                        var currentItemIndex = item.index(),
                            cursorItemIndex = $this.cursorItem.index(),
                            startIndex = (currentItemIndex > cursorItemIndex) ? cursorItemIndex : currentItemIndex,
                            endIndex = (currentItemIndex > cursorItemIndex) ? (currentItemIndex + 1) : (cursorItemIndex + 1);

                        for (var i = startIndex; i < endIndex; i++) {
                            var it = $this.allItems.eq(i);

                            if (it.is(':visible') && !it.hasClass('ui-state-disabled')) {
                                $this.selectItem(it);
                            }
                        }
                    }
                    else {
                        $this.selectItem(item);
                        $this.cursorItem = item;
                    }
                }

                $this.input.trigger('change');
                $this.input.trigger('click');
                PrimeFaces.clearSelection();
                e.preventDefault();
            }).on('keydown.selectListbox', function(event) {
                $this.onKeyDown(event);
            });

            if (this.cfg.showCheckbox) {
                this.checkboxes = this.jq.find('.ui-selectlistbox-item:not(.ui-state-disabled) div.ui-chkbox > div.ui-chkbox-box');

                this.checkboxes.on('mouseenter.selectManyMenu', function(e) {
                    $(this).addClass('ui-state-hover');
                })
                    .on('mouseleave.selectManyMenu', function(e) {
                        $(this).removeClass('ui-state-hover');
                    })
                    .on('click.selectManyMenu', function(e) {
                        $this.checkboxClick = true;

                        var item = $(this).closest('.ui-selectlistbox-item');
                        if (item.hasClass('ui-state-highlight'))
                            $this.unselectItem(item);
                        else
                            $this.selectItem(item);

                        $this.input.trigger('change');
                    });
            }
        }
    },

    /**
     * Handle keyboard events.
     * @param {Event} event the DOM onKeyDown event
     * @private
     */
    onKeyDown: function(event) {
        var $this = this,
            item = $(event.currentTarget),
            isMetaKey = (event.metaKey || event.ctrlKey),
            metaKey = this.cfg.metaKeySelection && isMetaKey;

        switch (event.code) {
            case 'Enter':
            case 'Space':
                if (!metaKey && !$this.cfg.showCheckbox) {
                    $this.unselectAll();
                }

                if ((metaKey || $this.cfg.showCheckbox) && item.hasClass('ui-state-highlight')) {
                    $this.unselectItem(item);
                }
                else {
                    $this.selectItem(item);
                    $this.cursorItem = item;
                }
                event.preventDefault();
                break;
            case 'KeyA':
                if (metaKey) $this.selectAll();
                event.preventDefault();
                break;
            case 'KeyD':
                if (metaKey) $this.unselectAll();
                event.preventDefault();
                break;
            case 'ArrowUp':
            case 'ArrowDown':
                if (!event.shiftKey) {
                    var newItem = event.key === 'ArrowDown' ? item.next() : item.prev();
                    if (!newItem.hasClass('ui-selectlistbox-item')) {
                        return;
                    }
                    $this.focus(newItem);
                }
                event.preventDefault();
                break;
            case 'Home':
            case 'PageUp':
                $this.focus($this.items.first());
                event.preventDefault();
                break;
            case 'End':
            case 'PageDown':
                $this.focus($this.items.last());
                event.preventDefault();
                break;
        }
    },

    /**
     * Focus the item.
     * @param {JQuery} item The item to focus.
     */
    focus: function(item) {
        this.cursorItem = item;
        item.trigger('focus');
    },

    /**
     * Selects all available items of this select many menu.
     */
    selectAll: function() {
        // ~~~ PERF ~~~
        // See https://github.com/primefaces/primefaces/issues/2089
        //
        // 1) don't use jquery wrappers
        // 2) don't use existing methods like selectItem
        var filteredItems = this.items.filter(":visible");

        for (var i = 0; i < filteredItems.length; i++) {
            var item = filteredItems.eq(i);
            var itemNative = item[0];

            itemNative.classList.add('ui-state-highlight');
            itemNative.classList.remove('ui-state-hover');
            itemNative.setAttribute('aria-selected', 'true');

            if (this.cfg.showCheckbox) {
                var checkbox = item.find('div.ui-chkbox').children('div.ui-chkbox-box');

                var checkboxNative = checkbox[0];
                checkboxNative.classList.remove('ui-state-hover');
                checkboxNative.classList.add('ui-state-active');

                var checkboxIconNative = checkbox.children('span.ui-chkbox-icon')[0];
                checkboxIconNative.classList.remove('ui-icon-blank');
                checkboxIconNative.classList.add('ui-icon-check');
            }
        }

        for (var i = 0; i < this.options.length; i++) {
            this.options[i].selected = true;
        }
    },

    /**
     * @override
     * @inheritdoc
     */
    unselectAll: function() {
        // ~~~ PERF ~~~
        // See https://github.com/primefaces/primefaces/issues/2089
        //
        // 1) don't use jquery wrappers
        // 2) don't use existing methods like unselectItem
        var filteredItems = this.items.filter(":visible");

        for (var i = 0; i < filteredItems.length; i++) {
            var item = filteredItems.eq(i);
            var itemNative = item[0];

            itemNative.classList.remove('ui-state-highlight');
            itemNative.setAttribute('aria-selected', 'false');

            if (this.cfg.showCheckbox) {
                var checkbox = item.find('div.ui-chkbox').children('div.ui-chkbox-box');

                var checkboxNative = checkbox[0];
                checkboxNative.classList.remove('ui-state-active');

                var checkboxIconNative = checkbox.children('span.ui-chkbox-icon')[0];
                checkboxIconNative.classList.add('ui-icon-blank');
                checkboxIconNative.classList.remove('ui-icon-check');
            }
        }

        for (var i = 0; i < this.options.length; i++) {
            this.options[i].selected = false;
        }
    },

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} item
     */
    selectItem: function(item) {
        this._super(item);

        if (this.cfg.showCheckbox) {
            this.selectCheckbox(item.find('div.ui-chkbox-box'));
        }
    },

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} item
     */
    unselectItem: function(item) {
        this._super(item);

        if (this.cfg.showCheckbox) {
            this.unselectCheckbox(item.find('div.ui-chkbox-box'));
        }
    },

    /**
     * Select the given checkbox. Does not unselect any other checkboxes that are currently selected.
     * @param {JQuery} chkbox A CHECKBOX element to select.
     */
    selectCheckbox: function(chkbox) {
        chkbox.addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
    },

    /**
     * Unselects the given checkbox. Does not modify any other checkboxes.
     * @param {JQuery} chkbox A CHECKBOX element to unselect.
     */
    unselectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
    }
});
