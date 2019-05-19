/**
 * PrimeFaces SelectCheckboxMenu Widget
 */
PrimeFaces.widget.SelectCheckboxMenu = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.labelContainer = this.jq.find('.ui-selectcheckboxmenu-label-container');
        this.label = this.jq.find('.ui-selectcheckboxmenu-label');
        this.menuIcon = this.jq.children('.ui-selectcheckboxmenu-trigger');
        this.triggers = this.jq.find('.ui-selectcheckboxmenu-trigger, .ui-selectcheckboxmenu-label');
        this.disabled = this.jq.hasClass('ui-state-disabled');
        this.inputs = this.jq.find(':checkbox');
        this.panelId = this.id + '_panel';
        this.labelId = this.id + '_label';
        this.keyboardTarget = $(this.jqId + '_focus');
        this.tabindex = this.keyboardTarget.attr('tabindex');
        this.cfg.showHeader = (this.cfg.showHeader === undefined) ? true : this.cfg.showHeader;
        this.cfg.dynamic = this.cfg.dynamic === true ? true : false;
        this.isDynamicLoaded = false;
        this.cfg.labelSeparator = (this.cfg.labelSeparator === undefined) ? ', ' : this.cfg.labelSeparator;

        if(!this.disabled) {
            if(this.cfg.multiple) {
                this.triggers = this.jq.find('.ui-selectcheckboxmenu-trigger, .ui-selectcheckboxmenu-multiple-container');
            }

            if(!this.cfg.dynamic) {
                this._renderPanel();
            }

            this.bindEvents();
            this.bindKeyEvents();

            //mark trigger and descandants of trigger as a trigger for a primefaces overlay
            this.triggers.data('primefaces-overlay-target', true).find('*').data('primefaces-overlay-target', true);

            if(!this.cfg.multiple) {
                if(this.cfg.updateLabel) {
                    this.defaultLabel = this.label.text();
                    this.label.css({
                        'text-overflow': 'ellipsis',
                        overflow: 'hidden'
                    });

                    this.updateLabel();
                }

                this.label.attr('id', this.labelId);
                this.keyboardTarget.attr('aria-expanded', false).attr('aria-labelledby', this.labelId);
            }
        } else {
            // disabled
            if(!this.cfg.multiple) {
                if (this.cfg.updateLabel) {
                    this.defaultLabel = this.label.text();
                    this.label.css({
                        'text-overflow': 'ellipsis',
                        overflow: 'hidden'
                    });

                    this.updateLabel();
                }
            }
        }

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    //@override
    refresh: function(cfg) {
        this._super(cfg);
    },

    _renderPanel: function() {
        this.renderPanel();

        if(this.tabindex) {
            this.panel.find('a, input').attr('tabindex', this.tabindex);
        }

        this.checkboxes = this.itemContainer.find('.ui-chkbox-box:not(.ui-state-disabled)');
        this.labels = this.itemContainer.find('label');

        this.bindPanelEvents();
        this.bindPanelKeyEvents();

        this.isDynamicLoaded = true;
    },

    renderPanel: function() {
        this.panel = $('<div id="' + this.panelId + '" class="ui-selectcheckboxmenu-panel ui-widget ui-widget-content ui-corner-all ui-helper-hidden ui-input-overlay" role="dialog"></div>');

        PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');

        if(this.cfg.panelStyle) {
            this.panel.attr('style', this.cfg.panelStyle);
        }

        if(this.cfg.panelStyleClass) {
            this.panel.addClass(this.cfg.panelStyleClass);
        }

        this.renderHeader();

        this.renderItems();

        if(this.cfg.scrollHeight) {
            this.itemContainerWrapper.height(this.cfg.scrollHeight);
        }
        else if(this.inputs.length > 10) {
            this.itemContainerWrapper.height(200);
        }
    },

    renderHeader: function() {
        this.header = $('<div class="ui-widget-header ui-corner-all ui-selectcheckboxmenu-header ui-helper-clearfix"></div>')
                        .appendTo(this.panel);

        if(!this.cfg.showHeader) {
            this.header.removeClass('ui-helper-clearfix').addClass('ui-helper-hidden');
        }
        //toggler
        this.toggler = $('<div class="ui-chkbox ui-widget"><div class="ui-helper-hidden-accessible"><input type="checkbox" role="checkbox" aria-label="Select All" readonly="readonly"/></div><div class="ui-chkbox-box ui-widget ui-corner-all ui-state-default"><span class="ui-chkbox-icon ui-icon ui-icon-blank"></span></div></div>')
                            .appendTo(this.header);
        this.togglerBox = this.toggler.children('.ui-chkbox-box');
        if(this.inputs.filter(':not(:checked)').length === 0) {
            this.check(this.togglerBox);
        }

        //filter
        if(this.cfg.filter) {
            this.filterInputWrapper = $('<div class="ui-selectcheckboxmenu-filter-container"></div>').appendTo(this.header);
            this.filterInput = $('<input type="text" aria-multiline="false" aria-readonly="false" aria-disabled="false" aria-label="Filter Input" role="textbox" class="ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all">')
                                .appendTo(this.filterInputWrapper);

            if(this.cfg.filterPlaceholder) {
                this.filterInput.attr('placeholder', this.cfg.filterPlaceholder);
            }

            this.filterInputWrapper.append("<span class='ui-icon ui-icon-search'></span>");
        }

        //closer
        this.closer = $('<a class="ui-selectcheckboxmenu-close ui-corner-all" href="#"><span class="ui-icon ui-icon-circle-close"></span></a>')
                    .attr('aria-label', 'Close').appendTo(this.header);

    },

    renderItems: function() {
        var $this = this;

        this.itemContainerWrapper = $('<div class="ui-selectcheckboxmenu-items-wrapper"><ul class="ui-selectcheckboxmenu-items ui-selectcheckboxmenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset"></ul></div>')
                .appendTo(this.panel);

        this.itemContainer = this.itemContainerWrapper.children('ul.ui-selectcheckboxmenu-items');

        //check if inputs must be grouped
        var grouped = this.inputs.filter('[group-label]');

        var currentGroupName = null;
        for(var i = 0; i < this.inputs.length; i++) {
            var input = this.inputs.eq(i),
            label = input.next(),
            disabled = input.is(':disabled'),
            checked = input.is(':checked'),
            title = input.attr('title'),
            boxClass = 'ui-chkbox-box ui-widget ui-corner-all ui-state-default',
            itemClass = 'ui-selectcheckboxmenu-item ui-selectcheckboxmenu-list-item ui-corner-all',
            escaped = input.data('escaped');

            if(grouped.length && currentGroupName !== input.attr('group-label')) {
                currentGroupName = input.attr('group-label');
            	var itemGroup = $('<li class="ui-selectcheckboxmenu-item-group ui-selectcheckboxmenu-group-list-item ui-corner-all"></li>');
            	itemGroup.text(currentGroupName);
            	$this.itemContainer.append(itemGroup);
            }

            if(disabled) {
                boxClass += " ui-state-disabled";
            }

            if(checked) {
                boxClass += " ui-state-active";
            }

            var iconClass = checked ? 'ui-chkbox-icon ui-icon ui-icon-check' : 'ui-chkbox-icon ui-icon ui-icon-blank',
            itemClass = checked ? itemClass + ' ui-selectcheckboxmenu-checked' : itemClass + ' ui-selectcheckboxmenu-unchecked';

            var item = $('<li class="' + itemClass + '"></li>');
            item.append('<div class="ui-chkbox ui-widget"><div class="ui-helper-hidden-accessible"><input type="checkbox" role="checkbox" readonly="readonly"></input></div>' +
                    '<div class="' + boxClass + '"><span class="' + iconClass + '"></span></div></div>');

            var uuid = PrimeFaces.uuid();
            var itemLabel = $('<label for='+uuid+'></label>'),
            labelHtml = label.html().trim(),
            labelLength = labelHtml.length;
            if (labelLength > 0 && labelHtml !== '&nbsp;')
                if(escaped)
                    itemLabel.text(label.text());
                else
                    itemLabel.html(label.html());
            else
                itemLabel.text(input.val());

            itemLabel.appendTo(item);

            if(title) {
                item.attr('title', title);
            }

            if($this.cfg.multiple) {
                item.attr('data-item-value', input.val());
            }

            item.find('> .ui-chkbox > .ui-helper-hidden-accessible > input').prop('checked', checked).attr('aria-checked', checked).attr('id', uuid);
            $this.itemContainer.attr('role', 'group');

            $this.itemContainer.append(item);
        }

        this.items = this.itemContainer.children('li.ui-selectcheckboxmenu-item');
        this.groupHeaders = this.itemContainer.children('li.ui-selectcheckboxmenu-item-group');
    },

    bindEvents: function() {
        var $this = this;

        //Events to show/hide the panel
        this.triggers.on('mouseover.selectCheckboxMenu', function() {
            if(!$this.disabled&&!$this.triggers.hasClass('ui-state-focus')) {
                $this.jq.addClass('ui-state-hover');
                $this.triggers.addClass('ui-state-hover');
            }
        }).on('mouseout.selectCheckboxMenu', function() {
            if(!$this.disabled) {
                $this.jq.removeClass('ui-state-hover');
                $this.triggers.removeClass('ui-state-hover');
            }
        }).on('mousedown.selectCheckboxMenu', function(e) {
            if(!$this.disabled) {
                if($this.cfg.multiple && $(e.target).is('.ui-selectcheckboxmenu-token-icon')) {
                    return;
                }

                if($this.cfg.dynamic && !$this.isDynamicLoaded) {
                    $this._renderPanel();
                }

                if($this.panel.is(":hidden")) {
                    $this.show();
                }
                else {
                    $this.hide(true);
                }
            }
        }).on('click.selectCheckboxMenu', function(e) {
            $this.jq.removeClass('ui-state-hover');
            $this.triggers.removeClass('ui-state-hover');
            $this.keyboardTarget.trigger('focus');
            e.preventDefault();
        });

        if(this.cfg.multiple) {
            this.bindMultipleModeEvents();
        }

        //Client Behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.inputs, this.cfg.behaviors);
        }
    },

    bindPanelEvents: function() {
        var $this = this;

        //Events for checkboxes
        this.bindCheckboxHover(this.checkboxes);
        this.checkboxes.on('click.selectCheckboxMenu', function() {
            $this.toggleItem($(this));
        });

        //Toggler
        this.bindCheckboxHover(this.togglerBox);
        this.togglerBox.on('click.selectCheckboxMenu', function() {
            var el = $(this);
            if(el.hasClass('ui-state-active')) {
                $this.uncheckAll();
                el.addClass('ui-state-hover');
            }
            else {
                $this.checkAll();
                el.removeClass('ui-state-hover');
            }
        });

        //filter
        if(this.cfg.filter) {
            this.setupFilterMatcher();

            PrimeFaces.skinInput(this.filterInput);

            this.filterInput.on('keyup.selectCheckboxMenu', function() {
                $this.filter($(this).val());
            }).on('keydown.selectCheckboxMenu', function(e) {
                if(e.which === $.ui.keyCode.ESCAPE) {
                    $this.hide();
                }
            });
        }

        //Closer
        this.closer.on('mouseenter.selectCheckboxMenu', function(){
            $(this).addClass('ui-state-hover');
        }).on('mouseleave.selectCheckboxMenu', function() {
            $(this).removeClass('ui-state-hover');
        }).on('click.selectCheckboxMenu', function(e) {
            $this.hide(true);

            e.preventDefault();
        });

        //Labels
        this.labels.on('click.selectCheckboxMenu', function() {
            var checkbox = $(this).prev().children('.ui-chkbox-box');
            $this.toggleItem(checkbox);
            checkbox.removeClass('ui-state-hover');
            PrimeFaces.clearSelection();
        });

        PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $this.panel,
            function() { return $this.triggers; },
            function(e, eventTarget) {
                if(!($this.panel.is(eventTarget) || $this.panel.has(eventTarget).length > 0)) {
                    $this.hide(true);
                }
            });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.panel, function() {
            $this.alignPanel();
        });
    },

    bindKeyEvents: function() {
        var $this = this;

        this.keyboardTarget.on('focus.selectCheckboxMenu', function() {
            $this.jq.addClass('ui-state-focus');
            $this.menuIcon.addClass('ui-state-focus');
        }).on('blur.selectCheckboxMenu', function() {
            $this.jq.removeClass('ui-state-focus');
            $this.menuIcon.removeClass('ui-state-focus');
        }).on('keydown.selectCheckboxMenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if($this.cfg.dynamic && !$this.isDynamicLoaded) {
                $this._renderPanel();
            }

            switch(key) {
                case keyCode.ENTER:
                case keyCode.SPACE:
                    if ($this.panel.is(":hidden"))
                        $this.show();
                    else
                        $this.hide(true);

                    e.preventDefault();
                break;
                
                case keyCode.DOWN:
                    if (e.altKey) {
                        if ($this.panel.is(":hidden"))
                            $this.show();
                        else
                            $this.hide(true);
                    }

                    e.preventDefault();
                break;

                case keyCode.TAB:
                    if($this.panel.is(':visible')) {
                       if(!$this.cfg.showHeader) {
                            $this.itemContainer.children('li:not(.ui-state-disabled):first').find('div.ui-helper-hidden-accessible > input').trigger('focus');
                        }
                        else {
                            $this.toggler.find('> div.ui-helper-hidden-accessible > input').trigger('focus');
                        }
                        e.preventDefault();
                    }

                break;

                case keyCode.ESCAPE:
                    $this.hide();
                break;
            };
        });
    },

    bindPanelKeyEvents: function() {
        var $this = this;

        this.closer.on('focus.selectCheckboxMenu', function(e) {
            $this.closer.addClass('ui-state-focus');
        })
        .on('blur.selectCheckboxMenu', function(e) {
            $this.closer.removeClass('ui-state-focus');
        })
        .on('keydown.selectCheckboxMenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.ENTER:
                    $this.hide(true);

                    e.preventDefault();
                break;

                case keyCode.ESCAPE:
                    $this.hide();
                break;
            };
        });

        var togglerCheckboxInput = this.toggler.find('> div.ui-helper-hidden-accessible > input');
        this.bindCheckboxKeyEvents(togglerCheckboxInput);
        togglerCheckboxInput.on('keyup.selectCheckboxMenu', function(e) {
                    if(e.which === $.ui.keyCode.SPACE) {
                        var input = $(this);

                        if(input.prop('checked'))
                            $this.uncheckAll();
                        else
                            $this.checkAll();

                        e.preventDefault();
                    }
                })
                .on('change.selectCheckboxMenu', function(e) {
                    var input = $(this);

                    if(input.prop('checked')) {
                        $this.checkAll();
                    }
                    else {
                        $this.uncheckAll();
                    }
                });

        var itemKeyInputs = this.itemContainer.find('> li > div.ui-chkbox > div.ui-helper-hidden-accessible > input');
        this.bindCheckboxKeyEvents(itemKeyInputs);
        itemKeyInputs.on('keyup.selectCheckboxMenu', function(e) {
                    if(e.which === $.ui.keyCode.SPACE) {
                        var input = $(this),
                        box = input.parent().next();

                        if(input.prop('checked'))
                            $this.uncheck(box, true);
                        else
                            $this.check(box, true);

                        e.preventDefault();
                    }
                })
                .on('change.selectCheckboxMenu', function(e) {
                    var input = $(this),
                    box = input.parent().next();

                    if(input.prop('checked')) {
                        $this.check(box, true);
                    }
                    else {
                        $this.uncheck(box, true);
                    }
                });
    },

    bindMultipleModeEvents: function() {
        var $this = this;
        this.multiItemContainer = this.jq.children('.ui-selectcheckboxmenu-multiple-container');

        var closeSelector = '> li.ui-selectcheckboxmenu-token > .ui-selectcheckboxmenu-token-icon';
        this.multiItemContainer.off('click', closeSelector).on('click', closeSelector, null, function(e) {
            var item = $this.items.filter('[data-item-value="' + $(this).parent().data("item-value") +'"]');
            if(item && item.length) {
                if($this.cfg.dynamic && !$this.isDynamicLoaded) {
                    $this._renderPanel();
                }

                $this.uncheck(item.children('.ui-chkbox').children('.ui-chkbox-box'), true);
            }
            
            e.stopPropagation();
        });
    },

    bindCheckboxHover: function(item) {
        item.on('mouseenter.selectCheckboxMenu', function() {
            var item = $(this);
            if(!item.hasClass('ui-state-active')&&!item.hasClass('ui-state-disabled')) {
                item.addClass('ui-state-hover');
            }
        }).on('mouseleave.selectCheckboxMenu', function() {
            $(this).removeClass('ui-state-hover');
        });
    },

    filter: function(value) {
        var filterValue = this.cfg.caseSensitive ? $.trim(value) : $.trim(value).toLowerCase();

        if(filterValue === '') {
            this.itemContainer.children('li.ui-selectcheckboxmenu-item').filter(':hidden').show();
        }
        else {
            for(var i = 0; i < this.labels.length; i++) {
                var labelElement = this.labels.eq(i),
                item = labelElement.parent(),
                itemLabel = this.cfg.caseSensitive ? labelElement.text() : labelElement.text().toLowerCase();

                if(this.filterMatcher(itemLabel, filterValue)) {
                    item.show();
                }
                else {
                    item.hide();
                }
            }
        }

        var groupHeaderLength = this.groupHeaders.length;
        for(var i = 0; i < groupHeaderLength; i++) {
            var header = $(this.groupHeaders[i]),
            groupedItems = header.nextUntil('li.ui-selectcheckboxmenu-item-group');

            if(groupedItems.length === groupedItems.filter(':hidden').length) {
                header.hide();
            }
            else {
                header.show();
            }
        }

        if(this.cfg.scrollHeight) {
            if(this.itemContainer.height() < this.cfg.initialHeight) {
                this.itemContainerWrapper.css('height', 'auto');
            }
            else {
                this.itemContainerWrapper.height(this.cfg.initialHeight);
            }
        }

        this.updateToggler();
        this.alignPanel();
    },

    setupFilterMatcher: function() {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode||'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            ,'contains': this.containsFilter
            ,'endsWith': this.endsWithFilter
            ,'custom': this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    },

    startsWithFilter: function(value, filter) {
        return value.indexOf(filter) === 0;
    },

    containsFilter: function(value, filter) {
        return value.indexOf(filter) !== -1;
    },

    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    checkAll: function() {
        for(var i = 0; i < this.items.length; i++) {
            var el = this.items.eq(i);

            if(el.is(':visible')) {
                var input = this.inputs.eq(i);
                var inputNative = input[0];

                if(!inputNative.disabled) {
                    input.prop('checked', true).attr('aria-checked', true);
                    this.check(el.children('.ui-chkbox').children('.ui-chkbox-box'));

                    if (this.cfg.multiple) {
                        this.createMultipleItem(el);
                    }
                }
            }
        }

        this.check(this.togglerBox);

        var togglerInput = this.togglerBox.prev().children('input');
        if(this.cfg.onChange) {
            this.cfg.onChange.call(this);
        }

        if(!this.togglerBox.hasClass('ui-state-disabled')) {
            togglerInput.trigger('focus.selectCheckboxMenu');
            this.togglerBox.addClass('ui-state-active');
        }

        if(this.cfg.multiple) {
            this.alignPanel();
        }

        this.fireToggleSelectEvent(true);
    },

    uncheckAll: function() {
        for(var i = 0; i < this.items.length; i++) {
            var el = this.items.eq(i);

            if(el.is(':visible')) {
                var input = this.inputs.eq(i);
                var inputNative = input[0];

                if(!inputNative.disabled) {
                    this.inputs.eq(i).prop('checked', false).attr('aria-checked', false);
                    this.uncheck(el.children('.ui-chkbox').children('.ui-chkbox-box'));

                    if (this.cfg.multiple) {
                        this.multiItemContainer.children().remove();
                    }
                }
            }
        }

        this.uncheck(this.togglerBox);

        var togglerInput = this.togglerBox.prev().children('input');
        if(this.cfg.onChange) {
            this.cfg.onChange.call(this);
        }

        if(!this.togglerBox.hasClass('ui-state-disabled')) {
            togglerInput.trigger('focus.selectCheckboxMenu');
        }

        if(this.cfg.multiple) {
            this.alignPanel();
        }

        this.fireToggleSelectEvent(false);
    },

    fireToggleSelectEvent: function(checked) {
        if(this.hasBehavior('toggleSelect')) {
            var ext = {
                params: [{name: this.id + '_checked', value: checked}]
            };

            this.callBehavior('toggleSelect', ext);
        }
    },

    check: function(checkbox, updateInput) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            var checkedInput = checkbox.prev().children('input'),
            item = checkbox.closest('li.ui-selectcheckboxmenu-item');

            checkedInput.prop('checked', true).attr('aria-checked', true);
            if(updateInput) {
                checkedInput.trigger('focus.selectCheckboxMenu');
            }

            checkbox.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
            item.removeClass('ui-selectcheckboxmenu-unchecked').addClass('ui-selectcheckboxmenu-checked');

            if(updateInput) {
            	var itemGroups = item.prevAll('li.ui-selectcheckboxmenu-item-group'),
                input = this.inputs.eq(item.index() - itemGroups.length);
                input.prop('checked', true).attr('aria-checked', true).change();

                this.updateToggler();

                if(this.cfg.multiple) {
                    this.createMultipleItem(item);
                    this.alignPanel();
                }
            }

            if(this.cfg.updateLabel) {
                this.updateLabel();
            }
        }
    },

    uncheck: function(checkbox, updateInput) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            var uncheckedInput = checkbox.prev().children('input'),
            item = checkbox.closest('li.ui-selectcheckboxmenu-item');
            checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
            checkbox.closest('li.ui-selectcheckboxmenu-item').addClass('ui-selectcheckboxmenu-unchecked').removeClass('ui-selectcheckboxmenu-checked');
            uncheckedInput.prop('checked', false).attr('aria-checked', false);

            if(updateInput) {
                var itemGroups = item.prevAll('li.ui-selectcheckboxmenu-item-group'),
                input = this.inputs.eq(item.index() - itemGroups.length);
                input.prop('checked', false).attr('aria-checked', false).change();
                uncheckedInput.trigger('focus.selectCheckboxMenu');
                this.updateToggler();

                if(this.cfg.multiple) {
                    this.removeMultipleItem(item);
                    this.alignPanel();
                }
            }

            if(this.cfg.updateLabel) {
                this.updateLabel();
            }
        }
    },

    show: function() {
        this.alignPanel();
        this.keyboardTarget.attr('aria-expanded', true);
        this.panel.show();

        this.postShow();
    },

    hide: function(animate) {
        var $this = this;

        this.keyboardTarget.attr('aria-expanded', false);

        if(animate) {
            this.panel.fadeOut('fast', function() {
                $this.postHide();
            });
        }

        else {
            this.panel.hide();
            this.postHide();
        }
    },

    postShow: function() {
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },

    postHide: function() {
        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    },

    alignPanel: function() {
        var fixedPosition = this.panel.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null,
        panelStyle = this.panel.attr('style');

        this.panel.css({
                'left':'',
                'top':'',
                'z-index': ++PrimeFaces.zindex
        });

        if(this.panel.parent().attr('id') === this.id) {
            this.panel.css({
                left: 0,
                top: this.jq.innerHeight()
            });
        }
        else {
            this.panel.position({
                                my: 'left top'
                                ,at: 'left bottom'
                                ,of: this.jq
                                ,offset : positionOffset
                                ,collision: 'flipfit'
                            });
        }

        if(!this.widthAligned && (this.panel.width() < this.jq.width()) && (!panelStyle||panelStyle.toLowerCase().indexOf('width') === -1)) {
            this.panel.width(this.jq.width());
            this.widthAligned = true;
        }
    },

    toggleItem: function(checkbox) {
        if(!checkbox.hasClass('ui-state-disabled')) {
            if(checkbox.hasClass('ui-state-active')) {
                this.uncheck(checkbox, true);
                checkbox.addClass('ui-state-hover');
            }
            else {
                this.check(checkbox, true);
                checkbox.removeClass('ui-state-hover');
            }
        }
    },

    updateToggler: function() {
        var visibleItems = this.itemContainer.children('li.ui-selectcheckboxmenu-item:visible');

        if(visibleItems.length && visibleItems.filter('.ui-selectcheckboxmenu-unchecked').length === 0) {
            this.check(this.togglerBox);
        }
        else {
            this.uncheck(this.togglerBox);
        }
    },

    bindCheckboxKeyEvents: function(items) {
        var $this = this;

        items.on('focus.selectCheckboxMenu', function(e) {
            var input = $(this),
            box = input.parent().next();

            box.addClass('ui-state-focus');

            PrimeFaces.scrollInView($this.itemContainerWrapper, box);
        })
        .on('blur.selectCheckboxMenu', function(e) {
            var input = $(this),
            box = input.parent().next();

            box.removeClass('ui-state-focus');
        })
        .on('keydown.selectCheckboxMenu', function(e) {
            var keyCode = $.ui.keyCode,
            key = e.which;

            if(key === keyCode.SPACE) {
                e.preventDefault();
            }
            else if(key === keyCode.ESCAPE) {
                $this.hide();
            }
        });
    },

    updateLabel: function() {
        var checkedItems = this.jq.find(':checked'),
            labelText = '';

        if(checkedItems && checkedItems.length) {
            for(var i = 0; i < checkedItems.length; i++) {
                if(i != 0) {
                    labelText = labelText + this.cfg.labelSeparator;
                }
                labelText = labelText + $(checkedItems[i]).next().text();
            }
        }
        else {
            if (this.cfg.emptyLabel) {
                labelText = this.cfg.emptyLabel;
            } else {
                labelText = this.defaultLabel;
            }
        }

        this.label.text(labelText);
        this.labelContainer.attr('title', labelText);
    },

    createMultipleItem: function(item) {
        var items = this.multiItemContainer.children();
        if(items.length && items.filter('[data-item-value="' + item.data('item-value') + '"]').length > 0) {
            return;
        }

        var itemGroups = item.prevAll('li.ui-selectcheckboxmenu-item-group'),
        input = this.inputs.eq(item.index() - itemGroups.length),
        escaped = input.data('escaped'),
        labelHtml = input.next().html().trim(),
        labelLength = labelHtml.length,
        label = labelLength > 0 && labelHtml !== '&nbsp;' ? (escaped ? PrimeFaces.escapeHTML(input.next().text()) : input.next().html()) : PrimeFaces.escapeHTML(input.val()),
        itemDisplayMarkup = '<li class="ui-selectcheckboxmenu-token ui-state-active ui-corner-all" data-item-value="' + PrimeFaces.escapeHTML(input.val()) +'">';
        itemDisplayMarkup += '<span class="ui-selectcheckboxmenu-token-icon ui-icon ui-icon-close" />';
        itemDisplayMarkup += '<span class="ui-selectcheckboxmenu-token-label">' + label + '</span></li>';

        this.multiItemContainer.append(itemDisplayMarkup);
    },

    removeMultipleItem: function(item) {
        var items = this.multiItemContainer.children();
        if(items.length) {
            items.filter('[data-item-value="' + item.data('item-value') + '"]').remove();
        }
    },

    selectValue: function(value) {                                                                     // Patch
        var idx = -1;
        // find input-index
        for(var i = 0; i < this.inputs.length; i++) {
            if (this.inputs.eq(i).val() === value) {
                idx = i;
                break;
            }
        }
        if (idx === -1) {
            return;
        }
        var input = this.inputs.eq(idx);   // the hidden input
        var item  = this.items.eq(idx);    // the Overlay-Panel-Item (li)

        // check (see this.checkAll())
        input.prop('checked', true).attr('aria-checked', true);
        this.check(item.children('.ui-chkbox').children('.ui-chkbox-box'));

        if(this.cfg.multiple) {
            this.createMultipleItem(item);
        }
    }

});
