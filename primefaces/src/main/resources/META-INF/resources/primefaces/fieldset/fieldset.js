/**
 * __PrimeFaces Fieldset Widget__
 * 
 * Fieldset is a grouping component as an extension to html fieldset.
 * 
 * @prop {JQuery} legend The DOM element with the legend of this fieldset.
 * @prop {JQuery} toggler The DOM element with the toggler for hiding or showing the content of this fieldset.
 * @prop {JQuery} content The DOM element with the content of this fieldset.
 * @prop {JQuery} stateHolder The DOM element with the hidden input field for the state of this fieldset.
 * @prop {boolean} loaded When dynamic loading is enabled, whether the content was already loaded.
 * 
 * @interface {PrimeFaces.widget.FieldsetCfg} cfg The configuration for the {@link  Fieldset| Fieldset widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.toggleable Whether the content of this fieldset can be toggled between expanded and collapsed.
 * @prop {boolean} cfg.collapsed Whether this fieldset is currently collapsed (content invisible) or expanded (content
 * visible).
 * @prop {number} cfg.toggleSpeed Toggle duration in milliseconds.
 * @prop {boolean} cfg.dynamic `true` to load the content via AJAX when the fieldset panel is opened, `false` to load
 * the content immediately.
 */
PrimeFaces.widget.Fieldset = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.legend = this.jq.children('.ui-fieldset-legend');

        var $this = this;

        if(this.cfg.toggleable) {
            this.content = this.jq.children('.ui-fieldset-content');
            this.toggler = this.legend.children('.ui-fieldset-toggler');
            this.stateHolder = $(this.jqId + '_collapsed');

            //Add clickable legend state behavior
            this.legend.on('click', function(e) {
                $this.toggle(e);
            })
            .on('mouseover', function() {
                $this.legend.toggleClass('ui-state-hover');
            })
            .on('mouseout', function() {
                $this.legend.toggleClass('ui-state-hover');
            })
            .on('mousedown', function() {
                $this.legend.toggleClass('ui-state-active');
            })
            .on('mouseup', function() {
                $this.legend.toggleClass('ui-state-active');
            })
            .on('focus', function() {
                $this.legend.toggleClass('ui-state-focus');
            })
            .on('blur', function() {
                $this.legend.toggleClass('ui-state-focus');
            })
            .on('keydown', function(e) {
                var key = e.which,
                keyCode = $.ui.keyCode;

                if((key === keyCode.ENTER)) {
                    $this.toggle(e);
                    e.preventDefault();
                }
            });
        }
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
     * Toggles the content of this fieldset (collapsed or expanded).
     * @param {JQuery.TriggeredEvent} [e] Optional event that triggered the toggling.
     */
    toggle: function(e) {
        var $this = this,
            collapsed = this.cfg.collapsed;

        if (!this.loaded && this.cfg.dynamic && collapsed) {
            this.loadContents();
        }
        else {
            this.updateToggleState(collapsed);
        }

        this.content.slideToggle(this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            $this.callBehavior('toggle');
        });

        PrimeFaces.invokeDeferredRenders(this.id);
    },

    /**
     * Updates the visual toggler state and saves its state
     * @private
     * @param {boolean} collapsed If this fieldset is now collapsed or expanded.
     */
    updateToggleState: function(collapsed) {
        if(collapsed) {
            this.toggler.removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
        }
        else {
            this.toggler.removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');
        }

        this.cfg.collapsed = !collapsed;

        this.stateHolder.val(!collapsed);
    },

    /**
     * Loads the contents of this fieldset panel dynamically via AJAX, if dynamic loading is enabled.
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
                            this.content.html(content);
                            $this.loaded = true;
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.updateToggleState($this.cfg.collapsed);
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

});