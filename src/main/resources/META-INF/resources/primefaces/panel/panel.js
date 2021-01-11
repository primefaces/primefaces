/**
 * __PrimeFaces Panel Widget__
 * 
 * Panel is a grouping component with content toggle, close and menu integration.
 * 
 * @typedef {"vertical" | "horizontal"} PrimeFaces.widget.Panel.ToggleOrientation When toggling a panel, defines whether
 * it slides up and down; or left and right.
 * 
 * @prop {JQuery} closer The DOM element for the icon that closes this panel.
 * @prop {JQuery} content The DOM element for the content of this panel.
 * @prop {JQuery} header The DOM element for the header of this panel. 
 * @prop {boolean} isTitlebarClicked Whether the title bar was recently clicked.
 * @prop {number} originalWidth The original width of this panel before it got collapsed.
 * @prop {JQuery} title The DOM element for the title text in the header of this panel. 
 * @prop {JQuery} toggler The DOM element for the icon that toggles this panel.
 * @prop {JQuery} toggleStateHolder The DOM element for the hidden input storing whether this panel is currently
 * expanded or collapsed.
 * @prop {JQuery} visibleStateHolder The DOM element for the hidden input storing whether this panel is currently
 * visible or hidden.
 * 
 * @interface {PrimeFaces.widget.PanelCfg} cfg The configuration for the {@link  Panel| Panel widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.closable Whether panel is closable.
 * @prop {number} cfg.closeSpeed Speed of closing effect in milliseconds
 * @prop {boolean} cfg.collapsed Whether the panel is initially collapsed.
 * @prop {boolean} cfg.hasMenu Whether this panel has a toggleable menu in the panel header. 
 * @prop {boolean} cfg.toggleable Whether the panel can be toggled (expanded and collapsed).
 * @prop {boolean} cfg.toggleableHeader Defines if the panel is toggleable by clicking on the whole panel header.
 * @prop {PrimeFaces.widget.Panel.ToggleOrientation} cfg.toggleOrientation Defines the orientation of the toggling.
 * @prop {number} cfg.toggleSpeed Speed of toggling in milliseconds.
 */
PrimeFaces.widget.Panel = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.header = this.jq.children('div.ui-panel-titlebar');
        this.title = this.header.children('span.ui-panel-title');
        this.content = $(this.jqId + '_content');

        this.bindEvents();
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        if(this.cfg.toggleable) {
            this.bindToggler();

            if(this.cfg.toggleableHeader) {
                this.header.on('click', function() {
                    if(!$this.isTitlebarClicked) {
                        $this.toggle();
                    }

                    $this.isTitlebarClicked = false;
                });
            }
        }

        if(this.cfg.closable) {
            this.bindCloser();
        }

        if(this.cfg.hasMenu) {
            $(this.jqId + '_menu').on('click.panel', function(e) {
                e.preventDefault();
            });
        }

        //visuals for action items
        this.header.find('.ui-panel-titlebar-icon').on('mouseover.panel',function() {
            $(this).addClass('ui-state-hover');
        }).on('mouseout.panel',function() {
            $(this).removeClass('ui-state-hover');
        }).on('click.panel', function(e) {
            var href = $(this).attr('href');
            if(!href || href == '#') {
                e.preventDefault();
            }

            $this.isTitlebarClicked = true;
        });
    },

    /**
     * Expands this panel if it is currently collapsed, or collapses it if it is currently expanded.
     */
    toggle: function() {
        if(this.cfg.collapsed) {
            this.expand();
            PrimeFaces.invokeDeferredRenders(this.id);
        }
        else {
            this.collapse();
        }
    },

    /**
     * Expands this panel, if not already expanded.
     */
    expand: function() {
        this.toggleState(false, 'ui-icon-plusthick', 'ui-icon-minusthick');

        if(this.cfg.toggleOrientation === 'vertical')
            this.slideDown();
        else if(this.cfg.toggleOrientation === 'horizontal')
            this.slideRight();
    },

    /**
     * Collapses this panel, if not already collapsed.
     */
    collapse: function() {
        this.toggleState(true, 'ui-icon-minusthick', 'ui-icon-plusthick');

        if(this.cfg.toggleOrientation === 'vertical')
            this.slideUp();
        else if(this.cfg.toggleOrientation === 'horizontal')
            this.slideLeft();
    },

    /**
     * Closes this panel by sliding it up.
     * @private
     */
    slideUp: function() {
        this.content.slideUp(this.cfg.toggleSpeed, 'easeInOutCirc');
    },

    /**
     * Opens this panel by sliding it down.
     * @private
     */
    slideDown: function() {
        this.content.slideDown(this.cfg.toggleSpeed, 'easeInOutCirc');
    },

    /**
     * Closes this panel by sliding it to the left.
     * @private
     */
    slideLeft: function() {
        var $this = this;

        this.originalWidth = this.jq.width();

        this.title.hide();
        this.toggler.hide();
        this.content.hide();

        this.jq.animate({
            width: '42px'
        }, this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            $this.toggler.show();
            $this.jq.addClass('ui-panel-collapsed-h');
        });
    },

    /**
     * Opens this panel by sliding it to the right.
     * @private
     */
    slideRight: function() {
        var $this = this,
        expandWidth = this.originalWidth||'100%';

        this.toggler.hide();

        this.jq.animate({
            width: expandWidth
        }, this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            $this.jq.removeClass('ui-panel-collapsed-h');
            $this.title.show();
            $this.toggler.show();

            $this.content.css({
                'visibility': 'visible'
                ,'display': 'block'
                ,'height': 'auto'
            });
        });
    },

    /**
     * Toggles the expansion state of this panel.
     * @private
     * @param {boolean} collapsed Whether the panel is now to be collapsed.
     * @param {JQuery} removeIcon Icon for closing this panel. 
     * @param {JQuery} addIcon Icon for opening this panel.
     */
    toggleState: function(collapsed, removeIcon, addIcon) {
        this.toggler.children('span.ui-icon').removeClass(removeIcon).addClass(addIcon);
        this.cfg.collapsed = collapsed;
        this.toggleStateHolder.val(collapsed);

        this.callBehavior('toggle');
    },

    /**
     * Closes this panel, if not already closed.
     */
    close: function() {
        if(this.visibleStateHolder) {
            this.visibleStateHolder.val(false);
        }

        var $this = this;
        this.jq.fadeOut(this.cfg.closeSpeed, function(e) {
            if($this.hasBehavior('close')) {
                $this.callBehavior('close');
            }
        });
    },

    /**
     * Shows this panel, if not already shown.
     */
    show: function() {
        var $this = this;
        this.jq.fadeIn(this.cfg.closeSpeed, function() {
            PrimeFaces.invokeDeferredRenders($this.id);
        });

        if(this.visibleStateHolder) {
            this.visibleStateHolder.val(true);
        }
    },

    /**
     * Sets up the event listeners for the button that toggles this panel between opened and closes.
     * @private
     */
    bindToggler: function() {
        var $this = this;

        this.toggler = $(this.jqId + '_toggler');
        this.toggleStateHolder = $(this.jqId + '_collapsed');

        this.toggler.on("click", function() {
            $this.toggle();

            return false;
        });
    },

    /**
     * Sets up the event listeners for the button that closes this panel.
     * @private
     */
    bindCloser: function() {
        var $this = this;

        this.closer = $(this.jqId + '_closer');
        this.visibleStateHolder = $(this.jqId + "_visible");

        this.closer.on("click", function(e) {
            $this.close();
            e.preventDefault();

            return false;
        });
    }

});