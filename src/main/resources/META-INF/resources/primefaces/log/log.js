/**
 * PrimeFaces Log Widget
 */
PrimeFaces.widget.Log = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.header = this.jq.children('.ui-log-header');
        this.content = this.jq.children('.ui-log-content');
        this.itemsContainer = this.content.find('.ui-log-items');
        this.filters = this.header.children('.ui-log-button');
        this.severity = 'all';
        var _self = this;

        //make draggable
        this.jq.draggable({handle:this.header});
        this.header.mousedown(function(e) {
            _self.jq.zIndex(++PrimeFaces.zindex);
        });

        //attach events
        this.bindEvents();

        //append to body
        this.jq.appendTo('body');

        //attach
        PrimeFaces.logger = this;
    },
    
    bindEvents: function() {
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
            _self.severity = 'all';
            e.preventDefault();
        });

        //all
        this.header.children('.ui-log-all').click(function(e) {
            _self.itemsContainer.children().show();
            _self.filters.filter('.ui-state-active').removeClass('ui-state-active');
            $(this).addClass('ui-state-active').removeClass('ui-state-hover');
            _self.severity = 'all';
            e.preventDefault();
        });

        //info
        this.header.children('.ui-log-info').click(function(e) {
            _self.handleFilterClick(e, '.ui-log-item-info', 'info', $(this));
        });

        //warn
        this.header.children('.ui-log-warn').click(function(e) {
            _self.handleFilterClick(e, '.ui-log-item-warn', 'warn', $(this));
        });

        //debug
        this.header.children('.ui-log-debug').click(function(e) {
            _self.handleFilterClick(e, '.ui-log-item-debug', 'debug', $(this));
        });

        //error
        this.header.children('.ui-log-error').click(function(e) {
            _self.handleFilterClick(e, '.ui-log-item-error', 'error', $(this));
        });
    },
    
    info: function(msg) {
        this.add(msg, 'info', 'ui-icon-info');
    },
    
    warn: function(msg) {
        this.add(msg, 'warn', 'ui-icon-notice');
    },
    
    debug: function(msg) {
        this.add(msg, 'debug', 'ui-icon-search');
    },
    
    error: function(msg) {
        this.add(msg, 'error', 'ui-icon-alert');
    },
    
    add: function(msg, severity, icon) {
        var visible = this.severity == severity || this.severity == 'all',
        style = visible ? 'display:block' : 'display:none';

        var item = '<li class="ui-log-item ui-log-item-' + severity + ' ui-helper-clearfix" style="' + style + 
            '"><span class="ui-icon ' + icon + '"></span>' + new Date().toLocaleString() + ' : '  + msg + '</li>';

        this.itemsContainer.append(item);
    },
    
    filter: function(severity) {
        this.itemsContainer.children().hide().filter(severity).show();
    },
    
    handleFilterClick: function(event, severityClass, severity, button) {
        this.filter(severityClass);
        this.filters.filter('.ui-state-active').removeClass('ui-state-active');
        button.addClass('ui-state-active').removeClass('ui-state-hover');
        this.severity = severity;
        event.preventDefault();
    }
    
});