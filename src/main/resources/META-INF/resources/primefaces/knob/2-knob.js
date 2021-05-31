/**
 * __PrimeFaces Knob Widget__
 * 
 * Knob is an input component to insert numeric values in a range.
 * 
 * @typedef PrimeFaces.widget.Knob.OnChangeCallback Client side callback to invoke when value changes. See also
 * {@link KnobCfg.onchange}.
 * @param {number} PrimeFaces.widget.Knob.OnChangeCallback.currentValue Current numerical value of the knob.
 * 
 * @interface {PrimeFaces.widget.Knob.ColorTheme} ColorTheme A color theme for the knob, consisting of the color for the
 * filled and unfilled part of the knob.
 * @prop {string} ColorTheme.fgColor The foreground color, i.e. the color of the filled part of the knob. Must be a CSS
 * color, e.g. `#ff0000`.
 * @prop {string} ColorTheme.bgColor The background color, i.e. the color of the unfilled part of the knob. Must be a
 * CSS color, e.g. `#ff0000`.
 * 
 * @prop {string} colorTheme Name of the color theme to use. You can use on of the keys defined in
 * `PrimeFaces.widget.Knob.ColorThemes`.
 * @prop {number} cursorExt Used for the computing the theme layout.
 * @prop {JQuery} input The DOM Element for the hidden input that stores the value of this widget.
 * @prop {JQuery} knob The DOM element on which the JQuery knob plugin was initialized.
 * @prop {number} min Minimum allowed value for this knob.
 * @prop {number} max Maximum allowed value for this knob.
 * @prop {number} step Step size for incrementing or decrementing the value of this knob.
 * @prop {PrimeFaces.widget.Knob.ColorTheme} themeObject Color theme data to be used.
 * 
 * @interface {PrimeFaces.widget.KnobCfg} cfg The configuration for the {@link  Knob| Knob widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.colorTheme Theme of the knob.
 * @prop {string} cfg.bgColor Foreground color of the component.
 * @prop {string} cfg.fgColor Background color of the component.
 * @prop {string} cfg.labelTemplate Template of the progress value e.g. `{value}%`.
 * @prop {PrimeFaces.widget.Knob.OnChangeCallback} cfg.onchange Client side callback to invoke when value changes.
 * @prop {string} cfg.styleClass Style class of the component.
 */
