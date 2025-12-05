import Quill from "quill";

/**
 * __PrimeFaces TextEditor Widget__
 * 
 * Editor is an input component with rich text editing capabilities based on [Quill](https://quilljs.com/).
 * 
 * @prop {boolean} disabled Whether this text editor is disabled.
 * @prop {Quill} editor The current Quill text editor instance.
 * @prop {JQuery} editorContainer The DOM element for the container with the Quill editor.
 * @prop {JQuery} input The DOM element for the hidden input field with the current value.
 * @prop {JQuery} toolbar The DOM element for the toolbar of the editor.
 * 
 * @interface {PrimeFaces.widget.TextEditorCfg} cfg The configuration for the {@link  TextEditor| TextEditor widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * @extends {ExpandedQuillOptions} cfg
 * 
 * @prop {boolean} cfg.disabled Whether this text editor is initially disabled.
 * @prop {boolean} cfg.toolbarVisible Whether the editor toolbar should be displayed.
 */
PrimeFaces.widget.TextEditor = class TextEditor extends PrimeFaces.widget.DeferredWidget {

    /**
     * The default HTML template for the toolbar of the editor. Use the appropriate CSS classes to insert a toolbar
     * button.
     * @type {string}
     */
    static toolbarTemplate = '<div class="ui-editor-toolbar">' +
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
            '</div>';

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.disabled = (cfg.disabled === undefined) ? false : cfg.disabled;

        this.editorContainer = $(this.jqId + '_editor');
        this.input = this.jq.children('input');

        if (this.disabled) {
            this.input.attr("disabled", "disabled");
            this.jq.addClass("ui-state-disabled");
            this.cfg.readOnly = true;
        }

        this.renderDeferred();
    }

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render() {
        var $this = this;

        //toolbar
        this.toolbar = $(this.jqId + '_toolbar');
        if(!this.toolbar.length && this.cfg.toolbarVisible) {
            this.jq.prepend(TextEditor.toolbarTemplate);
            this.toolbar = this.jq.children('.ui-editor-toolbar')
            this.toolbar.attr('id', this.id + '_toolbar');
        }

        //configuration

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
        
        //update input on API change
        this.editor.on('text-change', function(delta, oldDelta, source) {
            if (source !== 'user') {
                $this.input.val($this.getEditorValue());
                $this.callBehavior('change');
            }
        });

        this.editor.on('selection-change', function(range, oldRange, source) {
            if(range && !oldRange) {
                $this.callBehavior('focus');
            }
            if(!range && oldRange) {
                $this.callBehavior('blur');

                // Handle change event here, quilljs text-change behaves more like an 'input' event
                if ($this.input.val() !== $this.getEditorValue()) {
                    $this.input.val($this.getEditorValue());
                    $this.callBehavior('change');
                }
            }
            if(range && oldRange) {
                $this.callBehavior('select');
            }
        });

        // QuillJS doesn't handle blurring when focus is lost to e.g. a button, handle blur event separately here as a workaround
        // See https://github.com/quilljs/quill/issues/1397
        this.editor.root.addEventListener('blur', function(e) {
            // Ignore if focus has been lost to the toolbar or user pasting
            if ($this.cfg.toolbarVisible && $this.toolbar[0].contains(e.relatedTarget) || e.relatedTarget === $this.editor.clipboard.container) {
                return;
            }

            // MS Edge spell check completion event
            if (!PrimeFaces.env.browser.safari && !e.relatedTarget && e.target === $this.editor.root) {
                return;
            }

            // Triggers selection-change event above
            $this.editor.blur();
        });
    }

    /**
     * Sets the content of the editor, the hidden input and calls the change behavior.
     * @param {string} value New value to be set
     */
    setValue(value) {
        this.editor.setText(value);
        this.input.val(this.getEditorValue());
        this.callBehavior('change');
    }

    /**
     * Finds an returns the current contents of the editor.
     * @return {string} The current contents of the editor, as an HTML string.
     */
    getEditorValue() {
        var html = this.editorContainer.get(0).children[0].innerHTML;
        var value = (html == '<p><br></p>') ? '' : html;

        return value;
    }

    /**
     * Clears the entire text of the editor.
     */
    clear() {
        this.editor.setText('');
    }

    /**
     * Registers an event with the Quill editor and invokes the appropriate behavior when that event is triggered.
     * @private
     * @param {string} event Name of the event to register. 
     */
    registerEvent(event) {
        var $this = this;
        if(this.hasBehavior(event)) {
            this.editorContainer.on(event, function () {
                $this.callBehavior(event);
            });
        }
    }

    /**
     * Enables this text editor so that text can be entered.
     */
    enable() {
        this.editor.enable();
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
        this.disabled = false;
    }

    /**
     * Disables this text editor so that no text can be entered or removed.
     */
    disable() {
        this.editor.disable();
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
        this.disabled = true;
    }

}