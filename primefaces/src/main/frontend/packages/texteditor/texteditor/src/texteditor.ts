import Quill, { type Op, type QuillOptions } from "quill";

/**
 * The clipboard module of Quill, with the fields that are read but do not exist in the Quill typings.
 */
type QuillClipboard = Quill["clipboard"] & {
    /** The clipboard container of Quill 1, `undefined` in Quill 2. */
    container?: EventTarget;
};

/**
 * The configuration for the {@link TextEditor} widget.
 */
export interface TextEditorCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg, QuillOptions {
    /** Whether this text editor is initially disabled. */
    disabled: boolean;
    /** Whether the editor toolbar should be displayed. */
    toolbarVisible: boolean;
}

/**
 * __PrimeFaces TextEditor Widget__
 *
 * Editor is an input component with rich text editing capabilities based on [Quill](https://quilljs.com/).
 */
export class TextEditor<Cfg extends TextEditorCfg = TextEditorCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {

    /**
     * The default HTML template for the toolbar of the editor. Use the appropriate CSS classes to insert a toolbar
     * button.
     */
    static toolbarTemplate: string = '<div class="p-editor-toolbar">' +
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

    /** Whether this text editor is disabled. */
    disabled: boolean = false;
    /** The current Quill text editor instance. */
    editor?: Quill;
    /** The DOM element for the container with the Quill editor. */
    editorContainer: JQuery = $();
    /** The DOM element for the hidden input field with the current value. */
    input: JQuery = $();
    /** The DOM element for the toolbar of the editor. */
    toolbar: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.disabled = (cfg.disabled === undefined) ? false : cfg.disabled;

        this.editorContainer = $(this.jqId + '_editor');
        this.input = this.jq.children('input');

        if (this.disabled) {
            this.input.attr("disabled", "disabled");
            this.jq.addClass("p-state-disabled");
            this.cfg.readOnly = true;
        }

        this.renderDeferred();
    }

    protected override _render(): void {
        //toolbar
        this.toolbar = $(this.jqId + '_toolbar');
        if (!this.toolbar.length && this.cfg.toolbarVisible) {
            this.jq.prepend(TextEditor.toolbarTemplate);
            this.toolbar = this.jq.children('.p-editor-toolbar');
            this.toolbar.attr('id', this.id + '_toolbar');
        }

        //configuration

        this.cfg.theme = 'snow';
        this.cfg.modules = {
            toolbar: this.cfg.toolbarVisible ? PrimeFaces.escapeClientId(this.id + '_toolbar') : false
        };

        //initialize
        this.editor = new Quill(PrimeFaces.escapeClientId(this.getId()) + '_editor', this.cfg);

        const events = ["keyup", "keydown", "click", "dblclick", "keypress", "mousedown", "mousemove", "mouseout",
                        "mouseover", "mouseup"];

        $.each(events, (index, value) => {
            this.registerEvent(value);
        });

        //set initial value
        this.input.val(this.getEditorValue());

        //update input on API change
        this.editor.on('text-change', (delta, oldDelta, source) => {
            if (source !== 'user') {
                this.input.val(this.getEditorValue());
                this.callBehavior('change');
            }
        });

        this.editor.on('selection-change', (range, oldRange, source) => {
            if (range && !oldRange) {
                this.callBehavior('focus');
            }
            if (!range && oldRange) {
                this.callBehavior('blur');

                // Handle change event here, quilljs text-change behaves more like an 'input' event
                if (this.input.val() !== this.getEditorValue()) {
                    this.input.val(this.getEditorValue());
                    this.callBehavior('change');
                }
            }
            if (range && oldRange) {
                this.callBehavior('select');
            }
        });

        // QuillJS doesn't handle blurring when focus is lost to e.g. a button, handle blur event separately here as a workaround
        // See https://github.com/quilljs/quill/issues/1397
        this.editor.root.addEventListener('blur', (e) => {
            // Ignore if focus has been lost to the toolbar or user pasting
            if (this.cfg.toolbarVisible && this.toolbar[0]!.contains(e.relatedTarget as Node | null) || e.relatedTarget === (this.editor!.clipboard as QuillClipboard).container) {
                return;
            }

            // MS Edge spell check completion event
            if (PrimeFaces.env.browser.msedge && !e.relatedTarget && e.target === this.editor!.root) {
                return;
            }

            // Triggers selection-change event above
            this.editor!.blur();
        });

        // #14843: Replace &nbsp; with space in clipboard operations
        this.editor.clipboard.addMatcher(Node.TEXT_NODE, function (node, delta) {
            delta.ops.forEach((op: Op) => {
                if (typeof op.insert === "string") {
                    op.insert = op.insert.replace(/&nbsp;|\u00A0/g, " ");
                }
            });

            return delta;
        });

        // #15217: QuillJS builds the text/html clipboard flavor with getSemanticHTML(), which replaces
        // every normal space with an "&nbsp;" entity (see convertHTML in quill/core/editor.js). Other
        // applications that prefer text/html therefore paste non-breaking spaces. Undo that here, right
        // before the HTML is handed to the clipboard.
        // Only the entity is replaced: a non-breaking space the user really typed is escaped as a raw
        // U+00A0 character by QuillJS and so survives the copy.
        const originalOnCopy = this.editor.clipboard.onCopy;
        this.editor.clipboard.onCopy = function (this: Quill["clipboard"], range, isCut) {
            const clipboardData = originalOnCopy.call(this, range, isCut);
            clipboardData.html = clipboardData.html.replace(/&nbsp;/g, ' ');

            return clipboardData;
        };
    }

    /**
     * Sets the content of the editor, the hidden input and calls the change behavior.
     * @param value New value to be set
     */
    setValue(value: string): void {
        this.editor!.setText(value);
        this.input.val(this.getEditorValue());
        this.callBehavior('change');
    }

    /**
     * Finds an returns the current contents of the editor.
     * @return The current contents of the editor, as an HTML string.
     */
    getEditorValue(): string {
        const html = this.editorContainer.get(0)!.children[0]!.innerHTML;
        const value = (html == '<p><br></p>') ? '' : html;

        return value;
    }

    /**
     * Clears the entire text of the editor.
     */
    clear(): void {
        this.editor!.setText('');
    }

    /**
     * Registers an event with the Quill editor and invokes the appropriate behavior when that event is triggered.
     * @param event Name of the event to register.
     */
    private registerEvent(event: string): void {
        if (this.hasBehavior(event)) {
            this.editorContainer.on(event, () => {
                this.callBehavior(event);
            });
        }
    }

    /**
     * Enables this text editor so that text can be entered.
     */
    override enable(): void {
        this.editor!.enable();
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
        this.disabled = false;
    }

    /**
     * Disables this text editor so that no text can be entered or removed.
     */
    override disable(): void {
        this.editor!.disable();
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
        this.disabled = true;
    }

}
