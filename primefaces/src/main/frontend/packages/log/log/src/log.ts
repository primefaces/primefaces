/**
 * The configuration for the {@link Log} widget.
 */
export interface LogCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
}

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
 */
export class Log<Cfg extends LogCfg = LogCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The DOM element for the content with the log messages. */
    content: JQuery = $();
    /** The DOM element for the header. */
    header: JQuery = $();
    /** The DOM element for the items container. */
    itemsContainer: JQuery = $();
    /** The DOM elements for the filter buttons. */
    filters: JQuery = $();
    /** The current severity level that controls which log messages are shown. */
    severity: PrimeFacesTypes.widget.Log.Severity = 'all';

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.header = this.jq.children('.p-log-header');
        this.content = this.jq.children('.p-log-content');
        this.itemsContainer = this.content.find('.p-log-items');
        this.filters = this.header.children('.p-log-button');
        this.severity = 'all';

        //make draggable
        // the draggable finds the handle with jQuery.find, which also accepts elements (typed as selector only)
        PrimeFaces.interactions.draggable(this.jq, { handle: this.header as unknown as string });

        //z-index
        PrimeFaces.nextZindex(this.jq);
        this.header.on("mousedown", () => {
            PrimeFaces.nextZindex(this.jq);
        });

        //attach events
        this.bindEvents();

        //append to body
        this.jq.appendTo('body');

        //attach
        PrimeFaces.logger = this as PrimeFacesTypes.Logger;
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        // remove the old element, which was moved to body - an update of the log itself replaces it in place
        // inside body, so it must not be removed by its id
        this.jq.remove();
        super.refresh(cfg);
    }

    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindEvents(): void {
        //visuals
        this.header.children('.p-log-button').on("mouseover", (e) => {
            const el = $(e.currentTarget);
            if (!el.hasClass('p-state-active'))
                $(e.currentTarget).addClass('p-state-hover');
        }).on("mouseout", (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        });

        //clear
        this.header.children('.p-log-clear').on("click", (e) => {
            this.itemsContainer.html('');
            this.filters.filter('.p-state-active').removeClass('p-state-active');
            this.filters.filter('.p-log-all').addClass('p-state-active');
            this.severity = 'all';
            e.preventDefault();
        });

        //all
        this.header.children('.p-log-all').on("click", (e) => {
            this.itemsContainer.children().show();
            this.filters.filter('.p-state-active').removeClass('p-state-active');
            $(e.currentTarget).addClass('p-state-active').removeClass('p-state-hover');
            this.severity = 'all';
            e.preventDefault();
        });

        //info
        this.header.children('.p-log-info').on("click", (e) => {
            this.handleFilterClick(e, '.p-log-item-info', 'info', $(e.currentTarget));
        });

        //warn
        this.header.children('.p-log-warn').on("click", (e) => {
            this.handleFilterClick(e, '.p-log-item-warn', 'warn', $(e.currentTarget));
        });

        //debug
        this.header.children('.p-log-debug').on("click", (e) => {
            this.handleFilterClick(e, '.p-log-item-debug', 'debug', $(e.currentTarget));
        });

        //error
        this.header.children('.p-log-error').on("click", (e) => {
            this.handleFilterClick(e, '.p-log-item-error', 'error', $(e.currentTarget));
        });
    }

    /**
     * Logs the given message at the `info` level.
     * @param msg Message to log
     */
    info(msg: string): void {
        this.add(msg, 'info', 'p-icon p-info-icon p-log-info-icon');
    }

    /**
     * Logs the given message at the `warn` level.
     * @param msg Message to log
     */
    warn(msg: string): void {
        this.add(msg, 'warn', 'p-icon p-warn-icon p-log-warn-icon');
    }

    /**
     * Logs the given message at the `debug` level.
     * @param msg Message to log
     */
    debug(msg: string): void {
        this.add(msg, 'debug', 'p-icon p-search-icon p-log-debug-icon');
    }

    /**
     * Logs the given message at the `error` level.
     * @param msg Message to log
     */
    error(msg: string): void {
        this.add(msg, 'error', 'p-icon p-error-icon p-log-error-icon');
    }

    /**
     * Logs a message at the given severity level.
     * @param msg Message to log
     * @param severity Severity of the log message
     * @param icon Style classes of the icon to show near the log message
     */
    add(msg: string, severity: PrimeFacesTypes.widget.Log.Severity, icon: string): void {
        const visible = this.severity == severity || this.severity == 'all',
            style = visible ? 'display:block' : 'display:none';

        const item = '<li class="p-log-item p-log-item-' + severity + '" style="' + style +
            '"><span class="' + icon + '"></span>' + new Date().toLocaleString() + ' : ' + PrimeFaces.escapeHTML(msg) + '</li>';

        this.itemsContainer.append(item);
    }

    /**
     * Hides all log messages except those at the given severity level
     * @param severity Severity of the log messages to show
     */
    filter(severity: string): void {
        this.itemsContainer.children().hide().filter(severity).show();
    }

    /**
     * Callback for when a click occurred on the log message header.
     * @param event The event that occurred.
     * @param severityClass Class for the severity of the log message.
     * @param severity Severity of the log message.
     * @param button The button that was pressed.
     */
    private handleFilterClick(event: JQuery.TriggeredEvent, severityClass: string, severity: PrimeFacesTypes.widget.Log.Severity, button: JQuery): void {
        this.filter(severityClass);
        this.filters.filter('.p-state-active').removeClass('p-state-active');
        button.addClass('p-state-active').removeClass('p-state-hover');
        this.severity = severity;
        event.preventDefault();
    }

    /**
     * Shows all log messages.
     */
    show(): void {
        this.jq.show();
    }

    /**
     * Hides all log messages.
     */
    hide(): void {
        this.jq.hide();
    }

}
