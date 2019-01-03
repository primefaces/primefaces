PrimeFaces.widget.ContextMenu = PrimeFaces.widget.TieredMenu.extend({

    init: function(cfg) {
        cfg.autoDisplay = true;
        this._super(cfg);
        this.cfg.selectionMode = this.cfg.selectionMode||'multiple';

        var $this = this,
        documentTarget = (this.cfg.target === undefined);

        //event
        this.cfg.event = this.cfg.event||'contextmenu';

        //target
        this.jqTargetId = documentTarget ? document : PrimeFaces.escapeClientId(this.cfg.target);
        this.jqTarget = $(this.jqTargetId);

        //append to body
        this.cfg.appendTo = '@(body)';
        PrimeFaces.utils.registerDynamicOverlay(this, this.jq, this.id);

        //attach contextmenu
        if(documentTarget) {
            $(document).off('contextmenu.ui-contextmenu').on('contextmenu.ui-contextmenu', function(e) {
                $this.show(e);
            });
        }
        else {
            var binded = false;

            if (this.cfg.targetWidgetVar) {
                var targetWidget = PrimeFaces.widgets[this.cfg.targetWidgetVar];

                if (targetWidget) {
                    if (typeof targetWidget.bindContextMenu === 'function') {
                        targetWidget.bindContextMenu(this, targetWidget, this.jqTargetId, this.cfg);
                        binded = true;
                    }
                }
                else {
                    PrimeFaces.warn("ContextMenu targets a widget which is not available yet. Please place the contextMenu after the target component. targetWidgetVar: " + this.cfg.targetWidgetVar);
                }
            }

            if (binded === false) {
                var event = this.cfg.event + '.ui-contextmenu';

                $(document).off(event, this.jqTargetId).on(event, this.jqTargetId, null, function(e) {
                    $this.show(e);
                });
            }
        }


        PrimeFaces.utils.registerHideOverlayHandler(this, 'click.' + this.id + '_hide', this.jq,
            function(e) { return e.which == 3 ? $this.jqTarget : null; },
            function(e, eventTarget) {
                if(!($this.jq.is(eventTarget) || $this.jq.has(eventTarget).length > 0)) {
                    $this.hide();
                }
            });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', this.jq, function() {
            $this.hide();
        });
    },

    // @Override
    bindItemEvents: function() {
        this._super();

        var $this = this;

        //hide menu on item click
        this.links.on('click', function(e) {
            var target = $(e.target),
                submenuLink = target.hasClass('ui-submenu-link') ? target : target.closest('.ui-submenu-link');

            if (submenuLink.length) {
                return;
            }

            $this.hide();
        });
    },

    show: function(e) {
        if(this.cfg.targetFilter && $(e.target).is(':not(' + this.cfg.targetFilter + ')')) {
            return;
        }

        //hide other contextmenus if any
        $(document.body).children('.ui-contextmenu:visible').hide();

        if(this.cfg.beforeShow) {
            var retVal = this.cfg.beforeShow.call(this, e);
            if(retVal === false) {
                return;
            }
        }

        var win = $(window),
        left = e.pageX,
        top = e.pageY,
        width = this.jq.outerWidth(),
        height = this.jq.outerHeight();

        //collision detection for window boundaries
        if((left + width) > (win.width())+ win.scrollLeft()) {
            left = left - width;
        }
        if((top + height ) > (win.height() + win.scrollTop())) {
            top = top - height;
        }
        if(top < 0) {
            top = e.pageY;
        }

        this.jq.css({
            'left': left,
            'top': top,
            'z-index': ++PrimeFaces.zindex
        }).show();

        e.preventDefault();
        e.stopPropagation();
    },

    hide: function() {
        var $this = this;

        //hide submenus
        this.jq.find('li.ui-menuitem-active').each(function() {
            $this.deactivate($(this), true);
        });

        this.jq.fadeOut('fast');
    },

    isVisible: function() {
        return this.jq.is(':visible');
    },

    getTarget: function() {
        return this.jqTarget;
    }

});
