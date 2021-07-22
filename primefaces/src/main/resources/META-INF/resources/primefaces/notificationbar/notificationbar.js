/**
 * __PrimeFaces NotificationBar Widget__
 *
 * NotificationBar displays a multipurpose fixed positioned panel for notification.
 *
 * @typedef {"slide" | "fade" | "none"} PrimeFaces.widget.NotificationBar.Effect Possible values for the effect applied
 * when the notification bar is shown or hidden.
 *
 * @typedef {"top" | "bottom"} PrimeFaces.widget.NotificationBar.Position Possible values for where the notification bar
 * is shown.
 *
 * @typedef {"fast" | "normal" | "slow"} PrimeFaces.widget.NotificationBar.EffectSpeed Possible values for speed of the
 * effect when the notification bar is shown or hidden.
 *
 * @interface {PrimeFaces.widget.NotificationBarCfg} cfg The configuration for the {@link  NotificationBar| NotificationBar widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {boolean} cfg.autoDisplay Whether the notification bar is shown by default on page load.
 * @prop {PrimeFaces.widget.NotificationBar.Effect} cfg.effect Effect applied when the notification bar is shown or
 * hidden.
 * @prop {PrimeFaces.widget.NotificationBar.EffectSpeed} cfg.effectSpeed Speed of the effect when the notification bar
 * is shown or hidden.
 * @prop {PrimeFaces.widget.NotificationBar.Position} cfg.position Position of the bar, either top or bottom.
 */
PrimeFaces.widget.NotificationBar = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        var _self = this;

        //relocate
        this.jq.css(this.cfg.position, '0px').appendTo($('body'));

        //display initially
        if(this.cfg.autoDisplay) {
            $(this.jq).css('display', 'block');
        }

        //bind events
        this.jq.children('.ui-notificationbar-close').on("click", function() {
            _self.hide();
        });
    },

    /**
     * Shows the notification bar.
     *
     * The up-to-three arguments will be routed to jQuery as-is.
     *
     * @param {any} [a1] First parameter passed through to jQuery UI.
     * @param {any} [a2] Second parameter passed through to jQuery UI.
     * @param {any} [a3] Third parameter passed through to jQuery UI.
     *
     * @see http://api.jquery.com/slidedown/
     * @see http://api.jquery.com/fadein/
     * @see http://api.jquery.com/show/
     */
    show: function(a1, a2, a3) {
        if(this.cfg.effect === 'slide')
            $(this.jq).slideDown(a1, a2, a3);
        else if(this.cfg.effect === 'fade')
            $(this.jq).fadeIn(a1, a2, a3);
        else if(this.cfg.effect === 'none')
            $(this.jq).show(a1, a2, a3);
    },

    /**
     * Hides the notification bar.
     */
    hide: function() {
        if(this.cfg.effect === 'slide')
            $(this.jq).slideUp(this.cfg.effect);
        else if(this.cfg.effect === 'fade')
            $(this.jq).fadeOut(this.cfg.effect);
        else if(this.cfg.effect === 'none')
            $(this.jq).hide();
    },

    /**
     * Checks whether the notification bar is currently displayed.
     * @return {boolean} `true` if the notification bar is currently visible, `false` otherwise.
     */
    isVisible: function() {
        return this.jq.is(':visible');
    },

    /**
     * Shows the notification bar it is currently hidden, or hides it if it is currently displayed.
     */
    toggle: function() {
        if(this.isVisible())
            this.hide();
        else
            this.show();
    }

});
