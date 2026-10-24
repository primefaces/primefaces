import autosize from "autosize";

/**
 * __PrimeFaces InputTextarea Widget__
 *
 * InputTextarea is an extension to standard inputTextarea with autoComplete, autoResize, remaining characters counter
 * and theming features.
 *
 * @prop {JQuery | null} [counter] The DOM element for the counter that informs the user about the number of
 * characters they can still enter before they reach the limit.
 * @prop {JQuery} [panel] The DOM element for the overlay panel with the autocomplete suggestions, when
 * autocomplete is enabled.
 * @prop {JQuery} [items] The DOM elements in the autocomplete panel that the user can select.
 * @prop {string} [query] The keyword or search term the autocomplete method receives as input.
 * @prop {number | null} [timeout] The internal timeout ID of the most recent timeout that was started.
 *
 * @interface {PrimeFaces.widget.InputTextareaCfg} cfg The configuration for the {@link  InputTextarea| InputTextarea widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {boolean} cfg.autoResize Enables auto growing when being typed.
 * @prop {boolean} cfg.autoComplete Enables autocompletion that suggests tokens to the user as they type.
 * @prop {string} cfg.counter ID of the label component to display remaining and entered characters.
 * @prop {string} cfg.counterTemplate Template text to display in counter, default value is `{0}`.
 * @prop {number} cfg.maxlength Maximum number of characters that may be entered in this field.
 * @prop {number} cfg.minQueryLength Number of characters to be typed to run a query.
 * @prop {number} cfg.queryDelay Delay in milliseconds before sending each query.
 * @prop {number} cfg.scrollHeight Height of the viewport for autocomplete suggestions.
 */