PrimeFaces.widget.Knob = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);

        this.colorTheme = this.cfg.colorTheme;
        this.input = $(this.jqId + "_hidden");
        this.min = parseInt(this.jq.data('min'), 10);
        this.max = parseInt(this.jq.data('max'), 10);
        this.step = parseInt(this.jq.data('step'), 10);

        this.createKnob();

    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        if (this.knob) {
            this.knob.children('canvas').remove();
            this.input.remove();
            this.knob.children('input').unwrap();
        }
        
        this.init(cfg);
    },
    
    /**
     * Creates the knob widget and sets up all event listeners.
     * @private
     */
    createKnob: function () {
        var $this = this;

        this.themeObject = PrimeFaces.widget.Knob.colorThemes[this.colorTheme || 'aristo'];

        this.jq.data('fgcolor', this.cfg.fgColor || this.themeObject.fgColor);
        this.jq.data('bgcolor', this.cfg.bgColor || this.themeObject.bgColor);

        /* PrimeFaces Github #4085 */
        this.jq.css({
            'pointer-events': 'none',
            '-moz-user-select': 'none'
        });

        this.knob = this.jq.knob({
            release: function (value) {
                $this.input.val(value);

                if ($this.cfg.onchange) {
                    $this.cfg.onchange(value);
                }

                if ($this.hasBehavior('change')) {
                    var ext = {
                        params: [
                            {name: $this.id + '_hidden', value: value}
                        ]
                    };

                    $this.callBehavior('change', ext);
                }
            },
            format: function (value) {
                return $this.cfg.labelTemplate.replace('{value}', value);
            },
            draw: function () {

                // "tron" case
                if (this.$.data('skin') == 'tron') {

                    this.cursorExt = 0.3;

                    var a = this.arc(this.cv) // Arc
                            , pa // Previous arc
                            , r = 1;

                    this.g.lineWidth = this.lineWidth;

                    if (this.o.displayPrevious) {
                        pa = this.arc(this.v);
                        this.g.beginPath();
                        this.g.strokeStyle = this.pColor;
                        this.g.arc(this.xy, this.xy, this.radius - this.lineWidth, pa.s, pa.e, pa.d);
                        this.g.stroke();
                    }

                    this.g.beginPath();
                    this.g.strokeStyle = r ? this.o.fgColor : this.fgColor;
                    this.g.arc(this.xy, this.xy, this.radius - this.lineWidth, a.s, a.e, a.d);
                    this.g.stroke();

                    this.g.lineWidth = 2;
                    this.g.beginPath();
                    this.g.strokeStyle = this.o.fgColor;
                    this.g.arc(this.xy, this.xy, this.radius - this.lineWidth + 1 + this.lineWidth * 2 / 3, 0, 2 * Math.PI, false);
                    this.g.stroke();

                    return false;
                }
            }
        });
        
        this.knob.addClass(this.cfg.styleClass);
    },

    /**
     * Sets the value of this knob widget to the given value.
     * @param {number} value Value to set on this knob.
     */
    setValue: function (value) {
        this.input.val(value);
        this.jq.val(value).trigger('change');
    },

    /**
     * Retrieves the current value of this knob, as a number.
     * @return {number} The current numerical value of this knob.
     */
    getValue: function () {
        return parseInt(this.jq.val());
    },

    /**
     * Increments the value of this knob by the current step size.
     */
    increment: function () {
        var value = this.getValue() + this.step;
        value = value <= this.max ? value : this.max;
        this.setValue(value);
    },

    /**
     * Decrements the value of this knob by the current step size.
     */
    decrement: function () {
        var value = this.getValue() - this.step;
        value = value >= this.min ? value : this.min;
        this.setValue(value);
    },

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable: function() {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
    },

    /**
     * Enables this input so that the user can enter a value.
     */
    enable: function() {
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
    }
});

/**
 * Interface for the the list of available builtin color themes for the {@link Knob} widget.
 * @interface {PrimeFaces.widget.Knob.ColorThemes} .
 * @constant {PrimeFaces.widget.Knob.colorThemes} . Contains a list with the available builtin color themes for the
 * {@link Knob} widget.
 */
