import {
    AsYouType, getCountries, getCountryCallingCode, getExampleNumber, parsePhoneNumberFromString, validatePhoneNumberLength
} from "libphonenumber-js/max";
import type { CountryCode, PhoneNumber } from "libphonenumber-js/max";
import examples from "libphonenumber-js/mobile/examples";
import { isAllowedCountry } from "./inputphone.validator.js";

/**
 * The configuration for the {@link InputPhone} widget.
 */
export interface InputPhoneCfg extends PrimeFacesTypes.widget.DynamicOverlayWidgetCfg {
    /** Whether the user can select the country. */
    allowDropdown: boolean;
    /** In the international format: whether the calling code is filled in on focus and removed if nothing else is entered. */
    autoHideDialCode: boolean;
    /** Example number as placeholder: `polite` if the input has no placeholder, `aggressive` always, `off` never. */
    autoPlaceholder: 'polite' | 'aggressive' | 'off';
    /** Whether the component is disabled. */
    disabled: boolean;
    /** Countries the user can not select (ISO 3166-1 alpha-2 codes). */
    excludeCountries: string[];
    /** Whether the panel has the width of the component, otherwise it grows with the longest name. */
    fixDropdownWidth: boolean;
    /** Whether the number is formatted while the user types. */
    formatAsYouType: boolean;
    /** Whether the value is formatted when it is displayed. */
    formatOnDisplay: boolean;
    /** Looks up the country of the user (initialCountry="auto") and calls `success` with its code. */
    geoIpLookup: (this: InputPhone, success: (countryCode: string) => void, failure: () => void) => void;
    /** The country selected for an empty value. */
    initialCountry: string;
    /** Names of the countries by code, replace the names of the browser. */
    localizedCountries: Record<string, string>;
    /** Whether the number is shown in the national format, otherwise in the international format. */
    nationalMode: boolean;
    /** Countries the user can select (ISO 3166-1 alpha-2 codes), all if empty. */
    onlyCountries: string[];
    /** Countries listed at the top. */
    preferredCountries: string[];
    /** Whether the component is read only. */
    readonly: boolean;
}

/**
 * A country of the country panel.
 */
interface Country {
    /** The ISO 3166-1 alpha-2 code. */
    code: CountryCode;
    /** The name in the language of the page. */
    name: string;
    /** The calling code with plus sign, e.g. `+49`. */
    dialCode: string;
    /** The name, code and calling code in lower case without diacritics, matched by the filter. */
    search: string;
}

/**
 * Intl.DisplayNames (ES2020) for the country names, the lib of the project (ES2016) does not declare it. The
 * declaration hides the global Intl in this module, so it also declares the Collator that sorts the names.
 */
declare namespace Intl {
    class DisplayNames {
        constructor(locales: string[], options: { type: 'region' });
        of(code: string): string | undefined;
        static supportedLocalesOf(locales: string | string[]): string[];
    }
    class Collator {
        constructor(locales: string[]);
        compare(a: string, b: string): number;
    }
}

/**
 * Normalizes a text for the filter: lower case, without diacritics.
 * @param text The text.
 * @returns The normalized text.
 */
function normalize(text: string): string {
    return text.normalize('NFD').replace(/\p{M}/gu, '').toLowerCase();
}

/**
 * Converts a Java locale (`Locale.toString()`, e.g. `de_AT` or `sr_RS_#Latn`) to a language tag (`de-AT`,
 * `sr-Latn-RS`), the variant and the extensions are dropped.
 * @param locale The Java locale.
 * @returns The language tag.
 */
function toLanguageTag(locale: string): string {
    const [base = '', extensions = ''] = locale.split('_#');
    const [language = '', country = ''] = base.split('_');
    const script = /^[A-Za-z]{4}(?=-|$)/.exec(extensions)?.[0];
    return [language, script, country].filter((part) => part).join('-');
}

/**
 * Whether the browser has the names of the countries in a language.
 * @param locale A language tag.
 * @returns `true` if the language is supported, `false` if not or if the tag is malformed.
 */
function isSupportedLocale(locale: string): boolean {
    try {
        return !!locale && Intl.DisplayNames.supportedLocalesOf(locale).length > 0;
    }
    catch {
        return false;
    }
}

