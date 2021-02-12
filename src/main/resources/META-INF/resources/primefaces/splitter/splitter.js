/**
 * __PrimeFaces Splitter Widget__
 *
 * Splitter represents entities using icons, labels and images.
 *
 * @typedef {"horizontal" | "vertical"} PrimeFaces.widget.Splitter.Layout Defines how the panel are split.
 * - `horizontal`: The two panels are split in two horizontally by the splitter.
 * - `vertically`: The two panels are split in two vertically by the splitter.
 * 
 * @typedef {"local" | "session"} PrimeFaces.widget.Splitter.StateStorage Defines where to store the current position of the
 * splitter so that it can be restored later.
 * - `local`: Use the browser's local storage which keeps data between sessions.
 * - `session`: Use the browser's session storage which is cleared when the session ends.
 * 
 * @prop {JQuery} panels DOM elements of the splitter panels in splitter.
 * @prop {JQuery} gutters DOM elements of the gutter elements in splitter.
 * @prop {boolean} horizontal Whether splitter element is horizontal or vertical.
 * @prop {number[]} panelSizes Array of the panels size for save and restore state.
 *
 * @interface {PrimeFaces.widget.SplitterCfg} cfg The configuration for the {@link  Splitter| Splitter widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {number} cfg.gutterSize Defines Size of the divider in pixels.
 * @prop {PrimeFaces.widget.Splitter.Layout} cfg.layout Defines orientation of the panels, whether the panels are split
 * horizontally or vertically.
 * @prop {string} cfg.stateKey Defines storage identifier of a stateful Splitter.
 * @prop {PrimeFaces.widget.Splitter.StateStorage} cfg.stateStorage Defines where a stateful splitter keeps its state.
 */
