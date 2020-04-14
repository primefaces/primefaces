/**
 * PrimeFaces Growl Widget
 */
PrimeFaces.widget.Growl = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.render();
    },

    //Override
    refresh: function(cfg) {
    	this.cfg = cfg;
        this.show(cfg.msgs);

        this.removeScriptElement(this.id);
    },

    show: function(msgs) {
        var $this = this;

        this.jq.css('z-index', ++PrimeFaces.zindex);

        if(!this.cfg.keepAlive) {
            //clear previous messages
            this.removeAll();
        }

        $.each(msgs, function(index, msg) {
            $this.renderMessage(msg);
        });
    },

    removeAll: function() {
        this.jq.children('div.ui-growl-item-container').remove();
    },

    render: function() {
        //create container
        this.jq = $('<div id="' + this.id + '_container" class="ui-growl ui-widget" aria-live="polite"></div>');
        this.jq.appendTo($(document.body));

        //render messages
        this.show(this.cfg.msgs);
    },

    renderMessage: function(msg) {
        var markup = '<div class="ui-growl-item-container ui-state-highlight ui-corner-all ui-helper-hidden ui-shadow ui-growl-' + msg.severity + '">';
        markup += '<div role="alert" class="ui-growl-item">';
        markup += '<div class="ui-growl-icon-close ui-icon ui-icon-closethick" style="display:none"></div>';
        markup += '<span class="ui-growl-image ui-growl-image-' + msg.severity + '" />';
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

    bindEvents: function(message) {
        var $this = this,
        sticky = this.cfg.sticky;

        message.mouseover(function() {
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
        .mouseout(function() {
            //visuals
            $(this).find('div.ui-growl-icon-close:first').hide();

            // setup hide timeout again after mouseout
            if(!sticky) {
                $this.setRemovalTimeout(message);
            }
        });

        //remove message on click of close icon
        message.find('div.ui-growl-icon-close').click(function() {
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

    removeMessage: function(message) {
        message.fadeTo('normal', 0, function() {
            message.slideUp('normal', 'easeInOutCirc', function() {
                message.remove();
            });
        });
    },

    setRemovalTimeout: function(message) {
        var $this = this;

        var timeout = setTimeout(function() {
            $this.removeMessage(message);
        }, this.cfg.life);

        message.data('timeout', timeout);
    }
});