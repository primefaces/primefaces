/**
 * PrimeFaces Growl Widget
 */
PrimeFaces.widget.Growl = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id
    this.jqId = PrimeFaces.escapeClientId(this.id);

    this.render();
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Growl, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Growl.prototype.show = function(msgs) {
    var _self = this;
    
    //clear previous messages
    this.removeAll();
    
    $.each(msgs, function(index, msg) {
        _self.renderMessage(msg);
    }); 
}

PrimeFaces.widget.Growl.prototype.removeAll = function() {
    this.jq.children('div.ui-growl-item-container').remove();
}

PrimeFaces.widget.Growl.prototype.render = function() {
    //create container
    this.jq = $('<div id="' + this.id + '_container" class="ui-growl ui-widget"></div>');
    this.jq.appendTo($(document.body));

    //render messages
    this.show(this.cfg.msgs);
}

PrimeFaces.widget.Growl.prototype.renderMessage = function(msg) {
    var markup = '<div class="ui-growl-item-container ui-widget-header ui-corner-all ui-helper-hidden">';
    markup += '<div class="ui-growl-item">';
    markup += '<div class="ui-growl-icon-close ui-icon ui-icon-closethick"></div>';
    markup += '<img class="ui-growl-image" src="' + msg.image + '" />';
    markup += '<div class="ui-growl-message">';
    markup += '<span class="ui-growl-title">' + msg.title + '</span>';
    markup += '<p>' + msg.text + '</p>';
    markup += '</div><div style="clear: both;"></div></div></div>';

    var message = $(markup);
    
    this.bindEvents(message);

    message.appendTo(this.jq).fadeIn();
}

PrimeFaces.widget.Growl.prototype.bindEvents = function(message) {
    var _self = this,
    sticky = this.cfg.sticky;
    
    message.mouseover(function() {
        var msg = $(this);

        //visuals
        if(!msg.is(':animated')) {
            msg.addClass('ui-state-hover');
        }
    })
    .mouseout(function() {        
        //visuals
        $(this).removeClass('ui-state-hover');
    });
    
    //remove message on click of close icon
    message.find('div.ui-growl-icon-close').click(function() {
        _self.removeMessage(message);
        
        //clear timeout if removed manually
        if(!sticky) {
            clearTimeout(message.data('timeout'));
        }
    });
    
    //hide the message after given time if not sticky
    if(!sticky) {
        this.setRemovalTimeout(message);
    }
}

/**
 * First fades out to hide and then slides up to remove allocated space, finally removes from dom
 */
PrimeFaces.widget.Growl.prototype.removeMessage = function(message) {
    message.fadeTo('normal', 0, function() {
        message.slideUp('normal', 'easeInOutCirc', function() {
            message.remove();
        });
    });
}

/**
 * Defines a timeout for removal
 */
PrimeFaces.widget.Growl.prototype.setRemovalTimeout = function(message) {
    var _self = this;
    
    var timeout = setTimeout(function() {
        _self.removeMessage(message);
    }, this.cfg.life);

    message.data('timeout', timeout);
}