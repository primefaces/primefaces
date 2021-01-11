/**
 * __PrimeFaces Growl Widget__
 *
 * Growl is based on the Mac’s growl notification widget and used to display FacesMessages in an overlay.
 *
 * @interface {PrimeFaces.widget.GrowlCfg} cfg The configuration for the {@link  Growl| Growl widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {boolean} cfg.escape `true` to treat the message's summary and details as plain text, `false` to treat them as
 * an HTML string.
 * @prop {boolean} cfg.keepAlive Defines if previous messages should be kept on a new message is shown.
 * @prop {number} cfg.life Duration in milliseconds to display non-sticky messages.
 * @prop {PrimeFaces.FacesMessage[]} cfg.msgs List of messages that are shown initially when the widget is loaded or
 * refreshed.
 * @prop {boolean} cfg.sticky Specifies if the message should stay instead of hidden automatically.
 */
PrimeFaces.widget.Growl = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        //create container
        this.jq = $('<div id="' + this.id + '_container" class="ui-growl ui-widget" aria-live="polite"></div>');
        this.jq.appendTo($(document.body));

        //render messages
        this.show(this.cfg.msgs);
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
    	this.cfg = cfg;
        this.show(cfg.msgs);

        this.removeScriptElement(this.id);
    },

    /**
     * Appends a message to the current displayed messages.
     *
     * @param {PrimeFaces.FacesMessage} msg A message to translate into an HTML element.
     */
    add: function(msg) {
        this.renderMessage(msg);
    },

    /**
     * Appends all messages to the current displayed messages.
     *
     * @param {PrimeFaces.FacesMessage[]} msgs The messages to translate into HTML elements.
     */
    addAll: function(msgs) {
        var $this = this;
        $.each(msgs, function(index, msg) {
            $this.renderMessage(msg);
        });
    },

    /**
     * Displays the given messages in the growl window represented by this growl widget.
     *
     * @param {PrimeFaces.FacesMessage[]} msgs Messages to display in this growl
     */
    show: function(msgs) {
        var $this = this;

        this.jq.css('z-index', PrimeFaces.nextZindex());

        if(!this.cfg.keepAlive) {
            //clear previous messages
            this.removeAll();
        }

        $.each(msgs, function(index, msg) {
            $this.renderMessage(msg);
        });
    },

    /**
     * Removes all growl messages that are currently displayed.
     */
    removeAll: function() {
        this.jq.children('div.ui-growl-item-container').remove();
    },

    /**
     * Creates the HTML elements for the given faces message, and adds it to the DOM.
     * @private
     * @param {PrimeFaces.FacesMessage} msg A message to translate into an HTML element.
     */
    renderMessage: function(msg) {
        var markup = '<div class="ui-growl-item-container ui-state-highlight ui-corner-all ui-helper-hidden ui-shadow ui-growl-' + msg.severity + '">';
        markup += '<div role="alert" class="ui-growl-item">';
        markup += '<div class="ui-growl-icon-close ui-icon ui-icon-closethick" style="display:none"></div>';
        markup += '<span class="ui-growl-image ui-growl-image-' + msg.severity + '" ></span>';
        if (msg.severityText) {
            // GitHub #5153 for screen readers
            markup += '<span class="ui-growl-severity ui-helper-hidden-accessible">' + msg.severityText + '</span>';
        }
        markup += '<div class="ui-growl-message">';
        markup += '<span class="ui-growl-title"></span>';
        markup += '<p></p>';
        markup += '</div><div style="clear: both;"></div></div></div>';

        var message = $(markup),
        summaryEL = message.find('span.ui-growl-title'),
        detailEL = summaryEL.next();

        if(this.cfg.escape) {
            summaryEL.text(msg.summary);
            detailEL.text(msg.detail);
        }
        else {
            summaryEL.html(msg.summary);
            detailEL.html(msg.detail);
        }

        this.bindEvents(message);

        message.appendTo(this.jq).fadeIn();
    },

    /**
     * Sets up all event listeners for the given message, such as for closing the message when the close icon clicked.
     * @private
     * @param {JQuery} message The message for which to set up the event listeners
     */
    bindEvents: function(message) {
        var $this = this,
        sticky = this.cfg.sticky;

        message.on("mouseover", function() {
            var msg = $(this);

            //visuals
            if(!msg.is(':animated')) {
                msg.find('div.ui-growl-icon-close:first').show();
            }

            // clear hide timeout on mouseover
            if(!sticky) {
                clearTimeout(msg.data('timeout'));
            }
        })
        .on("mouseout", function() {
            //visuals
            $(this).find('div.ui-growl-icon-close:first').hide();

            // setup hide timeout again after mouseout
            if(!sticky) {
                $this.setRemovalTimeout(message);
            }
        });

        //remove message on click of close icon
        message.find('div.ui-growl-icon-close').on("click", function() {
            $this.removeMessage(message);

            //clear timeout if removed manually
            if(!sticky) {
                clearTimeout(message.data('timeout'));
            }
        });

        //hide the message after given time if not sticky
        if(!sticky) {
            this.setRemovalTimeout(message);
        }
    },

    /**
     * Removes the given message from the screen, if it is currently displayed.
     * @param {JQuery} message The message to remove, an HTML element with the class `ui-growl-item-container`.
     */
    removeMessage: function(message) {
        message.fadeTo('normal', 0, function() {
            message.slideUp('normal', 'easeInOutCirc', function() {
                message.remove();
            });
        });
    },

    /**
     * Starts a timeout that removes the given message after a certain delay (as defined by this widget's
     * configuration).
     * @private
     * @param {JQuery} message The message to remove, an HTML element with the class `ui-growl-item-container`.
     */
    setRemovalTimeout: function(message) {
        var $this = this;

        var timeout = setTimeout(function() {
            $this.removeMessage(message);
        }, this.cfg.life);

        message.data('timeout', timeout);
    }
});