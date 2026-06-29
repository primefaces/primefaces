/**
 * __PrimeFaces InputTextarea Widget__
 * 
 * InputTextarea is an extension to standard inputTextarea with autoComplete, autoResize, remaining characters counter
 * and theming features.
 * 
 * @prop {JQuery} counter The DOM element for the counter that informs the user about the number of characters they can
 * still enter before they reach the limit.
 * @prop {JQuery} panel The DOM element for the overlay panel with the autocomplete suggestions.
 * @prop {JQuery} items The DOM elements in the autocomplete panel that the user can select.
 * @prop {number} timeout The internal timeout ID of the most recent timeout that was started.
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
PrimeFaces.widget.InputTextarea = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.autoResize)
            this.renderDeferred();
        else
            this._render();
    },
    
    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
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
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        //remove autocomplete panel
        if(cfg.autoComplete) {
            $(PrimeFaces.escapeClientId(cfg.id + '_panel')).remove();
        }

        this._super(cfg);
    },

    /**
     * Initializes the auto resize functionality that resize this textarea depending on the entered text.
     * @private
     */
    setupAutoResize: function() {
        autosize(this.jq);
    },

    /**
     * Applies the value of the max length setting, counting line breaks correctly.
     * @private
     */
    applyMaxlength: function() {
        var _self = this;

        this.jq.on('keyup.inputtextarea-maxlength', function(e) {
            var value = _self.jq.val(),
            length = value.length;

            if(length > _self.cfg.maxlength) {
                _self.jq.val(value.substr(0, _self.cfg.maxlength));
            }
        });
    },

    /**
     * Updates the counter value that keeps count of how many more characters the user can enter before they reach the
     * limit.
     * @private
     */
    updateCounter: function() {
        var value = this.jq.val(),
        length = value.length;

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
    },

    /**
     * Sets up the server-side auto complete functionality that suggests tokens while the user types.
     * @private
     */
    setupAutoComplete: function() {
        var panelMarkup = '<div id="' + this.id + '_panel" class="ui-autocomplete-panel ui-widget-content ui-corner-all ui-helper-hidden ui-shadow"></div>',
        _self = this;

        this.panel = $(panelMarkup).appendTo(document.body);

        this.jq.on("keyup", function(e) {
            var keyCode = $.ui.keyCode;

            switch(e.which) {

                case keyCode.UP:
                case keyCode.LEFT:
                case keyCode.DOWN:
                case keyCode.RIGHT:
                case keyCode.ENTER:
                case keyCode.TAB:
                case keyCode.SPACE:
                case 17: //keyCode.CONTROL:
                case 18: //keyCode.ALT:
                case keyCode.ESCAPE:
                case 224:   //mac command
                    //do not search
                break;

                default:
                    var query = _self.extractQuery();
                    if(query && query.length >= _self.cfg.minQueryLength) {

                         //Cancel the search request if user types within the timeout
                        if(_self.timeout) {
                            _self.clearTimeout(_self.timeout);
                        }

                        _self.timeout = setTimeout(function() {
                            _self.search(query);
                        }, _self.cfg.queryDelay);

                    }
                break;
            }

        }).on("keydown", function(e) {
            var overlayVisible = _self.panel.is(':visible'),
            keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.UP:
                case keyCode.LEFT:
                    if(overlayVisible) {
                        var highlightedItem = _self.items.filter('.ui-state-highlight'),
                        prev = highlightedItem.length == 0 ? _self.items.eq(0) : highlightedItem.prev();

                        if(prev.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            prev.addClass('ui-state-highlight');

                            if(_self.cfg.scrollHeight) {
                                PrimeFaces.scrollInView(_self.panel, prev);
                            }
                        }

                        e.preventDefault();
                    }
                    else {
                        _self.clearTimeout();
                    }
                break;

                case keyCode.DOWN:
                case keyCode.RIGHT:
                    if(overlayVisible) {
                        var highlightedItem = _self.items.filter('.ui-state-highlight'),
                        next = highlightedItem.length == 0 ? _self.items.eq(0) : highlightedItem.next();

                        if(next.length == 1) {
                            highlightedItem.removeClass('ui-state-highlight');
                            next.addClass('ui-state-highlight');

                            if(_self.cfg.scrollHeight) {
                                PrimeFaces.scrollInView(_self.panel, next);
                            }
                        }

                        e.preventDefault();
                    }
                    else {
                        _self.clearTimeout();
                    }
                break;

                case keyCode.ENTER:
                    if(overlayVisible) {
                        _self.items.filter('.ui-state-highlight').trigger('click');

                        e.preventDefault();
                    }
                    else {
                        _self.clearTimeout();
                    }
                break;

                case keyCode.SPACE:
                case 17: //keyCode.CONTROL:
                case 18: //keyCode.ALT:
                case keyCode.BACKSPACE:
                case keyCode.ESCAPE:
                case 224:   //mac command
                    _self.clearTimeout();

                    if(overlayVisible) {
                        _self.hide();
                    }
                break;

                case keyCode.TAB:
                    _self.clearTimeout();

                    if(overlayVisible) {
                        _self.items.filter('.ui-state-highlight').trigger('click');
                        _self.hide();
                    }
                break;
            }
        });

        //hide panel when outside is clicked
        $(document.body).on('mousedown.ui-inputtextarea', function (e) {
            if(_self.panel.is(":hidden")) {
                return;
            }
            var offset = _self.panel.offset();
            if(e.target === _self.jq.get(0)) {
                return;
            }

            if (e.pageX < offset.left ||
                e.pageX > offset.left + _self.panel.width() ||
                e.pageY < offset.top ||
                e.pageY > offset.top + _self.panel.height()) {
                _self.hide();
            }
        });

        //Hide overlay on resize
        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', _self.panel, function() {
            _self.hide();
        });

        //dialog support
        this.setupDialogSupport();
    },

    /**
     * Sets up all event listeners for the various events required by this widget.
     * @private
     */
    bindDynamicEvents: function() {
        var _self = this;

        //visuals and click handler for items
        this.items.on('mouseover', function() {
            var item = $(this);

            if(!item.hasClass('ui-state-highlight')) {
                _self.items.filter('.ui-state-highlight').removeClass('ui-state-highlight');
                item.addClass('ui-state-highlight');
            }
        })
        .on('click', function(event) {
            var item = $(this),
            itemValue = item.attr('data-item-value'),
            selectionStart = _self.jq.getSelection().start,
            queryLength = _self.query.length;

            _self.jq.trigger('focus');

            _self.jq.setSelection(selectionStart-queryLength, selectionStart);
            _self.jq.replaceSelectedText(itemValue);

            _self.invokeItemSelectBehavior(event, itemValue);

            _self.hide();
        });
    },

    /**
     * Callback that is invoked when the user has selected one of the suggested tokens.
     * @private
     * @param {JQuery.TriggeredEvent} event Event that triggered the item selection (usually a click or enter press).
     * @param {string} itemValue Value of the suggestion that was selected.
     */
    invokeItemSelectBehavior: function(event, itemValue) {
        if(this.hasBehavior('itemSelect')) {
            var ext = {
                params : [
                    {name: this.id + '_itemSelect', value: itemValue}
                ]
            };

            this.callBehavior('itemSelect', ext);
        }
    },

    /**
     * Clears the timeout that was set up by the autocomplete feature.
     * @private
     */
    clearTimeout: function() {
        if(this.timeout) {
            clearTimeout(this.timeout);
        }

        this.timeout = null;
    },

    /**
     * Finds the keyword to be used for the autocomplete search.
     * @private
     * @return {string} The keyword or search term the autocomplete method receives as input.
     */
    extractQuery: function() {
        var end = this.jq.getSelection().end,
        result = /\S+$/.exec(this.jq.get(0).value.slice(0, end)),
        lastWord = result ? result[0] : null;

        return lastWord;
    },

    /**
     * Performs an autocomplete search for the given search term. Opens the windows with the suggestions.
     * @param {string} query Search term to search for.
     */
    search: function(query) {
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
    },

    /**
     * Aligns the search window panel of the autocomplete feature.
     */
    alignPanel: function() {
        var pos = this.jq.getCaretPosition(),
        posLeft = (pos.left > 0 ? '+' : '-') + pos.left,
        posTop = (pos.top > 0 ? '+' : '-') + pos.top;

        this.panel.css({left:'', top:''}).position({
            my: 'left top'
            ,at: 'left' + posLeft + 'px' +  ' top' + posTop + 'px'
            ,of: this.jq
        });
    },

    /**
     * Brings up the search window panel of the autocomplete feature.
     * @private
     */
    show: function() {
        this.panel.css({
            'z-index': PrimeFaces.nextZindex(),
            'width': this.jq.innerWidth() + 'px',
            'visibility': 'hidden'
        }).show();

        this.alignPanel();

        this.panel.css('visibility', '');
    },

    /**
     * Hides the search window panel of the autocomplete feature.
     * @private
     */
    hide: function() {
        this.panel.hide();
    },

    /**
     * Adjust the search window panel of the autocomplete in case this widget is inside a dialog overlay.
     * @private
     */
    setupDialogSupport: function() {
        var dialog = this.jq.parents('.ui-dialog:first');

        if(dialog.length == 1 && dialog.css('position') === 'fixed') {
            this.panel.css('position', 'fixed');
        }
    }

});
