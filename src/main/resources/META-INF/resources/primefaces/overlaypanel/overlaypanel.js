/**
 * __PrimeFaces OverlayPanel Widget__
 * 
 * OverlayPanel is a generic panel component that can be displayed on top of other content.
 * 
 * @prop {JQuery} closerIcon The DOM element for the icon that closes the overlay panel.
 * @prop {JQuery} content The DOM element for the content of the overlay panel.
 * @prop {boolean} loaded When dynamic loading is enabled, whether the content was already loaded.
 * @prop {number} showTimeout The set-timeout timer ID of the timer used for showing the overlay panel.
 * @prop {JQuery} target The DOM element for the target component that triggers this overlay panel.
 * @prop {JQuery} targetElement The DOM element for the resolved target component that triggers this overlay panel.
 * @prop {number} targetZindex The z-index of the target component that triggers this overlay panel.
 * 
 * @interface {PrimeFaces.widget.OverlayPanelCfg} cfg The configuration for the {@link  OverlayPanel| OverlayPanel widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DynamicOverlayWidgetCfg} cfg
 * 
 * @prop {string} cfg.appendTo Appends the overlayPanel to the given search expression.
 * @prop {string} cfg.at Position of the target relative to the panel.
 * @prop {boolean} cfg.dynamic `true` to load the content via AJAX when the overlay panel is opened, `false` to load
 * the content immediately.
 * @prop {string} cfg.hideEvent Event on target to hide the panel.
 * @prop {string} cfg.collision When the positioned element overflows the window in some direction, move it to an
 * alternative position. Similar to my and at, this accepts a single value or a pair for horizontal/vertical, e.g.,
 * `flip`, `fit`, `fit flip`, `fit none`.
 * @prop {boolean} cfg.dismissable When set `true`, clicking outside of the panel hides the overlay.
 * @prop {boolean} cfg.modal Specifies whether the document should be shielded with a partially transparent mask to
 * require the user to close the panel before being able to activate any elements in the document.
 * @prop {string} cfg.my Position of the panel relative to the target.
 * @prop {} cfg.onHide Client side callback to execute when panel is shown.
 * @prop {} cfg.onShow Client side callback to execute when panel is hidden.
 * @prop {boolean} cfg.showCloseIcon Displays a close icon to hide the overlay, default is `false`.
 * @prop {number} cfg.showDelay Delay in milliseconds applied when the overlay panel is shown.
 * @prop {string} cfg.showEvent Event on target to hide the panel.
 * @prop {string} cfg.target Search expression for target component to display panel next to.
 */
