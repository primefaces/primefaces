/**
 * The configuration for the {@link  Growl} widget.
 * 
 * You can access this configuration via {@link Growl.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface GrowlCfg extends PrimeType.widget.BaseWidgetCfg {
    /**
     * `true` to treat the message's summary and details as plain text, `false` to treat them as
     * an HTML string.
     */
    escape: boolean;

    /**
     * Defines if previous messages should be kept on a new message is shown.
     */
    keepAlive: boolean;

    /**
     * Duration in milliseconds to display non-sticky messages.
     */
    life: number;

    /**
     * List of messages that are shown initially when the widget is loaded or
     * refreshed.
     */
    msgs: PrimeType.FacesMessage[];

    /**
     * Specifies if the message should stay instead of hidden automatically.
     */
    sticky: boolean;
}

/**
 * __PrimeFaces Growl Widget__
 *
 * Growl is based on the Mac’s growl notification widget and used to display FacesMessages in an overlay.
 *
 * @typeParam Cfg Type of the configuration object.
 */
export class Growl<Cfg extends GrowlCfg = GrowlCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        //create container
        var styleClass = "ui-growl ui-widget";
        styleClass = this.cfg.sticky ? styleClass + " ui-growl-sticky" : styleClass;
        this.jq = $('<div id="' + this.id + '_container" class="' + styleClass + '" aria-live="polite"></div>');
        this.jq.appendTo($(document.body));

        //render messages
        this.show(this.cfg.msgs ?? []);
    }

    override refresh(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        this.cfg = cfg;
        this.show(cfg.msgs ?? []);

        this.removeScriptElement(this.id);
    }

    /**
     * Appends a message to the current displayed messages.
     *
     * @param msg A message to translate into an HTML element.
     */
    add(msg: PrimeType.FacesMessage): void {
        this.renderMessage(msg);
    }

    /**
     * Appends all messages to the current displayed messages.
     *
     * @param msgs The messages to translate into HTML elements.
     */
    addAll(msgs: readonly PrimeType.FacesMessage[]): void {
        for (const msg of msgs ?? []) {
            this.renderMessage(msg);
        }
    }

    /**
     * Displays the given messages in the growl window represented by this growl widget.
     *
     * @param msgs Messages to display in this growl
     */
    show(msgs: PrimeType.FacesMessage[]): void {
        PrimeFaces.nextZindex(this.jq);

        if (!this.cfg.keepAlive) {
            //clear previous messages
            this.removeAll();
        }

        for (const msg of msgs ?? []) {
            this.renderMessage(msg);
        }
    }

    /**
     * Removes all growl messages that are currently displayed.
     */
    removeAll(): void {
        this.jq.children('div.ui-growl-item-container').remove();
    }

    /**
     * Creates the HTML elements for the given faces message, and adds it to the DOM.
     * @param msg A message to translate into an HTML element.
     */
    renderMessage(msg: PrimeType.FacesMessage): void {
        let markup = '<div class="ui-growl-item-container ui-state-highlight ui-helper-hidden ui-shadow ui-growl-' + msg.severity + '">';
        markup += '<div role="alert" class="ui-growl-item">';
        markup += '<div class="ui-growl-icon-close ui-icon ui-icon-closethick" style="display:none"></div>';
        markup += '<span class="ui-growl-image ui-growl-image-' + msg.severity + '" ></span>';
        // GitHub #5153 for screen readers
        markup += '<span class="ui-growl-severity ui-helper-hidden-accessible">' + this.getAriaLabel(`messages.${PrimeFaces.utils.toRootUpperCase(msg.severity)}`) + '</span>';
        markup += '<div class="ui-growl-message">';
        markup += '<span class="ui-growl-title"></span>';
        markup += '<p></p>';
        markup += '</div><div style="clear: both;"></div></div></div>';

        const message = $(markup);
        const summaryEL = message.find('span.ui-growl-title');
        const detailEL = summaryEL.next();

        if (this.cfg.escape) {
            summaryEL.text(msg.summary);
            detailEL.text(msg.detail);
        }
        else {
            summaryEL.html(msg.summary);
            detailEL.html(msg.detail);
        }

        this.bindEvents(message);

        message.appendTo(this.jq).fadeIn();
    }

    /**
     * Sets up all event listeners for the given message, such as for closing the message when the close icon clicked.
     * @param message The message for which to set up the event listeners
     */
    private bindEvents(message: JQuery): void {
        var $this = this,
            sticky = this.cfg.sticky;

        message.on("mouseover", function() {
            var msg = $(this);

            //visuals
            if (!msg.is(':animated')) {
                msg.find('div.ui-growl-icon-close:first').show();
            }

            // clear hide timeout on mouseover
            if (!sticky) {
                clearTimeout(msg.data('timeout'));
            }
        })
            .on("mouseout", function() {
                //visuals
                $(this).find('div.ui-growl-icon-close:first').hide();

                // setup hide timeout again after mouseout
                if (!sticky) {
                    $this.setRemovalTimeout(message);
                }
            });

        //remove message on click of close icon
        const closeIcon = message.find('div.ui-growl-icon-close');
        PrimeFaces.skinCloseAction(closeIcon);
        closeIcon.on("click", function() {
            $this.removeMessage(message);

            //clear timeout if removed manually
            if (!sticky) {
                clearTimeout(message.data('timeout'));
            }
        });

        //hide the message after given time if not sticky
        if (!sticky) {
            this.setRemovalTimeout(message);
        }
    }

    /**
     * Removes the given message from the screen, if it is currently displayed.
     * @param message The message to remove, an HTML element with the class `ui-growl-item-container`.
     */
    removeMessage(message: JQuery): void {
        message.fadeTo(400, 0, function() {
            message.slideUp(400, 'easeInOutCirc', function() {
                message.remove();
            });
        });
    }

    /**
     * Starts a timeout that removes the given message after a certain delay (as defined by this widget's
     * configuration).
     * @param message The message to remove, an HTML element with the class `ui-growl-item-container`.
     */
    private setRemovalTimeout(message: JQuery): void {
        const timeout = PrimeFaces.queueTask(() => this.removeMessage(message), this.cfg.life);
        message.data('timeout', timeout ?? "");
    }
}

