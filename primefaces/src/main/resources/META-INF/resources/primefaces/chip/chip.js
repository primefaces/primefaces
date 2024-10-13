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
PrimeFaces.widget.Chip = class Chip extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.removeIcon = this.jq.children('.ui-chip-remove-icon');

        this.bindEvents();
    }

    /**
     * Sets up all event listeners required for this widget.
     * @private
     */
    bindEvents() {
        var $this = this;
        
        this.jq.on("click.chip", function() {
           $this.callBehavior("select");
        });

        this.removeIcon.on("keydown.chip", function(e) {
            if (PrimeFaces.utils.isActionKey(e)) {
                $this.close();
                e.preventDefault();
            }
        }).on("click.chip", function() {
            $this.close();
        });
    }

    /**
     * Closes the chip.
     * @private
     */
    close() {
        this.jq.remove();
        this.callBehavior("close");
    }
}
