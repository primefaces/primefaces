/**
 * The configuration for the {@link BlockUI} widget.
 */
export interface BlockUICfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether the blocker is shown and hidden with a fade animation, normalized to a boolean on init. */
    animate: boolean;
    /** Search expression of the component(s) to block. */
    block: string;
    /** Whether the target is blocked initially, normalized to a boolean on init. */
    blocked: boolean;
    /** Delay in milliseconds before the blocker is shown. */
    delay: number;
    /** Style class of the blocker. */
    styleClass: string;
    /** Search expression of the components whose requests show the blocker. */
    triggers: string;
}

/**
 * __PrimeFaces BlockUI Widget__
 *
 * BlockUI is used to block interactivity of Faces components with optional AJAX integration.
 */
export class BlockUI<Cfg extends BlockUICfg = BlockUICfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The blocked component(s). */
    target: JQuery = $();
    /** The content shown on the blocker(s), one per target with several targets. */
    content: JQuery = $();
    /** The blocker(s), one per target. */
    blocker: JQuery = $();
    /** Aligns the blocker when the target changes. */
    resizeHandler?: PrimeFacesTypes.Controllable;
    /** The timer ID of the delayed show. */
    timeout?: number | null;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.block);
        this.content = this.jq;
        this.cfg.animate = this.cfg.animate !== false;
        this.cfg.blocked = !!this.cfg.blocked;

        this.render();

        if (this.cfg.triggers) {
            // #10970 must wait until all components have been rendered and ready
            PrimeFaces.queueTask(() => { this.bindTriggers(); }, 1);
        }

        this.bindResizer();

        if (this.cfg.blocked) {
            this.show();
        }
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        // an update of this widget itself renders the new element in place, inside of the blocker in the body,
        // so move it out to keep it when the blocker is removed
        this.blocker.children(this.jqId).appendTo(document.body);
        this._cleanup();
        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();
        this._cleanup();
    }

    /**
     * Removes the blocker and the content and restores the target.
     */
    private _cleanup(): void {
        this.deleteTimeout();
        this.content.remove();
        this.blocker.remove();
        this.jq.remove();
        this.restoreTargetState();
        $(document).off('pfAjaxSend.' + this.id + ' pfAjaxUpdated.' + this.id + ' pfAjaxComplete.' + this.id);
    }

    private bindResizer(): void {
        this.resizeHandler = PrimeFaces.utils.registerMutationObserver(this, this.target, () => {
            if (this.isBlocking()) {
                this.alignOverlay(false);
            }
        });
    }

    private bindTriggers(): void {
        // listen global ajax send and complete callbacks
        $(document).on('pfAjaxSend.' + this.id, (_e, _xhr, settings) => {
            if (!this.cfg.blocked && PrimeFaces.ajax.Utils.isXhrSourceATrigger(this, settings, true)) {
                this.show();
            }
            else {
                // subscribe to all DOM update events so we can resize even if another DOM element changed
                PrimeFaces.queueTask(() => { this.alignOverlay(); });
            }
        }).on('pfAjaxComplete.' + this.id, (_e, _xhr, settings, args) => {
            // #14913 if it's redirect don't hide, so the current page stays blocked until the new page is ready
            if (args && args.redirect) {
                return;
            }
            if (!this.cfg.blocked && PrimeFaces.ajax.Utils.isXhrSourceATrigger(this, settings, false)) {
                this.hide();
            }
        }).on('pfAjaxUpdated.' + this.id, (_e, _xhr, settings) => {
            // subscribe to all DOM update events so we can resize even if another DOM element changed
            if (!this.cfg.blocked && !PrimeFaces.ajax.Utils.isXhrSourceATrigger(this, settings, true)) {
                PrimeFaces.queueTask(() => { this.alignOverlay(); });
            }
        });
    }

    /**
     * Shows the blocker, after the configured delay.
     * @param duration Duration of the animation in milliseconds, or `fast` / `slow`.
     */
    show(duration?: number | string): void {
        if (this.isBlocking()) {
            return;
        }

        const delay = this.cfg.delay || 0;
        this.timeout = PrimeFaces.queueTask(() => {
            // #10484: if delayed and AJAX event already finished
            // blocked on the server: not shown by a request, blocked without one
            if ((this.cfg.triggers || delay > 0) && !this.cfg.blocked && PrimeFaces.ajax.Queue.isEmpty()) {
                PrimeFaces.warn('BlockUI AJAX event completed before showing the block.');
                return;
            }
            this.alignOverlay();

            const animated = this.cfg.animate;
            if (animated) {
                this.blocker.fadeIn(duration as JQuery.Duration);
            }
            else {
                this.blocker.show(duration as JQuery.Duration);
            }

            if (this.hasContent()) {
                if (animated) {
                    this.content.fadeIn(duration as JQuery.Duration);
                }
                else {
                    this.content.show(duration as JQuery.Duration);
                }
            }

            // prevent mouse events
            this.target.attr('aria-busy', 'true').css({
                'pointer-events': 'none',
                'user-select': 'none'
            });
            // prevent keyboard focus from entering the blocked area
            this.target.find(':tabbable').each((_i, el) => {
                const $element = $(el);
                const currentTabIndex = $element.attr('tabindex');
                $element.attr('data-bui-tabindex', currentTabIndex || 0).attr('tabindex', -1);
            });
        }, delay);
    }

    /**
     * Hides the blocker.
     * @param duration Duration of the animation in milliseconds, or `fast` / `slow`.
     */
    hide(duration?: number | string): void {
        if (!this.isBlocking()) {
            return;
        }
        this.deleteTimeout();
        const animated = this.cfg.animate;
        const hasContent = this.hasContent();
        const resetPositionCallback = () => {
            for (const targetElement of this.target) {
                const currentTarget = $(targetElement);
                const previousPosition = currentTarget.data('p-position');
                if (previousPosition) {
                    currentTarget.css('position', previousPosition);
                }
            }
        };
        const callback = () => {
            if (!hasContent) {
                resetPositionCallback();
            }
        };

        if (animated) {
            this.blocker.fadeOut(duration as JQuery.Duration, callback);
        }
        else {
            this.blocker.hide((duration || 0) as JQuery.Duration, callback);
        }

        if (hasContent) {
            if (animated) {
                this.content.fadeOut(duration as JQuery.Duration, resetPositionCallback);
            }
            else {
                this.content.hide((duration || 0) as JQuery.Duration, resetPositionCallback);
            }
        }

        this.restoreTargetState();
    }

    /**
     * Restores mouse events and keyboard focus on the target.
     */
    private restoreTargetState(): void {
        // restore mouse events on the target
        this.target.attr('aria-busy', 'false').css({
            'pointer-events': 'auto',
            'user-select': 'auto'
        });
        // restore keyboard focus on the blocked area
        this.target.find('[data-bui-tabindex]').each((_i, el) => {
            const $element = $(el);
            const originalTabIndex = $element.attr('data-bui-tabindex') as string;
            $element.attr('tabindex', originalTabIndex).removeAttr('data-bui-tabindex');
        });
    }

    /**
     * Creates a blocker for each target and moves the content into it.
     */
    private render(): void {
        const widgetId = this.getId();
        const shouldClone = this.hasMultipleTargets() && this.hasContent();
        // there can be 1 to N targets
        for (const targetElement of this.target) {
            const currentTarget = $(targetElement);
            const currentTargetId = currentTarget.attr('id') || widgetId;
            let currentContent = this.jq;

            // create a specific blocker for this target
            const currentBlocker = $('<div id="' + widgetId + '_' + currentTargetId + '_blocker" class="p-blockui p-widget-overlay p-hidden"></div>');

            // style the blocker
            if (this.cfg.styleClass) {
                currentBlocker.addClass(this.cfg.styleClass);
            }

            // when more than 1 target need to clone the content for each target
            if (shouldClone) {
                currentContent = currentContent.clone();
                currentContent.attr('id', widgetId + '_' + currentTargetId + '_blockcontent');
            }

            // assign data ids to this widget
            currentBlocker.attr('data-bui-overlay', widgetId);
            currentContent.attr('data-bui-content', widgetId);

            // ARIA
            currentTarget.attr('aria-busy', String(this.cfg.blocked));

            // append the blocker to the document
            $(document.body).append(currentBlocker);
            currentBlocker.append(currentContent);
        }

        // assign all matching blockers to widget
        this.blocker = $('[data-bui-overlay~="' + widgetId + '"]');
        this.content = $('[data-bui-content~="' + widgetId + '"]');

        // set the size and position to match the target
        this.alignOverlay();
    }

    /**
     * Sets the size and position of the blockers and the content to cover the targets.
     * @param forceUpdate `false` to update the styles only if the size or position has changed.
     */
    private alignOverlay(forceUpdate: boolean = true): void {
        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.block);

        // center position of content
        for (let i = 0; i < this.target.length; i++) {
            const currentTarget = $(this.target[i] as HTMLElement);
            const blocker = $(this.blocker[i] as HTMLElement);
            const content = $(this.content[i] as HTMLElement);

            // configure the target positioning
            const position = currentTarget.css('position');
            if (position !== 'fixed' && position !== 'absolute') {
                currentTarget.data('p-position', position).css('position', 'relative');
            }

            // set the size and position to match the target
            const height = currentTarget.outerHeight();
            const width = currentTarget.outerWidth();
            const offset = currentTarget.offset();

            // Only update blocker CSS if dimensions changed
            const currentBlockerHeight = blocker.height();
            const currentBlockerWidth = blocker.width();
            const currentBlockerOffset = blocker.offset();

            if (forceUpdate || (height !== currentBlockerHeight
                    || width !== currentBlockerWidth
                    || offset?.left !== currentBlockerOffset?.left
                    || offset?.top !== currentBlockerOffset?.top)) {

                blocker.css({
                    'height': height + 'px',
                    'width': width + 'px',
                    'left': offset?.left + 'px',
                    'top': offset?.top + 'px',
                    'z-index': PrimeFaces.nextZindex()
                });
            }

            const contentElement = this.content[i];
            if (this.hasContent() && contentElement) {
                let contentHeight = content.outerHeight() ?? 0;
                let contentWidth = content.outerWidth() ?? 0;

                // #9496 if display:none then we need to clone to get its dimensions
                if ((content.height() ?? 0) <= 0) {
                    const currentWidth = contentElement.getBoundingClientRect().width;
                    const styleWidth = currentWidth ? 'width: ' + currentWidth + 'px' : '';
                    const clone = contentElement.cloneNode(true) as HTMLElement;
                    clone.style.cssText = 'position: fixed; top: 0; left: 0; overflow: auto; visibility: hidden; pointer-events: none; height: unset; '
                            + 'max-height: unset;' + styleWidth;
                    document.body.append(clone);
                    const jqClone = $(clone);
                    contentHeight = jqClone.outerHeight() ?? 0;
                    contentWidth = jqClone.outerWidth() ?? 0;
                    jqClone.remove();
                }

                // Only update content CSS if position changed
                const currentContentLeft = parseFloat(content.css('left')) || 0;
                const currentContentTop = parseFloat(content.css('top')) || 0;
                const newContentLeft = (((blocker.width() ?? 0) - contentWidth) / 2);
                const newContentTop = (((blocker.height() ?? 0) - contentHeight) / 2);
                if (forceUpdate || (newContentLeft !== currentContentLeft || newContentTop !== currentContentTop)) {
                    content.css({
                        'left': newContentLeft + 'px',
                        'top': newContentTop + 'px',
                        'z-index': PrimeFaces.nextZindex()
                    });
                }
            }
        }
    }

    /**
     * @returns `true` if the blocker has content.
     */
    hasContent(): boolean {
        return this.content.contents().length > 0;
    }

    /**
     * @returns `true` if more than one component is blocked.
     */
    private hasMultipleTargets(): boolean {
        return this.target.length > 1;
    }

    /**
     * @returns `true` if the blocker is shown.
     */
    isBlocking(): boolean {
        return this.blocker.is(':visible');
    }

    /**
     * Clears the timer of the delayed show.
     */
    private deleteTimeout(): void {
        clearTimeout(this.timeout ?? undefined);
        this.timeout = null;
    }
}