PrimeFaces.widget.OverlayPanel = PrimeFaces.widget.DynamicOverlayWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.content = this.jq.children('div.ui-overlaypanel-content');

        //configuration
        this.cfg.my = this.cfg.my||'left top';
        this.cfg.at = this.cfg.at||'left bottom';
        this.cfg.collision = this.cfg.collision||'flip';
        this.cfg.showEvent = this.cfg.showEvent||'click.ui-overlaypanel';
        this.cfg.hideEvent = this.cfg.hideEvent||'click.ui-overlaypanel';
        this.cfg.dismissable = (this.cfg.dismissable === false) ? false : true;
        this.cfg.showDelay = this.cfg.showDelay || 0;

        if(this.cfg.showCloseIcon) {
            this.closerIcon = $('<a href="#" class="ui-overlaypanel-close ui-state-default"><span class="ui-icon ui-icon-closethick"></span></a>')
                              .attr('aria-label', PrimeFaces.getAriaLabel('overlaypanel.CLOSE')).appendTo(this.jq);
        }

        this.bindCommonEvents();

        if(this.cfg.target) {
            this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);
            this.bindTargetEvents();

            // set aria attributes
            this.target.attr({
                'aria-expanded': false,
                'aria-controls': this.id
            });

            //dialog support
            this.setupDialogSupport();
        }
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this._super(cfg);

        // fix #4307
        this.loaded = false;

        // see #setupDialogSupport
        if (!this.cfg.appendTo) {
            PrimeFaces.utils.removeDynamicOverlay(this, this.jq, this.id, $(document.body));
        }
    },

    /**
     * @override
     * @inheritdoc
     */
    destroy: function() {
        this._super();

        // see #setupDialogSupport
        if (!this.cfg.appendTo) {
            PrimeFaces.utils.removeDynamicOverlay(this, this.jq, this.id, $(document.body));
        }
    },

    /**
     * Sets up the event listeners for the target component that triggers this overlay panel.
     * @private
     */
    bindTargetEvents: function() {
        var $this = this;

        //mark target and descandants of target as a trigger for a primefaces overlay
        this.target.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);

        //show and hide events for target
        if(this.cfg.showEvent === this.cfg.hideEvent) {
            var event = this.cfg.showEvent;

            this.target.on(event, function(e) {
                $this.toggle();
            });
        }
        else {
            var showEvent = this.cfg.showEvent + '.ui-overlaypanel',
            hideEvent = this.cfg.hideEvent + '.ui-overlaypanel';

            this.target.off(showEvent + ' ' + hideEvent).on(showEvent, function(e) {
                if(!$this.isVisible()) {
                    $this.show();
                    if(showEvent === 'contextmenu.ui-overlaypanel') {
                        e.preventDefault();
                    }
                }
            })
            .on(hideEvent, function(e) {
            	clearTimeout($this.showTimeout);
                if($this.isVisible()) {
                    $this.hide();
                }
            });
        }

        $this.target.off('keydown.ui-overlaypanel keyup.ui-overlaypanel')
        .on('keydown.ui-overlaypanel', PrimeFaces.utils.blockEnterKey)
        .on('keyup.ui-overlaypanel', function(e) {
            var keyCode = $.ui.keyCode, key = e.which;

            if(key === keyCode.ENTER) {
                $this.toggle();
                e.preventDefault();
            }
        });

    },

    /**
     * Sets up some common event listeners always required by this widget.
     * @private
     */
    bindCommonEvents: function() {
        var $this = this;

        if(this.cfg.showCloseIcon) {
            this.closerIcon.on('mouseover.ui-overlaypanel', function() {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseout.ui-overlaypanel', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.ui-overlaypanel', function(e) {
                $this.hide();
                e.preventDefault();
            })
            .on('focus.ui-overlaypanel', function() {
                $(this).addClass('ui-state-focus');
            })
            .on('blur.ui-overlaypanel', function() {
                $(this).removeClass('ui-state-focus');
            });
        }

        //hide overlay when mousedown is at outside of overlay
        if(this.cfg.dismissable && !this.cfg.modal) {
            PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', $this.jq,
                function() { return $this.target; },
                function(e, eventTarget) {
                    if(!($this.jq.is(eventTarget) || $this.jq.has(eventTarget).length > 0 || eventTarget.closest('.ui-input-overlay').length > 0)) {
                        $this.hide();
                    }
                });
        }

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.jq, function() {
            $this.align();
        });
        PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.id + '_align', function() {
            $this.align();
        });
    },

    /**
     * Brings up the overlay panel if it is currently hidden, or hides it if it is currently displayed.
     */
    toggle: function() {
        if(!this.isVisible()) {
            this.show();
        }
        else {
            clearTimeout(this.showTimeout);
            this.hide();
        }
    },

    /**
     * Brings up the overlay panel so that is displayed and visible.
     * @param {string | JQuery} [target] ID or DOM element of the target component that triggers this overlay panel.
     */
    show: function(target) {
    	var thisPanel = this;
        this.showTimeout = setTimeout(function() {
            if (!thisPanel.loaded && thisPanel.cfg.dynamic) {
                thisPanel.loadContents(target);
            }
            else {
                thisPanel._show(target);
            }
        }, this.cfg.showDelay);
    },

    /**
     * Makes the overlay panel visible.
     * @private
     * @param {string | JQuery} [target] ID or DOM element of the target component that triggers this overlay panel.
     */
    _show: function(target) {
        var $this = this;

        this.align(target);

        //replace visibility hidden with display none for effect support, toggle marker class
        this.jq.removeClass('ui-overlay-hidden').addClass('ui-overlay-visible').css({
            'display':'none'
        });

        if(this.cfg.showEffect) {
            this.jq.show(this.cfg.showEffect, {}, 200, function() {
                $this.postShow();
            });
        }
        else {
            this.jq.show();
            this.postShow();
        }

        if(this.cfg.modal) {
            this.enableModality();
        }
    },

    /**
     * Aligns the overlay panel so that it is shown at the correct position.
     * @private
     * @param {string | JQuery} [target] ID or DOM element of the target component that triggers this overlay panel.
     */
    align: function(target) {
        var win = $(window),
        allowedNegativeValuesByParentOffset = this.jq.offsetParent().offset(),
        $this = this;

        if (target) {
            if (typeof target === 'string') {
                this.targetElement = $(document.getElementById(target));
            }
            else if (target instanceof $) {
                this.targetElement = target;
            }
        }
        else if (this.target) {
            this.targetElement = this.target;
        }

        if (this.targetElement) {
            this.targetZindex = this.targetElement.zIndex();
        }

        this.jq.css({'left':'', 'top':'', 'z-index': PrimeFaces.nextZindex()})
                .position({
                    my: this.cfg.my
                    ,at: this.cfg.at
                    ,of: this.targetElement
                    ,collision: this.cfg.collision
                    ,using: function(pos, info) {
                        if(pos.top < -allowedNegativeValuesByParentOffset.top) {
                            pos.top = -allowedNegativeValuesByParentOffset.top;
                        }
                        
                        if(pos.left < -allowedNegativeValuesByParentOffset.left) {
                            pos.left = -allowedNegativeValuesByParentOffset.left;
                        }
                        
                        $this.jq.css(pos);
                    }
                });

        var widthOffset = this.jq.width() - this.content.width();
        this.jq.css('max-width', win.width() - widthOffset + 'px');
    },

    /**
     * Hides this overlay panel so that it is not displayed anymore.
     */
    hide: function() {
        var $this = this;

        if(this.cfg.hideEffect) {
            this.jq.hide(this.cfg.hideEffect, {}, 200, function() {
                if($this.cfg.modal) {
                    $this.disableModality();
                }
                $this.postHide();
            });
        }
        else {
            this.jq.hide();
            if($this.cfg.modal) {
                $this.disableModality();
            }
            this.postHide();
        }
    },

    /**
     * Callback that is invoked after this overlay panel was opened.
     * @private
     */
    postShow: function() {

        this.callBehavior('show');

        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }

        this.applyFocus();

        if (this.target) {
            this.target.attr('aria-expanded', true);
        }
    },

    /**
     * Callback that is invoked after this overlay panel was closed.
     * @private
     */
    postHide: function() {
        //replace display block with visibility hidden for hidden container support, toggle marker class
        this.jq.removeClass('ui-overlay-visible').addClass('ui-overlay-hidden').css({
            'display':'block'
        });

        this.callBehavior('hide');

        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }

        if (this.target) {
            this.target.attr('aria-expanded', false);
        }
    },

    /**
     * In case this overlay panel is inside a dialog widget, applies some CSS fixes so that this overlay panel is above
     * the dialog-
     * @private
     */
    setupDialogSupport: function() {
        var dialog = this.target[0].closest('.ui-dialog');
        if (dialog) {
            var $dialog = $(dialog);
            if ($dialog.length == 1) {
                //set position as fixed to scroll with dialog
                if($dialog.css('position') === 'fixed') {
                    this.jq.css('position', 'fixed');
                }

                //append to body if not already appended by user choice
                if(!this.cfg.appendTo) {
                    this.jq.appendTo(document.body);
                }
            }
        }
    },

    /**
     * Loads the contents of this overlay panel dynamically via AJAX, if dynamic loading is enabled.
     * @private
     * @param {string | JQuery} [target] ID or DOM element of the target component that triggers this overlay panel.
     */
    loadContents: function(target) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                            this.loaded = true;
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this._show(target);
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    /**
     * Checks whether this overlay panel is currently visible.
     * @return {boolean} `true` if this overlay panel is currently displayed, or `false` otherwise.
     */
    isVisible: function() {
        return this.jq.hasClass('ui-overlay-visible');
    },

    /**
     * Applies focus to the first focusable element of the content in the panel.
     */
    applyFocus: function() {
        this.jq.find(':not(:submit):not(:button):input:visible:enabled:first').trigger('focus');
    },

    /**
     * @override
     * @inheritdoc
     */
    enableModality: function() {
        this._super();

        if(this.targetElement) {
            this.targetElement.css('z-index', String(this.jq.css('z-index')));
        }
    },

    /**
     * @override
     * @inheritdoc
     */
    disableModality: function(){
        this._super();

        if(this.targetElement) {
            this.targetElement.css('z-index', String(this.targetZindex));
        }
    },

    /**
     * @override
     * @inheritdoc
     * @return {JQuery}
     */
    getModalTabbables: function(){
        var tabbables = this.jq.find(':tabbable');

        if (this.targetElement && this.targetElement.is(':tabbable')) {
            tabbables = tabbables.add(this.targetElement);
        }

        return tabbables;
    }
});
