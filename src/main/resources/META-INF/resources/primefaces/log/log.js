/**
 * __PrimeFaces Log Widget__
 * 
 * Log component is a visual console to display logs on JSF pages.
 * 
 * The Log API is also available via global PrimeFaces object in case you’d like to use the log component to display
 * your logs:
 * 
 * ```javascript
 * PrimeFaces.info("Info message");
 * PrimeFaces.debug("Debug message");
 * PrimeFaces.warn("Warning message");
 * PrimeFaces.error("Error message");
 * ```
 * 
 * @typedef {"all" | "info" | "warn" | "debug" | "error"} PrimeFaces.widget.Log.Severity Available severity levels for log messages used by
 * the `Log` widget.
 * 
 * @prop {JQuery} content The DOM element for the content with the log messages
 * @prop {JQuery} header The DOM element for the header
 * @prop {JQuery} itemsContainer The DOM element for the items container
 * @prop {JQuery} filters The DOM elements for the filter buttons
 * @prop {PrimeFaces.widget.Log.Severity} severity The current severity level that controls which log messages are shown
 * 
 * @interface {PrimeFaces.widget.LogCfg} cfg
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.Log = PrimeFaces.widget.BaseWidget.extend({
    
    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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
        
        //z-index
        this.jq.zIndex(PrimeFaces.nextZindex());
        this.header.mousedown(function() {
            _self.jq.zIndex(PrimeFaces.nextZindex());
        });

        //attach events
        this.bindEvents();

        //append to body
        this.jq.appendTo('body');

        //attach
        PrimeFaces.logger = this;
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var _self = this;

        //visuals
        this.header.children('.ui-log-button').on("mouseover", function() {
            var el = $(this);
            if(!el.hasClass('ui-state-active'))
                $(this).addClass('ui-state-hover');
        }).on("mouseout", function() {
            $(this).removeClass('ui-state-hover');
        });

        //clear
        this.header.children('.ui-log-clear').on("click", function(e) {
            _self.itemsContainer.html('');
            _self.filters.filter('.ui-state-active').removeClass('ui-state-active');
            _self.filters.filter('.ui-log-all').addClass('ui-state-active');
            _self.severity = 'all';
            e.preventDefault();
        });

        //all
        this.header.children('.ui-log-all').on("click", function(e) {
            _self.itemsContainer.children().show();
            _self.filters.filter('.ui-state-active').removeClass('ui-state-active');
            $(this).addClass('ui-state-active').removeClass('ui-state-hover');
            _self.severity = 'all';
            e.preventDefault();
        });

        //info
        this.header.children('.ui-log-info').on("click", function(e) {
            _self.handleFilterClick(e, '.ui-log-item-info', 'info', $(this));
        });

        //warn
        this.header.children('.ui-log-warn').on("click", function(e) {
            _self.handleFilterClick(e, '.ui-log-item-warn', 'warn', $(this));
        });

        //debug
        this.header.children('.ui-log-debug').on("click", function(e) {
            _self.handleFilterClick(e, '.ui-log-item-debug', 'debug', $(this));
        });

        //error
        this.header.children('.ui-log-error').on("click", function(e) {
            _self.handleFilterClick(e, '.ui-log-item-error', 'error', $(this));
        });
    },
    
    /**
     * Logs the given message at the `info` level.
     * @param {string} msg Message to log
     */
    info: function(msg) {
        this.add(msg, 'info', 'ui-icon-info');
    },
    
    /**
     * Logs the given message at the `warn` level.
     * @param {string} msg Message to log
     */
    warn: function(msg) {
        this.add(msg, 'warn', 'ui-icon-notice');
    },
    
    /**
     * Logs the given message at the `debug` level.
     * @param {string} msg Message to log
     */
    debug: function(msg) {
        this.add(msg, 'debug', 'ui-icon-search');
    },
    
    /**
     * Logs the given message at the `error` level.
     * @param {string} msg Message to log
     */
    error: function(msg) {
        this.add(msg, 'error', 'ui-icon-alert');
    },
    
    /**
     * Logs a message at the given severity level.
     * @param {string} msg Message to log
     * @param {PrimeFaces.widget.Log.Severity} severity Severity of the log message
     * @param {string} icon Icon to show near the log message
     */
    add: function(msg, severity, icon) {
        var visible = this.severity == severity || this.severity == 'all',
        style = visible ? 'display:block' : 'display:none';

        var item = '<li class="ui-log-item ui-log-item-' + severity + ' ui-helper-clearfix" style="' + style + 
            '"><span class="ui-icon ' + icon + '"></span>' + new Date().toLocaleString() + ' : '  + PrimeFaces.escapeHTML(msg) + '</li>';

        this.itemsContainer.append(item);
    },

    /**
     * Hides all log messages except those at the given severity level
     * @param {PrimeFaces.widget.Log.Severity} severity Severity of the log messages to show
     */
    filter: function(severity) {
        this.itemsContainer.children().hide().filter(severity).show();
    },

    /**
     * Callback for when a click occurred on the log message header.
     * @private
     * @param {JQuery.TriggeredEvent} event The event that occurred.
     * @param {string} severityClass Class for the severity of the log message.
     * @param {PrimeFaces.widget.Log.Severity} severity Severity of the log message. 
     * @param {JQuery} button The button that was pressed.
     */
    handleFilterClick: function(event, severityClass, severity, button) {
        this.filter(severityClass);
        this.filters.filter('.ui-state-active').removeClass('ui-state-active');
        button.addClass('ui-state-active').removeClass('ui-state-hover');
        this.severity = severity;
        event.preventDefault();
    },
    
    /**
     * Shows all log messages.
     */
    show: function() {
        this.jq.show();
    },
    
    /**
     * Hides all log messages.
     */
    hide: function() {
        this.jq.hide();
    }
    
});