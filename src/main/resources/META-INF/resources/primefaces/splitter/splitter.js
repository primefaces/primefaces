/**
 * __PrimeFaces Splitter Widget__
 *
 * Splitter represents entities using icons, labels and images.
 *
 *
 * @prop {JQuery} splitter DOM element of the splitter.
 * @prop {JQuery} panels DOM elements of the splitter panels in splitter.
 * @prop {JQuery} gutters DOM elements of the gutter elements in splitter.
 * @prop {JQuery} panelsLength Length of the panels array.
 * @prop {JQuery} parentElementSize Size of the splitter element.
 * @prop {JQuery} isStateful Whether splitter element is stateful or not.
 * @prop {JQuery} horizontal Whether splitter element is horizontal or vertical.
 * @prop {JQuery} pressed When pressed on gutter value is true, default value is false.
 * @prop {JQuery} panelSizes Array of the panels size for save and restore state.
 *
 * @interface {PrimeFaces.widget.SplitterCfg} cfg The configuration for the {@link  Splitter| Splitter widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {string} cfg.layout Defines orientation of the panels, "horizontal" or "vertical".
 * @prop {number} cfg.gutterSize Defines Size of the divider in pixels.
 * @prop {string} cfg.stateKey Defines storage identifier of a stateful Splitter.
 * @prop {string} cfg.stateStorage Defines where a stateful splitter keeps its state, "session" or "local".
 *
 */
