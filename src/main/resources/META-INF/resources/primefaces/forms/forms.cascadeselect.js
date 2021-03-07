/**
 * __PrimeFaces CascadeSelect Widget__
 * 
 * CascadeSelect CascadeSelect displays a nested structure of options.
 * 
 * @prop {JQuery} contents The DOM element for the content in the available selectable options.
 * @prop {JQuery} input The DOM element for the hidden input with the current value.
 * @prop {JQuery} items The DOM elements for the the available selectable options.
 * @prop {JQuery} itemsWrapper The DOM element for the wrapper with the container with the available selectable
 * options.
 * @prop {JQuery} label The DOM element for the label indicating the currently selected option.
 * @prop {JQuery} panel The DOM element for the overlay panel with the available selectable options.
 * @prop {JQuery} triggers The DOM elements for the buttons that can trigger (hide or show) the overlay panel with the
 * available selectable options.
 * 
 * @interface {PrimeFaces.widget.CascadeSelectCfg} cfg The configuration for the {@link  CascadeSelect| CascadeSelect widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.appendTo Appends the overlay to the element defined by search expression. Defaults to the document
 * body.
 * @prop {boolean} cfg.disabled If true, disables the component.
 */
PrimeFaces.widget.CascadeSelect = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        
        this.input = $(this.jqId + '_input');
        this.label = this.jq.children('.ui-cascadeselect-label');
        this.triggers = this.jq.children('.ui-cascadeselect-trigger').add(this.label);
        this.panel = $(this.jqId + '_panel');
        this.itemsWrapper = this.panel.children('.ui-cascadeselect-items-wrapper');
        this.items = this.itemsWrapper.find('li.ui-cascadeselect-item');
        this.contents = this.items.children('.ui-cascadeselect-item-content');
        this.cfg.disabled = this.jq.hasClass('ui-state-disabled');
        this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this, this.panel);
        
        if (!this.cfg.disabled) {
            this.bindEvents();
        
            PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');
            this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'ui-connected-overlay');
        }
    },
    
    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;
    
        this.triggers.off('click.cascadeselect').on('click.cascadeselect', function(e) {
            if ($this.panel.is(':hidden')) {
                $this.show();
            }
            else {
                $this.hide();
            }

            $this.input.trigger('focus.cascadeselect');
            e.preventDefault();
        });
    
        this.input.off('focus.cascadeselect blur.cascadeselect keydown.cascadeselect')
            .on('focus.cascadeselect', function() {
                $this.jq.addClass('ui-state-focus');
            })
            .on('blur.cascadeselect', function(){
                $this.jq.removeClass('ui-state-focus');
            })
            .on('keydown.cascadeselect', function(e) {
                var keyCode = $.ui.keyCode,
                key = e.which;
        
                switch(key) {
                    case keyCode.DOWN:
                        if ($this.panel.is(':visible')) {
                            $this.panel.find('.ui-cascadeselect-item:first > .ui-cascadeselect-item-content').focus();
                        }
                        else if (e.altKey) {
                            $this.show();
                        }
                        e.preventDefault();
                        break;
        
                    case keyCode.ESCAPE:
                        if ($this.panel.is(':visible')) {
                            $this.hide();
                            e.preventDefault();
                        }
                        break;
        
                    case keyCode.TAB:
                        $this.hide();
                        break;
        
                    default:
                        break;
                }
            });
    
        this.contents.off('click.cascadeselect keydown.cascadeselect')
            .on('click.cascadeselect', function(e) {
                var item = $(this).parent();
                var subpanel = item.children('.ui-cascadeselect-panel');

                $this.deactivateItems(item);
                item.addClass('ui-cascadeselect-item-active ui-state-highlight');
                
                if (subpanel.length > 0) {
                    var parentPanel = item.closest('.ui-cascadeselect-panel');
                    $this.alignSubPanel(subpanel, parentPanel);
                    subpanel.show();
                }
                else {
                    $this.input.val(item.attr('data-value'));
                    $this.label.text(item.attr('data-label'));
                    $this.callBehavior('itemSelect');
                    $this.hide();
                    e.stopPropagation();
                }
            })
            .on('keydown.cascadeselect', function(e) {
                var item = $(this).parent();
                var keyCode = $.ui.keyCode,
                key = e.which;
        
                switch(key) {
                    case keyCode.DOWN:
                        var nextItem = item.next();
                        if (nextItem) {
                            nextItem.children('.ui-cascadeselect-item-content').focus();
                        }
                        break;
        
                    case keyCode.UP:
                        var prevItem = item.prev();
                        if (prevItem) {
                            prevItem.children('.ui-cascadeselect-item-content').focus();
                        }
                        break;
        
                    case keyCode.RIGHT:
                        if (item.hasClass('ui-cascadeselect-item-group')) {
                            if (item.hasClass('ui-cascadeselect-item-active')) {
                                item.find('> .ui-cascadeselect-panel > .ui-cascadeselect-item:first > .ui-cascadeselect-item-content').focus();
                            }
                            else {
                                item.children('.ui-cascadeselect-item-content').trigger('click.cascadeselect');
                            }
                        }
                        break;
        
                    case keyCode.LEFT:
                        $this.hideGroup(item);
                        $this.hideGroup(item.siblings('.ui-cascadeselect-item-active'));

                        var parentItem = item.parent().closest('.ui-cascadeselect-item');
                        if (parentItem) {
                            parentItem.children('.ui-cascadeselect-item-content').focus();
                        }
                        break;
        
                    case keyCode.ENTER:
                        item.children('.ui-cascadeselect-item-content').trigger('click.cascadeselect');
                        if (!item.hasClass('ui-cascadeselect-item-group')) {
                            $this.input.trigger('focus.cascadeselect');
                        }
                        break;
        
                    default:
                        break;
                }
        
                e.preventDefault();
            });
    },

    /**
     * Removes some event listeners when this widget was disabled.
     * @private
     */
    unbindEvents: function() {
        this.contents.off();
        this.triggers.off();
        this.input.off();
    },

    /**
     * Disables this widget so that the user cannot select any option.
     */
    disable: function() {
        if (!this.cfg.disabled) {
            this.cfg.disabled = true;
            this.jq.addClass('ui-state-disabled');
            this.input.attr('disabled', 'disabled');
            this.unbindEvents();
        }
    },

    /**
     * Enables this widget so that the user can select an option.
     */
    enable: function() {
        if (this.cfg.disabled) {
            this.cfg.disabled = false;
            this.jq.removeClass('ui-state-disabled');
            this.input.removeAttr('disabled');
            this.bindEvents();
        }
    },

    /**
     * Deactivate siblings and active children of an item.
     * @private
     * @param {JQuery} item Cascade select panel element.
     */
    deactivateItems: function(item) {
        var parentItem = item.parent().parent();
        var siblings = item.siblings('.ui-cascadeselect-item-active');

        this.hideGroup(siblings);
        this.hideGroup(siblings.find('.ui-cascadeselect-item-active'));
        
        if (!parentItem.is(this.itemsWrapper)) {
            this.deactivateItems(parentItem);
        }
    },

    /**
     * Sets up all panel event listeners
     * @private
     */
    bindPanelEvents: function() {
        var $this = this;

        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.panel,
            function() { return  $this.triggers },
            function(e, eventTarget) {
                if(!($this.panel.is(eventTarget) || $this.panel.has(eventTarget).length > 0)) {
                    $this.hide();
                }
            });

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.panel, function() {
            $this.hide();
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, function() {
            $this.hide();
        });
    },

    /**
     * Unbind all panel event listeners
     * @private
     */
    unbindPanelEvents: function() {
        if (this.hideOverlayHandler) {
            this.hideOverlayHandler.unbind();
        }

        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }
    
        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    },

    /**
     * Brings up the overlay panel with the available options.
     */
    show: function() {
        var $this = this;

        if (this.transition) {
            this.transition.show({
                onEnter: function() {
                    $this.panel.css('z-index', PrimeFaces.nextZindex());
                    $this.alignPanel();
                },
                onEntered: function() {
                    $this.input.attr('aria-expanded', true);
                    $this.bindPanelEvents();
                }
            });
        }
    },

    /**
     * Hides the panel of a group item.
     * @param {JQuery} item Dom element of the cascadeselect.
     */
    hideGroup: function(item) {
        item.removeClass('ui-cascadeselect-item-active ui-state-highlight').children('.ui-cascadeselect-panel').hide();
    },

    /**
     * Hides the overlay panel with the available options.
     */
    hide: function() {
        if (this.panel.is(':visible') && this.transition) {
            var $this = this;

            this.transition.hide({
                onExit: function() {
                    $this.unbindPanelEvents();
                },
                onExited: function() {
                    $this.panel.css('z-index', '');
                    $this.input.attr('aria-expanded', false);
                }
            });
        }
    },

    /**
     * Adjust the width of the overlay panel.
     * @private 
     */
    alignPanelWidth: function() {
        //align panel and container
        if (this.cfg.appendTo) {
            this.panel.css('min-width', this.jq.outerWidth());
        }
    },

    /**
     * Align the overlay panel with the available options.
     * @private
     */
    alignPanel: function() {
        this.alignPanelWidth();

        if (this.panel.parent().is(this.jq)) {
            this.panel.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px',
                'transform-origin': 'center top'
            });
        }
        else {
            this.panel.css({left:'0px', top:'0px', 'transform-origin': 'center top'}).position({
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.jq
                ,collision: 'flipfit'
                ,using: function(pos, directions) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }
    },

    /**
     * Align the sub overlay panel with the available options.
     * @private
     * @param {JQuery} subpanel Sub panel element of the cascade select panel.
     * @param {JQuery} parentPanel Parent panel element of the sub panel element.
     */
    alignSubPanel: function(subpanel, parentPanel) {
        var subitemWrapper = subpanel.children('.ui-cascadeselect-items-wrapper');
        subpanel.css({'display':'block', 'opacity':'0', 'pointer-events': 'none'});
        subitemWrapper.css({'overflow': 'scroll'});

        subpanel.css({left:'0px', top:'0px'}).position({
                my: 'left top'
                ,at: 'right top'
                ,of: parentPanel
                ,collision: 'flipfit'
            });

        subpanel.css({'display':'none', 'opacity':'', 'pointer-events': '', 'z-index': PrimeFaces.nextZindex()});
        subitemWrapper.css({'overflow': ''});
    }
});
