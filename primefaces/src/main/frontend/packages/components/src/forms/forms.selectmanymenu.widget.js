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
 * @prop {JQuery} toggler DOM element of the checkbox that selects or unselects all items, if enabled.
 * @prop {JQuery} footer DOM element of the footer with the number of selected items, if enabled.
 *
 * @prop {boolean} cfg.disabled Whether the select many menu is initially disabled.
 * @prop {boolean} cfg.metaKeySelection Whether the meta key (`SHIFT` or `CTRL`) must be held down to select multiple
 * items.
 * @prop {boolean} cfg.showCheckbox When set to `true`, a checkbox is displayed next to each item.
 */
PrimeFaces.widget.SelectManyMenu = class SelectManyMenu extends PrimeFaces.widget.SelectListbox {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.cfg.metaKeySelection = this.cfg.metaKeySelection != undefined ? this.cfg.metaKeySelection : true;

        this.allItems.filter('.p-state-highlight').find('> .p-chkbox > .p-chkbox-box').addClass('p-state-active');
        // Checkbox is inside TD element when using custom content
        this.allItems.filter('.p-state-highlight').find('> td > .p-chkbox > .p-chkbox-box').addClass('p-state-active');

        this.footer = this.jq.children('.p-selectmanymenu-footer');
        this.updateSelectionInfo();
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    bindEvents() {
        super.bindEvents();
        var $this = this;

        this.toggler = this.jq.find('> .p-selectmanymenu-header > .p-chkbox > .p-chkbox-box').attr('role', 'checkbox');

        if (!this.cfg.disabled) {
            this.toggler.attr('tabindex', '0').on('click.selectManyMenu', function() {
                if ($(this).hasClass('p-state-active')) {
                    $this.unselectAll();
                }
                else {
                    $this.selectAll();
                }

                $this.input.trigger('change');
            }).on('keydown.selectManyMenu', function(e) {
                if (e.code === 'Space') {
                    $(this).trigger('click');
                    e.preventDefault();
                }
            }).on('focus.selectManyMenu', function() {
                $(this).addClass('p-state-focus');
            }).on('blur.selectManyMenu', function() {
                $(this).removeClass('p-state-focus');
            });

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

                    if ((metaKey || $this.cfg.showCheckbox) && item.hasClass('p-state-highlight')) {
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

                            if (it.is(':visible') && !it.hasClass('p-state-disabled')) {
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
                this.checkboxes = this.jq.find('.p-selectlistbox-item:not(.p-state-disabled) div.p-chkbox > div.p-chkbox-box');

                this.checkboxes.on('mouseenter.selectManyMenu', function(e) {
                    $(this).addClass('p-state-hover');
                })
                    .on('mouseleave.selectManyMenu', function(e) {
                        $(this).removeClass('p-state-hover');
                    })
                    .on('click.selectManyMenu', function(e) {
                        $this.checkboxClick = true;

                        var item = $(this).closest('.p-selectlistbox-item');
                        if (item.hasClass('p-state-highlight'))
                            $this.unselectItem(item);
                        else
                            $this.selectItem(item);

                        $this.input.trigger('change');
                    });
            }
        }
    }

    /**
     * Handle keyboard events.
     * @param {Event} event the DOM onKeyDown event
     * @private
     */
    onKeyDown(event) {
        var $this = this,
            item = $(event.currentTarget),
            isMetaKey = (event.metaKey || event.ctrlKey),
            metaKey = this.cfg.metaKeySelection && isMetaKey;

        switch (event.code) {
            case 'Enter':
            case 'NumpadEnter':
            case 'Space':
                if (!metaKey && !$this.cfg.showCheckbox) {
                    $this.unselectAll();
                }

                if ((metaKey || $this.cfg.showCheckbox) && item.hasClass('p-state-highlight')) {
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
                    if (!newItem.hasClass('p-selectlistbox-item')) {
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
    }

    /**
     * Focus the item.
     * @param {JQuery} item The item to focus.
     */
    focus(item) {
        this.cursorItem = item;
        item.trigger('focus');
    }

    /**
     * Selects all available items of this select many menu.
     */
    selectAll() {
        // ~~~ PERF ~~~
        // See https://github.com/primefaces/primefaces/issues/2089
        //
        // 1) don't use jquery wrappers
        // 2) don't use existing methods like selectItem
        var filteredItems = this.items.not('.p-hidden');

        for (var i = 0; i < filteredItems.length; i++) {
            var item = filteredItems.eq(i);
            var itemNative = item[0];

            itemNative.classList.add('p-state-highlight');
            itemNative.classList.remove('p-state-hover');
            itemNative.setAttribute('aria-selected', 'true');

            if (this.cfg.showCheckbox) {
                var checkbox = item.find('div.p-chkbox').children('div.p-chkbox-box');

                var checkboxNative = checkbox[0];
                checkboxNative.classList.remove('p-state-hover');
                checkboxNative.classList.add('p-state-active');

                var checkboxIconNative = checkbox.children('span.p-chkbox-icon')[0];
                checkboxIconNative.classList.add('p-checkbox-checked-icon');
            }
        }

        // filtered out and disabled items keep their state
        for (var j = 0; j < this.options.length; j++) {
            this.options[j].selected = this.allItems[j].classList.contains('p-state-highlight');
        }

        this.updateSelectionInfo();
    }

    /**
     * @override
     * @inheritdoc
     */
    unselectAll() {
        // ~~~ PERF ~~~
        // See https://github.com/primefaces/primefaces/issues/2089
        //
        // 1) don't use jquery wrappers
        // 2) don't use existing methods like unselectItem
        var filteredItems = this.items.not('.p-hidden');

        for (var i = 0; i < filteredItems.length; i++) {
            var item = filteredItems.eq(i);
            var itemNative = item[0];

            itemNative.classList.remove('p-state-highlight');
            itemNative.setAttribute('aria-selected', 'false');

            if (this.cfg.showCheckbox) {
                var checkbox = item.find('div.p-chkbox').children('div.p-chkbox-box');

                var checkboxNative = checkbox[0];
                checkboxNative.classList.remove('p-state-active');

                var checkboxIconNative = checkbox.children('span.p-chkbox-icon')[0];
                checkboxIconNative.classList.remove('p-checkbox-checked-icon');
            }
        }

        // filtered out and disabled items keep their state
        for (var j = 0; j < this.options.length; j++) {
            this.options[j].selected = this.allItems[j].classList.contains('p-state-highlight');
        }

        this.updateSelectionInfo();
    }

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} item
     */
    selectItem(item) {
        super.selectItem(item);

        if (this.cfg.showCheckbox) {
            this.selectCheckbox(item.find('div.p-chkbox-box'));
        }

        this.updateSelectionInfo();
    }

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} item
     */
    unselectItem(item) {
        super.unselectItem(item);

        if (this.cfg.showCheckbox) {
            this.unselectCheckbox(item.find('div.p-chkbox-box'));
        }

        this.updateSelectionInfo();
    }

    /**
     * @override
     * @inheritdoc
     * @param {string} value
     */
    filter(value) {
        super.filter(value);
        this.updateSelectionInfo();
    }

    /**
     * Updates the number of selected items in the footer and the state of the toggle all checkbox, if enabled.
     * @private
     */
    updateSelectionInfo() {
        if (this.footer.length) {
            var count = this.input[0].selectedOptions.length;
            this.footer.text(count ? this.getLabel('selectionMessage').replace('{0}', count) : this.getLabel('emptySelectionMessage'));
        }

        if (this.toggler.length) {
            var selectable = this.items.not('.p-hidden'),
                checked = selectable.length > 0 && selectable.not('.p-state-highlight').length === 0;

            if (checked) {
                this.selectCheckbox(this.toggler);
            }
            else {
                this.unselectCheckbox(this.toggler);
            }

            this.toggler.attr({
                'aria-checked': checked,
                'aria-label': this.getAriaLabel(checked ? 'selectAll' : 'unselectAll')
            });
        }
    }

    /**
     * Select the given checkbox. Does not unselect any other checkboxes that are currently selected.
     * @param {JQuery} chkbox A CHECKBOX element to select.
     */
    selectCheckbox(chkbox) {
        chkbox.addClass('p-state-active').children('span.p-chkbox-icon').addClass('p-checkbox-checked-icon');
    }

    /**
     * Unselects the given checkbox. Does not modify any other checkboxes.
     * @param {JQuery} chkbox A CHECKBOX element to unselect.
     */
    unselectCheckbox(chkbox) {
        chkbox.removeClass('p-state-active').children('span.p-chkbox-icon').removeClass('p-checkbox-checked-icon');
    }
}
