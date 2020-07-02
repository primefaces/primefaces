/**
 * __PrimeFaces LinkButton Widget__
 * 
 * LinkButton a simple link, which is styled as a button and integrated with JSF navigation model.
 *
 * @prop {JQuery} link The DOM element for the link that is a child of the button.
 * 
 * @interface {PrimeFaces.widget.LinkButtonCfg} cfg The configuration for the {@link  LinkButton| LinkButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.LinkButton = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        this.link = this.jq.children('a');

        PrimeFaces.skinButton(this.jq);

        this.bindEvents();
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function () {
        var $this = this;

        if (this.link.length > 0) {
            this.link.off('focus.linkbutton keydown.linkbutton blur.linkbutton')
                .on('focus.linkbutton keydown.linkbutton', function () {
                    $this.jq.addClass('ui-state-focus ui-state-active');
                }).on('blur.linkbutton', function () {
                    $this.jq.removeClass('ui-state-focus ui-state-active');
                });
        }
    },

    /**
     * Disables this link button so that it cannot be clicked.
     */
    disable: function () {
        this.jq.removeClass('ui-state-hover ui-state-focus ui-state-active')
                .addClass('ui-state-disabled').attr('disabled', 'disabled');
    },

    /**
     * Enables this link button so that it can be clicked.
     */
    enable: function () {
        this.jq.removeClass('ui-state-disabled').removeAttr('disabled');
    }

});