PrimeFaces.widget.Splitter = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.panels = this.jq.children('.ui-splitter-panel');
        this.gutters = this.jq.children('.ui-splitter-gutter');
        this.panelSizes = [];
        this.horizontal = this.cfg.layout === 'horizontal';
        
        this.initPanelSize();
        this.bindGutterEvent();
    },
    
    /**
     * Initialize panels size.
     * @private
     */
    initPanelSize: function() {
        var $this = this;
        var initialized = false;

        if (this.isStateful()) {
            initialized = this.restoreState();
        }

        if (!initialized) {
            this.panels.each(function(i, panel) {
                var panelInitialSize = panel.dataset && panel.dataset.size;
                var panelSize = panelInitialSize || (100 / $this.panels.length);
                $this.panelSizes[i] = panelSize;
                panel.style.flexBasis = 'calc(' + panelSize + '% - ' + (($this.panels.length - 1) * $this.cfg.gutterSize) + 'px)';
            });
        }
    },

    /**
     * Bind document events
     * @private
     */
    bindDocumentEvents: function() {
        var $this = this;
        
        $(document).on('mousemove.splitter', function(event) {
                $this.onResize(event);
            })
            .on('mouseup.splitter', function(event) {
                $this.onResizeEnd(event);
                $this.unbindDocumentEvents();
            });
    },
    
    /**
     * Removes document events
     * @private
     */
    unbindDocumentEvents: function() {
        $(document).off('mousemove.splitter mouseup.splitter');
    },
    
    /**
     * Set up event for the gutters.
     * @private
     */
    bindGutterEvent: function() {
        var $this = this;
        
        this.gutters.off('mousedown.splitter touchstart.splitter touchmove.splitter touchend.splitter')
            .on('mousedown.splitter', function(event) {
                $this.onResizeStart(event);
                $this.bindDocumentEvents();
            })
            .on('touchstart.splitter', function(event) {
                $this.onResizeStart(event);
                event.preventDefault();
            })
            .on('touchmove.splitter', function(event) {
                $this.onResize(event);
                event.preventDefault();
            })
            .on('touchend.splitter', function(event) {
                $this.onResizeEnd(event);
                event.preventDefault();
            });
    },
    
    /**
     * The method that is called when the 'resize' event starts.
     * @private
     * @param {JQuery.TriggeredEvent} event Event triggered for the drag.
     */
    onResizeStart: function(event) {
        this.gutterElement = $(event.currentTarget);
        this.size = this.horizontal ? this.jq.width() : this.jq.height();
        this.dragging = true;
        this.startPos = this.horizontal ? event.pageX : event.pageY;
        this.prevPanelElement = this.gutterElement.prev();
        this.nextPanelElement = this.gutterElement.next();
        this.prevPanelSize = 100 * (this.horizontal ? this.prevPanelElement.outerWidth(true) : this.prevPanelElement.outerHeight(true)) / this.size;
        this.nextPanelSize = 100 * (this.horizontal ? this.nextPanelElement.outerWidth(true) : this.nextPanelElement.outerHeight(true)) / this.size;
        this.prevPanelIndex = this.panels.index(this.prevPanelElement);
        this.gutterElement.addClass('ui-splitter-gutter-resizing');
        this.jq.addClass('ui-splitter-resizing');
    },

    /**
     * The method called while the 'resize' event is running.
     * @private
     * @param {JQuery.TriggeredEvent} event Event triggered for the resize.
     */
    onResize: function(event) {
        var newPos;

        if (this.horizontal)
            newPos = (event.pageX * 100 / this.size) - (this.startPos * 100 / this.size);
        else
            newPos = (event.pageY * 100 / this.size) - (this.startPos * 100 / this.size);

        var newPrevPanelSize = this.prevPanelSize + newPos;
        var newNextPanelSize = this.nextPanelSize - newPos;

        if (this.validateResize(newPrevPanelSize, newNextPanelSize)) {
            this.prevPanelElement.css('flexBasis', 'calc(' + newPrevPanelSize + '% - ' + ((this.panels.length - 1) * this.cfg.gutterSize) + 'px)');
            this.nextPanelElement.css('flexBasis', 'calc(' + newNextPanelSize + '% - ' + ((this.panels.length - 1) * this.cfg.gutterSize) + 'px)');
            this.panelSizes[this.prevPanelIndex] = newPrevPanelSize;
            this.panelSizes[this.prevPanelIndex + 1] = newNextPanelSize;
        }
    },

    /**
     * The method that is called when the 'resize' event ends.
     * @private
     * @param {JQuery.TriggeredEvent} event Event triggered for the resize end.
     */
    onResizeEnd: function(event) {
        if (this.isStateful()) {
            this.saveState();
        }

        this.gutterElement.removeClass('ui-splitter-gutter-resizing');
        this.jq.removeClass('ui-splitter-resizing');
        this.clear();
    },
    
    /**
     * Clear all variables
     * @private
     */
    clear: function() {
        this.dragging = false;
        this.size = null;
        this.startPos = null;
        this.prevPanelElement = null;
        this.nextPanelElement = null;
        this.prevPanelSize = null;
        this.nextPanelSize = null;
        this.gutterElement = null;
        this.prevPanelIndex = null;
    },
    
    /**
     * Checks the new values according to the size and minimum size values
     * @private
     * @param {number} newPrevPanelSize The new previous panel size.
     * @param {number} newNextPanelSize The new next panel size.
     * @return {boolean} `true` if resized, `false` if not.
     */
    validateResize: function(newPrevPanelSize, newNextPanelSize) {
        if (this.panels[0].dataset && parseFloat(this.panels[0].dataset.minsize) > newPrevPanelSize) {
            return false;
        }

        if (this.panels[1].dataset && parseFloat(this.panels[1].dataset.minsize) > newNextPanelSize) {
            return false;
        }

        return true;
    },

    /**
     * Whether the splitter keeps its dimensions between different page loads.
     * @return {boolean} Whether the splitter is retaining its state.
     */
    isStateful: function() {
        return this.cfg.stateKey != null;
    },
    
    /**
     * Save current panel sizes to the (local or session) storage.
     * @private
     */
    saveState: function() {
        this.getStorage().setItem(this.cfg.stateKey, JSON.stringify(this.panelSizes));
    },

    /**
     * Restore panel sizes from (local or session) storage.
     * @return {boolean} `true` when the state restore operation was successful, `false` otherwise.
     */
    restoreState: function() {
        var storage = this.getStorage();
        var stateString = storage.getItem(this.cfg.stateKey);
        var $this = this;

        if (stateString) {
            this.panelSizes = JSON.parse(stateString);
            this.panels.each(function(i, panel) {
                panel.style.flexBasis = 'calc(' + $this.panelSizes[i] + '% - ' + (($this.panels.length - 1) * $this.cfg.gutterSize) + 'px)';
            });
            
            return true;
        }
        
        return false;
    },

    /**
     * Returns either the local storage or session storage, depending on the current widget configuration.
     * @return {Storage} The storage to be used.
     */
    getStorage: function() {
        switch(this.cfg.stateStorage) {
            case 'local':
                return window.localStorage;

            case 'session':
                return window.sessionStorage;

            default:
                throw new Error(this.cfg.stateStorage + ' is not a valid value for the state storage, supported values are "local" and "session".');
        }
    }
});