/**
 * __PrimeFaces InputPhone Widget__
 *
 * InputPhone is an input for international phone numbers. The country is selected in a panel with a filter or
 * detected from a number entered with its calling code (e.g. `+44`). The number is formatted with libphonenumber-js
 * while the user types and submitted in the E.164 format (e.g. `+4930123456`); a number that is not valid for its
 * country is submitted as entered, so the server rejects it.
 */
export class InputPhone<Cfg extends InputPhoneCfg = InputPhoneCfg> extends PrimeFaces.widget.DynamicOverlayWidget<Cfg> {
    /** The visible input with the formatted number. */
    input: JQuery<HTMLInputElement> = $();
    /** The hidden input with the submitted number. */
    hiddenInput: JQuery<HTMLInputElement> = $();
    /** The hidden input with the selected country, it keeps the country of an invalid number and is the country of the `countrySelect` event. */
    countryInput: JQuery<HTMLInputElement> = $();
    /** The country selector, a button (or a plain element if the country can't be selected). */
    countryButton: JQuery = $();
    /** The panel with the filter and the countries, a child of the body (appendTo). */
    panel: JQuery = $();
    /** The selected country. */
    country: CountryCode = 'US';

    /** The countries the user can select, the preferred ones first. */
    private countries: Country[] = [];
    /** The filter input of the panel. */
    private filterInput: JQuery<HTMLInputElement> = $();
    /** The list of the panel. */
    private list: JQuery = $();
    /** The message shown when no country matches the filter. */
    private emptyMessage: JQuery = $();
    /** The items of the list, created when the panel is shown the first time. */
    private items: JQuery = $();
    /** The highlighted item (mouse or keyboard). */
    private highlightedItem: JQuery = $();
    /** Shows and hides the panel with the connected overlay transition. */
    private transition: PrimeFacesTypes.CssTransitionHandler | null = null;
    /** Unbind callback of the handler that hides the panel on a click outside. */
    private hideOverlayHandler?: PrimeFacesTypes.Unbindable;
    /** Unbind callback of the resize handler. */
    private resizeHandler?: PrimeFacesTypes.Unbindable;
    /** Unbind callback of the scroll handler. */
    private scrollHandler?: PrimeFacesTypes.Unbindable;
    /** The placeholder rendered by the server, `null` if there is none. */
    private placeholder: string | null = null;
    /** Whether the user selected a country, a looked up country (geoIpLookup) does not replace it then. */
    private countrySelected: boolean = false;

    /**
     * Initializes the widget: creates the panel, selects the country of the value (or the initial country) and
     * formats the value.
     * @param cfg The configuration of the widget.
     */
    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        // an AJAX update calls init() again on this instance: the state of the old panel is reset
        this.items = $();
        this.highlightedItem = $();
        this.transition = null;
        this.countrySelected = false;

        // the panel is created here, so the dynamic overlay feature appends it to the body (appendTo); the panel of
        // the component before an AJAX update is removed, it is still in the body if the component is disabled now
        const id = String(cfg.id);
        $(PrimeFaces.escapeClientId(id + '_panel')).remove();
        this.panel = this.createPanel(id);
        $(PrimeFaces.escapeClientId(id)).append(this.panel);
        super.init(cfg, this.panel, id + '_panel');

        this.input = $(this.jqId + '_input') as JQuery<HTMLInputElement>;
        this.hiddenInput = $(this.jqId + '_hidden') as JQuery<HTMLInputElement>;
        this.countryInput = $(this.jqId + '_countryCode') as JQuery<HTMLInputElement>;
        this.countryButton = $(this.jqId + '_country');
        this.countries = this.createCountries();
        // a placeholder of the server: autoPlaceholder="polite" keeps it
        this.placeholder = this.input.attr('placeholder') ?? null;

        // a number without calling code (e.g. a value stored before) is a number of the initial country
        const initialCountry = this.findCountry(this.cfg.initialCountry)?.code ?? this.countries[0]?.code ?? 'US';
        const value = String(this.hiddenInput.val() ?? '');
        const phoneNumber = value ? parsePhoneNumberFromString(value, initialCountry) : undefined;
        this.showCountry(phoneNumber?.country && this.findCountry(phoneNumber.country) ? phoneNumber.country : initialCountry);
        if (phoneNumber?.isValid()) {
            if (this.cfg.formatOnDisplay !== false) {
                this.input.val(this.format(phoneNumber));
            }
            this.hiddenInput.val(phoneNumber.number);
        }

        // client side validation
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hiddenInput.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        PrimeFaces.skinInput(this.input);
        this.bindEvents();

