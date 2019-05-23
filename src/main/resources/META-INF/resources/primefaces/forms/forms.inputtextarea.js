/**
 * PrimeFaces InputTextarea Widget
 */
PrimeFaces.widget.InputTextarea = PrimeFaces.widget.DeferredWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.autoResize)
            this.renderDeferred();
        else
            this._render();
    },

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

    //@override
    refresh: function(cfg) {
        //remove autocomplete panel
        if(cfg.autoComplete) {
            $(PrimeFaces.escapeClientId(cfg.id + '_panel')).remove();
        }

        this._super(cfg);
    },

    setupAutoResize: function() {
        autosize(this.jq);
    },

    applyMaxlength: function() {
        var _self = this;

        this.jq.on('keyup.inputtextarea-maxlength', function(e) {
            var value = _self.normalizeNewlines(_self.jq.val()),
            length = value.length;

            if(length > _self.cfg.maxlength) {
                _self.jq.val(value.substr(0, _self.cfg.maxlength));
            }
        });
    },

    updateCounter: function() {
        var value = this.normalizeNewlines(this.jq.val()),
        length = value.length;

        if(this.counter) {
            var remaining = this.cfg.maxlength - length;
            if(remaining < 0) {
                remaining = 0;
            }

            var counterText = this.cfg.counterTemplate.replace('{0}', remaining);
            counterText = counterText.replace('{1}', length);

            this.counter.text(counterText);
        }
    },

    normalizeNewlines: function(text) {
        return text.replace(/(\r\n|\r|\n)/g, '\r\n');
    },

    setupAutoComplete: function() {
        var panelMarkup = '<div id="' + this.id + '_panel" class="ui-autocomplete-panel ui-widget-content ui-corner-all ui-helper-hidden ui-shadow"></div>',
        _self = this;

        this.panel = $(panelMarkup).appendTo(document.body);

        this.jq.keyup(function(e) {
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

        }).keydown(function(e) {
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
            insertValue = itemValue.substring(_self.query.length);

            _self.jq.focus();

            _self.jq.insertText(insertValue, _self.jq.getSelection().start, true);

            _self.invokeItemSelectBehavior(event, itemValue);

            _self.hide();
        });
    },

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

    clearTimeout: function() {
        if(this.timeout) {
            clearTimeout(this.timeout);
        }

        this.timeout = null;
    },

    extractQuery: function() {
        var end = this.jq.getSelection().end,
        result = /\S+$/.exec(this.jq.get(0).value.slice(0, end)),
        lastWord = result ? result[0] : null;

        return lastWord;
    },

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

        PrimeFaces.ajax.Request.handle(options);
    },

    alignPanel: function() {
        var pos = this.jq.getCaretPosition(),
        posLeft = (pos.left > 0 ? '+' : '-') + pos.left,
        posTop = (pos.top > 0 ? '+' : '-') + pos.top;

        this.panel.css({left:'', top:''}).position({
            my: 'left top'
            ,at: 'left' + posLeft +  ' top' + posTop
            ,of: this.jq
        });
    },

    show: function() {
        this.panel.css({
            'z-index': ++PrimeFaces.zindex,
            'width': this.jq.innerWidth(),
            'visibility': 'hidden'
        }).show();
        
        this.alignPanel();
        
        this.panel.css('visibility', '');
    },

    hide: function() {
        this.panel.hide();
    },

    setupDialogSupport: function() {
        var dialog = this.jq.parents('.ui-dialog:first');

        if(dialog.length == 1 && dialog.css('position') === 'fixed') {
            this.panel.css('position', 'fixed');
        }
    }

});
