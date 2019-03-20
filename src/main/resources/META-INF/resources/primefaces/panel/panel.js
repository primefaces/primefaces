/**
 * PrimeFaces Panel Widget
 */
PrimeFaces.widget.Panel = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.header = this.jq.children('div.ui-panel-titlebar');
        this.title = this.header.children('span.ui-panel-title');
        this.content = $(this.jqId + '_content');

        this.bindEvents();
    },

    bindEvents: function() {
        var $this = this;

        if(this.cfg.toggleable) {
            this.bindToggler();

            if(this.cfg.toggleableHeader) {
                this.header.on('click', function() {
                    if(!$this.isTitlebarClicked) {
                        $this.toggle();
                    }

                    $this.isTitlebarClicked = false;
                });
            }
        }

        if(this.cfg.closable) {
            this.bindCloser();
        }

        if(this.cfg.hasMenu) {
            $(this.jqId + '_menu').on('click.panel', function(e) {
                e.preventDefault();
            });
        }

        //visuals for action items
        this.header.find('.ui-panel-titlebar-icon').on('mouseover.panel',function() {
            $(this).addClass('ui-state-hover');
        }).on('mouseout.panel',function() {
            $(this).removeClass('ui-state-hover');
        }).on('click.panel', function(e) {
            var href = $(this).attr('href');
            if(!href || href == '#') {
                e.preventDefault();
            }

            $this.isTitlebarClicked = true;
        });
    },

    toggle: function() {
        if(this.cfg.collapsed) {
            this.expand();
            PrimeFaces.invokeDeferredRenders(this.id);
        }
        else {
            this.collapse();
        }
    },

    expand: function() {
        this.toggleState(false, 'ui-icon-plusthick', 'ui-icon-minusthick');

        if(this.cfg.toggleOrientation === 'vertical')
            this.slideDown();
        else if(this.cfg.toggleOrientation === 'horizontal')
            this.slideRight();
    },

    collapse: function() {
        this.toggleState(true, 'ui-icon-minusthick', 'ui-icon-plusthick');

        if(this.cfg.toggleOrientation === 'vertical')
            this.slideUp();
        else if(this.cfg.toggleOrientation === 'horizontal')
            this.slideLeft();
    },

    slideUp: function() {
        this.content.slideUp(this.cfg.toggleSpeed, 'easeInOutCirc');
    },

    slideDown: function() {
        this.content.slideDown(this.cfg.toggleSpeed, 'easeInOutCirc');
    },

    slideLeft: function() {
        var $this = this;

        this.originalWidth = this.jq.width();

        this.title.hide();
        this.toggler.hide();
        this.content.hide();

        this.jq.animate({
            width: '42px'
        }, this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            $this.toggler.show();
            $this.jq.addClass('ui-panel-collapsed-h');
        });
    },

    slideRight: function() {
        var $this = this,
        expandWidth = this.originalWidth||'100%';

        this.toggler.hide();

        this.jq.animate({
            width: expandWidth
        }, this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            $this.jq.removeClass('ui-panel-collapsed-h');
            $this.title.show();
            $this.toggler.show();

            $this.content.css({
                'visibility': 'visible'
                ,'display': 'block'
                ,'height': 'auto'
            });
        });
    },

    toggleState: function(collapsed, removeIcon, addIcon) {
        this.toggler.children('span.ui-icon').removeClass(removeIcon).addClass(addIcon);
        this.cfg.collapsed = collapsed;
        this.toggleStateHolder.val(collapsed);

        this.callBehavior('toggle');
    },

    close: function() {
        if(this.visibleStateHolder) {
            this.visibleStateHolder.val(false);
        }

        var $this = this;
        this.jq.fadeOut(this.cfg.closeSpeed, function(e) {
            if($this.hasBehavior('close')) {
                $this.callBehavior('close');
            }
        });
    },

    show: function() {
        var $this = this;
        $(this.jqId).fadeIn(this.cfg.closeSpeed, function() {
            PrimeFaces.invokeDeferredRenders($this.id);
        });

        this.visibleStateHolder.val(true);
    },

    bindToggler: function() {
        var $this = this;

        this.toggler = $(this.jqId + '_toggler');
        this.toggleStateHolder = $(this.jqId + '_collapsed');

        this.toggler.click(function() {
            $this.toggle();

            return false;
        });
    },

    bindCloser: function() {
        var $this = this;

        this.closer = $(this.jqId + '_closer');
        this.visibleStateHolder = $(this.jqId + "_visible");

        this.closer.click(function(e) {
            $this.close();
            e.preventDefault();

            return false;
        });
    }

});