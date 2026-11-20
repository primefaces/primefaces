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
 * @prop {boolean} cfg.toolbarVisible Whether the editor toolbar should be displayed.
 * @prop {string} [cfg.ariaLabel] ARIA label of the editing area.
 * @prop {string} [cfg.ariaLabelledBy] ID of the label of the editing area.
 * @prop {boolean} [cfg.ariaRequired] Whether a value is required.
 * @prop {boolean} [cfg.ariaInvalid] Whether the value is invalid.
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
     * ARIA locale keys of the toolbar controls, by `format` or `format:value`.
     * @type {Record<string, string>}
     */
    ariaKeys: {
        'font': 'texteditor.FONT',
        'font:': 'texteditor.SANS_SERIF',
        'font:serif': 'texteditor.SERIF',
        'font:monospace': 'texteditor.MONOSPACE',
        'size': 'texteditor.SIZE',
        'size:': 'texteditor.NORMAL',
        'size:small': 'texteditor.SMALL',
        'size:large': 'texteditor.LARGE',
        'size:huge': 'texteditor.HUGE',
        'header': 'texteditor.HEADING',
        'header:': 'texteditor.NORMAL',
        'bold': 'texteditor.BOLD',
        'italic': 'texteditor.ITALIC',
        'underline': 'texteditor.UNDERLINE',
        'strike': 'texteditor.STRIKE',
        'color': 'texteditor.COLOR',
        'color:': 'texteditor.DEFAULT',
        'background': 'texteditor.BACKGROUND',
        'background:': 'texteditor.DEFAULT',
        'script:sub': 'texteditor.SUBSCRIPT',
        'script:super': 'texteditor.SUPERSCRIPT',
        'blockquote': 'texteditor.BLOCKQUOTE',
        'code': 'texteditor.CODE',
        'code-block': 'texteditor.CODE_BLOCK',
        'list:ordered': 'texteditor.ORDERED_LIST',
        'list:bullet': 'texteditor.BULLET_LIST',
        'list:check': 'texteditor.CHECK_LIST',
        'indent:-1': 'texteditor.OUTDENT',
        'indent:+1': 'texteditor.INDENT',
        'direction:rtl': 'texteditor.RTL',
        'align': 'texteditor.ALIGN',
        'align:': 'texteditor.ALIGN_LEFT',
        'align:center': 'texteditor.ALIGN_CENTER',
        'align:right': 'texteditor.ALIGN_RIGHT',
        'align:justify': 'texteditor.ALIGN_JUSTIFY',
        'link': 'texteditor.LINK',
        'image': 'texteditor.IMAGE',
        'video': 'texteditor.VIDEO',
        'formula': 'texteditor.FORMULA',
        'clean': 'texteditor.CLEAN'
    },

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
            this.cfg.readOnly = true;
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

        this.cfg.theme = 'snow';
        this.cfg.modules = {
            toolbar: this.cfg.toolbarVisible ? PrimeFaces.escapeClientId(this.id + '_toolbar') : false
        };

        //initialize
        this.editor = new Quill(PrimeFaces.escapeClientId(this.id) + '_editor', this.cfg);
        this.bindAria();

        var events = ["keyup", "keydown", "click", "dblclick", "keypress", "mousedown", "mousemove", "mouseout",
                        "mouseover", "mouseup"];

        $.each(events, function(index, value) {
            $this.registerEvent(value);
        });

        //set initial value
        this.input.val(this.getEditorValue());
        
        //update input on change
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

            $this.editor.blur(); // Triggers selection-change event above
        });
    },

    /**
     * Sets the content of the editor, the hidden input and calls the change behavior.
     * @param {string} value New value to be set
     */
    setValue: function(value) {
        this.editor.setText(value);
        this.input.val(this.getEditorValue());
        this.callBehavior('change');
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
     * Makes the contenteditable of Quill a named textbox; the generic name is a fallback without any label.
     * @private
     */
    bindAria: function() {
        $(this.editor.root).attr({
            'role': 'textbox',
            'aria-multiline': 'true',
            'aria-label': this.cfg.ariaLabel || (this.cfg.ariaLabelledBy ? null : PrimeFaces.getAriaLabel('texteditor.EDITOR')),
            'aria-labelledby': this.cfg.ariaLabelledBy || null,
            'aria-required': this.cfg.ariaRequired ? 'true' : null,
            'aria-invalid': this.cfg.ariaInvalid ? 'true' : null,
            // disabled also sets cfg.readOnly, see init
            'aria-readonly': this.cfg.readOnly && !this.disabled ? 'true' : null,
            'aria-disabled': this.disabled ? 'true' : null,
            'aria-placeholder': this.cfg.placeholder || null
        });

        this.bindLabel();
        this.nameToolbar();
        this.name(this.jq.find('.ql-tooltip input[type="text"]'), PrimeFaces.getAriaLabel('texteditor.URL'));
    },

    /**
     * Focuses the editor on a click on its label. The label is not part of this widget, so it can survive an
     * update of this widget.
     * @private
     */
    bindLabel: function() {
        if (!this.cfg.ariaLabelledBy) {
            return;
        }
        var $this = this;
        var label = $(PrimeFaces.escapeClientId(this.cfg.ariaLabelledBy));
        var labelClickEvent = 'click.texteditor.' + this.id;
        label.off(labelClickEvent).on(labelClickEvent, function() {
            if (!$this.disabled && $this.editor) {
                $this.editor.focus();
            }
        });
        this.addDestroyListener(function() {
            label.off(labelClickEvent);
        });
    },

    /**
     * Quill leaves the controls of a toolbar given as HTML unnamed; existing aria-labels, e.g. of a facet, are kept.
     * @private
     */
    nameToolbar: function() {
        if (!this.cfg.toolbarVisible) {
            return;
        }
        var $this = this;
        if (!this.toolbar.attr('role')) {
            this.toolbar.attr('role', 'toolbar');
        }
        this.name(this.toolbar, PrimeFaces.getAriaLabel('texteditor.TOOLBAR'));

        this.toolbar.find('button').each(function(i, btn) {
            var format = $this.format(btn);
            if (format) {
                $this.name($(btn), $this.ariaText(format, btn.getAttribute('value')));
            }
        });

        this.toolbar.find('.ql-picker').each(function(i, picker) {
            var format = $this.format(picker);
            if (!format) {
                return;
            }
            var jq = $(picker);
            $this.name(jq.children('.ql-picker-label'), $this.ariaText(format, null));
            jq.find('.ql-picker-item').each(function(j, item) {
                var value = item.getAttribute('data-value') || '';
                $this.name($(item), $this.ariaText(format, value, item.getAttribute('data-label') || value || format));
            });
        });
    },

    /**
     * Finds the ARIA label of a toolbar control.
     * @private
     * @param {string} format Quill format of the control, e.g. `bold`.
     * @param {string | null} value `null` for the control itself, an empty string for the default picker item.
     * @param {string} [fallback] Text for a format without a label, defaults to the format.
     * @return {string} The ARIA label.
     */
    ariaText: function(format, value, fallback) {
        if (format === 'header' && value) {
            return PrimeFaces.getAriaLabel('texteditor.HEADING_LEVEL').replace('{level}', value);
        }
        var key = this.ariaKeys[value === null ? format : format + ':' + value];
        return key ? PrimeFaces.getAriaLabel(key) : (fallback || format);
    },

    /**
     * Finds the Quill format from the first `ql-*` class, e.g. `bold`; pickers also get `ql-picker`,
     * `ql-color-picker`, ...
     * @private
     * @param {Element} el A toolbar control.
     * @return {string | undefined} The Quill format of the control.
     */
    format: function(el) {
        var cls = (el.getAttribute('class') || '').split(/\s+/).filter(function(c) {
            return c.indexOf('ql-') === 0 && !/(^ql-|-)picker$/.test(c);
        })[0];
        return cls ? cls.substring(3) : undefined;
    },

    /**
     * Sets the aria-label unless one is present.
     * @private
     * @param {JQuery} jq Elements to name.
     * @param {string} text The ARIA label.
     */
    name: function(jq, text) {
        jq.each(function(i, el) {
            if (!el.getAttribute('aria-label')) {
                el.setAttribute('aria-label', text);
            }
        });
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
        this.editor.root.removeAttribute('aria-disabled');
        this.editor.root.removeAttribute('aria-readonly');
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
        this.disabled = false;
    },

    /**
     * Disables this text editor so that no text can be entered or removed.
     */
    disable: function () {
        this.editor.disable();
        this.editor.root.setAttribute('aria-disabled', 'true');
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
        this.disabled = true;
    }

});