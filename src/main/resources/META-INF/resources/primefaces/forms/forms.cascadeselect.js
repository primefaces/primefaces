/**
 * __PrimeFaces CascadeSelect Widget__
 *
 * CascadeSelect displays a nested structure of options
 *
 * @prop {JQuery} cascadeSelect DOM element of the cascadeSelect.
 *
 * @interface {PrimeFaces.widget.CascadeSelectCfg} cfg The configuration for the {@link  CascadeSelect| CascadeSelect widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.CascadeSelect = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.cascadeSelect = $(this.jqId);
        this.input = $(this.jqId + '_input');
        this.label = this.jq.find('.ui-cascadeselect-label');
        this.menuIcon = this.jq.children('.ui-cascadeselect-trigger');
        this.itemsWrapper = this.panel.children('.ui-cascadeselect-items-wrapper');
        this.disabled = this.jq.hasClass('ui-state-disabled');
        this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this);

        if(!this.disabled) {
            this.bindEvents();
            this.bindConstantEvents();

            PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');
        }
    },

});
