/**
 * PrimeFaces SelectManyMenu Widget
 */
PrimeFaces.widget.SelectManyMenu = PrimeFaces.widget.SelectListbox.extend({

    init: function(cfg) {
        this._super(cfg);

        this.allItems.filter('.ui-state-highlight').find('> .ui-chkbox > .ui-chkbox-box').addClass('ui-state-active');
    },

    bindEvents: function() {
        this._super();
        var $this = this;

        if(!this.cfg.disabled) {
            this.items.on('click.selectListbox', function(e) {
                //stop propagation
                if($this.checkboxClick) {
                    $this.checkboxClick = false;
                    return;
                }

                var item = $(this),
                selectedItems = $this.items.filter('.ui-state-highlight'),
                metaKey = (e.metaKey||e.ctrlKey),
                unchanged = (!metaKey && selectedItems.length === 1 && selectedItems.index() === item.index());

                if(!e.shiftKey) {
                    if(!metaKey && !$this.cfg.showCheckbox) {
                        $this.unselectAll();
                    }

                    if((metaKey || $this.cfg.showCheckbox) && item.hasClass('ui-state-highlight')) {
                        $this.unselectItem(item);
                    }
                    else {
                        $this.selectItem(item);
                        $this.cursorItem = item;
                    }
                }
                else {
                    //range selection
                    if($this.cursorItem) {
                        $this.unselectAll();

                        var currentItemIndex = item.index(),
                        cursorItemIndex = $this.cursorItem.index(),
                        startIndex = (currentItemIndex > cursorItemIndex) ? cursorItemIndex : currentItemIndex,
                        endIndex = (currentItemIndex > cursorItemIndex) ? (currentItemIndex + 1) : (cursorItemIndex + 1);

                        for(var i = startIndex ; i < endIndex; i++) {
                            var it = $this.allItems.eq(i);

                            if(it.is(':visible') && !it.hasClass('ui-state-disabled')) {
                                $this.selectItem(it);
                            }
                        }
                    }
                    else {
                        $this.selectItem(item);
                        $this.cursorItem = item;
                    }
                }

                if(!unchanged) {
                    $this.input.trigger('change');
                }

                $this.input.trigger('click');
                PrimeFaces.clearSelection();
                e.preventDefault();
            });

            if(this.cfg.showCheckbox) {
                this.checkboxes = this.jq.find('.ui-selectlistbox-item:not(.ui-state-disabled) div.ui-chkbox > div.ui-chkbox-box');

                this.checkboxes.on('mouseover.selectManyMenu', function(e) {
                    var chkbox = $(this);

                    if(!chkbox.hasClass('ui-state-active'))
                        chkbox.addClass('ui-state-hover');
                })
                .on('mouseout.selectManyMenu', function(e) {
                    $(this).removeClass('ui-state-hover');
                })
                .on('click.selectManyMenu', function(e) {
                    $this.checkboxClick = true;

                    var item = $(this).closest('.ui-selectlistbox-item');
                    if(item.hasClass('ui-state-highlight'))
                        $this.unselectItem(item);
                    else
                        $this.selectItem(item);

                    $this.input.trigger('change');
                });
            }
        }
    },

    selectAll: function() {
        // ~~~ PERF ~~~
        // See https://github.com/primefaces/primefaces/issues/2089
        //
        // 1) don't use jquery wrappers
        // 2) don't use existing methods like selectItem

        for(var i = 0; i < this.items.length; i++) {
            var item = this.items.eq(i);
            var itemNative = item[0];

            itemNative.classList.add('ui-state-highlight');
            itemNative.classList.remove('ui-state-hover');

            if(this.cfg.showCheckbox) {
                var checkbox = item.children('div.ui-chkbox').children('div.ui-chkbox-box');

                var checkboxNative = checkbox[0];
                checkboxNative.classList.remove('ui-state-hover');
                checkboxNative.classList.add('ui-state-active');

                var checkboxIconNative = checkbox.children('span.ui-chkbox-icon')[0];
                checkboxIconNative.classList.remove('ui-icon-blank');
                checkboxIconNative.classList.add('ui-icon-check');
            }
        }

        for(var i = 0; i < this.options.length; i++) {
            this.options[i].selected = true;
        }
    },

    unselectAll: function() {
        // ~~~ PERF ~~~
        // See https://github.com/primefaces/primefaces/issues/2089
        //
        // 1) don't use jquery wrappers
        // 2) don't use existing methods like unselectItem

        for(var i = 0; i < this.items.length; i++) {
            var item = this.items.eq(i);
            var itemNative = item[0];

            itemNative.classList.remove('ui-state-highlight');

            if(this.cfg.showCheckbox) {
                var checkbox = item.children('div.ui-chkbox').children('div.ui-chkbox-box');

                var checkboxNative = checkbox[0];
                checkboxNative.classList.remove('ui-state-active');

                var checkboxIconNative = checkbox.children('span.ui-chkbox-icon')[0];
                checkboxIconNative.classList.add('ui-icon-blank');
                checkboxIconNative.classList.remove('ui-icon-check');
            }
        }

        for(var i = 0; i < this.options.length; i++) {
            this.options[i].selected = false;
        }
    },

    selectItem: function(item) {
        this._super(item);

        if(this.cfg.showCheckbox) {
            this.selectCheckbox(item.find('div.ui-chkbox-box'));
        }
    },

    unselectItem: function(item) {
        this._super(item);

        if(this.cfg.showCheckbox) {
            this.unselectCheckbox(item.find('div.ui-chkbox-box'));
        }
    },

    selectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-hover').addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
    },

    unselectCheckbox: function(chkbox) {
        chkbox.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
    }
});
