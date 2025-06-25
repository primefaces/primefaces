/**
 * __PrimeFaces Lifecycle Widget__
 * 
 * Lifecycle is a utility component which displays the execution time of each Faces phase. It also synchronizes
 * automatically after each AJAX request.
 * 
 * @prop {boolean} initialized Whether this widget was initialized.
 * @prop {boolean} updating Whether the lifecycle display is currently being updated.
 * 
 * @interface {PrimeFaces.widget.LifecycleCfg} cfg The configuration for the {@link  Lifecycle| Lifecycle widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.Lifecycle = class Lifecycle extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        if (!this.initialized) {
            this.initialized = true;
            this.updating = false;

            this.update();
        }

        var $this = this;
        $(document).on('pfAjaxSuccess.'+this.id, function() {
            if (!$this.updating) {
                PrimeFaces.queueTask(function() {
                    if (!$this.updating) {
                        $this.update();
                    }
                }, 25);
            }
        });
        this.addDestroyListener(function() {
            $(document).off('pfAjaxSuccess.'+this.id);
        });

        if (window.faces && window.faces.ajax) {
            faces.ajax.addOnEvent(function(data) {
                if (data.status === 'success') {
                    if (!$this.updating) {
                        PrimeFaces.queueTask(function() {
                            if (!$this.updating) {
                                $this.update();
                            }
                        }, 25);
                    }
                }
            });
        }
    }

    /**
     * Updates this lifecycle widget after an AJAX request.
     * @private
     */
    update() {
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
}
