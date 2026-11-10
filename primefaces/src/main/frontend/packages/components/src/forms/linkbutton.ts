/**
 * The configuration for the {@link LinkButton} widget.
 */
export interface LinkButtonCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
}

/**
 * __PrimeFaces LinkButton Widget__
 *
 * LinkButton is a simple link, which is styled as a button and integrated with the Faces navigation model.
 */
export class LinkButton<Cfg extends LinkButtonCfg = LinkButtonCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The link inside the button. */
    link: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.link = this.jq.children('a');

        PrimeFaces.skinButton(this.jq);

        this.bindEvents();
    }

    private bindEvents(): void {
        if (this.link.length > 0) {
            this.link.off('focus.linkbutton keydown.linkbutton blur.linkbutton click.linkbutton')
                .on('focus.linkbutton keydown.linkbutton', () => {
                    this.jq.addClass('p-state-focus p-state-active');
                }).on('blur.linkbutton', () => {
                    this.jq.removeClass('p-state-focus p-state-active');
                }).on('click.linkbutton', (e) => {
                    // disabled on the server or via disable(): the link keeps its href, so prevent the navigation
                    if (this.jq.hasClass('p-state-disabled')) {
                        e.preventDefault();
                        e.stopImmediatePropagation();
                    }
                });
        }
    }

    /**
     * Disables this link button so that it cannot be clicked.
     */
    override disable(): void {
        PrimeFaces.utils.disableButton(this.jq);
    }

    /**
     * Enables this link button so that it can be clicked.
     */
    override enable(): void {
        PrimeFaces.utils.enableButton(this.jq);
    }
}