PrimeFaces.widget.InputTextarea = class InputTextarea extends PrimeFaces.widget.DeferredWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.items = $();

        if(this.cfg.autoResize)
            this.renderDeferred();
        else
            this._render();
    }

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render() {
        //Visuals
        PrimeFaces.skinInput(this.jq);

        //autoComplete
        if(this.cfg.autoComplete) {
            this.setupAutoComplete();
        }

        //Counter
        if(this.cfg.counter) {
            this.counter = this.cfg.counter ? $(PrimeFaces.escapeClientId(this.cfg.counter)) : null;
            this.cfg.counterTemplate = this.cfg.counterTemplate||'{0}';
            this.updateCounter();

            if(this.counter) {
                var $this = this;
                this.jq.on('input.inputtextarea-counter', function(e) {
                    $this.updateCounter();
                });
            }
        }

        //maxLength
        if(this.cfg.maxlength) {
            this.applyMaxlength();
        }

        //autoResize
        if(this.cfg.autoResize) {
            this.setupAutoResize();
        }
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        //remove autocomplete panel
        if(cfg.autoComplete) {
            $(PrimeFaces.escapeClientId(cfg.id + '_panel')).remove();
        }

        super.refresh(cfg);
    }

    /**
     * Initializes the auto resize functionality that resize this textarea depending on the entered text.
     * @private
     */
    setupAutoResize() {
        autosize(this.jq);
    }

    /**
     * Applies the value of the max length setting, counting line breaks correctly.
     * @private
     */
    applyMaxlength() {
        var $this = this;

        this.jq.on('keyup.inputtextarea-maxlength', function(e) {
            var value = $this.jq.val(),
            length = $this.cfg.countBytesAsChars ? PrimeFaces.utils.countBytes(value) : value.length;

            if(length > $this.cfg.maxlength) {
                // Trim by bytes if counting bytes, otherwise by chars
                if ($this.cfg.countBytesAsChars) {
                    // Keep removing chars from end until we're under byte limit
                    while (PrimeFaces.utils.countBytes(value) > $this.cfg.maxlength) {
                        value = value.slice(0, -1);
                    }
                    $this.jq.val(value);
                }
                else {
                    $this.jq.val(value.slice(0, $this.cfg.maxlength));
                }
            }
        });
    }

    /**
     * Updates the counter value that keeps count of how many more characters the user can enter before they reach the
     * limit.
     * @private
     */
    updateCounter() {
        var value = this.jq.val(),
        length = this.cfg.countBytesAsChars ? PrimeFaces.utils.countBytes(value) : value.length;

        if(this.counter) {
            var remaining = this.cfg.maxlength - length;
            if(remaining < 0) {
                remaining = 0;
            }

            var counterText = this.cfg.counterTemplate
                    .replace('{0}', remaining)
                    .replace('{1}', length)
                    .replace('{2}', this.cfg.maxlength);

            this.counter.text(counterText);
        }
    }
    /**
     * Sets up the server-side auto complete functionality that suggests tokens while the user types.
     * @private
     */
    setupAutoComplete() {
        var panelMarkup = '<div id="' + this.id + '_panel" class="ui-autocomplete-panel ui-widget-content p-hidden"></div>',
        $this = this;

        this.panel = $(panelMarkup).appendTo(document.body);

        this.jq.on("keyup", function(e) {
            switch(e.code) {
                case 'ArrowUp':
                case 'ArrowDown':
                case 'ArrowLeft':
                case 'ArrowRight':
                case 'Enter':
                case 'NumpadEnter':
                case 'Tab':
                case 'Space':
                case 'Shift':
                case 'Control':
                case 'Alt':
                case 'Meta':
                case 'Escape':
                    //do not search
                break;

                default:
                    var query = $this.extractQuery();
                    if(query && query.length >= $this.cfg.minQueryLength) {

                         //Cancel the search request if user types within the timeout
                        if($this.timeout) {
                            $this.clearTimeout($this.timeout);
                        }

                        $this.timeout = PrimeFaces.queueTask(function() {
                            $this.search(query);
                        }, $this.cfg.queryDelay);

                    }
                break;
            }

        }).on("keydown", function(e) {
            var overlayVisible = $this.panel.is(':visible');
            var highlightedItem = $this.items.filter('.ui-state-highlight')

            switch(e.key) {
                case 'ArrowUp':
                case 'ArrowLeft':
                    if(overlayVisible) {
                        var prev = highlightedItem.length == 0 ? $this.items.eq(0) : highlightedItem.prev();

                        if(prev.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            prev.addClass('ui-state-highlight');

                            if($this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView($this.panel, prev);
                            }
                        }

                        e.preventDefault();
                    }
                    else {
                        $this.clearTimeout();
                    }
                break;

                case 'ArrowDown':
                case 'ArrowRight':
                    if(overlayVisible) {
                        var next = highlightedItem.length == 0 ? $this.items.eq(0) : highlightedItem.next();

                        if(next.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            next.addClass('ui-state-highlight');

                            if($this.cfg.scrollHeight) {
                                PrimeFaces.scrollInView($this.panel, next);
                            }
                        }

                        e.preventDefault();
                    }
                    else {
                        $this.clearTimeout();
                    }
                break;

                case 'Enter':
                    if(overlayVisible) {
                        $this.items.filter('.ui-state-highlight').trigger('click');

                        e.preventDefault();
                    }
                    else {
                        $this.clearTimeout();
                    }
                break;

                case ' ':
                case 'Shift':
                case 'Control':
                case 'Alt':
                case 'Meta':
                case 'Backspace':
                case 'Escape':
                    $this.clearTimeout();

                    if(overlayVisible) {
                        $this.hide();
                    }
                break;

                case 'Tab':
                    $this.clearTimeout();

                    if(overlayVisible) {
                        $this.items.filter('.ui-state-highlight').trigger('click');
                        $this.hide();
                    }
                break;
            }
        });

        //hide panel when outside is clicked
        $(document.body).on('mousedown.ui-inputtextarea'+this.id, function (e) {
            if($this.panel.is(":hidden")) {
                return;
            }
            var offset = $this.panel.offset();
            if(e.target === $this.jq.get(0)) {
                return;
            }

            if (e.pageX < offset.left ||
                e.pageX > offset.left + $this.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + $this.panel.height()) {
                $this.hide();
            }
        });
        var cleanup = function() {
             $(document.body).off('mousedown.ui-inputtextarea'+this.id);
             this.panel.remove();
        };
        this.addRefreshListener(cleanup);
        this.addDestroyListener(cleanup);

        //Hide overlay on resize
        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.panel, function() {
            $this.hide();
        });

        //dialog support
        this.setupDialogSupport();

        if (!this.jq.hasClass('ui-state-disabled')) {
            this.jq.data('primefaces-overlay-target', true);
        }
    }

    /**
     * Sets up all event listeners for the various events required by this widget.
     * @private
     */
    bindDynamicEvents() {
        var $this = this;

        //visuals and click handler for items
        this.items.on('mouseover', function() {
            var item = $(this);

            if(!item.hasClass('ui-state-highlight')) {
                $this.items.filter('.ui-state-highlight').removeClass('ui-state-highlight');
                item.addClass('ui-state-highlight');
            }
        })
        .on('click', function(event) {
            var item = $(this),
            itemValue = item.attr('data-item-value'),
            selectionStart = PrimeFaces.utils.getTextSelection($this.jq).start,
            queryLength = $this.query.length;

            $this.jq.trigger('focus');

            PrimeFaces.utils.setTextSelection($this.jq, selectionStart-queryLength, selectionStart);
            PrimeFaces.utils.replaceTextSelection($this.jq, itemValue);

            $this.invokeItemSelectBehavior(event, itemValue);

            $this.hide();
        });
    }

    /**
     * Callback that is invoked when the user has selected one of the suggested tokens.
     * @private
     * @param {JQuery.TriggeredEvent} event Event that triggered the item selection (usually a click or enter press).
     * @param {string} itemValue Value of the suggestion that was selected.
     */
    invokeItemSelectBehavior(event, itemValue) {
        if(this.hasBehavior('itemSelect')) {
            var ext = {
                params : [
                    {name: this.id + '_itemSelect', value: itemValue}
                ]
            };

            this.callBehavior('itemSelect', ext);
        }
    }

    /**
     * Clears the timeout that was set up by the autocomplete feature.
     * @private
     */
    clearTimeout() {
        if(this.timeout) {
            clearTimeout(this.timeout);
        }

        this.timeout = null;
    }

    /**
     * Finds the keyword to be used for the autocomplete search.
     * @private
     * @return {string} The keyword or search term the autocomplete method receives as input.
     */
    extractQuery() {
        var end = PrimeFaces.utils.getTextSelection(this.jq).end,
        result = /\S+$/.exec(this.jq.get(0).value.slice(0, end)),
        lastWord = result ? result[0] : null;

        return lastWord;
    }

    /**
     * Performs an autocomplete search for the given search term. Opens the windows with the suggestions.
     * @param {string} query Search term to search for.
     */
    search(query) {
        this.query = query;

        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            params: [
                {name: this.id + '_query', value: query}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.panel.html(content);
                            this.items = $this.panel.find('.ui-autocomplete-item');

                            this.bindDynamicEvents();

                            if(this.items.length > 0) {
                                //highlight first item
                                this.items.eq(0).addClass('ui-state-highlight');

                                //adjust height
                                if(this.cfg.scrollHeight && this.panel.height() > this.cfg.scrollHeight) {
                                    this.panel.height(this.cfg.scrollHeight);
                                }

                                if(this.panel.is(':hidden')) {
                                    this.show();
                                }  else {
                                    this.alignPanel(); //with new items
                                }

                            }
                            else {
                                this.panel.hide();
                            }
                        }
                    });

                return true;
            }
        };

        if (this.hasBehavior('query')) {
            this.callBehavior('query', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Aligns the search window panel of the autocomplete feature.
     */
    alignPanel() {
        var pos = this.getCaretPosition(),
        posLeft = (pos.left > 0 ? '+' : '-') + pos.left,
        posTop = (pos.top > 0 ? '+' : '-') + pos.top;

        PrimeFaces.utils.position(this.panel.css({left:'', top:''}), {
            my: 'left top'
            ,at: 'left' + posLeft + 'px' +  ' top' + posTop + 'px'
            ,of: this.jq
            ,collision: 'flipfit'
        });
    }

    /**
     * Finds the position of the caret in pixels by rendering the text into a hidden copy of the textarea with the same
     * styles, formerly the jQuery plugin `getCaretPosition`.
     * @private
     * @return {{top: number, left: number}} The position below the caret line, relative to the top left corner of the
     * textarea.
     */
    getCaretPosition() {
        var element = this.jq[0],
            value = element.value,
            caret = element.selectionStart || 0,
            mirror = $('<div></div>').css({
                position: 'absolute',
                top: '0px',
                left: '0px',
                visibility: 'hidden',
                width: this.jq.width() + 'px',
                height: this.jq.height() + 'px',
                // the size is the content size of the textarea, the global border-box would subtract padding and border
                'box-sizing': 'content-box',
                'white-space': 'pre-wrap',
                'word-wrap': 'break-word',
                'overflow-x': 'hidden',
                'overflow-y': 'auto'
            }),
            // a zero width space, so the caret has a line box in every browser
            focus = $('<span></span>').text('​');

        for (var style of ['fontFamily', 'fontSize', 'fontWeight', 'fontVariant', 'fontStyle', 'lineHeight', 'letterSpacing',
                'wordSpacing', 'textTransform', 'textIndent', 'tabSize',
                'paddingLeft', 'paddingTop', 'paddingBottom', 'paddingRight', 'borderLeftStyle', 'borderTopStyle',
                'borderBottomStyle', 'borderRightStyle', 'borderLeftWidth', 'borderTopWidth', 'borderBottomWidth',
                'borderRightWidth']) {
            mirror.css(style, this.jq.css(style));
        }

        mirror.append(document.createTextNode(value.substring(0, caret)), focus, document.createTextNode(value.substring(caret)))
            .appendTo(document.body);

        var focusOffset = focus.offset(),
            mirrorOffset = mirror.offset(),
            position = {
                top: focusOffset.top - mirrorOffset.top - element.scrollTop + (parseFloat(this.jq.css('fontSize')) || 0),
                left: focusOffset.left - mirrorOffset.left - element.scrollLeft
            };
        mirror.remove();
        return position;
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable() {
        PrimeFaces.utils.disableInputWidget(this.jq);
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    enable() {
        PrimeFaces.utils.enableInputWidget(this.jq);
    }

    /**
     * Brings up the search window panel of the autocomplete feature.
     * @private
     */
    show() {
        this.panel.css({
            'z-index': PrimeFaces.nextZindex(),
            'width': this.jq.innerWidth() + 'px',
            'visibility': 'hidden'
        }).show();

        this.alignPanel();

        this.panel.css('visibility', '');
    }

    /**
     * Hides the search window panel of the autocomplete feature.
     * @private
     */
    hide() {
        this.panel.hide();
    }

    /**
     * Adjust the search window panel of the autocomplete in case this widget is inside a dialog overlay.
     * @private
     */
    setupDialogSupport() {
        var dialog = this.jq.parents('.ui-dialog:first');

        if(dialog.length == 1 && dialog.css('position') === 'fixed') {
            this.panel.css('position', 'fixed');
        }
    }

}
