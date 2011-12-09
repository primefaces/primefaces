/**
 * PrimeFaces Log Widget
 */
PrimeFaces.widget.Log = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.header = this.jq.children('.ui-log-header');
    this.content = this.jq.children('.ui-log-content');
    this.itemsContainer = this.content.find('.ui-log-items');
    this.filters = this.header.children('.ui-log-button');
    var _self = this;
    
    //make draggable
    this.jq.draggable({handle:this.header});
    this.header.mousedown(function(e) {
        _self.jq.zIndex(++PrimeFaces.zindex);
    });
    
    //attach events
    this.bindEvents();

    //attach
    PrimeFaces.logger = this;
}

PrimeFaces.widget.Log.prototype.bindEvents = function() {
    var _self = this;
    
    //visuals
    this.header.children('.ui-log-button').mouseover(function() {
        var el = $(this);
        if(!el.hasClass('ui-state-active'))
            $(this).addClass('ui-state-hover');
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    });
    
    //clear
    this.header.children('.ui-log-clear').click(function(e) {
        _self.itemsContainer.html('');
        _self.filters.filter('.ui-state-active').removeClass('ui-state-active');
        _self.filters.filter('.ui-log-all').addClass('ui-state-active');
        e.preventDefault();
    });
    
    //all
    this.header.children('.ui-log-all').click(function(e) {
        _self.itemsContainer.children().show();
        _self.filters.filter('.ui-state-active').removeClass('ui-state-active');
        $(this).addClass('ui-state-active').removeClass('ui-state-hover');
        e.preventDefault();
    });
    
    //info
    this.header.children('.ui-log-info').click(function(e) {
        _self.handleFilterClick(e, '.ui-log-item-info', $(this));
    });
    
    //warn
    this.header.children('.ui-log-warn').click(function(e) {
        _self.handleFilterClick(e, '.ui-log-item-warn', $(this));
    });
    
    //debug
    this.header.children('.ui-log-debug').click(function(e) {
        _self.handleFilterClick(e, '.ui-log-item-debug', $(this));
    });
    
    //error
    this.header.children('.ui-log-error').click(function(e) {
        _self.handleFilterClick(e, '.ui-log-item-error', $(this));
    });
}

PrimeFaces.widget.Log.prototype.info = function(msg) {
    this.add(msg, 'info', 'ui-icon-info');
}

PrimeFaces.widget.Log.prototype.warn = function(msg) {
    this.add(msg, 'warn', 'ui-icon-notice');
}

PrimeFaces.widget.Log.prototype.debug = function(msg) {
    this.add(msg, 'debug', 'ui-icon-search');
}

PrimeFaces.widget.Log.prototype.error = function(msg) {
    this.add(msg, 'error', 'ui-icon-alert');
}

PrimeFaces.widget.Log.prototype.add = function(msg, severity, icon) {
    this.itemsContainer.append('<li class="ui-log-item ui-log-item-' + severity + ' ui-helper-clearfix"><span class="ui-icon ' + icon + '"></span>' + msg + '</li>');
}

PrimeFaces.widget.Log.prototype.filter = function(severity) {
    this.itemsContainer.children().hide().filter(severity).show();
}

PrimeFaces.widget.Log.prototype.handleFilterClick = function(event, severity, button) {
    this.filter(severity);
    this.filters.filter('.ui-state-active').removeClass('ui-state-active');
    button.addClass('ui-state-active').removeClass('ui-state-hover');
    event.preventDefault();
}