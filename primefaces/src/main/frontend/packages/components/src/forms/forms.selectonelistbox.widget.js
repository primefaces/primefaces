/**
 * __PrimeFaces SelectOneListbox Widget__
 * 
 * SelectOneListbox is an extended version of the standard selectOneListbox component.
 * 
 * @prop {JQuery} focusedItem The DOM element for the button select item currently focused.
 * @prop {JQuery} items The DOM element for the select items the user can select.
 * @prop {JQuery} input The DOM element for the hidden input field storing the selected item.
 * 
 * @interface {PrimeFaces.widget.SelectOneListboxCfg} cfg The configuration for the {@link  SelectOneListbox| SelectOneListbox widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.SelectListboxCfg} cfg
 * 
 * @prop {boolean} cfg.disabled Whether this widget is currently disabled.
 */
PrimeFaces.widget.SelectOneListbox = class SelectOneListbox extends PrimeFaces.widget.SelectListbox {

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    bindEvents() {
        super.bindEvents();
        var $this = this;

        if (!this.cfg.disabled) {
            this.focusedItem = null;

            this.items.on('click.selectListbox', function (e) {
                var item = $(this),
                    selectedItem = $this.items.filter('.p-state-highlight');

                if (item.index() !== selectedItem.index()) {
                    if (selectedItem.length) {
                        $this.unselectItem(selectedItem);
                    }

                    $this.selectItem(item);
                    $this.input.trigger('change');
                }

                /* For keyboard navigation */
                $this.removeOutline();
                $this.focusedItem = item;
                $this.input.trigger('focus');
                $this.input.trigger('click');

                PrimeFaces.clearSelection();
                e.preventDefault();
            });
        }

        this.bindKeyEvents();
    }

    /**
     * Sets up the event listeners for keyboard related events.
     * @private
     */
    bindKeyEvents() {
        var $this = this;

        this.input.off('focus.selectListbox blur.selectListbox keydown.selectListbox')
            .on('focus.selectListbox', function (e) {
                $this.jq.addClass('p-state-focus');

                var activeItem = $this.focusedItem || $this.items.filter('.p-state-highlight:visible:first');
                if (activeItem.length) {
                    $this.focusedItem = activeItem;
                }
                else {
                    $this.focusedItem = $this.items.filter(':visible:first');
                }

                PrimeFaces.queueTask(function () {
                    if ($this.focusedItem) {
                        PrimeFaces.scrollInView($this.listContainer, $this.focusedItem);
                        $this.focusedItem.addClass('p-listbox-outline');
                    }
                });
            })
            .on('blur.selectListbox', function () {
                $this.jq.removeClass('p-state-focus');
                $this.removeOutline();
                $this.focusedItem = null;
            })
            .on('keydown.selectListbox', function (e) {
                if (!$this.focusedItem) {
                    return;
                }

                switch (e.code) {
                    case 'ArrowLeft':
                    case 'ArrowUp':
                        $this.select($this.focusedItem.prevAll('.p-selectlistbox-item:visible:first'));
                        e.preventDefault();
                        break;
                    case 'ArrowRight':
                    case 'ArrowDown':
                        $this.select($this.focusedItem.nextAll('.p-selectlistbox-item:visible:first'));
                        e.preventDefault();
                        break;
                    case 'Home':
                    case 'PageUp':
                        $this.select($this.items.first());
                        e.preventDefault();
                        break;
                    case 'End':
                    case 'PageDown':
                        $this.select($this.items.last());
                        e.preventDefault();
                        break;
                };
            });

    }

    /**
     * Select the item.
     * @param {JQuery} item The item to focus.
     */
    select(item) {
        if (!this.focusedItem.hasClass('p-state-highlight')) {
            this.focusedItem.trigger('click.selectListbox');
        }
        else if (item && item.length) {
            item.trigger('click.selectListbox');
            PrimeFaces.scrollInView(this.listContainer, item);
        }
    }

    /**
     * Removes the outline around the listbox with the select options.
     * @private
     */
    removeOutline() {
        if (this.focusedItem && this.focusedItem.hasClass('p-listbox-outline')) {
            this.focusedItem.removeClass('p-listbox-outline');
        }
    }
}