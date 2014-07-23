/**
 * PrimeFaces Dialog Widget
 */ 
PrimeFaces.widget.Dialog = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.content = this.jq.children('.ui-dialog-content');
        this.titlebar = this.jq.children('.ui-dialog-titlebar');
        this.footer = this.jq.find('.ui-dialog-footer');
        this.icons = this.titlebar.children('.ui-dialog-titlebar-icon');
        this.closeIcon = this.titlebar.children('.ui-dialog-titlebar-close');
        this.minimizeIcon = this.titlebar.children('.ui-dialog-titlebar-minimize');
        this.maximizeIcon = this.titlebar.children('.ui-dialog-titlebar-maximize');
        this.blockEvents = 'focus.' + this.id + ' mousedown.' + this.id + ' mouseup.' + this.id;

        //configuration
        this.cfg.width = this.cfg.width||'auto';
        this.cfg.height = this.cfg.height||'auto';
        this.cfg.draggable = this.cfg.draggable === false ? false : true;
        this.cfg.resizable = this.cfg.resizable === false ? false : true;
        this.cfg.minWidth = this.cfg.minWidth||150;
        this.cfg.minHeight = this.cfg.minHeight||this.titlebar.outerHeight();
        this.cfg.position = this.cfg.position||'center';
        this.parent = this.jq.parent();

        this.initSize();
       
        //events
        this.bindEvents();

        if(this.cfg.draggable) {
            this.setupDraggable();
        }

        if(this.cfg.resizable){
            this.setupResizable();
        }

        if(this.cfg.modal) {
            this.syncWindowResize();
        }

        if(this.cfg.appendTo) {
        	this.jq.appendTo(PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo));
        }

        //docking zone
        if($(document.body).children('.ui-dialog-docking-zone').length === 0) {
            $(document.body).append('<div class="ui-dialog-docking-zone"></div>')
        }

        //remove related modality if there is one
        var modal = $(this.jqId + '_modal');
        if(modal.length > 0) {
            modal.remove();
        }
        
        //aria
        this.applyARIA();

        if(this.cfg.visible){
            this.show();
        }
    },
            
    //override
    refresh: function(cfg) {
        this.positionInitialized = false;
        this.loaded = false;
        
        $(document).off('keydown.dialog_' + cfg.id);
        
        if(cfg.appendTo) {
            var jqs = $(this.jqId);
            if(jqs.length > 1) {
            	PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(cfg.appendTo).children(this.jqId).remove();
            }
        }
        
        this.init(cfg);
    },
    
    initSize: function() {
        this.jq.css({
            'width': this.cfg.width,
            'height': 'auto'
        });
        
        this.content.height(this.cfg.height);
        
        if(this.cfg.fitViewport) {
            this.fitViewport();
        }
        
        //ie7 width auto width bug workaround
        if(this.cfg.width === 'auto' && PrimeFaces.isIE(7)) {
            this.jq.width(this.content.outerWidth());
        }
    },
    
    fitViewport: function() {
        var winHeight = $(window).height(),
        contentPadding = this.content.innerHeight() - this.content.height();

        if(this.jq.innerHeight() > winHeight) {
            this.content.height(winHeight - this.titlebar.innerHeight() - contentPadding);
        }
    },
    
    enableModality: function() {
        var $this = this,
        doc = $(document);

        $(document.body).append('<div id="' + this.id + '_modal" class="ui-widget-overlay"></div>')
                        .children(this.jqId + '_modal').css({
                            'width' : doc.width(),
                            'height' : doc.height(),
                            'z-index' : this.jq.css('z-index') - 1
                        });

        //Disable tabbing out of modal dialog and stop events from targets outside of dialog
        doc.on('keydown.' + this.id,
                function(event) {
                    var target = $(event.target);

                    if(event.keyCode === $.ui.keyCode.TAB) {
                        var tabbables = $this.content.find(':tabbable').add($this.footer.find(':tabbable'));
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
        $(document.body).children(this.jqId + '_modal').remove();
        $(document).off(this.blockEvents).off('keydown.' + this.id);
    },
    
    syncWindowResize: function() {
        $(window).resize(function() {
            $(document.body).children('.ui-widget-overlay').css({
                'width': $(document).width()
                ,'height': $(document).height()
            });
        });
    },
    
    show: function() {
        if(this.jq.hasClass('ui-overlay-visible')) {
            return;
        }

        if(!this.loaded && this.cfg.dynamic) {
            this.loadContents();
        } 
        else {
            if(!this.positionInitialized) {
                this.initPosition();
            }

            this._show();
        }
    },
    
    _show: function() {
        //replace visibility hidden with display none for effect support, toggle marker class
        this.jq.removeClass('ui-overlay-hidden').addClass('ui-overlay-visible').css({
            'display':'none'
            ,'visibility':'visible'
        });
        
        this.moveToTop();
        
        if(this.cfg.showEffect) {
            var $this = this;

            this.jq.show(this.cfg.showEffect, null, 'normal', function() {
                $this.postShow();
            });
        }    
        else {
            //display dialog
            this.jq.show();

            this.postShow();
        }

        if(this.cfg.modal) {
            this.enableModality();
        }
    },
    
    postShow: function() {   
        //execute user defined callback
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
        
        this.jq.attr({
            'aria-hidden': false
            ,'aria-live': 'polite'
        });
        
        this.applyFocus();
    },
    
    hide: function() {   
        if(this.jq.hasClass('ui-overlay-hidden')) {
            return;
        }
        
        if(this.cfg.hideEffect) {
            var $this = this;

            this.jq.hide(this.cfg.hideEffect, null, 'normal', function() {
                if($this.cfg.modal) {
                    $this.disableModality();
                }
                $this.onHide();
            });
        }
        else {
            this.jq.hide();
            if(this.cfg.modal) {
                this.disableModality();
            }
            this.onHide();
        }   
    },
    
    applyFocus: function() {
        if(this.cfg.focus)
        	PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.focus).focus();
        else
            this.jq.find(':not(:submit):not(:button):not(:radio):not(:checkbox):input:visible:enabled:first').focus();
    },
    
    bindEvents: function() {   
        var $this = this;

        //Move dialog to top if target is not a trigger for a PrimeFaces overlay
        this.jq.mousedown(function(e) {
            if(!$(e.target).data('primefaces-overlay-target')) {
                $this.moveToTop();
            }
        });
        
        this.icons.mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        });

        this.closeIcon.click(function(e) {
            $this.hide();
            e.preventDefault();
        });

        this.maximizeIcon.click(function(e) {
            $this.toggleMaximize();
            e.preventDefault();
        });

        this.minimizeIcon.click(function(e) {
            $this.toggleMinimize();
            e.preventDefault();
        });
        
        if(this.cfg.closeOnEscape) {
            $(document).on('keydown.dialog_' + this.id, function(e) {
                var keyCode = $.ui.keyCode,
                active = parseInt($this.jq.css('z-index')) === PrimeFaces.zindex;

                if(e.which === keyCode.ESCAPE && $this.jq.hasClass('ui-overlay-visible') && active) {
                    $this.hide();
                };
            });
        }
    },
    
    setupDraggable: function() {
        var $this = this;
        
        this.jq.draggable({
            cancel: '.ui-dialog-content, .ui-dialog-titlebar-close',
            handle: '.ui-dialog-titlebar',
            containment : 'document',
            stop: function( event, ui ) {
                if($this.hasBehavior('move')) {
                    var move = $this.cfg.behaviors['move'];
                    var ext = {
                        params: [
                            {name: $this.id + '_top', value: ui.offset.top},
                            {name: $this.id + '_left', value: ui.offset.left}
                        ]
                    };
                    move.call($this, ext);
                }
            }
        });
    },
    
    setupResizable: function() {
        var $this = this;

        this.jq.resizable({
            handles : 'n,s,e,w,ne,nw,se,sw',
            minWidth : this.cfg.minWidth,
            minHeight : this.cfg.minHeight,
            alsoResize : this.content,
            containment: 'document',
            start: function(event, ui) {
                $this.jq.data('offset', $this.jq.offset());
            },
            stop: function(event, ui) {
                var offset = $this.jq.data('offset')

                $this.jq.css('position', 'fixed');
                $this.jq.offset(offset);
            }
        });

        this.resizers = this.jq.children('.ui-resizable-handle');
    },
    
    initPosition: function() {
        //reset
        this.jq.css({left:0,top:0});

        if(/(center|left|top|right|bottom)/.test(this.cfg.position)) {
            this.cfg.position = this.cfg.position.replace(',', ' ');

            this.jq.position({
                        my: 'center'
                        ,at: this.cfg.position
                        ,collision: 'fit'
                        ,of: window
                        //make sure dialog stays in viewport
                        ,using: function(pos) {
                            var l = pos.left < 0 ? 0 : pos.left,
                            t = pos.top < 0 ? 0 : pos.top;

                            $(this).css({
                                left: l
                                ,top: t
                            });
                        }
                    });
        }
        else {
            var coords = this.cfg.position.split(','),
            x = $.trim(coords[0]),
            y = $.trim(coords[1]);

            this.jq.offset({
                left: x
                ,top: y
            });
        }

        this.positionInitialized = true;
    },
    
    onHide: function(event, ui) {
        //replace display block with visibility hidden for hidden container support, toggle marker class
        this.jq.removeClass('ui-overlay-visible').addClass('ui-overlay-hidden').css({
            'display':'block'
            ,'visibility':'hidden'
        });
        
        if(this.cfg.behaviors) {
            var closeBehavior = this.cfg.behaviors['close'];

            if(closeBehavior) {
                closeBehavior.call(this);
            }
        }
        
        this.jq.attr({
            'aria-hidden': true
            ,'aria-live': 'off'
        });
        
        if(this.cfg.onHide) {
            this.cfg.onHide.call(this, event, ui);
        }
    },
    
    moveToTop: function() {
        this.jq.css('z-index', ++PrimeFaces.zindex);
    },
    
    toggleMaximize: function() {
        if(this.minimized) {
            this.toggleMinimize();
        }

        if(this.maximized) {
            this.jq.removeClass('ui-dialog-maximized');
            this.restoreState();

            this.maximizeIcon.children('.ui-icon').removeClass('ui-icon-newwin').addClass('ui-icon-extlink');
            this.maximized = false;
        } 
        else {
            this.saveState();

            var win = $(window);

            this.jq.addClass('ui-dialog-maximized').css({
                'width': win.width() - 6
                ,'height': win.height()
            }).offset({
                top: win.scrollTop()
                ,left: win.scrollLeft()
            });

            //maximize content
            this.content.css({
                width: 'auto',
                height: 'auto'
            });

            this.maximizeIcon.removeClass('ui-state-hover').children('.ui-icon').removeClass('ui-icon-extlink').addClass('ui-icon-newwin');
            this.maximized = true;
            
            if(this.cfg.behaviors) {
                var maximizeBehavior = this.cfg.behaviors['maximize'];

                if(maximizeBehavior) {
                    maximizeBehavior.call(this);
                }
            }
        }
    },
    
    toggleMinimize: function() {
        var animate = true,
        dockingZone = $(document.body).children('.ui-dialog-docking-zone');

        if(this.maximized) {
            this.toggleMaximize();
            animate = false;
        }

        var $this = this;

        if(this.minimized) {
            this.jq.appendTo(this.parent).removeClass('ui-dialog-minimized').css({'position':'fixed', 'float':'none'});
            this.restoreState();
            this.content.show();
            this.minimizeIcon.removeClass('ui-state-hover').children('.ui-icon').removeClass('ui-icon-plus').addClass('ui-icon-minus');
            this.minimized = false;

            if(this.cfg.resizable)
                this.resizers.show();
        }
        else {
            this.saveState();

            if(animate) {
                this.jq.effect('transfer', {
                                to: dockingZone
                                ,className: 'ui-dialog-minimizing'
                                }, 500, 
                                function() {
                                    $this.dock(dockingZone);
                                    $this.jq.addClass('ui-dialog-minimized');
                                });
            } 
            else {
                this.dock(dockingZone);
            }
        }
    },
    
    dock: function(zone) {
        this.jq.appendTo(zone).css('position', 'static');
        this.jq.css({'height':'auto', 'width':'auto', 'float': 'left'});
        this.content.hide();
        this.minimizeIcon.removeClass('ui-state-hover').children('.ui-icon').removeClass('ui-icon-minus').addClass('ui-icon-plus');
        this.minimized = true;

        if(this.cfg.resizable) {
            this.resizers.hide();
        }
        
        if(this.cfg.behaviors) {
            var minimizeBehavior = this.cfg.behaviors['minimize'];

            if(minimizeBehavior) {
                minimizeBehavior.call(this);
            }
        }
    },
    
    saveState: function() {
        this.state = {
            width: this.jq.width(),
            height: this.jq.height(),
            contentWidth: this.content.width(),
            contentHeight: this.content.height()
        };

        var win = $(window);
        this.state.offset = this.jq.offset();
        this.state.windowScrollLeft = win.scrollLeft();
        this.state.windowScrollTop = win.scrollTop();
    },
    
    restoreState: function() {
        this.jq.width(this.state.width).height(this.state.height);
        this.content.width(this.state.contentWidth).height(this.state.contentHeight);

        var win = $(window);
        this.jq.offset({
                top: this.state.offset.top + (win.scrollTop() - this.state.windowScrollTop)
                ,left: this.state.offset.left + (win.scrollLeft() - this.state.windowScrollLeft)
        });
    },
    
    loadContents: function() {
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
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.loaded = true;
                $this.show();
            }
        };
        
        PrimeFaces.ajax.Request.handle(options);
    },
    
    applyARIA: function() {
        this.jq.attr({
            'role': 'dialog'
            ,'aria-labelledby': this.id + '_title'
            ,'aria-hidden': !this.cfg.visible
        });
        
        this.titlebar.children('a.ui-dialog-titlebar-icon').attr('role', 'button');
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    }
    
});

