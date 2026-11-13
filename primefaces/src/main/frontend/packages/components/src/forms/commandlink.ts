/**
 * The configuration for the {@link CommandLink} component.
 */
export interface CommandLinkCfg extends PrimeFaces.component.BaseComponentCfg {
}

/**
 * __PrimeFaces CommandLink Component__
 *
 * CommandLink is an extended version of standard commandLink with AJAX and theming.
 */
export class CommandLink<Cfg extends CommandLinkCfg = CommandLinkCfg> extends PrimeFaces.component.BaseComponent<Cfg> {
    /** The tabindex of the link, restored by {@link enable}. */
    tabIndex: string = '0';

    override init(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);
        this.tabIndex = this.jq.attr('tabindex') || '0';
        this.bindTriggers();
    }

    override refresh(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
        this.jq.off('click.commandlink');
        $(document).off('.link' + this.id);

        super.refresh(cfg);
    }

    private bindTriggers(): void {
        this.ajaxCount = 0;
        this.jq.on('click.commandlink', (e) => {
            if (this.jq.hasClass('p-state-disabled')) {
                e.preventDefault();
            }
        });

        if (this.cfg.disableOnAjax !== false) {
            const namespace = '.link' + this.id;

            $(document).on('pfAjaxSend.' + namespace, (_e, _xhr, settings) => {
                if (PrimeFaces.ajax.Utils.isXhrSource(this, settings)) {
                    this.ajaxCount++;
                    if (this.ajaxCount > 1) {
                        return;
                    }
                    this.jq.addClass('p-state-loading');
                    this.ajaxStart = Date.now();
                    this.disable();
                }
            }).on('pfAjaxComplete.' + namespace, (_e, _xhr, settings, args) => {
                if (PrimeFaces.ajax.Utils.isXhrSource(this, settings)) {
                    this.ajaxCount--;
                    if (this.ajaxCount > 0 || !args || args.redirect) {
                        return;
                    }
                    PrimeFaces.queueTask(
                        () => { this.endAjaxDisabled(this); },
                        Math.max(PrimeFaces.ajax.minLoadAnimation + (this.ajaxStart ?? 0) - Date.now(), 0)
                    );
                    this.ajaxStart = null;
                }
            });

            this.addDestroyListener(() => {
                $(document).off(namespace);
            });
        }
    }

    /**
     * Ends the AJAX disabled state.
     * @param component The component, this link.
     */
    endAjaxDisabled(component: CommandLink): void {
        component.jq.removeClass('p-state-loading');

        if (!component.cfg.disabledAttr) {
            component.enable();
        }
    }

    /**
     * Disables this link so that the user cannot click the link anymore.
     */
    override disable(): void {
        this.jq.removeClass('p-state-hover p-state-focus p-state-active')
                .addClass('p-state-disabled')
                .attr('tabindex', '-1');
    }

    /**
     * Enables this link so that the user can click the link.
     */
    override enable(): void {
        this.jq.removeClass('p-state-disabled')
                .attr('tabindex', this.tabIndex);
    }
}
