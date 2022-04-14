/**
 * __PrimeFaces CommandLink Widget__
 * 
 * CommandLink is an extended version of standard commandLink with AJAX and theming.
 * 
 * @interface {PrimeFaces.widget.CommandLinkCfg} cfg The configuration for the {@link  CommandLink| CommandLink widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.CommandLink = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.bindTriggers();
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this.jq.off('click.commandlink');
        $(document).off('pfAjaxSend.' + this.id + ' pfAjaxComplete.' + this.id);

        this._super(cfg);
    },

    /**
     * Sets up the global event listeners on the link.
     * @private
     */
    bindTriggers: function() {
        var $this = this;

        this.jq.on('click.commandlink', function(e, xhr, settings) {
            if ($this.jq.hasClass('ui-state-disabled')) {
                e.preventDefault();
            }
        });

        if (this.cfg.disableOnAjax !== false) {
            $(document).on('pfAjaxSend.' + this.id, function(e, xhr, settings) {
                if (PrimeFaces.ajax.Utils.isXhrSource($this, settings)) {
                    $this.jq.toggleClass('ui-state-loading');
                    $this.disable();
                }
            }).on('pfAjaxComplete.' + this.id, function(e, xhr, settings) {
                if (PrimeFaces.ajax.Utils.isXhrSource($this, settings)) {
                    $this.jq.toggleClass('ui-state-loading');
                    $this.enable();
                }
            });
        }
    },

    /**
     * Disables this link so that the user cannot click the link anymore.
     */
    disable: function() {
        this.jq.removeClass('ui-state-hover ui-state-focus ui-state-active')
                .addClass('ui-state-disabled')
                .attr('tabindex', '-1')
                .attr('aria-disabled', 'true');
    },

    /**
     * Enables this link so that the user can click the link.
     */
    enable: function() {
        this.jq.removeClass('ui-state-disabled')
                .removeAttr('tabindex aria-disabled');
    }

});