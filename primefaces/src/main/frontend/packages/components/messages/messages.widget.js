/**
 * __PrimeFaces Messages Widget__
 * 
 * Messages is a pre-skinned extended version of the standard JSF messages component.
 * 
 * @prop {JQuery} closer The DOM element for the icon that closes this panel.
 * @interface {PrimeFaces.widget.MessagesCfg} cfg The configuration for the {@link  Messages| Messages widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.Messages = class Messages extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.closer = $('.ui-messages-close');
        PrimeFaces.skinCloseAction(this.closer);
    }

    /**
     * Creates the HTML elements for the given faces message, and adds it to the DOM.
     * @param {PrimeFaces.FacesMessage} msg A message to translate into an HTML element.
     */
    appendMessage(msg) {
        var closeLabel = this.getAriaLabel('close');

        var severityContainer =  this.jq.children('div.ui-messages-' + msg.severity);
        if (severityContainer.length === 0) {
            severityContainer = this.jq.append(
                 '<div class="ui-messages-' + msg.severity + ' ui-corner-all">' +
                    '<a href="#" class="ui-messages-close" onclick="$(this).parent().slideUp();return false;" role="button" aria-label="'+closeLabel+'">' +
                        '<span class="ui-icon ui-icon-close"></span>' +
                    '</a>' +
                    '<span class="ui-messages-' + msg.severity + '-icon"></span>' +
                    '<ul>' +

                    '</ul>' +
                '</div>');
        }

        severityContainer.find('ul').append(
            '<li>' +
                '<span class="ui-messages-' + msg.severity + '-summary">' + (msg.summary ? msg.summary : '') + '</span>' +
                '<span class="ui-messages-' + msg.severity + '-detail">' + (msg.detail ? msg.detail : '') + '</span>' +
            '</li>');
    }

    /**
     * Clears all current messages from the DOM.
     */
    clearMessages() {
        this.jq.children().remove();
    }
}