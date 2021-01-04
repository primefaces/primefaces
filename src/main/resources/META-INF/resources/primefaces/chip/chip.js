/**
 * __PrimeFaces Chip Widget__
 *
 * Chip represents entities using icons, labels and images.
 *
 * @prop {JQuery} chip DOM element of the chip.
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

        this.chip = $(this.jqId);
        this.removeIcon = this.chip.children('.pi-chip-remove-icon')

        this.bindEvents();
    },

    /**
     * Sets up all event listeners required for this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.removeIcon.on("keydown", function() {
            $this.close();
        }).on("click", function() {
            $this.close();
        });

        this.chip.on("click", function() {
            $this.callBehavior('click');
        });
    },

    /**
     * Closes the chip.
     * @private
     */
    close: function() {
        this.chip.remove();
        this.callBehavior('close');
    }
});