PrimeFaces.widget.Knob.colorThemes = {
    /** 
     * The default afterdark theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    afterdark: {
        fgColor: '#8C9B8C',
        bgColor: '#535353'
    },
    /** 
     * The default afternoon theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    afternoon: {
        fgColor: '#5E61B0',
        bgColor: '#EBF8FF'
    },
    /** 
     * The default afterwork theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    afterwork: {
        fgColor: '#000000',
        bgColor: '#EBEBEB'
    },
    /** 
     * The default aristo theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    aristo: {
        fgColor: '#000000',
        bgColor: '#E3E3E3'
    },
    /** 
     * The default blitzer theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    blitzer: {
        fgColor: '#CC0505',
        bgColor: '#F1F1F1'
    },
    /** 
     * The default bluesky theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    bluesky: {
        fgColor: 'black',
        bgColor: '#E5EEFA'
    },
    /** 
     * The default black tie theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'black-tie': {
        fgColor: 'black',
        bgColor: 'white'
    },
    /** 
     * The default bootstrap theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    bootstrap: {
        fgColor: '#000000',
        bgColor: '#EBEBEB'
    },
    /** 
     * The default casablanca theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    casablanca: {
        fgColor: '#030303',
        bgColor: '#F9F8F5'
    },
    /** 
     * The default cruze theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    cruze: {
        fgColor: '#C1C1C1',
        bgColor: '#3D3D3D'
    },
    /** 
     * The default cupertino theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    cupertino: {
        fgColor: '#2A7BAB',
        bgColor: '#D8EBF9'
    },
    /** 
     * The default dark hive theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'dark-hive': {
        fgColor: 'white',
        bgColor: '#5F5F5F'
    },
    /** 
     * The default delta theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    delta: {
        fgColor: '#1B1D1F',
        bgColor: '#F9F9FC'
    },
    /** 
     * The default dot luv theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'dot-luv': {
        fgColor: 'white',
        bgColor: '#083C6D'
    },
    /** 
     * The default eggplant theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    eggplant: {
        fgColor: 'white',
        bgColor: '#DFDCE1'
    },
    /** 
     * The default excite bike theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'excite-bike': {
        fgColor: '#E69700',
        bgColor: '#1E88E6'
    },
    /** 
     * The default flick theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    flick: {
        fgColor: '#1980EC',
        bgColor: '#E0E0E0'
    },
    /** 
     * The default glass x theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'glass-x': {
        fgColor: 'black',
        bgColor: '#D7E1E8'
    },
    /** 
     * The default home theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    home: {
        fgColor: '#424548',
        bgColor: '#747C89'
    },
    /** 
     * The default hot sneaks theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'hot-sneaks': {
        fgColor: '#D2D660',
        bgColor: '#35414F'
    },
    /** 
     * The default humanity theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    humanity: {
        fgColor: 'white',
        bgColor: '#CB842F'
    },
    /** 
     * The default le frog theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'le-frog': {
        fgColor: 'white',
        bgColor: '#5BA920'
    },
    /** 
     * The default midnight theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    midnight: {
        fgColor: 'white',
        bgColor: '#363641'
    },
    /** 
     * The default mint choc theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'mint-choc': {
        fgColor: '#E3DDC9',
        bgColor: '#59493D'
    },
    /** 
     * The default overcast theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    overcast: {
        fgColor: '#3383BB',
        bgColor: '#F2F2F2'
    },
    /** 
     * The default pepper grinder theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'pepper-grinder': {
        fgColor: '#654B24',
        bgColor: '#F6F5F4'
    },
    /** 
     * The default redmond theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    redmond: {
        fgColor: '#2E6E9E',
        bgColor: '#EAF4FD'
    },
    /** 
     * The default rocket theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    rocket: {
        fgColor: 'white',
        bgColor: '#292627'
    },
    /** 
     * The default sam theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    sam: {
        fgColor: '#000000',
        bgColor: '#E3E3E3'
    },
    /** 
     * The default smoothness theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    smoothness: {
        fgColor: '#000000',
        bgColor: '#E3E3E3'
    },
    /** 
     * The default south street theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'south-street': {
        fgColor: 'white',
        bgColor: '#4CA109'
    },
    /** 
     * The default start theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    start: {
        fgColor: '#222222',
        bgColor: '#2E90BD'
    },
    /** 
     * The default sunny theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    sunny: {
        fgColor: '#9A9384',
        bgColor: '#FCDA66'
    },
    /** 
     * The default swanky purse theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'swanky-purse': {
        fgColor: '#EFEC9F',
        bgColor: '#261803'
    },
    /** 
     * The default trontastic theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    trontastic: {
        fgColor: 'white',
        bgColor: '#BEE590'
    },
    /** 
     * The default ui darkness theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'ui-darkness': {
        fgColor: 'white',
        bgColor: '#585858'
    },
    /** 
     * The default ui lightness theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    'ui-lightness': {
        fgColor: 'white',
        bgColor: '#F7B13D'
    },
    /** 
     * The default vader theme.
     * @type {PrimeFaces.widget.Knob.ColorTheme}
     */
    vader: {
        fgColor: 'white',
        bgColor: '#AEAEAE'
    }
};