PrimeFaces.widget.Splitter = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        var $this = this;
        this.layout = $this.cfg.layout;
        this.gutterSize = $this.cfg.gutterSize;
        this.stateKey = $this.cfg.stateKey;
        this.stateStorage = $this.cfg.stateStorage;
        this.isStateful = this.stateKey !== null;
        if(this.isStateful && this.stateStorage === "local") {
            this.stateKey = PrimeFaces.createStorageKey(this.id, this.stateKey);
        }

        this.splitter = $(this.jqId);
        this.panels = this.splitter.children('.ui-splitter-panel');
        this.gutters = this.splitter.children('.ui-splitter-gutter');
        this.panelsLength = this.panels.length;
        this.parentElementSize = this.horizontal ? this.splitter.width() : this.splitter.height();
        this.horizontal = this.layout === 'horizontal';
        this.pressed = false;
        this.panelSizes = [];

        this.bindEvents();
    },

    /**
     * Sets up all event listeners required for this widget.
     * @private
     */
    bindEvents: function() {
        this.initPanelSize();
        this.bindResizeEvent();
    },

    /**
     * Initialize panels size.
     * @private
     */
    initPanelSize: function () {
        var $this = this;
        var initialized = false;
        if ($this.isStateful) initialized = $this.restoreState();

        if (!initialized) {
            $this.panels.each( function (i, panel) {
                var panelInitialSize = panel.props && panel.props.size ? panel.props.size : null;
                var panelSize = panelInitialSize || (100 / $this.panelsLength);
                $this.panelSizes[i] = panelSize;
                $(panel).css("flexBasis", 'calc(' + panelSize + '% - ' + (($this.panelsLength - 1) * $this.gutterSize) + 'px)');
            });
        }
    },

    /**
     * Add resize event to the splitter gutters.
     * @private
     */
    bindResizeEvent: function() {
        var $this = this;
        $this.gutters.off('mousedown').on('mousedown', function(e) {
            this.gutterElement = $(this);
            this.gutterElement.parent().addClass("ui-splitter-resizing");
            this.gutterElement.addClass("ui-splitter-gutter-resizing");
            $this.pressed = true;
            this.startPos = $this.horizontal ? e.pageX : e.pageY;
            this.prevPanelElement = $(this).prev();
            this.nextPanelElement = $(this).next();
            this.prevPanelElementSize = $this.horizontal ? this.prevPanelElement.width() : this.prevPanelElement.height();
            this.nextPanelElementSize = $this.horizontal ? this.nextPanelElement.width() : this.nextPanelElement.height();
            this.prevPanelSize = 100 * this.prevPanelElementSize / $this.parentElementSize;
            this.nextPanelSize = 100 * this.nextPanelElementSize / $this.parentElementSize;
            $this.onResize(this.startPos, this.prevPanelSize, this.nextPanelSize, this.prevPanelElement, this.nextPanelElement);
            $this.onResizeEnd(this.gutterElement);
        });
    },

    /**
     * Resize splitter panels.
     * @private
     * @param {number} startPos start position of the cursor in resize event.
     * @param {number} prevPanelSize size of the splitter panel previous of the selected gutter.
     * @param {number} nextPanelSize size of the splitter panel next of the selected gutter.
     * @param {JQuery} prevPanelElement DOM element of the splitter panel previous of the selected gutter element.
     * @param {JQuery} nextPanelElement DOM element of the splitter panel next of the selected gutter element.
     */
    onResize: function (startPos, prevPanelSize, nextPanelSize, prevPanelElement, nextPanelElement) {
        var $this = this;
        $(document).off('mousemove', '.ui-splitter').on('mousemove', '.ui-splitter', function(e) {
            if($this.pressed) {
                this.currPos = $this.horizontal ? e.pageX : e.pageY;
                this.newPos = (100 * this.currPos / $this.parentElementSize) - (startPos * 100 / $this.parentElementSize);
                this.newPrevPanelSize = prevPanelSize + this.newPos;
                this.newNextPanelSize = nextPanelSize - this.newPos;
                prevPanelElement.css('flexBasis', 'calc(' + this.newPrevPanelSize + '% - ' + (($this.panelsLength - 1) * $this.gutterSize) + 'px)');
                nextPanelElement.css('flexBasis', 'calc(' + this.newNextPanelSize + '% - ' + (($this.panelsLength - 1) * $this.gutterSize) + 'px)');
            }
        });
    },

    /**
     * Toggles the expansion state of this panel.
     * @private
     * @param {JQuery} gutterElement DOM element of the selected gutter element in the resize event.
     */
    onResizeEnd: function (gutterElement) {
        var $this = this;
        $(document).off('mouseup').on('mouseup', function() {
            if($this.pressed) {
                if ($this.isStateful) {
                    $this.panels.each( function (i, panel) {
                        var panelSize = $this.horizontal ? $(panel).width() : $(panel).height();
                        $this.panelSizes[i] = 100 * panelSize / $this.parentElementSize;
                    });
                    $this.saveState();
                }

                gutterElement.parent().removeClass("ui-splitter-resizing");
                gutterElement.removeClass("ui-splitter-gutter-resizing");
                $this.pressed = false;
            }
        });
    },

    /**
     * Save current panel sizes to the storage.
     * @private
     */
    saveState: function () {
        var $this = this;
        $this.getStorage().setItem($this.stateKey, JSON.stringify(this.panelSizes));
    },

    /**
     * Restore panel sizes from storage.
     * @return {boolean} if state restore operation is successful returns true, if not returns false
     */
    restoreState: function () {
        var $this = this;
        var storage = $this.getStorage();
        var stateString = storage.getItem(this.stateKey);

        if (stateString) {
            $this.panelSizes = JSON.parse(stateString);
            $this.panels.each(function (i, panel) {
                $(panel).css("flexBasis", 'calc(' + $this.panelSizes[i] + '% - ' + (($this.panelsLength - 1) * $this.gutterSize) + 'px)');
            });

            return true;
        }

        return false;
    },

    /**
     * Return localStorage or sessionStorage based on components stateStorage type.
     * @return {Storage} localStorage or sessionStorage.
     */
    getStorage: function () {
        var $this = this;
        switch($this.stateStorage) {
            case 'local':
                return localStorage;

            case 'session':
                return sessionStorage;

            default:
                throw new Error($this.stateStorage + ' is not a valid value for the state storage, supported values are "local" and "session".');
        }
    },

});
