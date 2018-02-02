/**
 * PrimeFaces OverlayPanel Widget
 */
PrimeFaces.widget.OverlayPanel = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.content = this.jq.children('div.ui-overlaypanel-content')

        //configuration
        this.cfg.my = this.cfg.my||'left top';
        this.cfg.at = this.cfg.at||'left bottom';
        this.cfg.showEvent = this.cfg.showEvent||'click.ui-overlaypanel';
        this.cfg.hideEvent = this.cfg.hideEvent||'click.ui-overlaypanel';
        this.cfg.dismissable = (this.cfg.dismissable === false) ? false : true;
        this.cfg.showDelay = this.cfg.showDelay || 0;

        if(this.cfg.showCloseIcon) {
            this.closerIcon = $('<a href="#" class="ui-overlaypanel-close ui-state-default" href="#"><span class="ui-icon ui-icon-closethick"></span></a>').appendTo(this.jq);
        }

        // prevent duplicate elements
        // the first check is required if the id contains a ':' - See #2485
        if(this.jq.length > 1) {
            $(document.body).children(this.jqId).remove();
            this.jq = $(this.jqId);
        }
        else {
            // this is required if the id does NOT contain a ':' - See #2485
            $(document.body).children("[id='" + this.id + "']").not(this.jq).remove();
        }

        //remove related modality if there is one
        var modal = $(this.jqId + '_modal');
        if(modal.length > 0) {
            modal.remove();
        }

        if(this.cfg.appendToBody) {
            this.jq.appendTo(document.body);
        }

        this.bindCommonEvents();

        if(this.cfg.target) {
            this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);
            this.bindTargetEvents();

            //dialog support
            this.setupDialogSupport();
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

            if(key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER) {
                e.preventDefault();
            }
        })
        .on('keyup.ui-overlaypanel', function(e) {
            var keyCode = $.ui.keyCode, key = e.which;

            if(key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER) {
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
            var hideNS = 'mousedown.' + this.id;
            $(document.body).off(hideNS).on(hideNS, function (e) {
                if($this.jq.hasClass('ui-overlay-hidden')) {
                    return;
                }

                //do nothing on target mousedown
                if($this.target) {
                    var target = $(e.target);
                    if($this.target.is(target)||$this.target.has(target).length > 0) {
                        return;
                    }
                }

                //hide overlay if mousedown is on outside
                var offset = $this.jq.offset();
                if(e.pageX < offset.left ||
                    e.pageX > offset.left + $this.jq.outerWidth() ||
                    e.pageY < offset.top ||
                    e.pageY > offset.top + $this.jq.outerHeight()) {

                    $this.hide();
                }
            });
        }

        //Hide overlay on resize
        var resizeNS = 'resize.' + this.id;
        $(window).off(resizeNS).on(resizeNS, function() {
            if($this.jq.hasClass('ui-overlay-visible')) {
                $this.align();
            }
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
        var $this = this,
            targetId = target||this.cfg.target;

        this.targetElement = $(document.getElementById(targetId));
        this.targetZindex = this.targetElement.zIndex();

        this.align(target);

        //replace visibility hidden with display none for effect support, toggle marker class
        this.jq.removeClass('ui-overlay-hidden').addClass('ui-overlay-visible').css({
            'display':'none'
            ,'visibility':'visible'
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
        var fixedPosition = this.jq.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null,
        targetId = target||this.cfg.target;

        this.jq.css({'left':'', 'top':'', 'z-index': ++PrimeFaces.zindex})
                .position({
                    my: this.cfg.my
                    ,at: this.cfg.at
                    ,of: document.getElementById(targetId)
                    ,offset: positionOffset
                });
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
    },

    postHide: function() {
        //replace display block with visibility hidden for hidden container support, toggle marker class
        this.jq.removeClass('ui-overlay-visible').addClass('ui-overlay-hidden').css({
            'display':'block'
            ,'visibility':'hidden'
        });

        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    },

    setupDialogSupport: function() {
        var dialog = this.target.closest('.ui-dialog');

        if(dialog.length == 1) {
            //set position as fixed to scroll with dialog
            this.jq.css('position', 'fixed');

            //append to body if not already appended by user choice
            if(!this.cfg.appendToBody) {
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

    enableModality: function() {
        var $this = this,
        doc = $(document);

        $(document.body).append('<div id="' + this.id + '_modal" class="ui-widget-overlay ui-overlaypanel-mask"></div>')
                        .children(this.jqId + '_modal').css('z-index' , this.jq.css('z-index') - 1);

        this.blockEvents = 'focus.' + this.id + ' mousedown.' + this.id + ' mouseup.' + this.id;
        if(this.targetElement) {
            this.targetElement.css('z-index', this.jq.css('z-index'));
        }

        //Disable tabbing out of modal overlaypanel and stop events from targets outside of overlaypanel
        doc.on('keydown.' + this.id,
                function(event) {
                    var target = $(event.target);

                    if(event.which === $.ui.keyCode.TAB) {
                        var tabbables = $this.getTabbables();

                        if(tabbables.length) {
                            var first = tabbables.filter(':first'),
                            last = tabbables.filter(':last'),
                            focusingRadioItem = null;

                            if(first.is(':radio')) {
                                focusingRadioItem = tabbables.filter('[name="' + first.attr('name') + '"]').filter(':checked');
                                if(focusingRadioItem.length > 0) {
                                    first = focusingRadioItem;
                                }
                            }

                            if(last.is(':radio')) {
                                focusingRadioItem = tabbables.filter('[name="' + last.attr('name') + '"]').filter(':checked');
                                if(focusingRadioItem.length > 0) {
                                    last = focusingRadioItem;
                                }
                            }

                            if(target.is(document.body)) {
                                first.focus(1);
                                event.preventDefault();
                            }
                            else if(event.target === last[0] && !event.shiftKey) {
                                first.focus(1);
                                event.preventDefault();
                            }
                            else if (event.target === first[0] && event.shiftKey) {
                                last.focus(1);
                                event.preventDefault();
                            }
                        }
                    }
                    else if(!target.is(document.body) && (target.zIndex() < $this.jq.zIndex())) {
                        event.preventDefault();
                    }
                })
                .on(this.blockEvents, function(event) {
                    if ($(event.target).zIndex() < $this.jq.zIndex()) {
                        event.preventDefault();
                    }
                });
    },

    disableModality: function(){
        if(this.targetElement) {
            this.targetElement.css('z-index', this.targetZindex);
        }

        $(document.body).children(this.jqId + '_modal').remove();
        $(document).off(this.blockEvents).off('keydown.' + this.id);
    },

    getTabbables: function(){
        var tabbableTarget;
        if(this.targetElement && this.targetElement.is(':tabbable')) {
            tabbableTarget = this.targetElement;
        }

        return this.jq.find(':tabbable').add(tabbableTarget);
    }

});
