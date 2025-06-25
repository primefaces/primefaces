/**
 * __PrimeFaces CommandLink Widget__
 * 
 * CommandLink is an extended version of standard commandLink with AJAX and theming.
 *
 * @prop {number} [ajaxCount] Number of concurrent active Ajax requests.
 * @prop {number} [ajaxStart] Timestamp of the Ajax request that started the animation.
 * @prop {string} [tabIndex] The link tabIndex or default to '0'.
 * 
 * @interface {PrimeFaces.widget.CommandLinkCfg} cfg The configuration for the {@link  CommandLink| CommandLink widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.CommandLink = class CommandLink extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.tabIndex = this.jq.attr('tabindex') || '0';
        this.bindTriggers();
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        this.jq.off('click.commandlink');
        $(document).off('.link' + this.id);

        super.refresh(cfg);
    }

    /**
     * Sets up the global event listeners on the link.
     * @private
     */
    bindTriggers() {
        var $this = this;
        $this.ajaxCount = 0;
        this.jq.on('click.commandlink', function(e, xhr, settings) {
            if ($this.jq.hasClass('ui-state-disabled')) {
                e.preventDefault();
            }
        });

        if (this.cfg.disableOnAjax !== false) {
            var namespace = '.link' + this.id;

            $(document).on('pfAjaxSend.' + namespace, function(e, xhr, settings) {
                if (PrimeFaces.ajax.Utils.isXhrSource($this, settings)) {
                    $this.ajaxCount++;
                    if ($this.ajaxCount > 1) {
                        return;
                    }
                    $this.jq.addClass('ui-state-loading');
                    $this.ajaxStart = Date.now();
                    $this.disable();
                }
            }).on('pfAjaxComplete.' + namespace, function(e, xhr, settings, args) {
                if (PrimeFaces.ajax.Utils.isXhrSource($this, settings)) {
                    $this.ajaxCount--;
                    if ($this.ajaxCount > 0 || !args || args.redirect) {
                        return;
                    }
                    PrimeFaces.queueTask(
                        function(){ $this.endAjaxDisabled($this); },
                        Math.max(PrimeFaces.ajax.minLoadAnimation + $this.ajaxStart - Date.now(), 0)
                    );
                    delete $this.ajaxStart;
                }
            });

            $this.addDestroyListener(function() {
                $(document).off(namespace);
            });
        }
    }

    /**
     * Ends the AJAX disabled state.
     * @param {PrimeFaces.widget.BaseWidget} [widget] the widget.
     */
    endAjaxDisabled(widget) {
        widget.jq.removeClass('ui-state-loading');

        if (!widget.cfg.disabledAttr) {
            widget.enable();
        }
    }

    /**
     * Disables this link so that the user cannot click the link anymore.
     */
    disable() {
        this.jq.removeClass('ui-state-hover ui-state-focus ui-state-active')
                .addClass('ui-state-disabled')
                .attr('tabindex', '-1');
    }

    /**
     * Enables this link so that the user can click the link.
     */
    enable() {
        this.jq.removeClass('ui-state-disabled')
                .attr('tabindex', this.tabIndex);
    }

}
