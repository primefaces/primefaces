/**
 * __PrimeFaces Dock Widget__
 * 
 * Dock component mimics the well known dock interface of Mac OS X.
 * 
 * @interface {PrimeFaces.widget.DockCfg} cfg The configuration for the {@link  Dock| Dock widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.container Selector for the container element of the dock. 
 * @prop {string} cfg.items Selector for the dock items (usually anchor elements).
 * @prop {string} cfg.itemsText Selector for the text of the items (usually span elements).
 */
PrimeFaces.widget.Dock = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg, this>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.items = 'a';
        this.cfg.itemsText = 'span';
        this.cfg.container = '.ui-dock-container-' + this.cfg.position;
        
        $(this.jqId).Fisheye(this.cfg);
    }
    
});