/**
 * Registers an implementation for the message render hook that displays
 * messages inside a growl widget.
 */
export function registerMessageRenderHookForGrowlWidget(): void {
    PrimeFaces.registerHook("messageRender", {
        clearMessagesForWidget: widget => {
            if (widget instanceof Growl) {
                widget.removeAll();
            }
        },
        renderMessageForWidget: (widget, messages) => {
            if (widget instanceof Growl) {
                for (const message of messages) {
                    widget.renderMessage(message);
                }
            }
        },
        renderMessagesInContainers: (messages, containers) => {
            let growlComponents = $();

            for (const container of containers) {
                const $container = $(container);
                if ($container.is('.ui-growl-pl')) {
                    growlComponents = growlComponents.add($container);
                }
                else {
                    growlComponents = growlComponents.add($container.find('.ui-growl-pl'));
                }
            }

            // filter out by severity
            growlComponents = growlComponents.filter((_, el) => {
                return $(el).data('severity').indexOf('error') !== -1;
            });

            for (let i = 0; i < growlComponents.length; i++) {
                const growlComponent = growlComponents.eq(i);
                const redisplay = growlComponent.data('redisplay');
                const globalOnly = growlComponent.data('global');
                const showSummary = growlComponent.data('summary');
                const showDetail = growlComponent.data('detail');
                const growlWidget = PrimeFaces.getWidgetById(growlComponent.attr('id') ?? "");

                if (!(growlWidget instanceof Growl)) {
                    continue;
                }

                growlWidget.removeAll();

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

                        growlWidget.renderMessage(msgToRender);
                        msg.rendered = true;
                    }
                }
            }
        },
    });
}
