/**
 * The configuration for the {@link  Messages} widget.
 * 
 * You can access this configuration via {@link Messages.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface MessagesCfg extends PrimeType.widget.BaseWidgetCfg {
    closable: boolean;
}

/**
 * __PrimeFaces Messages Widget__
 * 
 * Messages is a pre-skinned extended version of the standard Jakarta Faces messages component.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class Messages<Cfg extends MessagesCfg = MessagesCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /**
     * The DOM element for the icon that closes this panel.
     */
    closer: JQuery = $();

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.closer = $('.ui-messages-close');
        PrimeFaces.skinCloseAction(this.closer);
    }

    /**
     * Creates the HTML elements for the given faces message, and adds it to the DOM.
     * @param msg A message to translate into an HTML element.
     */
    appendMessage(msg: PrimeType.FacesMessage): void {
        const closeLabel = this.getAriaLabel('close');

        let severityContainer =  this.jq.children('div.ui-messages-' + msg.severity);
        if (severityContainer.length === 0) {
            severityContainer = this.jq.append(
                 '<div class="ui-messages-' + msg.severity + '">' +
                    (this.cfg.closable ? '<a href="#" class="ui-messages-close" onclick="$(this).parent().slideUp();return false;" role="button" aria-label="' + closeLabel + '">' +
                        '<span class="ui-icon ui-icon-close"></span>' +
                    '</a>' : '') +
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
    clearMessages(): void {
        this.jq.children().remove();
    }
}

/**
 * Registers an implementation for the message render hook that displays
 * messages inside a messages widget.
 */
export function registerMessageRenderHookForMessagesWidget(): void {
    PrimeFaces.registerHook("messageRender", {
        clearMessagesForWidget: widget => {
            if (widget instanceof Messages) {
                widget.clearMessages();
            }
        },
        renderMessageForWidget: (widget, messages) => {
            if (widget instanceof Messages) {
                for (const message of messages) {
                    widget.appendMessage(message);
                }
            }
        },
        renderMessagesInContainers: (messages, containers) => {
            let messagesComponents = $();

            for (const container of containers) {
                const $container = $(container);

                if ($container.is('div.ui-messages')) {
                    messagesComponents = messagesComponents.add($container);
                }
                else {
                    messagesComponents = messagesComponents.add($container.find('div.ui-messages'));
                }
            }

            // filter out by severity
            messagesComponents = messagesComponents.filter((_, el) => {
                if ($(el).is('.ui-fileupload-messages')) {
                    return false;
                }
                return $(el).data('severity').indexOf('error') !== -1;
            });

            for (let i = 0; i < messagesComponents.length; i++) {
                const messagesComponent = messagesComponents.eq(i);
                const globalOnly = messagesComponent.data('global');
                const redisplay = messagesComponent.data('redisplay');
                const showSummary = messagesComponent.data('summary');
                const showDetail = messagesComponent.data('detail');
                const messagesWidget = PrimeFaces.getWidgetById(messagesComponent.attr('id') ?? "");

                if (!(messagesWidget instanceof Messages)) {
                    continue;
                }

                messagesWidget.clearMessages();

                for (const clientId in messages) {
                    for (const msg of messages[clientId] ?? []) {
                        if (globalOnly || (msg.rendered && !redisplay)) {
                            continue;
                        }

                        const msgToRender = { ...msg };
                        if (!showSummary) {
                            msgToRender.summary = '';
                        }
                        if (!showDetail) {
                            msgToRender.detail = '';
                        }

                        messagesWidget.appendMessage(msgToRender);
                        msg.rendered = true;
                    }
                }
            }
        },
    });
}
