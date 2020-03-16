/**
 * PrimeFaces LinkButton Widget
 */
PrimeFaces.widget.LinkButton = PrimeFaces.widget.BaseWidget.extend({

    init : function (cfg) {
        this._super(cfg);
        this.button = this.jq;
        this.link = this.jq.children('a');

        PrimeFaces.skinButton(this.button);

        this.bindEvents();
    },

    bindEvents : function () {
        var $this = this;

        if (this.link) {
            this.link.off().on("focus.linkbutton keydown.linkbutton", function () {
                $this.button.addClass('ui-state-focus ui-state-active');
            }).on("blur.linkbutton", function () {
                $this.button.removeClass('ui-state-focus ui-state-active');
            });
        }
    },

    disable : function () {
        this.button.removeClass('ui-state-hover ui-state-focus ui-state-active')
                .addClass('ui-state-disabled').attr('disabled', 'disabled');
    },

    enable : function () {
        this.button.removeClass('ui-state-disabled').removeAttr('disabled');
    }

});