        if (this.cfg.geoIpLookup) {
            this.cfg.geoIpLookup.call(this, (countryCode) => {
                // only if the user did not enter a number or select a country meanwhile
                const country = this.findCountry(String(countryCode || '').toUpperCase());
                if (country && !this.countrySelected && !/\d/.test(String(this.input.val() ?? ''))) {
                    this.showCountry(country.code);
                }
            }, () => {
                // keep the initial country
            });
        }
    }

    /**
     * Creates the panel, its list is filled when it is shown the first time.
     * @param id The client id of the component.
     * @returns The panel.
     */
    private createPanel(id: string): JQuery {
        const panel = $('<div class="p-inputphone-panel p-widget p-widget-content p-input-overlay p-hidden"></div>')
            .attr('id', id + '_panel');
        const filterContainer = $('<div class="p-inputphone-filter-container"></div>').appendTo(panel);
        this.filterInput = $('<input type="text" class="p-inputphone-filter p-inputfield p-inputtext p-widget p-state-default"'
            + ' autocomplete="off" role="combobox" aria-autocomplete="list" aria-expanded="true">')
            .attr({ 'aria-controls': id + '_list' })
            .appendTo(filterContainer) as JQuery<HTMLInputElement>;
        filterContainer.append('<span class="p-icon p-search-icon p-inputphone-filter-icon"></span>');
        this.list = $('<ul class="p-inputphone-list" role="listbox"></ul>').attr('id', id + '_list').appendTo(panel);
        this.emptyMessage = $('<div class="p-inputphone-empty-message p-hidden"></div>').appendTo(panel);
        return panel;
    }

    /**
     * Creates the countries the user can select: all countries of libphonenumber-js with the onlyCountries and
     * without the excludeCountries, sorted by name, the preferred countries first.
     * @returns The countries.
     */
    private createCountries(): Country[] {
        const only = new Set(this.cfg.onlyCountries ?? []);
        const exclude = new Set(this.cfg.excludeCountries ?? []);
        const locales = [toLanguageTag(String(PrimeFaces.settings.locale || '')), ...navigator.languages, 'en']
            .filter((locale) => isSupportedLocale(locale));
        const names = new Intl.DisplayNames(locales, { type: 'region' });
        // one collator: localeCompare with locales creates one per comparison
        const collator = new Intl.Collator(locales);
        const localizedNames: Record<string, string> = this.cfg.localizedCountries ?? {};

        const countries = getCountries()
            .filter((code) => (only.size === 0 || only.has(code)) && !exclude.has(code))
            .map((code) => {
                const name = localizedNames[code] ?? names.of(code) ?? code;
                const dialCode = '+' + getCountryCallingCode(code);
                return { code, name, dialCode, search: normalize(name + ' ' + code + ' ' + dialCode) };
            })
            .sort((a, b) => collator.compare(a.name, b.name));

        const preferred = (this.cfg.preferredCountries ?? [])
            .map((code) => countries.find((country) => country.code === code))
            .filter((country): country is Country => !!country);
        return [...preferred, ...countries.filter((country) => !preferred.includes(country))];
    }

    /**
     * Finds a country the user can select.
     * @param code An ISO 3166-1 alpha-2 code.
     * @returns The country, `undefined` if the user can't select it.
     */
    private findCountry(code: string | undefined): Country | undefined {
        return this.countries.find((country) => country.code === code);
    }

    /**
     * Binds the events of the input and the country selector.
     */
    private bindEvents(): void {
        this.input
            .on('input.inputphone', (e) => this.onInput(e))
            .on('change.inputphone', () => this.reformat());

        // international format: the calling code is filled in for an empty input and removed if nothing else is entered
        if (this.cfg.nationalMode === false && this.cfg.autoHideDialCode !== false) {
            this.input
                .on('focus.inputphone', () => {
                    if (!this.cfg.readonly && !String(this.input.val() ?? '').trim()) {
                        this.input.val(this.getDialCode() + ' ');
                    }
                })
                .on('blur.inputphone', () => {
                    if (String(this.input.val() ?? '').trim() === this.getDialCode()) {
                        this.input.val('');
                        this.updateValue();
                    }
                });
        }

        if (!this.cfg.disabled) {
            this.bindDropdownEvents();
        }
    }

    /**
     * Binds the events of the country selector and the panel, if the user can select the country.
     */
    private bindDropdownEvents(): void {
        // allowDropdown is only rendered if it is false
        if (this.cfg.allowDropdown === false || this.cfg.readonly || this.transition) {
            return;
        }

        this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'p-connected-overlay');

        this.countryButton
            .on('click.inputphone', () => {
                if (this.panel.is(':visible')) {
                    this.hide();
                }
                else {
                    this.show();
                }
            })
            .on('keydown.inputphone', (e) => {
                if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
                    e.preventDefault();
                    this.show();
                }
            });

        this.filterInput
            .on('input.inputphone', () => this.filter(String(this.filterInput.val() ?? '')))
            .on('keydown.inputphone', (e) => this.onFilterKeyDown(e));

        this.list
            .on('click.inputphone', '.p-inputphone-item', (e) => {
                this.selectCountry($(e.currentTarget).data('country'));
                this.hide();
                this.input.trigger('focus');
            })
            .on('mouseenter.inputphone', '.p-inputphone-item', (e) => this.highlight($(e.currentTarget), false));
    }

    /**
     * Handles a change of the number: detects the country of a number entered with its calling code, formats the
     * number if the caret is at its end and stores the value.
     * @param e The input event.
     */
    private onInput(e: JQuery.TriggeredEvent): void {
        const input = this.input[0]!;
        const text = input.value;

        if (text.trim().startsWith('+')) {
            const asYouType = new AsYouType();
            asYouType.input(text);
            const country = asYouType.getCountry();
            if (country && country !== this.country && this.findCountry(country)) {
                this.showCountry(country);
            }
        }

        // no formatting while deleting (a deleted separator would come back) or editing in the middle
        const event = e.originalEvent as InputEvent | undefined;
        if (this.cfg.formatAsYouType !== false && !event?.inputType?.startsWith('delete') && input.selectionStart === text.length) {
            const formatted = new AsYouType(this.country).input(text);
            if (formatted && formatted !== text) {
                input.value = formatted;
            }
        }

        this.updateValue();
    }

    /**
     * Formats a valid number completely (unless `formatAsYouType` is disabled), called when the user leaves the input.
     */
    private reformat(): void {
        const phoneNumber = parsePhoneNumberFromString(String(this.input.val() ?? ''), this.country);
        if (phoneNumber?.isValid()) {
            if (phoneNumber.country && phoneNumber.country !== this.country && this.findCountry(phoneNumber.country)) {
                this.showCountry(phoneNumber.country);
            }
            if (this.cfg.formatAsYouType !== false) {
                this.input.val(this.format(phoneNumber));
            }
        }
        this.updateValue();
    }

    /**
     * Formats a number: national if `nationalMode` is set and the number belongs to the selected country,
     * international otherwise.
     * @param phoneNumber The number.
     * @returns The formatted number.
     */
    private format(phoneNumber: PhoneNumber): string {
        return this.cfg.nationalMode !== false && phoneNumber.country === this.country
            ? phoneNumber.formatNational()
            : phoneNumber.formatInternational();
    }

    /**
     * Stores the number in the hidden input: in the E.164 format if it is valid, as entered otherwise (the server
     * rejects it), empty if it is empty or only the calling code.
     */
    private updateValue(): void {
        const text = String(this.input.val() ?? '').trim();
        let value = '';
        // only the calling code (autoHideDialCode) is no number
        if (text && text !== this.getDialCode()) {
            // a number of a country that is not allowed (onlyCountries / excludeCountries) is submitted as entered too
            const phoneNumber = parsePhoneNumberFromString(text, this.country);
            value = phoneNumber?.isValid() && isAllowedCountry(phoneNumber.country, this.cfg.onlyCountries ?? [], this.cfg.excludeCountries ?? [])
                ? phoneNumber.number
                : text;
        }
        this.hiddenInput.val(value);
    }

    /**
     * Shows a country in the country selector and its example number as placeholder.
     * @param code The ISO 3166-1 alpha-2 code.
     */
    private showCountry(code: CountryCode): void {
        this.country = code;
        const country = this.findCountry(code);
        const dialCode = country?.dialCode ?? '+' + getCountryCallingCode(code);
        this.countryButton.children('.p-inputphone-country-code').text(code);
        this.countryButton.children('.p-inputphone-dial-code').text(dialCode);
        this.countryButton.attr('aria-label', (country?.name ?? code) + ' ' + dialCode);
        this.countryInput.val(code);

        // polite (default): only without a placeholder of the server
        const autoPlaceholder = this.cfg.autoPlaceholder ?? 'polite';
        if (autoPlaceholder === 'aggressive' || (autoPlaceholder === 'polite' && !this.placeholder)) {
            const example = getExampleNumber(code, examples);
            this.input.attr('placeholder', example ? this.format(example) : '');
        }
    }

    /**
     * The calling code of the selected country.
     * @returns The calling code with plus sign, e.g. `+49`.
     */
    private getDialCode(): string {
        return '+' + getCountryCallingCode(this.country);
    }

    /**
     * Selects a country like the user: moves the number to the new country and calls the `countrySelect` behavior.
     * @param code The ISO 3166-1 alpha-2 code.
     */
    private selectCountry(code: CountryCode): void {
        this.countrySelected = true;
        if (code === this.country) {
            return;
        }

        const text = String(this.input.val() ?? '').trim();
        const onlyDialCode = text === this.getDialCode();
        const phoneNumber = onlyDialCode ? undefined : parsePhoneNumberFromString(text, this.country);
        this.showCountry(code);
        if (onlyDialCode) {
            // autoHideDialCode: the calling code of the new country
            this.input.val(this.getDialCode() + ' ');
        }
        else if (phoneNumber) {
            const moved = parsePhoneNumberFromString('+' + getCountryCallingCode(code) + phoneNumber.nationalNumber);
            this.input.val(moved ? this.format(moved) : phoneNumber.nationalNumber);
        }
        this.updateValue();

        // the country is submitted with the country input
        if (this.hasBehavior('countrySelect')) {
            this.callBehavior('countrySelect');
        }
    }

    /**
     * Creates the items of the list.
     */
    private renderItems(): void {
        const preferredCount = (this.cfg.preferredCountries ?? []).filter((code) => this.findCountry(code)).length;
        const items = this.countries.map((country, index) => {
            const item = document.createElement('li');
            item.id = this.id + '_option_' + country.code;
            item.className = 'p-inputphone-item' + (index === preferredCount - 1 ? ' p-inputphone-item-preferred-last' : '');
            item.setAttribute('role', 'option');
            item.setAttribute('aria-selected', 'false');
            item.dataset['country'] = country.code;

            const name = document.createElement('span');
            name.className = 'p-inputphone-item-name';
            name.textContent = country.name;
            const dialCode = document.createElement('span');
            dialCode.className = 'p-inputphone-item-dial-code';
            dialCode.textContent = country.dialCode;
            item.append(name, dialCode);
            return item;
        });
        this.list.append(items).attr('aria-label', this.getAriaLabel('listLabel'));
        this.filterInput.attr('aria-label', String(this.getLabel('filter') ?? ''));
        this.emptyMessage.text(String(this.getLabel('emptySearchMessage') ?? ''));
        this.items = $(items);
    }

    /**
     * Shows the items matching a filter text: the name, the code or the calling code of the country contains it.
     * @param text The filter text.
     */
    private filter(text: string): void {
        const query = normalize(text.trim());
        const digits = query.replace(/^\+/, '');
        this.items.each((index, item) => {
            const country = this.countries[index]!;
            const visible = !query || country.search.includes(query) || (/^\d+$/.test(digits) && country.dialCode.startsWith('+' + digits));
            item.classList.toggle('p-hidden', !visible);
        });
        const visibleItems = this.items.not('.p-hidden');
        this.emptyMessage.toggleClass('p-hidden', visibleItems.length > 0);
        this.highlight(visibleItems.first(), true);
    }

    /**
     * Keyboard navigation in the filter: the arrow keys move the highlight, Enter selects the highlighted country,
     * Escape closes the panel.
     * @param e The keydown event.
     */
    private onFilterKeyDown(e: JQuery.TriggeredEvent): void {
        const visibleItems = this.items.not('.p-hidden');
        const index = visibleItems.index(this.highlightedItem);

        switch (e.key) {
            case 'ArrowDown':
            case 'ArrowUp': {
                e.preventDefault();
                const next = e.key === 'ArrowDown' ? Math.min(index + 1, visibleItems.length - 1) : Math.max(index - 1, 0);
                this.highlight(visibleItems.eq(next), true);
                break;
            }
            case 'Enter':
                e.preventDefault();
                if (this.highlightedItem.length) {
                    this.selectCountry(this.highlightedItem.data('country'));
                }
                this.hide();
                this.input.trigger('focus');
                break;
            case 'Escape':
                e.preventDefault();
                this.hide();
                this.countryButton.trigger('focus');
                break;
            case 'Tab':
                // the panel is appended to the body: continue at the number instead of the end of the page
                e.preventDefault();
                this.hide();
                this.input.trigger('focus');
                break;
            default:
                break;
        }
    }

    /**
     * Highlights an item.
     * @param item The item, none if empty.
     * @param scroll Whether the item is scrolled into view (keyboard).
     */
    private highlight(item: JQuery, scroll: boolean): void {
        this.highlightedItem.removeClass('p-state-hover');
        this.highlightedItem = item.addClass('p-state-hover');
        this.filterInput.attr('aria-activedescendant', item.attr('id') ?? null);
        if (scroll && item.length) {
            item[0]!.scrollIntoView({ block: 'nearest' });
        }
    }

    /**
     * Shows the panel: the filter is cleared and focused, the selected country highlighted.
     */
    show(): void {
        if (!this.transition || this.panel.is(':visible') || this.jq.hasClass('p-state-disabled')) {
            return;
        }
        if (this.items.length === 0) {
            this.renderItems();
        }

        this.filterInput.val('');
        this.filter('');
        this.items.attr('aria-selected', 'false').removeClass('p-state-highlight');
        const selected = this.items.filter((_, item) => item.dataset['country'] === this.country)
            .attr('aria-selected', 'true').addClass('p-state-highlight');

        // the component looks focused while the panel is open (the focus is in the filter of the panel)
        this.jq.addClass('p-state-focus');
        this.transition.show({
            onEnter: () => {
                PrimeFaces.nextZindex(this.panel);
                this.alignPanel();
                if (selected.length) {
                    this.highlight(selected, true);
                }
                this.filterInput.trigger('focus');
            },
            onEntered: () => this.bindPanelEvents()
        });
        this.countryButton.attr('aria-expanded', 'true');
    }

    /**
     * Hides the panel.
     */
    hide(): void {
        if (!this.transition || !this.panel.is(':visible')) {
            return;
        }
        this.transition.hide({
            onExit: () => this.unbindPanelEvents(),
            onExited: () => this.panel.css('z-index', '')
        });
        this.countryButton.attr('aria-expanded', 'false');
        this.jq.removeClass('p-state-focus');
    }

    /**
     * Positions the panel below the component (or above if there is no space).
     */
    private alignPanel(): void {
        const width = this.jq.outerWidth() ?? 0;
        this.panel.css(this.cfg.fixDropdownWidth === false ? { 'min-width': width, width: '' } : { 'min-width': '', width: width });
        // the panel keeps the focus ring of the component visible (it is focused while the panel is open)
        const style = getComputedStyle(this.jq[0]!);
        const ring = style.outlineStyle === 'none' ? 0 : (parseFloat(style.outlineWidth) || 0) + Math.max(parseFloat(style.outlineOffset) || 0, 0);
        // the panel is a child of the body: the direction of the component; a panel wider than the component
        // (fixDropdownWidth="false") starts at the start of the component
        this.panel.css('direction', style.direction);
        const start = style.direction === 'rtl' ? 'right' : 'left';
        PrimeFaces.utils.position(this.panel.css({ left: '0px', top: '0px', 'transform-origin': 'center top' }), {
            my: start + ' top',
            at: start + ' bottom+' + Math.ceil(ring),
            of: this.jq,
            collision: 'flipfit',
            using: function(position, feedback) {
                $(this).css({ 'transform-origin': 'center ' + feedback.vertical, top: position.top, left: position.left });
            }
        });
    }

    /**
     * Binds the events that hide the panel: a click outside, resizing and scrolling.
     */
    private bindPanelEvents(): void {
        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.panel,
            () => this.countryButton,
            (_e, eventTarget) => {
                if (!(this.panel.is(eventTarget) || this.panel.has(eventTarget[0]!).length > 0)) {
                    this.hide();
                }
            });
        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.panel,
            () => this.handleViewportChange());
        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq,
            () => this.handleViewportChange());
    }

    /**
     * Unbinds the events that hide the panel.
     */
    private unbindPanelEvents(): void {
        this.hideOverlayHandler?.unbind();
        this.resizeHandler?.unbind();
        this.scrollHandler?.unbind();
    }

    /**
     * Hides the panel when the viewport changes, on mobile devices it is aligned instead (the virtual keyboard
     * resizes the viewport).
     */
    private handleViewportChange(): void {
        if (PrimeFaces.env.mobile || PrimeFaces.hideOverlaysOnViewportChange === false) {
            this.alignPanel();
        }
        else {
            this.hide();
        }
    }

    /**
     * Retrieves the number in the E.164 format, as submitted.
     * @returns The number, as entered if it is not valid, empty if there is none.
     */
    getValue(): string {
        return String(this.hiddenInput.val() ?? '');
    }

    /**
     * Sets the number, the country follows the number.
     * @param value The number, in the E.164 format or in the national format of the selected country.
     */
    setValue(value: string | null | undefined): void {
        const phoneNumber = value ? parsePhoneNumberFromString(value, this.country) : undefined;
        if (phoneNumber?.country && this.findCountry(phoneNumber.country)) {
            this.showCountry(phoneNumber.country);
        }
        // only a valid number is formatted, an incomplete one is shown as it is
        this.input.val(phoneNumber?.isValid() ? this.format(phoneNumber) : value ?? '');
        this.updateValue();
        PrimeFaces.updateFilledState(this.input, this.input.parent());
    }

    /**
     * Retrieves the selected country.
     * @returns The ISO 3166-1 alpha-2 code.
     */
    getCountry(): string {
        return this.country;
    }

    /**
     * Selects a country, like the user (the `countrySelect` behavior is called).
     * @param code The ISO 3166-1 alpha-2 code, ignored if the user can't select it.
     */
    setCountry(code: string): void {
        const country = this.findCountry(code.toUpperCase());
        if (country) {
            this.selectCountry(country.code);
        }
    }

    /**
     * Whether the number is valid for its country (by the metadata of libphonenumber-js), which must be one of the
     * onlyCountries and none of the excludeCountries.
     * @returns `true` if the number is valid.
     */
    isValid(): boolean {
        const phoneNumber = this.parse();
        return !!phoneNumber?.isValid()
            && isAllowedCountry(phoneNumber.country, this.cfg.onlyCountries ?? [], this.cfg.excludeCountries ?? []);
    }

    /**
     * Whether the number has a possible length for its country.
     * @returns `true` if the length of the number is possible.
     */
    isPossible(): boolean {
        return !!this.parse()?.isPossible();
    }

    /**
     * Why the number is not possible, by its length.
     * @returns `TOO_SHORT`, `TOO_LONG`, `INVALID_LENGTH`, `INVALID_COUNTRY` or `NOT_A_NUMBER`, `undefined` if the length
     * is possible.
     */
    getValidationError(): string | undefined {
        return validatePhoneNumberLength(String(this.input.val() ?? ''), this.country);
    }

    /**
     * Alias of {@link getValue}, like the widget of PrimeFaces Extensions.
     * @returns The number in the E.164 format.
     */
    getNumber(): string {
        return this.getValue();
    }

    /**
     * Alias of {@link setValue}, like the widget of PrimeFaces Extensions.
     * @param number The number.
     */
    setNumber(number: string | null | undefined): void {
        this.setValue(number);
    }

    /**
     * Focuses the number.
     */
    focus(): void {
        this.input.trigger('focus');
    }

    /**
     * Parses the entered number.
     * @returns The number, `undefined` if it can't be parsed.
     */
    private parse(): PhoneNumber | undefined {
        return parsePhoneNumberFromString(String(this.input.val() ?? ''), this.country);
    }

    /**
     * Disables the component, the number is not submitted.
     */
    override disable(): void {
        this.hide();
        PrimeFaces.utils.disableInputWidget(this.input, null);
        this.hiddenInput.add(this.countryInput).prop('disabled', true);
        this.countryButton.prop('disabled', true);
        this.jq.addClass('p-state-disabled');
    }

    /**
     * Enables the component.
     */
    override enable(): void {
        if (this.cfg.disabled) {
            // rendered disabled: the panel is not appended to the body and the country selector not bound yet
            this.cfg.disabled = false;
            this.cfg.appendTo = PrimeFaces.utils.resolveAppendTo(this, this.jq, this.panel);
            PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');
            this.bindDropdownEvents();
        }
        PrimeFaces.utils.enableInputWidget(this.input);
        this.hiddenInput.add(this.countryInput).prop('disabled', false);
        this.countryButton.prop('disabled', false);
        this.jq.removeClass('p-state-disabled');
    }
}
