/**
 * __PrimeFaces StaticMessage Widget__
 * 
 * StaticMessage widget to handle events.
 */
PrimeFaces.widget.StaticMessage = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.bindEvents();
    },

    /**
     * Bind behavior events.
     */
    bindEvents: function () {
        var $this = this;

        $('.ui-messages-close', this.jq).on('click.staticmessage', function(e) {
            if ($this.hasBehavior('close')) {
                $this.callBehavior('close');
            }
        });
    }

});