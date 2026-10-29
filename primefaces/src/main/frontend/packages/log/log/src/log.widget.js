/**
 * __PrimeFaces Log Widget__
 * 
 * Log component is a visual console to display logs on Faces pages.
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
 * @interface {PrimeFaces.widget.LogCfg} cfg The configuration for the {@link  Log|Log widget}. You can access this
 * configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this configuration is
 * usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.Log = class Log extends PrimeFaces.widget.BaseWidget {
    
    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        
        this.header = this.jq.children('.p-log-header');
        this.content = this.jq.children('.p-log-content');
        this.itemsContainer = this.content.find('.p-log-items');
        this.filters = this.header.children('.p-log-button');
        this.severity = 'all';
        var _self = this;

        //make draggable
        PrimeFaces.interactions.draggable(this.jq, {handle:this.header});
        
        //z-index
        PrimeFaces.nextZindex(this.jq);
        this.header.on("mousedown", function() {
            PrimeFaces.nextZindex(_self.jq);
        });

        //attach events
        this.bindEvents();

        //append to body
        this.jq.appendTo('body');

        //attach
        PrimeFaces.logger = this;
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        // remove the old element, which was moved to body - an update of the log itself replaces it in place
        // inside body, so it must not be removed by its id
        this.jq.remove();
        super.refresh(cfg);
    }

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents() {
        var _self = this;

        //visuals
        this.header.children('.p-log-button').on("mouseover", function() {
            var el = $(this);
            if(!el.hasClass('p-state-active'))
                $(this).addClass('p-state-hover');
        }).on("mouseout", function() {
            $(this).removeClass('p-state-hover');
        });

        //clear
        this.header.children('.p-log-clear').on("click", function(e) {
            _self.itemsContainer.html('');
            _self.filters.filter('.p-state-active').removeClass('p-state-active');
            _self.filters.filter('.p-log-all').addClass('p-state-active');
            _self.severity = 'all';
            e.preventDefault();
        });

        //all
        this.header.children('.p-log-all').on("click", function(e) {
            _self.itemsContainer.children().show();
            _self.filters.filter('.p-state-active').removeClass('p-state-active');
            $(this).addClass('p-state-active').removeClass('p-state-hover');
            _self.severity = 'all';
            e.preventDefault();
        });

        //info
        this.header.children('.p-log-info').on("click", function(e) {
            _self.handleFilterClick(e, '.p-log-item-info', 'info', $(this));
        });

        //warn
        this.header.children('.p-log-warn').on("click", function(e) {
            _self.handleFilterClick(e, '.p-log-item-warn', 'warn', $(this));
        });

        //debug
        this.header.children('.p-log-debug').on("click", function(e) {
            _self.handleFilterClick(e, '.p-log-item-debug', 'debug', $(this));
        });

        //error
        this.header.children('.p-log-error').on("click", function(e) {
            _self.handleFilterClick(e, '.p-log-item-error', 'error', $(this));
        });
    }
    
    /**
     * Logs the given message at the `info` level.
     * @param {string} msg Message to log
     */
    info(msg) {
        this.add(msg, 'info', 'p-icon p-info-icon p-log-info-icon');
    }
    
    /**
     * Logs the given message at the `warn` level.
     * @param {string} msg Message to log
     */
    warn(msg) {
        this.add(msg, 'warn', 'p-icon p-warn-icon p-log-warn-icon');
    }
    
    /**
     * Logs the given message at the `debug` level.
     * @param {string} msg Message to log
     */
    debug(msg) {
        this.add(msg, 'debug', 'p-icon p-search-icon p-log-debug-icon');
    }
    
    /**
     * Logs the given message at the `error` level.
     * @param {string} msg Message to log
     */
    error(msg) {
        this.add(msg, 'error', 'p-icon p-error-icon p-log-error-icon');
    }
    
    /**
     * Logs a message at the given severity level.
     * @param {string} msg Message to log
     * @param {PrimeFaces.widget.Log.Severity} severity Severity of the log message
     * @param {string} icon Style classes of the icon to show near the log message
     */
    add(msg, severity, icon) {
        var visible = this.severity == severity || this.severity == 'all',
        style = visible ? 'display:block' : 'display:none';

        var item = '<li class="p-log-item p-log-item-' + severity + '" style="' + style + 
            '"><span class="' + icon + '"></span>' + new Date().toLocaleString() + ' : '  + PrimeFaces.escapeHTML(msg) + '</li>';

        this.itemsContainer.append(item);
    }

    /**
     * Hides all log messages except those at the given severity level
     * @param {PrimeFaces.widget.Log.Severity} severity Severity of the log messages to show
     */
    filter(severity) {
        this.itemsContainer.children().hide().filter(severity).show();
    }

    /**
     * Callback for when a click occurred on the log message header.
     * @private
     * @param {JQuery.TriggeredEvent} event The event that occurred.
     * @param {string} severityClass Class for the severity of the log message.
     * @param {PrimeFaces.widget.Log.Severity} severity Severity of the log message. 
     * @param {JQuery} button The button that was pressed.
     */
    handleFilterClick(event, severityClass, severity, button) {
        this.filter(severityClass);
        this.filters.filter('.p-state-active').removeClass('p-state-active');
        button.addClass('p-state-active').removeClass('p-state-hover');
        this.severity = severity;
        event.preventDefault();
    }
    
    /**
     * Shows all log messages.
     */
    show() {
        this.jq.show();
    }
    
    /**
     * Hides all log messages.
     */
    hide() {
        this.jq.hide();
    }
    
}