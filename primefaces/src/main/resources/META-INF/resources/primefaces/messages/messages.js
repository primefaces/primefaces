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
PrimeFaces.widget.Messages = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.closer = $('.ui-messages-close');
        this.closer.attr('aria-label', PrimeFaces.getAriaLabel('close'));
    },

    /**
     * Creates the HTML elements for the given faces message, and adds it to the DOM.
     * @param {PrimeFaces.FacesMessage} msg A message to translate into an HTML element.
     */
    appendMessage: function(msg) {
        var closeLabel = PrimeFaces.getAriaLabel('close');
        this.jq.append(
             '<div class="ui-messages-' + msg.severity + ' ui-corner-all">' +
                '<a href="#" class="ui-messages-close" onclick="$(this).parent().slideUp();return false;" aria-label="'+closeLabel+'">' +
                    '<span class="ui-icon ui-icon-close"></span>' +
                '</a>' +
                '<span class="ui-messages-' + msg.severity + '-icon"></span>' +
                '<ul>' +
                    '<li>' +
                        '<span class="ui-messages-' + msg.severity + '-summary">' + (msg.summary ? msg.summary : '') + '</span>' +
                        '<span class="ui-messages-' + msg.severity + '-detail">' + (msg.detail ? msg.detail : '') + '</span>' +
                    '</li>' +
                '</ul>' +
            '</div>');
    },

    /**
     * Clears all current messages from the DOM.
     */
    clearMessages: function() {
        this.jq.children().remove();
    },
});