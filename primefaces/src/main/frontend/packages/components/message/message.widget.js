/**
 * __PrimeFaces Message Widget__
 * 
 * Message is a pre-skinned extended version of the standard JSF message component.
 * 
 * @interface {PrimeFaces.widget.MessageCfg} cfg The configuration for the {@link  Message| Message widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.target Client ID of the target for which to show this message.
 */
PrimeFaces.widget.Message = class Message extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        var msgSrc = this.jq.find('.ui-message-error-summary');
        if (msgSrc.length === 0) {
            msgSrc = this.jq.find('.ui-message-error-detail');
        }

        var text = msgSrc.text();
        if (text) {
            var target = $(PrimeFaces.escapeClientId(this.cfg.target));

            if (this.cfg.tooltip) {
                target.data('tooltip', text);
            }

            target.attr('aria-describedby', msgSrc.attr('id'));
        }
    }

    /**
     * Renders the given msg.
     * @param {PrimeFaces.FacesMessage} msg Message to render.
     */
    renderMessage(msg) {
        var display = this.jq.data('display');

        if (display !== 'tooltip') {
            this.jq.addClass('ui-message-error ui-widget ui-corner-all ui-helper-clearfix');

            if (display === 'both') {
                this.jq.append('<div><span class="ui-message-error-icon"></span><span class="ui-message-error-detail">' + PrimeFaces.escapeHTML(msg.detail) + '</span></div>');
            }
            else if (display === 'text') {
                this.jq.append('<span class="ui-message-error-detail">' + PrimeFaces.escapeHTML(msg.detail) + '</span>');
            }
            else if (display === 'icon') {
                this.jq.addClass('ui-message-icon-only')
                    .append('<span class="ui-message-error-icon" title="' + PrimeFaces.escapeHTML(msg.detail) + '"></span>');
            }
        }
        else {
            this.jq.hide();
            $(PrimeFaces.escapeClientId(this.jq.data('target'))).attr('title', PrimeFaces.escapeHTML(msg.detail));
        }
    }

    /**
     * Removes the current displayed message.
     */
    clearMessage() {
        this.jq.html('');
        this.jq.removeClass('ui-message-error ui-message-icon-only ui-widget ui-corner-all ui-helper-clearfix');
    }
}