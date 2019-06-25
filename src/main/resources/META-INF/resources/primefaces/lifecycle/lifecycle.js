PrimeFaces.widget.Lifecycle = PrimeFaces.widget.BaseWidget.extend({

    init : function(cfg) {
        this._super(cfg);

        if (!this.initialized) {
            this.initialized = true;
            this.updating = false;

            this.update();
        }

        var $this = this;
        $(document).on('pfAjaxSuccess', function() {
            if (!$this.updating) {
                setTimeout(function() {
                    if (!$this.updating) {
                        $this.update();
                    }
                }, 25);
            }
        });

        if (window.jsf && window.jsf.ajax) {
            jsf.ajax.addOnEvent(function(data) {
                if (data.status === 'success') {
                    if (!$this.updating) {
                        setTimeout(function() {
                            if (!$this.updating) {
                                $this.update();
                            }
                        }, 25);
                    }
                }
            });
        }
    },

    update: function() {
        this.updating = true;

        var $this = this;

        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            ignoreAutoUpdate: true,
            formId: this.cfg.formId,
            params: [{ name: this.id + '_getlifecycleinfo', value: true }],
            oncomplete: function(xhr, status, args, data) {
                $this.updating = false;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }
});
