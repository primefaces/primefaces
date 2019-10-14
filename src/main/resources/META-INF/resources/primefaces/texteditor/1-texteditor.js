/**
 * PrimeFaces TextEditor Widget
 */
PrimeFaces.widget.TextEditor = PrimeFaces.widget.DeferredWidget.extend({

    toolbarTemplate: '<div class="ui-editor-toolbar">' +
                '<span class="ql-formats">' +
                    '<select class="ql-font"></select>' +
                    '<select class="ql-size"></select>' +
                '</span>' +
                '<span class="ql-formats">' +
                    '<button class="ql-bold"></button>' +
                    '<button class="ql-italic"></button>' +
                    '<button class="ql-underline"></button>' +
                    '<button class="ql-strike"></button>' +
                '</span>' +
                '<span class="ql-formats">' +
                    '<select class="ql-color"></select>' +
                    '<select class="ql-background"></select>' +
                '</span>' +
                '<span class="ql-formats">' +
                    '<button class="ql-script" value="sub"></button>' +
                    '<button class="ql-script" value="super"></button>' +
                '</span>' +
                '<span class="ql-formats">' +
                    '<button class="ql-header" value="1"></button>' +
                    '<button class="ql-header" value="2"></button>' +
                    '<button class="ql-blockquote"></button>' +
                    '<button class="ql-code-block"></button>' +
                '</span>' +
                '<span class="ql-formats">' +
                    '<button class="ql-list" value="ordered"></button>' +
                    '<button class="ql-list" value="bullet"></button>' +
                    '<button class="ql-indent" value="-1"></button>' +
                    '<button class="ql-indent" value="+1"></button>' +
                '</span>' +
                '<span class="ql-formats">' +
                    '<button class="ql-direction" value="rtl"></button>' +
                    '<select class="ql-align"></select>' +
                '</span>' +
                '<span class="ql-formats">' +
                    '<button class="ql-link"></button>' +
                    '<button class="ql-image"></button>' +
                    '<button class="ql-video"></button>' +
                '</span>' +
                '<span class="ql-formats">' +
                    '<button class="ql-clean"></button>' +
                '</span>' +
            '</div>',

    init: function(cfg) {
        this._super(cfg);

        this.editorContainer = $(this.jqId + '_editor');
        this.input = this.jq.children('input');
        this.renderDeferred();
    },

    _render: function() {
        var $this = this;

        //toolbar
        this.toolbar = $(this.jqId + '_toolbar');
        if(!this.toolbar.length && this.cfg.toolbarVisible) {
            this.jq.prepend(this.toolbarTemplate);
            this.toolbar = this.jq.children('.ui-editor-toolbar')
            this.toolbar.attr('id', this.id + '_toolbar');
        }

        //configuration
        if(this.cfg.height) {
            this.editorContainer.height(this.cfg.height);
        }

        this.cfg.theme = 'snow';
        this.cfg.modules = {
            toolbar: this.cfg.toolbarVisible ? PrimeFaces.escapeClientId(this.id + '_toolbar') : false
        };

        //initialize
        this.editor = new Quill(PrimeFaces.escapeClientId(this.id) + '_editor', this.cfg);

        var events = ["keyup", "keydown", "click", "dblclick", "keypress", "mousedown", "mousemove", "mouseout",
                        "mouseover", "mouseup"];

        $.each(events, function(index, value) {
            $this.registerEvent(value);
        });

        //set initial value
        this.input.val(this.getEditorValue());

        //update input on change
        this.editor.on('text-change', function(delta, oldDelta, source) {
            $this.input.val($this.getEditorValue());
            $this.callBehavior('change');
        });
        this.editor.on('selection-change', function(range, oldRange, source) {
            if(range && !oldRange) {
                $this.callBehavior('focus');
            }
            if(!range && oldRange) {
                $this.callBehavior('blur');
            }
            if(range && oldRange) {
                $this.callBehavior('select');
            }
        });

    },

    getEditorValue: function() {
        var html = this.editorContainer.get(0).children[0].innerHTML;
        var value = (html == '<p><br></p>') ? '' : html;

        return value;
    },

    clear: function() {
        this.editor.setText('');
    },

    registerEvent: function(event) {
        var $this = this;
        if(this.hasBehavior(event)) {
            this.editorContainer.on(event, function () {
                $this.callBehavior(event);
            });
        }
    }

});