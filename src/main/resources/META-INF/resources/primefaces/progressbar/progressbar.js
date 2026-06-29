/**
 * PrimeFaces ProgressBar widget
 */
PrimeFaces.widget.ProgressBar = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.jqValue = this.jq.children('.ui-progressbar-value');
        this.jqLabel = this.jq.children('.ui-progressbar-label');
        this.value = this.cfg.initialValue;
        this.cfg.global = (this.cfg.global === false) ? false : true;

        if(this.cfg.ajax) {
            this.cfg.formId = this.jq.closest('form').attr('id');
        }

        this.enableARIA();
    },

    setValue: function(value) {
        if(value >= 0 && value<=100) {
            if(value == 0) {
                this.jqValue.hide().css('width', '0%').removeClass('ui-corner-right');

                this.jqLabel.hide();
            }
            else {
                this.jqValue.show().animate({
                    'width': value + '%'
                }, this.cfg.animationDuration, 'easeInOutCirc');

                if(this.cfg.labelTemplate) {
                    var formattedLabel = this.cfg.labelTemplate.replace(/{value}/gi, value);
                    this.jqLabel.text(formattedLabel).show();
                }
            }

            this.value = value;
            this.jq.attr('aria-valuenow', value);
        }
    },

    getValue: function() {
        return this.value;
    },

    start: function() {
        var $this = this;

        if(this.cfg.ajax) {

            this.progressPoll = setInterval(function() {
                var options = {
                    source: $this.id,
                    process: $this.id,
                    formId: $this.cfg.formId,
                    global: $this.cfg.global,
                    async: true,
                    oncomplete: function(xhr, status, args, data) {
                        var value = args[$this.id + '_value'];
                        $this.setValue(value);

                        //trigger complete listener
                        if(value === 100) {
                            $this.fireCompleteEvent();
                        }
                    }
                };

                PrimeFaces.ajax.Request.handle(options);

            }, this.cfg.interval);
        }
    },

    fireCompleteEvent: function() {
        clearInterval(this.progressPoll);

        this.callBehavior('complete');
    },

    cancel: function() {
        clearInterval(this.progressPoll);
        this.setValue(0);
    },

    enableARIA: function() {
        this.jq.attr('role', 'progressbar')
                .attr('aria-valuemin', 0)
                .attr('aria-valuenow', this.value)
                .attr('aria-valuemax', 100);
    }

});