/**
 * The configuration for the {@link Inplace} widget.
 */
export interface InplaceCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether switching to the inline content is disabled. */
    disabled: boolean;
    /** `true` to add save and cancel buttons to the inline content. */
    editor: boolean;
    /** Effect used when toggling. */
    effect: PrimeFacesTypes.widget.Inplace.EffectType;
    /** Speed of the effect, `slow`, `normal` or `fast` (`normal` is the jQuery default duration). */
    effectSpeed: string;
    /** Client side event that shows the inline content, `dblclick` is changed to `click` on touch devices. */
    event: string;
    /** Whether the inline content can be shown by the user. */
    toggleable: boolean;
}

/**
 * __PrimeFaces Inplace Widget__
 *
 * Inplace provides easy inplace editing and inline content display. It consists of a display element, the initially
 * clickable label, and the inline content, which is shown when the display element is toggled.
 */
export class Inplace<Cfg extends InplaceCfg = InplaceCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The display element, shown while the inline content is hidden. */
    display: JQuery = $();
    /** The inline content or inplace editor. */
    content: JQuery = $();
    /** The container of the save and cancel buttons, only with `editor`. */
    editor?: JQuery;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.display = $(this.jqId + '_display');
        this.content = $(this.jqId + '_content');
        this.cfg.formId = this.jq.parents('form').first().attr('id');

        if (!this.cfg.disabled) {

            if (this.cfg.toggleable) {
                // GitHub #7948 special handling for mobile
                let touchtime = 0;
                const isDoubleTap = this.cfg.event === 'dblclick' && PrimeFaces.env.isTouchable(this.cfg);
                if (isDoubleTap) {
                    this.cfg.event = 'click';
                }

                this.display.on(this.cfg.event + '.inplace', () => {
                    if (isDoubleTap) {
                        if ((new Date().getTime() - touchtime) < 500) {
                            this.show();
                        }
                        touchtime = new Date().getTime();
                    }
                    else {
                        this.show();
                    }
                }).on('mouseover.inplace mouseout.inplace', (e) => {
                    $(e.currentTarget).toggleClass('p-state-highlight');
                });
                if (Number(this.display.attr('tabindex')) >= 0) {
                    this.display.on('keydown.inplace', (e) => {
                        if (PrimeFaces.utils.isActionKey(e)) {
                            this.display.trigger(this.cfg.event ?? '');
                            e.preventDefault();
                        }
                    }).on('focus.inplace blur.inplace', (e) => {
                        $(e.currentTarget).toggleClass('p-state-focus');
                    });
                }
            }
            else {
                this.display.css('cursor', 'default');
            }

            if (this.cfg.editor) {
                this.editor = $(this.jqId + '_editor');

                const saveButton = this.editor.children('.p-inplace-save');
                const cancelButton = this.editor.children('.p-inplace-cancel');

                PrimeFaces.skinButton(saveButton).skinButton(cancelButton);

                saveButton.on('click.inplace', (e) => {
                    this.save(e);
                });
                cancelButton.on('click.inplace', (e) => {
                    this.cancel(e);
                });
            }

            // to enter space in inplace input within multi-selection dataTable
            this.content.find('input:text,textarea').on('keydown.inplace-text', (e) => {
                if (e.key === ' ') {
                    e.stopPropagation();
                }
            });
        }
    }

    /**
     * Switches to editing mode and displays the inline content.
     */
    show(): void {
        this.toggle(this.content, this.display);
    }

    /**
     * Leaves editing mode and hides the inline content.
     */
    hide(): void {
        this.toggle(this.display, this.content);
    }

    /**
     * Hides the display element and shows the inline content, or vice versa.
     * @param elToShow Element to show, either the display element or the inline content.
     * @param elToHide Element to hide, either the display element or the inline content.
     */
    toggle(elToShow: JQuery, elToHide: JQuery): void {
        if (this.cfg.effect === 'fade') {
            elToHide.fadeOut(this.cfg.effectSpeed as JQuery.Duration, () => {
                elToShow.fadeIn(this.cfg.effectSpeed as JQuery.Duration);
                this.postShow();
            });
        }
        else if (this.cfg.effect === 'slide') {
            elToHide.slideUp(this.cfg.effectSpeed as JQuery.Duration, () => {
                elToShow.slideDown(this.cfg.effectSpeed as JQuery.Duration);
                this.postShow();
            });
        }
        else if (this.cfg.effect === 'none') {
            elToHide.hide();
            elToShow.show();
            this.postShow();
        }
    }

    /**
     * Focuses the first input of the inline content and renders deferred children.
     */
    private postShow(): void {
        this.content.find('input:text,textarea').filter(':visible:enabled').first().trigger('focus').trigger('select');

        PrimeFaces.invokeDeferredRenders(this.id);
    }

    /**
     * @returns The display element, shown while the inline content is hidden.
     */
    getDisplay(): JQuery {
        return this.display;
    }

    /**
     * @returns The inline content or inplace editor.
     */
    getContent(): JQuery {
        return this.content;
    }

    /**
     * Saves the value of the inplace editor via AJAX, the response updates the component.
     * @param _e The event which triggered the saving, unused.
     */
    save(_e?: JQuery.TriggeredEvent): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            formId: this.cfg.formId
        };

        if (this.hasBehavior('save')) {
            this.callBehavior('save', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Discards the changes of the inplace editor via AJAX, the response updates the component.
     * @param _e The event which triggered the cancellation, unused.
     */
    cancel(_e?: JQuery.TriggeredEvent): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            formId: this.cfg.formId,
            params: [
                { name: this.getId() + '_cancel', value: true }
            ]
        };

        if (this.hasBehavior('cancel')) {
            this.callBehavior('cancel', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }
}
