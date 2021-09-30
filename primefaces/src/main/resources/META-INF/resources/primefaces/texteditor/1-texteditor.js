/**
 * __PrimeFaces TextEditor Widget__
 * 
 * Editor is an input component with rich text editing capabilities based on [Quill](https://quilljs.com/).
 * 
 * @typedef {import("quill").QuillOptionsStatic} PrimeFaces.widget.TextEditor.QuillOptionsStatic Type alias for the
 * Quill editor options, needed for technical reasons. 
 * 
 * @prop {boolean} disabled Whether this text editor is disabled.
 * @prop {import("quill").Quill} editor The current Quill text editor instance.
 * @prop {JQuery} editorContainer The DOM element for the container with the Quill editor.
 * @prop {JQuery} input The DOM element for the hidden input field with the current value.
 * @prop {JQuery} toolbar The DOM element for the toolbar of the editor.
 * 
 * @interface {PrimeFaces.widget.TextEditorCfg} cfg The configuration for the {@link  TextEditor| TextEditor widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * @extends {PrimeFaces.widget.TextEditor.QuillOptionsStatic} cfg
 * 
 * @prop {boolean} cfg.disabled Whether this text editor is initially disabled.
 * @prop {number} cfg.height The height of the editor.
 * @prop {boolean} cfg.toolbarVisible Whether the editor toolbar should be displayed.
 */
PrimeFaces.widget.TextEditor = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * The default HTML template for the toolbar of the editor. Use the appopriate classes to insert a toolbar button.
     * @type {string}
     */
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

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.disabled = (cfg.disabled === undefined) ? false : cfg.disabled;

        this.editorContainer = $(this.jqId + '_editor');
        this.input = this.jq.children('input');

        if (this.disabled) {
            this.input.attr("disabled", "disabled");
            this.jq.addClass("ui-state-disabled");
        }

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
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

    /**
     * Finds an returns the current contents of the editor.
     * @return {string} The current contents of the editor, as an HTML string.
     */
    getEditorValue: function() {
        var html = this.editorContainer.get(0).children[0].innerHTML;
        var value = (html == '<p><br></p>') ? '' : html;

        return value;
    },

    /**
     * Clears the entire text of the editor.
     */
    clear: function() {
        this.editor.setText('');
    },

    /**
     * Registers an event with the Quill editor and invokes the appropriate behavior when that event is triggered.
     * @private
     * @param {string} event Name of the event to register. 
     */
    registerEvent: function(event) {
        var $this = this;
        if(this.hasBehavior(event)) {
            this.editorContainer.on(event, function () {
                $this.callBehavior(event);
            });
        }
    },

    /**
     * Enables this text editor so that text can be entered.
     */
    enable: function () {
        this.editor.enable();
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
        this.disabled = false;
    },

    /**
     * Disables this text editor so that no text can be entered or removed.
     */
    disable: function () {
        this.editor.disable();
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
        this.disabled = true;
    }

});