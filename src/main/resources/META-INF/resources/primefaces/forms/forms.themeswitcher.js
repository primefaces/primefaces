/**
 * __PrimeFaces ThemeSwitcher Widget__
 * 
 * ThemeSwitcher enables switching PrimeFaces themes on the fly with no page refresh.
 * 
 * @interface {PrimeFaces.widget.ThemeSwitcherCfg} cfg The configuration for the {@link  ThemeSwitcher| ThemeSwitcher widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.SelectOneMenuCfg} cfg
 * 
 * @prop {string} cfg.appendTo The search expression for the element to which the overlay panel should be appended.
 */
PrimeFaces.widget.ThemeSwitcher = PrimeFaces.widget.SelectOneMenu.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        var $this = this;
        this.input.on('change', function() {
            PrimeFaces.changeTheme($this.getSelectedValue());
        });
    }
});
