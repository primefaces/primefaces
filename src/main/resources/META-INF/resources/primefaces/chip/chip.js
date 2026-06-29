/**
 * __PrimeFaces Chip Widget__
 *
 * Chip represents entities using icons, labels and images.
 *
 * @prop {JQuery} removeIcon DOM element of the icon for closing this chip, when this chip is closable (an `x` by
 * default).
 *
 * @interface {PrimeFaces.widget.ChipCfg} cfg The configuration for the {@link  Chip| Chip widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.Chip = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.removeIcon = this.jq.children('.ui-chip-remove-icon');

        this.bindEvents();
    },

    /**
     * Sets up all event listeners required for this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;
        
        this.jq.on("click.chip", function() {
           $this.callBehavior("select");
        });

        this.removeIcon.on("keydown.chip", function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE || key === keyCode.ENTER) {
                $this.close();
                e.preventDefault();
            }
        }).on("click.chip", function() {
            $this.close();
        });
    },

    /**
     * Closes the chip.
     * @private
     */
    close: function() {
        this.jq.remove();
        this.callBehavior("close");
    }
});
