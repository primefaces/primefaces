/**
 * The configuration for the {@link Rating} widget.
 */
export interface RatingCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Callback that is invoked when the user gives a rating. */
    onRate: PrimeFacesTypes.widget.Rating.OnRateCallback;
    /** Whether this widget is in read-only mode. */
    readonly: boolean;
}

/**
 * __PrimeFaces Rating Widget__
 *
 * Rating component features a star based rating system.
 */
export class Rating<Cfg extends RatingCfg = RatingCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The DOM element for the cancel button. */
    cancel: JQuery = $();
    /** The DOM element for the hidden input field storing the value of this widget. */
    input: JQuery = $();
    /** The DOM elements for the clickable stars. */
    stars: JQuery = $();
    /** The current value, i.e. the number of selected stars, `null` when no star is selected. */
    value: number | null = null;
    /** The tabindex initially set. */
    tabindex: string | undefined = undefined;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.input = $(this.jqId + '_input');
        this.value = this.getValue();
        this.stars = this.jq.children('.p-rating-star');
        this.cancel = this.jq.children('.p-rating-cancel');
        this.tabindex = this.jq.attr('tabindex');

        if(!this.cfg.disabled && !this.cfg.readonly) {
            this.bindEvents();
        }
        else {
            this.input.attr('tabindex', -1);
        }

        if(this.cfg.readonly) {
            this.jq.children().css('cursor', 'default');
        }
        this.updateInput(this.value || '0');
    }

    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindEvents(): void {
        this.jq.attr('tabindex', this.tabindex as string);

        this.input.on("keydown.rating", (e) => {
            let value = this.getValue() || 0;
            const key = e.key;

            if ((key === 'ArrowLeft' || key === 'ArrowDown') && value > 0) {
                this.setValue(--value);
                e.preventDefault();
            }
            else if ((key === 'ArrowRight' || key === 'ArrowUp') && this.stars.length !== value) {
                this.setValue(++value);
                e.preventDefault();
            }

            this.focus(this.getFocusableElement());
        }).on("focus.rating", () => {
            this.focus(this.getFocusableElement());
        }).on("blur.rating", () => {
            this.jq.children('.p-state-focus').removeClass("p-state-focus");
        });

        this.stars.on("click.rating", (e) => {
            const value = this.stars.index(e.currentTarget) + 1;   //index starts from zero

            this.setValue(value);
            this.focus($(e.currentTarget), true);
        });

        this.cancel.on("mouseenter.rating", (e) => {
            $(e.currentTarget).addClass('p-rating-cancel-hover');
        }).on("mouseleave.rating", (e) => {
            $(e.currentTarget).removeClass('p-rating-cancel-hover');
        }).on("click.rating", (e) => {
            this.reset();
            this.focus($(e.currentTarget), true);
        });
    }

    /**
     * Focuses on a specified element and optionally sets focus to the input element.
     * @param star The element to which the focus class will be added.
     * @param isInputFocus If true, also sets focus to the input element.
     */
    focus(star: JQuery, isInputFocus?: boolean): void {
        if (!this.cfg.disabled && star) {
            this.jq.children('.p-state-focus').removeClass("p-state-focus");
            star.addClass('p-state-focus');

            if (isInputFocus) {
                this.input.focus();
            }
        }
    }

    /**
     * Get focusable element
     * @returns element
     */
    private getFocusableElement(): JQuery {
        const value = this.getValue() || 0;
        if (value === 0) {
            return this.cancel && this.cancel.length ? this.cancel : this.stars.eq(0);
        } else {
            return this.stars.eq(value - 1);
        }
    }

    /**
     * Removes the event listeners that were added, called when this widget is disabled.
     */
    private unbindEvents(): void {
        this.jq.attr('tabindex', -1);
        this.input.off('keydown.rating focus.rating blur.rating');
        this.stars.off('click.rating');
        this.cancel.off('mouseenter.rating mouseleave.rating click.rating');
    }

    /**
     * Updates the input element with the new rating value and sets the appropriate ARIA label.
     * @param value The new rating value to update the input element with.
     */
    private updateInput(value: number | string): void {
        const ariaLabel = value.toString() === '1' ? this.getAriaLabel('star') : this.getAriaLabel('stars').replace('{star}', String(value));
        this.input.val(value).attr('aria-label', ariaLabel);
    }

    /**
     * Finds the current rating, i.e. the number of stars selected.
     * @returns The current rating value.
     */
    getValue(): number | null {
        const inputVal = this.input.val();
        return inputVal === '0' ? null : parseInt(inputVal as string);
    }

    /**
     * Sets the rating to the given value.
     * @param value New rating value to set (number of stars selected). Pass `undefined` or a value not greater than 0
     * to reset the value.
     */
    setValue(value: number | undefined | null): void {
        if(this.isDisabled() || this.isReadOnly()) {
            return;
        }

        // check minimum and maximum
        let newValue = parseInt(String(value));
        if(isNaN(newValue) || newValue <= 0) {
            this.reset();
            return;
        }
        else if(newValue > this.stars.length) {
            newValue = this.stars.length;
        }

        //set hidden value
        this.updateInput(newValue);

        //update visuals
        this.stars.removeClass('p-rating-star-on');
        for(let i = 0; i < newValue; i++) {
            this.stars.eq(i).addClass('p-rating-star-on');
        }

        //invoke callback
        if(this.cfg.onRate) {
            this.cfg.onRate.call(this, newValue);
        }

        this.callBehavior('rate');
    }

    /**
     * Checks whether this widget is currently disabled. Whe disabled, the user cannot edit the value and it will not be
     * sent to the server when the form is submitted.
     * @returns `true` if this rating widget is disabled, `false` otherwise.
     */
    isDisabled(): boolean {
        return this.jq.hasClass('p-state-disabled');
    }

    /**
     * Checks whether this widget is currently read-only. When read-only, the user cannot edit the value, but the value
     * will be sent to the server when the form is submitted.
     * @returns `true` if this rating widget is read-only, `false` otherwise.
     */
    isReadOnly(): boolean | undefined {
        return this.cfg.readonly;
    }

    /**
     * Enables this rating widget so the user can give a rating.
     */
    override enable(): void {
        if(!this.isDisabled() || this.isReadOnly()) {
            return;
        }
        this.cfg.disabled = false;

        this.bindEvents();

        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
    }

    /**
     * Disables this rating widget so the user cannot give a rating anymore.
     */
    override disable(): void {
        if(this.isDisabled()) {
            return;
        }
        this.cfg.disabled = true;

        this.unbindEvents();

        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
    }

    /**
     * Resets the rating so that no stars are selected.
     */
    reset(): void {
        this.updateInput('0');

        this.stars.filter('.p-rating-star-on').removeClass('p-rating-star-on');

        this.callBehavior('cancel');
    }
}
