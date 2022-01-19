/**
 * __PrimeFaces CommandButton Widget__
 * 
 * CommandButton is an extended version of standard commandButton with AJAX and theming.
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

        if (this.cfg.disableOnAjax || this.cfg.inlineAjaxStatus) {
            this.bindTriggers();
        }
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

        $(document).on('pfAjaxSend.' + this.id, function(e, xhr, settings) {
            if (PrimeFaces.ajax.Utils.isXhrSource($this, settings)) {
                if ($this.cfg.disableOnAjax) {
                    $this.disable();
                }
                if ($this.cfg.inlineAjaxStatus) {
                    $this.addLoadingIcon();
                }
            }
        }).on('pfAjaxComplete.' + this.id, function(e, xhr, settings) {
            if (PrimeFaces.ajax.Utils.isXhrSource($this, settings)) {
                if ($this.cfg.disableOnAjax) {
                    $this.enable();
                }
                if ($this.cfg.inlineAjaxStatus) {
                    $this.removeLoadingIcon();
                }
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
    },


    /**
     * Adds the Ajax loading icon.
     * @private
     */
    addLoadingIcon: function() {
        var loadIcon = $('<span class="ui-loading-icon ui-icon ui-c pi pi-spin pi-spinner"></span>');
        var uiIcon = this.jq.find('.ui-icon').hide();
        if (uiIcon.length) {
            var prefix = 'ui-button-icon-';
            this.jq.append(loadIcon.addClass(prefix + uiIcon.attr('class').includes(prefix + 'left') ? 'left' : 'right'));
        }
        else {
            var text = this.jq.find('.ui-button-text');
            text.css('min-width', text.outerWidth() + 'px');
            this.buttonText = text.html();
            text.html(loadIcon);
        }
    },

    /**
     * Removes the Ajax loading icon.
     * @private
     */
    removeLoadingIcon: function() {
        this.jq.find('.ui-loading-icon').remove();
        this.jq.find('.ui-icon').show();
        this.jq.find('.ui-button-text').removeAttr('style').html(this.buttonText);
    }

});