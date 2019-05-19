PrimeFaces.widget.TabMenu = PrimeFaces.widget.Menu.extend({

    init: function(cfg) {
        this._super(cfg);

        this.items = this.jq.find('> .ui-tabmenu-nav > li:not(.ui-state-disabled)');

        this.bindEvents();
        this.bindKeyEvents();
    },

    bindEvents: function() {
        this.items.on('mouseover.tabmenu', function(e) {
                    var element = $(this);
                    if(!element.hasClass('ui-state-active')) {
                        element.addClass('ui-state-hover');
                    }
                })
                .on('mouseout.tabmenu', function(e) {
                    $(this).removeClass('ui-state-hover');
                });
    },

    bindKeyEvents: function() {
        /* For Keyboard accessibility and Screen Readers */
        this.items.attr('tabindex', 0);

        this.items.on('focus.tabmenu', function(e) {
            $(this).addClass('ui-menuitem-outline');
        })
        .on('blur.tabmenu', function(){
            $(this).removeClass('ui-menuitem-outline');
        })
        .on('keydown.tabmenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER) {
                var currentLink = $(this).children('a');
                currentLink.trigger('click');
                var href = currentLink.attr('href');
                if(href && href !== '#') {
                    window.location.href = href;
                }
                e.preventDefault();
            }
        });
    }
});
