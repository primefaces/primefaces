/**
 * PrimeFace Dock Widget
 */
PrimeFaces.widget.Dock = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this._super(cfg);

        this.items = $('.ui-dock ul li');
        this.links = $(".ui-dock li a");

        if (this.cfg.blockScroll) {
            PrimeFaces.utils.preventScrolling();
        }

        this.bindAnimations();
    },

    bindAnimations: function () {
        var $this = this;
        this.items.hover(
            function () {
                var item = $(this);
                item.addClass('active');
                item.prev().addClass('prev').prev().addClass('prev-anchor');
                item.next().addClass('next').next().addClass('next-anchor');
            },
            function () {
                $this.items.removeClass('active prev next next-anchor prev-anchor');
            }
        );

        // add bounce effect 
        if (this.cfg.animate) {
            this.links.click(function (e) {
                var item = $(this);
                item.addClass('ui-dock-bounce').delay($this.cfg.animationDuration).queue(function () {
                    item.removeClass('ui-dock-bounce');
                    item.dequeue();
                });
            });
        }

    }

});