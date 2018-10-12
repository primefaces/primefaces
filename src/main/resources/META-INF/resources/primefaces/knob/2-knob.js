PrimeFaces.widget.Knob = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this._super(cfg);

        this.colorTheme = this.cfg.colorTheme;
        this.input = $(this.jqId + "_hidden");
        this.min = parseInt(this.jq.data('min'), 10);
        this.max = parseInt(this.jq.data('max'), 10);
        this.step = parseInt(this.jq.data('step'), 10);

        this.createKnob();

    },

    refresh: function(cfg) {
        if (this.knob) {
            this.knob.children('canvas').remove();
            this.input.remove();
            this.knob.children('input').unwrap();
        }
        
        this.init(cfg);
    },
    
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

    setValue: function (value) {
        this.input.val(value);
        this.jq.val(value).trigger('change');
    },

    getValue: function () {
        return parseInt(this.jq.val());
    },

    increment: function () {
        var value = this.getValue() + this.step;
        value = value <= this.max ? value : this.max;
        this.setValue(value);
    },

    decrement: function () {
        var value = this.getValue() - this.step;
        value = value >= this.min ? value : this.min;
        this.setValue(value);
    }
});

PrimeFaces.widget.Knob.colorThemes = {
    afterdark: {
        fgColor: '#8C9B8C',
        bgColor: '#535353'
    },
    afternoon: {
        fgColor: '#5E61B0',
        bgColor: '#EBF8FF'
    },
    afterwork: {
        fgColor: '#000000',
        bgColor: '#EBEBEB'
    },
    aristo: {
        fgColor: '#000000',
        bgColor: '#E3E3E3'
    },
    blitzer: {
        fgColor: '#CC0505',
        bgColor: '#F1F1F1'
    },
    bluesky: {
        fgColor: 'black',
        bgColor: '#E5EEFA'
    },
    'black-tie': {
        fgColor: 'black',
        bgColor: 'white'
    },
    bootstrap: {
        fgColor: '#000000',
        bgColor: '#EBEBEB'
    },
    casablanca: {
        fgColor: '#030303',
        bgColor: '#F9F8F5'
    },
    cruze: {
        fgColor: '#C1C1C1',
        bgColor: '#3D3D3D'
    },
    cupertino: {
        fgColor: '#2A7BAB',
        bgColor: '#D8EBF9'
    },
    'dark-hive': {
        fgColor: 'white',
        bgColor: '#5F5F5F'
    },
    delta: {
        fgColor: '#1B1D1F',
        bgColor: '#F9F9FC'
    },
    'dot-luv': {
        fgColor: 'white',
        bgColor: '#083C6D'
    },
    eggplant: {
        fgColor: 'white',
        bgColor: '#DFDCE1'
    },
    'excite-bike': {
        fgColor: '#E69700',
        bgColor: '#1E88E6'
    },
    flick: {
        fgColor: '#1980EC',
        bgColor: '#E0E0E0'
    },
    'glass-x': {
        fgColor: 'black',
        bgColor: '#D7E1E8'
    },
    home: {
        fgColor: '#424548',
        bgColor: '#747C89'
    },
    'hot-sneaks': {
        fgColor: '#D2D660',
        bgColor: '#35414F'
    },
    humanity: {
        fgColor: 'white',
        bgColor: '#CB842F'
    },
    'le-frog': {
        fgColor: 'white',
        bgColor: '#5BA920'
    },
    midnight: {
        fgColor: 'white',
        bgColor: '#363641'
    },
    'mint-choc': {
        fgColor: '#E3DDC9',
        bgColor: '#59493D'
    },
    overcast: {
        fgColor: '#3383BB',
        bgColor: '#F2F2F2'
    },
    'pepper-grinder': {
        fgColor: '#654B24',
        bgColor: '#F6F5F4'
    },
    redmond: {
        fgColor: '#2E6E9E',
        bgColor: '#EAF4FD'
    },
    rocket: {
        fgColor: 'white',
        bgColor: '#292627'
    },
    sam: {
        fgColor: '#000000',
        bgColor: '#E3E3E3'
    },
    smoothness: {
        fgColor: '#000000',
        bgColor: '#E3E3E3'
    },
    'south-street': {
        fgColor: 'white',
        bgColor: '#4CA109'
    },
    start: {
        fgColor: '#222222',
        bgColor: '#2E90BD'
    },
    sunny: {
        fgColor: '#9A9384',
        bgColor: '#FCDA66'
    },
    'swanky-purse': {
        fgColor: '#EFEC9F',
        bgColor: '#261803'
    },
    trontastic: {
        fgColor: 'white',
        bgColor: '#BEE590'
    },
    'ui-darkness': {
        fgColor: 'white',
        bgColor: '#585858'
    },
    'ui-lightness': {
        fgColor: 'white',
        bgColor: '#F7B13D'
    },
    vader: {
        fgColor: 'white',
        bgColor: '#AEAEAE'
    }
};