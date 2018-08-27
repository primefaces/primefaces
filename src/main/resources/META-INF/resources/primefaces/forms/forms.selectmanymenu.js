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

                    if(metaKey && item.hasClass('ui-state-highlight')) {
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
        var jqClone = this.selectAll0();
        this.jq.replaceWith(jqClone);
        this.jq = jqClone;
        this.init(this.cfg);
    },

    selectAll0: function() {
        var jqClone = this._super();
        if(this.cfg.showCheckbox) {
            var checkboxes = jqClone.find('.ui-selectlistbox-item:not(.ui-state-disabled) div.ui-chkbox > div.ui-chkbox-box');
            checkboxes.addClass('ui-state-active').children('span.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
        }
        return jqClone;
    },

    unselectAll: function() {
        var jqClone = this.unselectAll0();
        this.jq.replaceWith(jqClone);
        this.jq = jqClone;
        this.init(this.cfg);
    },

    unselectAll0: function() {
        var jqClone = this._super();
        if(this.cfg.showCheckbox) {
            var checkboxes = jqClone.find('.ui-selectlistbox-item:not(.ui-state-disabled) div.ui-chkbox > div.ui-chkbox-box');
            checkboxes.removeClass('ui-state-active').children('span.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
        }
        return jqClone;
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
