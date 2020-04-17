/**
 * __PrimeFaces OutputPanel Widget__
 * 
 * OutputPanel is a panel component with the ability for deferred loading.
 * 
 * @typedef {"load" | "visible"} PrimeFaces.widget.OutputPanel.DeferredMode Mode that indicates how the content of an
 * output panel is loaded:
 * - `load`: Loads the content directly after the page was loaded.
 * - `visible`: Loads the panel once it is visible, e.g. once the user scrolled down.
 * 
 * @interface {PrimeFaces.widget.OutputPanelCfg} cfg The configuration for the {@link  OutputPanel| OutputPanel widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.deferred Deferred mode loads the contents after page load to speed up page load.
 * @prop {PrimeFaces.widget.OutputPanel.DeferredMode} cfg.deferredMode Defines deferred loading mode, whether the
 * content is loaded directly after the page is done loading, or only once the user scrolled to the panel.
 * @prop {boolean} cfg.global When the content is loaded via AJAX, whether AJAX request triggers the global
 * `ajaxStatus`.
 */
PrimeFaces.widget.OutputPanel = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.cfg.global = this.cfg.global||false;

        if(this.cfg.deferred) {
            if(this.cfg.deferredMode === 'load') {
                this.loadContent();
            }
            else if(this.cfg.deferredMode === 'visible') {
                if(this.visible())
                    this.loadContent();
                else
                    this.bindScrollMonitor();
            }
        }
    },

    /**
     * Loads the content of this panel via AJAX, if dynamic loading is enabled.
     * @private
     */
    loadContent: function() {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            async: true,
            ignoreAutoUpdate: true,
            global: false,
            params: [
                {name: this.id + '_load', value: true}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            $this.jq.html(content);
                        }
                    });

                return true;
            },
            onerror: function(xhr, status, errorThrown) {
                $this.jq.html('');
            }
        };

        if(this.hasBehavior('load')) {
            this.callBehavior('load', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Sets up the event listeners for handling scrolling.
     * @private
     */
    bindScrollMonitor: function() {
        var $this = this;

        PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.id + '_align', function() {
            if ($this.visible()) {
                PrimeFaces.utils.unbindScrollHandler($this, 'scroll.' + $this.id + '_align');
                $this.loadContent();
            }
        });
    },

    /**
     * Checks whether this panel is currently visible.
     * @return {boolean} `true` if this panel is currently visible, or `false` otherwise.
     */
    visible: function() {
        var win = $(window),
        scrollTop = win.scrollTop(),
        height = win.height(),
        top = this.jq.offset().top,
        bottom = top + this.jq.innerHeight();

        if((top >= scrollTop && top <= (scrollTop + height)) || (bottom >= scrollTop && bottom <= (scrollTop + height))) {
            return true;
        }
    }
});