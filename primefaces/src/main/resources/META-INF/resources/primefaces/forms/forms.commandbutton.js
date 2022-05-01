/**
 * __PrimeFaces CommandButton Widget__
 * 
 * CommandButton is an extended version of standard commandButton with AJAX and theming.
 *
 * @prop {number} [ajaxCount] number of concurrent active Ajax requests.
 *
 * @interface {PrimeFaces.widget.CommandButtonCfg} cfg The configuration for the {@link  CommandButton| CommandButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.CommandButton = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinButton(this.jq);

        this.bindTriggers();
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        $(document).off('pfAjaxSend.' + this.id + ' pfAjaxComplete.' + this.id);

        this._super(cfg);
    },

    /**
     * Sets up the global event listeners on the button.
     * @private
     */
    bindTriggers: function() {
        var $this = this;
        $this.ajaxCount = 0;
        $(document).on('pfAjaxSend.' + this.id, function(e, xhr, settings) {
            if (PrimeFaces.ajax.Utils.isXhrSource($this, settings)) {
                $this.ajaxCount++;
                if ($this.ajaxCount > 1) {
                    return;
                }
                $this.jq.toggleClass('ui-state-loading');
                if ($this.cfg.disableOnAjax !== false) {
                    $this.disable();
                }
                var loadIcon = $('<span class="ui-icon-loading ui-icon ui-c pi pi-spin pi-spinner"></span>');
                var uiIcon = $this.jq.find('.ui-icon');
                if (uiIcon.length) {
                    var prefix = 'ui-button-icon-';
                    loadIcon.addClass(prefix + uiIcon.attr('class').includes(prefix + 'left') ? 'left' : 'right');
                }
                $this.jq.prepend(loadIcon);
            }
        }).on('pfAjaxComplete.' + this.id, function(e, xhr, settings) {
            if (PrimeFaces.ajax.Utils.isXhrSource($this, settings)) {
                $this.ajaxCount--;
                if ($this.ajaxCount > 0) {
                    return;
                }
                $this.jq.toggleClass('ui-state-loading');
                if ($this.cfg.disableOnAjax !== false) {
                    $this.enable();
                }
                $this.jq.find('.ui-icon-loading').remove();
            }
        });
    },

    /**
     * Disables this button so that the user cannot press the button anymore.
     */
    disable: function() {
        PrimeFaces.utils.disableButton(this.jq);
    },

    /**
     * Enables this button so that the user can press the button.
     */
    enable: function() {
        PrimeFaces.utils.enableButton(this.jq);
    }

});