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
 * @prop {boolean} [dragging] Whether the splitter is currently being dragged, i.e. whether an element is
 * being resized.
 * @prop {JQuery} gutters DOM elements of the gutter elements in splitter.
 * @prop {JQuery | null} [gutterElement] When resizing, the DOM elements of the gutter used for resizing.
 *@prop {JQuery | null} [gutterHandle] DOM element of the gutter handle for keyboard events.
 * @prop {boolean} horizontal Whether splitter element is horizontal.
 * @prop {boolean} vertical Whether splitter element is vertical.
 * @prop {JQuery | null} [nextPanelElement] When resizing, the DOM element of the panel after the panel being
 * resized.  
 * @prop {number  | null} [nextPanelSize] When resizing, the width or height (depending on the resize
 * direction) of the panel after the panel being resized.  
 * @prop {number[]} panelSizes Array of the panels size for save and restore state.
 * @prop {JQuery} panels DOM elements of the splitter panels in splitter.
 * @prop {JQuery | null} [prevPanelElement] When resizing, the DOM element of the panel before the panel being
 * resized.  
 * @prop {number | null} [prevPanelSize] When resizing, the width or height (depending on the resize
 * direction) of the panel before the panel being resized.  
 * @prop {number | null} [prevPanelIndex] When resizing, the index of the panel before the panel being
 * resized.  
 * @prop {number | null} [size] Initial width or height of the splitter (depending on the resize direction)
 * when resizing started.
 * @prop {number | null} [startPos] Start position in pixels when resizing.
 * @prop {number} timeout The interval for repeating key events.
 *
 * @interface {PrimeFaces.widget.SplitterCfg} cfg The configuration for the {@link  Splitter| Splitter widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {number} cfg.gutterSize Defines Size of the divider in pixels.
 * @prop {number} cfg.step Defines step size when holding down keyboard arrow keys.
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
        this.horizontal = this.cfg.layout === 'horizontal';
        this.vertical = !this.horizontal;
        this.panelSizes = [];

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
            this.jq.hide();
            this.panels.each(function(i, panel) {
                var panelSize = parseInt(panel.dataset.size) || 100 / $this.panels.length;
                $this.panelSizes[i] = panelSize;

                panel.style.flexBasis = `calc(${panelSize}% - ${($this.panels.length - 1) * $this.cfg.gutterSize}px)`;

                $this.gutters.eq(i).find('.ui-splitter-gutter-handle')
                    .attr('aria-valuenow', panelSize.toString())
                    .attr('aria-valuetext', panelSize.toFixed(0) + '%');
                $this.prevPanelSize = panelSize;
            });
            this.jq.show();
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
        }).on('mouseup.splitter', function(event) {
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

        this.gutters.each(function(index, gutter) {
            $(gutter).off('mousedown.splitter touchstart.splitter touchmove.splitter touchend.splitter keydown.splitter keyup.splitter')
                .on('keydown.splitter', function(event) {
                    $this.onGutterKeyDown(event, index);
                })
                .on('keyup.splitter', function(event) {
                    $this.onGutterKeyUp(event, index);
                })
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
        });
    },

    /**
     * Event handler for key down events.
     * @private
     * @param {JQuery.TriggeredEvent} event Event triggered for the key down.
     * 
     */
    onGutterKeyDown: function(event) {
        this.onResizeStart(event);
        var minSize = parseFloat(this.panels[0].dataset.minsize) || 0;

        switch (event.code) {
            case 'ArrowLeft': {
                if (this.horizontal) {
                    this.setTimer(event, this.cfg.step * -1);
                }

                event.preventDefault();
                break;
            }

            case 'ArrowRight': {
                if (this.horizontal) {
                    this.setTimer(event, this.cfg.step);
                }

                event.preventDefault();
                break;
            }

            case 'ArrowDown': {
                if (this.vertical) {
                    this.setTimer(event, this.cfg.step * -1);
                }

                event.preventDefault();
                break;
            }

            case 'ArrowUp': {
                if (this.vertical) {
                    this.setTimer(event, this.cfg.step);
                }

                event.preventDefault();
                break;
            }

            case 'Home': {
                this.resizePanel(100, minSize);

                event.preventDefault();
                break;
            }

            case 'End': {
                this.resizePanel(minSize, 100);

                event.preventDefault();
                break;
            }

            case 'Enter': {
                if (this.prevPanelSize > (100 - (minSize || 5))) {
                    this.resizePanel(minSize, 100);
                } else {
                    this.resizePanel(100, minSize);
                }

                event.preventDefault();
                break;
            }

            default:
                //no op
                break;
        }
    },

    /**
     * Event handler for key up events.
     * @private
     * @param {JQuery.TriggeredEvent} event Event triggered for the key up.
     * 
     */
    onGutterKeyUp: function(event) {
        this.clearTimer();
        this.onResizeEnd(event);
    },

    /**
     * The method that is called when the 'resize' event starts.
     * @private
     * @param {JQuery.TriggeredEvent} event Event triggered for the drag.
     * @param {boolean} isKeyDown is key being held down
     */
    onResizeStart: function(event, isKeyDown) {
        var pageX = event.type === 'touchstart' ? event.touches[0].pageX : event.pageX;
        var pageY = event.type === 'touchstart' ? event.touches[0].pageY : event.pageY;

        this.gutterElement = $(event.currentTarget);
        this.gutterHandle = this.gutterElement.find('.ui-splitter-gutter-handle')
        this.size = this.horizontal ? this.jq.width() : this.jq.height();
        this.dragging = true;
        this.startPos = this.horizontal ? pageX : pageY;
        this.prevPanelElement = this.gutterElement.prev();
        this.nextPanelElement = this.gutterElement.next();
        this.prevPanelIndex = this.panels.index(this.prevPanelElement);
        this.gutterElement.addClass('ui-splitter-gutter-resizing');
        this.jq.addClass('ui-splitter-resizing');
        if (isKeyDown) {
            this.prevPanelSize = this.horizontal ? this.prevPanelElement.outerWidth(true) : this.prevPanelElement.outerHeight(true);
            this.nextPanelSize = this.horizontal ? this.nextPanelElement.outerWidth(true) : this.nextPanelElement.outerHeight(true);
        } else {
            this.prevPanelSize = 100 * (this.horizontal ? this.prevPanelElement.outerWidth(true) : this.prevPanelElement.outerHeight(true)) / this.size;
            this.nextPanelSize = 100 * (this.horizontal ? this.nextPanelElement.outerWidth(true) : this.nextPanelElement.outerHeight(true)) / this.size;
        }
    },

    /**
     * The method called while the 'resize' event is running.
     * @private
     * @param {JQuery.TriggeredEvent} event Event triggered for the resize.
     * @param {number} step the step size
     * @param {boolean} isKeyDown is key being held down
     */
    onResize: function(event, step = 0, isKeyDown = false) {
        var newPos, newNextPanelSize, newPrevPanelSize;
        var pageX = event.type === 'touchmove' ? event.touches[0].pageX : event.pageX;
        var pageY = event.type === 'touchmove' ? event.touches[0].pageY : event.pageY;

        if (isKeyDown) {
            if (this.horizontal) {
                newPrevPanelSize = (100 * (this.prevPanelSize + step)) / this.size;
                newNextPanelSize = (100 * (this.nextPanelSize - step)) / this.size;
            } else {
                newPrevPanelSize = (100 * (this.prevPanelSize - step)) / this.size;
                newNextPanelSize = (100 * (this.nextPanelSize + step)) / this.size;
            }
        } else {
            if (this.horizontal) newPos = (pageX * 100) / this.size - (this.startPos * 100) / this.size;
            else newPos = (pageY * 100) / this.size - (this.startPos * 100) / this.size;
            newPrevPanelSize = this.prevPanelSize + newPos;
            newNextPanelSize = this.nextPanelSize - newPos;
        }

        this.resizePanel(newPrevPanelSize, newNextPanelSize);
    },

    /**
     * The method that is called when the 'resize' event ends and calls the server-side `resizeEnd` ajax behavior event
     * if such a behavior exists and call user 'onResizeEnd' callback.
     * Use `<p:ajax event="resizeEnd" listener="#{splitterView.onResizeEnd}" />` on the component to define a behavior.
     * @private
     * @param {JQuery.TriggeredEvent} event Event triggered for the resize end.
     */
    onResizeEnd: function(event) {
        if (!this.gutterElement) {
            return;
        }
        if (this.isStateful()) {
            this.saveState();
        }

        this.gutterElement.removeClass('ui-splitter-gutter-resizing');
        this.jq.removeClass('ui-splitter-resizing');

        //Call user onResizeEnd callback
        if (this.cfg.onResizeEnd) {
            this.cfg.onResizeEnd.call(this, this.panelSizes);
        }

        if (this.hasBehavior('resizeEnd')) {
            var sizesArr = this.panelSizes;
            var ext = {
                params: [
                    { name: this.id + '_panelSizes', value: sizesArr.map(function(e) { return e.toFixed(2) }).join('_') },
                ]
            };

            this.callBehavior('resizeEnd', ext);
        }

        this.clear();
    },

    /**
     * Resizes the panel.
     * @param {number} newPrevPanelSize the new size of the primary panel
     * @param {number} newNextPanelSize the new size of the secondary panel
     */
    resizePanel: function(newPrevPanelSize, newNextPanelSize) {
        if (this.validateResize(newPrevPanelSize, newNextPanelSize)) {
            this.prevPanelElement.css('flexBasis', 'calc(' + newPrevPanelSize + '% - ' + ((this.panels.length - 1) * this.cfg.gutterSize) + 'px)');
            this.nextPanelElement.css('flexBasis', 'calc(' + newNextPanelSize + '% - ' + ((this.panels.length - 1) * this.cfg.gutterSize) + 'px)');
            this.panelSizes[this.prevPanelIndex] = newPrevPanelSize;
            this.panelSizes[this.prevPanelIndex + 1] = newNextPanelSize;

            // update ARIA for the primary panel
            this.gutterHandle.attr('aria-valuenow', newPrevPanelSize.toString())
                .attr('aria-valuetext', newPrevPanelSize.toFixed(0) + '%');
            this.prevPanelSize = newPrevPanelSize;
        }
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
        this.gutterHandle = null;
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
        if (newPrevPanelSize > 100 || newPrevPanelSize < 0) return false;
        if (newNextPanelSize > 100 || newNextPanelSize < 0) return false;

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
        switch (this.cfg.stateStorage) {
            case 'local':
                return window.localStorage;

            case 'session':
                return window.sessionStorage;

            default:
                throw new Error(this.cfg.stateStorage + ' is not a valid value for the state storage, supported values are "local" and "session".');
        }
    },

    /**
     * Repeat the current key using a step.
     * @param {JQuery.TriggeredEvent} event Event triggered for the repeat.
     * @param {number} step the increment to step by
     * @private
     */
    repeat: function(event, step) {
        this.onResizeStart(event, true);
        this.onResize(event, step, true);
    },

    /**
     * Sets the current interval for repeating keyboard events.
     * @param {JQuery.TriggeredEvent} event Event triggered for the repeat.
     * @param {number} step the increment to step by
     * @private
     */
    setTimer: function(event, step) {
        if (!this.timeout) {
            var $this = this;
            this.clearTimer();
            this.timeout = setInterval(() => { $this.repeat(event, step); }, 40);
        }
    },

    /**
     * Clears the current interval for repeating keyboard events.
     * @private
     */
    clearTimer: function() {
        if (this.timeout) {
            clearInterval(this.timeout);
            this.timeout = null;
        }
    }
});
