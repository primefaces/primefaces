/**
 * PrimeFaces OverlayPanel Widget
 */
PrimeFaces.widget.OverlayPanel = PrimeFaces.widget.DynamicOverlayWidget.extend({

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

    //@Override
    refresh: function(cfg) {
        this._super(cfg);

        // fix #4307
        this.loaded = false;

        // see #setupDialogSupport
        if (!this.cfg.appendTo) {
            PrimeFaces.utils.removeDynamicOverlay(this, this.jq, this.id, $(document.body));
        }
    },

    //@Override
    destroy: function() {
        this._super();

        // see #setupDialogSupport
        if (!this.cfg.appendTo) {
            PrimeFaces.utils.removeDynamicOverlay(this, this.jq, this.id, $(document.body));
        }
    },

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

        $this.target.off('keydown.ui-overlaypanel keyup.ui-overlaypanel').on('keydown.ui-overlaypanel', function(e) {
            var keyCode = $.ui.keyCode, key = e.which;

            if(key === keyCode.ENTER) {
                e.preventDefault();
            }
        })
        .on('keyup.ui-overlaypanel', function(e) {
            var keyCode = $.ui.keyCode, key = e.which;

            if(key === keyCode.ENTER) {
                $this.toggle();
                e.preventDefault();
            }
        });

    },

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
    },

    toggle: function() {
        if(!this.isVisible()) {
            this.show();
        }
        else {
            clearTimeout(this.showTimeout);
            this.hide();
        }
    },

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

    align: function(target) {
        var win = $(window);

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

        this.jq.css({'left':'', 'top':'', 'z-index': ++PrimeFaces.zindex})
                .position({
                    my: this.cfg.my
                    ,at: this.cfg.at
                    ,of: this.targetElement
                    ,collision: this.cfg.collision
                });

        var widthOffset = this.jq.width() - this.content.width();
        this.jq.css('max-width', win.width() - widthOffset + 'px');
    },

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

    postShow: function() {
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }

        this.applyFocus();

        if (this.target) {
            this.target.attr('aria-expanded', true);
        }
    },

    postHide: function() {
        //replace display block with visibility hidden for hidden container support, toggle marker class
        this.jq.removeClass('ui-overlay-visible').addClass('ui-overlay-hidden').css({
            'display':'block'
        });

        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }

        if (this.target) {
            this.target.attr('aria-expanded', false);
        }
    },

    setupDialogSupport: function() {
        var dialog = this.target.closest('.ui-dialog');

        if(dialog.length == 1) {
            //set position as fixed to scroll with dialog
            if(dialog.css('position') === 'fixed') {
                this.jq.css('position', 'fixed');
            }

            //append to body if not already appended by user choice
            if(!this.cfg.appendTo) {
                this.jq.appendTo(document.body);
            }
        }
    },

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

    isVisible: function() {
        return this.jq.hasClass('ui-overlay-visible');
    },

    applyFocus: function() {
        this.jq.find(':not(:submit):not(:button):input:visible:enabled:first').focus();
    },

    //@override
    enableModality: function() {
        this._super();

        if(this.targetElement) {
            this.targetElement.css('z-index', this.jq.css('z-index'));
        }
    },

    //@override
    disableModality: function(){
        this._super();

        if(this.targetElement) {
            this.targetElement.css('z-index', this.targetZindex);
        }
    },

    //@override
    getModalTabbables: function(){
        var tabbables = this.jq.find(':tabbable');

        if (this.targetElement && this.targetElement.is(':tabbable')) {
            tabbables = tabbables.add(this.targetElement);
        }

        return tabbables;
    }
});