/**
 * PrimeFaces ConfirmDialog Widget
 */
PrimeFaces.widget.ConfirmDialog = PrimeFaces.widget.Dialog.extend({

    init: function(cfg) {
        cfg.draggable = false;
        cfg.resizable = false;
        cfg.modal = true;

        if (!cfg.appendTo && cfg.global) {
        	cfg.appendTo = '@(body)';
        }

        this._super(cfg);
        
        this.title = this.titlebar.children('.ui-dialog-title');
        this.message = this.content.children('.ui-confirm-dialog-message');
        this.icon = this.content.children('.ui-confirm-dialog-severity');

        if(this.cfg.global) {
            PrimeFaces.confirmDialog = this;

            this.jq.find('.ui-confirmdialog-yes').on('click.ui-confirmdialog', function(e) {                
                if(PrimeFaces.confirmSource) {
                    var fn = eval('(function(element,event){' + PrimeFaces.confirmSource.data('pfconfirmcommand') + '})');
                    
                    fn.call(PrimeFaces.confirmSource,PrimeFaces.confirmSource.get(0),e);
                    PrimeFaces.confirmDialog.hide();
                    PrimeFaces.confirmSource = null;
                }
                
                e.preventDefault();
            });

            this.jq.find('.ui-confirmdialog-no').on('click.ui-confirmdialog', function(e) {
                PrimeFaces.confirmDialog.hide();
                PrimeFaces.confirmSource = null;
                
                e.preventDefault();
            });
        }
    },

    applyFocus: function() {
        this.jq.find(':button,:submit').filter(':visible:enabled').eq(0).focus();
    },
            
    showMessage: function(msg) {
        if(msg.header)
            this.title.text(msg.header);
        
        if(msg.message)
            this.message.text(msg.message);
        
        if(msg.icon)
            this.icon.removeClass().addClass('ui-icon ui-confirm-dialog-severity ' + msg.icon);
        
        this.show();
    }

});