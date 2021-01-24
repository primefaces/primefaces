/**
 * __PrimeFaces Sidebar Widget__
 * 
 * Sidebar is a panel component displayed as an overlay at the edges of the screen.
 * 
 * @prop {JQuery} closeIcon The DOM element for the icon that closes this sidebar.
 * @prop {boolean} loaded When dynamic loading is enabled, whether the content was already loaded.
 * 
 * @typedef PrimeFaces.widget.Sidebar.OnHideCallback Callback that is invoked when the sidebar is opened. See also
 * {@link SidebarCfg.onHide}.
 * @this {PrimeFaces.widget.Sidebar} PrimeFaces.widget.Sidebar.OnHideCallback 
 * 
 * @typedef PrimeFaces.widget.Sidebar.OnShowCallback Callback that is invoked when the sidebar is closed. See also
 * {@link SidebarCfg.onShow}.
 * @this {PrimeFaces.widget.Sidebar} PrimeFaces.widget.Sidebar.OnShowCallback 
 * 
 * @interface {PrimeFaces.widget.SidebarCfg} cfg The configuration for the {@link  Sidebar| Sidebar widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DynamicOverlayWidgetCfg} cfg
 * 
 * @prop {string} cfg.appendTo The search expression for the element to which the overlay panel should be appended.
 * @prop {number} cfg.baseZIndex Base z-index for the sidebar.
 * @prop {PrimeFaces.widget.Sidebar.OnHideCallback} cfg.onHide Callback that is invoked when the sidebar is opened.
 * @prop {PrimeFaces.widget.Sidebar.OnShowCallback} cfg.onShow Callback that is invoked when the sidebar is closed.
 * @prop {boolean} cfg.visible Whether the sidebar is initially opened.
 * @prop {boolean} cfg.dynamic `true` to load the content via AJAX when the overlay panel is opened, `false` to load
 * the content immediately.
 */
PrimeFaces.widget.Sidebar = PrimeFaces.widget.DynamicOverlayWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.closeIcon = this.jq.children('.ui-sidebar-close');
        this.cfg.baseZIndex = this.cfg.baseZIndex||0;

        //aria
        this.applyARIA();

        if(this.cfg.visible){
            this.show();
        }

        this.bindEvents();
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this._super(cfg);

        this.loaded = false;
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.closeIcon.on('mouseover', function() {
            $(this).addClass('ui-state-hover');
        }).on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        }).on('focus', function() {
            $(this).addClass('ui-state-focus');
        }).on('blur', function() {
            $(this).removeClass('ui-state-focus');
        }).on('click', function(e) {
            $this.hide();
            e.preventDefault();
        });
    },

    /**
     * Brings up this sidebar in case is is not already visible.
     */
    show: function() {
        if(this.isVisible()) {
            return;
        }

        if (!this.loaded && this.cfg.dynamic) {
            this.loadContents();
        }
        else {
            this._show();
        }
    },

    /**
     * Makes the sidebar panel visible.
     * @private
     */
    _show: function() {
        this.jq.addClass('ui-sidebar-active');
        this.jq.css('z-index', String(this.cfg.baseZIndex + (++PrimeFaces.zindex)));

        this.postShow();
        this.enableModality();
    },

    /**
     * Callback function that is invoked when this sidebar is hidden.
     * @private
     */
    postShow: function() {
        PrimeFaces.invokeDeferredRenders(this.id);

        //execute user defined callback
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },

    /**
     * Hides this sidebara in case it is not already hidden.
     */
    hide: function() {
        if(!this.isVisible()) {
            return;
        }

        this.jq.removeClass('ui-sidebar-active');
        this.onHide();
        this.disableModality();
    },

    /**
     * Checks whether this sidebar is currently visible.
     * @return {boolean} `true` if this sideplay is visible, or `false` otherwise.
     */
    isVisible: function() {
        return this.jq.hasClass('ui-sidebar-active');
    },

    /**
     * Callback function that is invoked when this sidebar is hidden.
     * @private
     * @param {JQuery.TriggeredEvent} event Currently unused.
     * @param {unknown} ui Currently unused.
     */
    onHide: function(event, ui) {
        if(this.cfg.onHide) {
            this.cfg.onHide.call(this, event, ui);
        }
    },

    /**
     * Hides this sidebar if it is visible or brings it up if it is hidden.
     */
    toggle: function() {
        if(this.isVisible())
            this.hide();
        else
            this.show();
    },

    /**
     * @override
     * @inheritdoc
     */
    enableModality: function() {
        this._super();

        var $this = this;
        this.modalOverlay.one('click.sidebar', function() {
            $this.hide();
        });
    },

    /**
     * @override
     * @inheritdoc
     * @return {JQuery}
     */
    getModalTabbables: function(){
        return this.jq.find(':tabbable');
    },

    /**
     * Sets all ARIA attributes on the elements and the icons.
     * @private
     */
    applyARIA: function() {
        this.jq.attr({
            'role': 'dialog'
            ,'aria-hidden': !this.cfg.visible
            ,'aria-modal': this.cfg.visible
        });

        this.closeIcon.attr('role', 'button');
    },

    /**
     * Loads the contents of this sidebar panel dynamically via AJAX, if dynamic loading is enabled.
     * @private
     */
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
                            $this.jq.html(content);
                            $this.loaded = true;
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this._show();
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

});