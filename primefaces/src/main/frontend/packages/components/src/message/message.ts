
/**
 * The configuration for the {@link  Message} widget.
 * 
 * You can access this configuration via {@link Message.cfg | cfg}. Please note
 * that this configuration is usually meant to be read-only and should not be modified.
 */
export interface MessageCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /**
     * Client ID of the target for which to show this message.
     */
    target: string;

    /**
     * Tooltip to show when the user hovers over the message.
     */
    tooltip: string;
}

/**
 * __PrimeFaces Message Widget__
 * 
 * Message is a pre-skinned extended version of the standard Jakarta Faces message component.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class Message<Cfg extends MessageCfg = MessageCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        let msgSrc = this.jq.find('.p-message-error-summary');
        if (msgSrc.length === 0) {
            msgSrc = this.jq.find('.p-message-error-detail');
        }

        const text = msgSrc.text();
        if (text) {
            const target = this.cfg.target ? $(PrimeFaces.escapeClientId(this.cfg.target)) : $();

            if (this.cfg.tooltip) {
                target.data('tooltip', text);
            }

            target.attr('aria-describedby', msgSrc.attr('id') ?? "");
        }
    }

    /**
     * Renders the given msg.
     * @param msg Message to render.
     */
    renderMessage(msg: PrimeFacesTypes.FacesMessage): void {
        const display = this.jq.data('display');

        if (display !== 'tooltip') {
            this.jq.addClass('p-message-error p-widget');

            if (display === 'both') {
                this.jq.append('<div><span class="p-icon p-error-icon p-message-error-icon"></span><span class="p-message-error-detail">' + PrimeFaces.escapeHTML(msg.detail) + '</span></div>');
            }
            else if (display === 'text') {
                this.jq.append('<span class="p-message-error-detail">' + PrimeFaces.escapeHTML(msg.detail) + '</span>');
            }
            else if (display === 'icon') {
                this.jq.addClass('p-message-icon-only')
                    .append('<span class="p-icon p-error-icon p-message-error-icon" title="' + PrimeFaces.escapeHTML(msg.detail) + '"></span>');
            }
        }
        else {
            this.jq.hide();
            $(PrimeFaces.escapeClientId(this.jq.data('target'))).attr('title', PrimeFaces.escapeHTML(msg.detail));
        }
    }

    /**
     * Removes the currently displayed message.
     */
    clearMessage(): void {
        this.jq.html('');
        this.jq.removeClass('p-message-error p-message-icon-only p-widget');
    }
}

/**
 * Registers an implementation for the message render hook that displays
 * messages inside a message widget.
 */
export function registerMessageRenderHookForMessageWidget(): void {
    PrimeFaces.registerHook("messageRender", {
        clearMessagesForWidget: widget => {
            if (widget instanceof Message) {
                widget.clearMessage();
            }
        },
        renderMessageForWidget: (widget, messages) => {
            const message = messages[0];
            if (message && widget instanceof Message) {
                widget.renderMessage(message);
            }
        },
        renderMessagesInContainers: (messages, containers) => {
            let messageComponents = $();

            for (const container of containers) {
                const $container = $(container);

                if ($container.is('div.p-message')) {
                    messageComponents = messageComponents.add($container);
                }
                else {
                    messageComponents = messageComponents.add($container.find('div.p-message'));
                }
            }

            for (let i = 0; i < messageComponents.length; i++) {
                const messageComponent = messageComponents.eq(i);
                const target = messageComponent.data('target');
                const redisplay = messageComponent.data('redisplay');
                const messageWidget = PrimeFaces.getWidgetById(messageComponent.attr('id') ?? "");

                if (!(messageWidget instanceof Message)) {
                    continue;
                }

                messageWidget.clearMessage();

                for (const clientId in messages) {
                    if (target !== clientId) {
                        continue;
                    }
                    for (const msg of messages[clientId] ?? []) {
                        if (msg.rendered && !redisplay) {
                            continue;
                        }

                        messageWidget.renderMessage(msg);
                        msg.rendered = true;
                    }
                }
            }
        },
    });
}