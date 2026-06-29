/**
 * PrimeFaces SelectOneListbox Widget
 */
PrimeFaces.widget.SelectOneListbox = PrimeFaces.widget.SelectListbox.extend({

    bindEvents: function() {
        this._super();
        var $this = this;

        if(!this.cfg.disabled) {
            this.focusedItem = null;
            
            this.items.on('click.selectListbox', function(e) {
                var item = $(this),
                selectedItem = $this.items.filter('.ui-state-highlight');

                if(item.index() !== selectedItem.index()) {
                    if(selectedItem.length) {
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
    },
    
    bindKeyEvents: function() {
        var $this = this;

        this.input.off('focus.selectListbox blur.selectListbox keydown.selectListbox').on('focus.selectListbox', function(e) {
            $this.jq.addClass('ui-state-focus');
            
            var activeItem = $this.focusedItem||$this.items.filter('.ui-state-highlight:visible:first');
            if(activeItem.length) {
                $this.focusedItem = activeItem;
            }
            else {
                $this.focusedItem = $this.items.filter(':visible:first');
            }
            
            setTimeout(function() {
                if($this.focusedItem) {
                    PrimeFaces.scrollInView($this.listContainer, $this.focusedItem);
                    $this.focusedItem.addClass('ui-listbox-outline');
                }
            }, 100);
        })
        .on('blur.selectListbox', function() {
            $this.jq.removeClass('ui-state-focus');
            $this.removeOutline();
            $this.focusedItem = null;
        })
        .on('keydown.selectListbox', function(e) {
            if(!$this.focusedItem) {
                return;
            }

            var keyCode = $.ui.keyCode,
                key = e.which;

            switch(key) {
                case keyCode.UP:
                    if(!$this.focusedItem.hasClass('ui-state-highlight')) {
                        $this.focusedItem.trigger('click.selectListbox');
                    }
                    else {
                        var prevItem = $this.focusedItem.prevAll('.ui-selectlistbox-item:visible:first');
                        if(prevItem.length) {
                            prevItem.trigger('click.selectListbox');

                            PrimeFaces.scrollInView($this.listContainer, $this.focusedItem);
                        }
                    }
                    e.preventDefault();
                break;

                case keyCode.DOWN:
                    if(!$this.focusedItem.hasClass('ui-state-highlight')) {
                        $this.focusedItem.trigger('click.selectListbox');
                    }
                    else {
                        var nextItem = $this.focusedItem.nextAll('.ui-selectlistbox-item:visible:first');
                        if(nextItem.length) {
                            nextItem.trigger('click.selectListbox');

                            PrimeFaces.scrollInView($this.listContainer, $this.focusedItem);
                        }
                    }
                    e.preventDefault();
                break;
            };
        });

    },
    
    removeOutline: function() {
        if(this.focusedItem && this.focusedItem.hasClass('ui-listbox-outline')) {
            this.focusedItem.removeClass('ui-listbox-outline');
        }
    }
});