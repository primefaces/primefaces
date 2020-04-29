/**
 * __PrimeFaces Sidebar Widget__
 * 
 * Sidebar is a panel component displayed as an overlay at the edges of the screen.
 * 
 * @prop {JQuery} closeIcon The DOM element for the icon that closes this sidebar.
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
 * @prop {number} cfg.baseZIndex Base z-index for the sidebar.
 * @prop {PrimeFaces.widget.Sidebar.OnHideCallback} cfg.onHide Callback that is invoked when the sidebar is opened.
 * @prop {PrimeFaces.widget.Sidebar.OnShowCallback} cfg.onShow Callback that is invoked when the sidebar is closed.
 * @prop {boolean} cfg.visible Whether the sidebar is initially opened.
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

        this.jq.addClass('ui-sidebar-active');
        this.jq.css('z-index', this.cfg.baseZIndex + (++PrimeFaces.zindex));

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

        this.jq.attr({
            'aria-hidden': false
            ,'aria-live': 'polite'
        });
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
     * @param {JQuery.Event} event Currently unused.
     * @param {unknown} ui Currently unused.
     */
    onHide: function(event, ui) {
        this.jq.attr({
            'aria-hidden': true
            ,'aria-live': 'off'
        });

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
        this.modalOverlay.on('click', function() {
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
        });

        this.closeIcon.attr('role', 'button');
    }

});