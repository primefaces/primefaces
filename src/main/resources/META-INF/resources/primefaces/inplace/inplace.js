/**
 * PrimeFaces Inplace Widget
 */
PrimeFaces.widget.Inplace = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.display = $(this.jqId + '_display');
        this.content = $(this.jqId + '_content');
        this.cfg.formId = this.jq.parents('form:first').attr('id');

        var $this = this;

        if(!this.cfg.disabled) {

            if(this.cfg.toggleable) {
                this.display.bind(this.cfg.event, function(){
                    $this.show();
                });

                this.display.mouseover(function(){
                    $(this).toggleClass("ui-state-highlight");
                }).mouseout(function(){
                    $(this).toggleClass("ui-state-highlight");
                });
            }
            else {
                this.display.css('cursor', 'default');
            }

            if(this.cfg.editor) {
                this.cfg.formId = $(this.jqId).parents('form:first').attr('id');

                this.editor = $(this.jqId + '_editor');

                var saveButton = this.editor.children('.ui-inplace-save'),
                cancelButton = this.editor.children('.ui-inplace-cancel');

                PrimeFaces.skinButton(saveButton).skinButton(cancelButton);

                saveButton.click(function(e) {$this.save(e)});
                cancelButton.click(function(e) {$this.cancel(e)});
            }

            /* to enter space in inplace input within multi-selection dataTable */
            this.content.find('input:text,textarea').on('keydown.inplace-text', function(e) {
                var keyCode = $.ui.keyCode;

                if(e.which === keyCode.SPACE) {
                    e.stopPropagation();
                }
            });
        }
    },

    show: function() {
        this.toggle(this.content, this.display);
    },

    hide: function() {
        this.toggle(this.display, this.content);
    },

    toggle: function(elToShow, elToHide) {
        var $this = this;

        if(this.cfg.effect === 'fade') {
            elToHide.fadeOut(this.cfg.effectSpeed,
                function() {
                    elToShow.fadeIn($this.cfg.effectSpeed);
                    $this.postShow();
                });
        }
        else if(this.cfg.effect === 'slide') {
            elToHide.slideUp(this.cfg.effectSpeed,
                function() {
                    elToShow.slideDown($this.cfg.effectSpeed);
                    $this.postShow();
            });
        }
        else if(this.cfg.effect === 'none') {
            elToHide.hide();
            elToShow.show();
            $this.postShow();
        }
    },

    postShow: function() {
        this.content.find('input:text,textarea').filter(':visible:enabled:first').focus().select();

        PrimeFaces.invokeDeferredRenders(this.id);
    },

    getDisplay: function() {
        return this.display;
    },

    getContent: function() {
        return this.content;
    },

    save: function(e) {
        var options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId
        };

        if(this.hasBehavior('save')) {
            this.callBehavior('save', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    cancel: function(e) {
        var options = {
            source: this.id,
            update: this.id,
            process: this.id,
            formId: this.cfg.formId
        };

        options.params = [
            {name: this.id + '_cancel', value: true}
        ];

        if(this.hasBehavior('cancel')) {
            this.callBehavior('cancel', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

});
