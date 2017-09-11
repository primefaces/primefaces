PrimeFaces.widget.ContextMenu = PrimeFaces.widget.TieredMenu.extend({

    init: function(cfg) {
        cfg.autoDisplay = true;
        this._super(cfg);
        this.cfg.selectionMode = this.cfg.selectionMode||'multiple';

        var _self = this,
        documentTarget = (this.cfg.target === undefined);

        //event
        this.cfg.event = this.cfg.event||'contextmenu';

        //target
        this.jqTargetId = documentTarget ? document : PrimeFaces.escapeClientId(this.cfg.target);
        this.jqTarget = $(this.jqTargetId);

        //append to body
        if(!this.jq.parent().is(document.body)) {
            this.jq.appendTo('body');
        }

        //attach contextmenu
        if(documentTarget) {
            $(document).off('contextmenu.ui-contextmenu').on('contextmenu.ui-contextmenu', function(e) {
                _self.show(e);
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
                    _self.show(e);
                });
            }
        }
    },

    refresh: function(cfg) {
        var jqId = PrimeFaces.escapeClientId(cfg.id),
        instances = $(jqId);

        if(instances.length > 1) {
            $(document.body).children(jqId).remove();
        }

        this.init(cfg);
    },

    bindItemEvents: function() {
        this._super();

        var _self = this;

        //hide menu on item click
        this.links.bind('click', function(e) {
            var target = $(e.target),
                submenuLink = target.hasClass('ui-submenu-link') ? target : target.closest('.ui-submenu-link');

            if(submenuLink.length) {
                return;
            }

            _self.hide();
        });
    },

    bindDocumentHandler: function() {
        var $this = this,
        clickNS = 'click.' + this.id;

        //hide overlay when document is clicked
        $(document.body).off(clickNS).on(clickNS, function(e) {
            var target = $(e.target),
                link = target.hasClass('ui-menuitem-link') ? target : target.closest('.ui-menuitem-link');

            if($this.jq.is(":hidden") || link.is('.ui-menuitem-link,.ui-state-disabled')) {
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

        this.jq.css({
            'left': left,
            'top': top,
            'z-index': ++PrimeFaces.zindex
        }).show();

        e.preventDefault();
        e.stopPropagation();
    },

    hide: function() {
        var _self = this;

        //hide submenus
        this.jq.find('li.ui-menuitem-active').each(function() {
            _self.deactivate($(this), true);
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
