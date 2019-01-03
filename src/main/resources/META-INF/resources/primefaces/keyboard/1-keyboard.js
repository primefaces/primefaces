/**
 * PrimeFaces Keyboard Widget
 */
 PrimeFaces.widget.Keyboard = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        var $this = this;   
        if(this.cfg.layoutTemplate)
            this.cfg.layout = PrimeFaces.widget.KeyboardUtils.createLayoutFromTemplate(this.cfg.layoutTemplate);
        else
            this.cfg.layout = PrimeFaces.widget.KeyboardUtils.getPresetLayout(this.cfg.layoutName);

        this.cfg.beforeShow = function(div, inst) {
            $(div).addClass('ui-input-overlay').css('z-index', ++PrimeFaces.zindex);
        };

        this.jq.keypad(this.cfg);

        //Visuals
        PrimeFaces.skinInput(this.jq);

        //Hide overlay on resize
        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', null, function() {
            $this.jq.keypad('hide');
        });
    }

});

PrimeFaces.widget.KeyboardUtils = {

    layouts : {
        qwertyBasic :
            [$.keypad.qwertyAlphabetic[0] + $.keypad.CLOSE,
            $.keypad.HALF_SPACE + $.keypad.qwertyAlphabetic[1] +
            $.keypad.HALF_SPACE + $.keypad.CLEAR,
            $.keypad.SPACE + $.keypad.qwertyAlphabetic[2] +
            $.keypad.SHIFT + $.keypad.BACK],

        qwerty : $.keypad.qwertyLayout,

        alphabetic :
            ['abcdefghij' + $.keypad.CLOSE,
            'klmnopqrst' + $.keypad.CLEAR,
            'uvwxyz' + $.keypad.SPACE + $.keypad.SPACE +
            $.keypad.SHIFT + $.keypad.BACK]
    },

    controls : {
        close : $.keypad.CLOSE,
        clear : $.keypad.CLEAR,
        back : $.keypad.BACK,
        shift : $.keypad.SHIFT,
        spacebar : $.keypad.SPACE_BAR,
        space : $.keypad.SPACE,
        halfspace : $.keypad.HALF_SPACE
    },

    getPresetLayout : function(name) {
        return this.layouts[name];
    },

    getPresetControl : function(name) {
        return this.controls[name];
    },

    isDefinedControl : function(key) {
        return this.controls[key] != undefined;
    },

    createLayoutFromTemplate : function(template) {
        // GitHub #3487: Unicode conversion
        template =  decodeURIComponent(JSON.parse('"' + template.replace(/\"/g, '\\"') + '"'));
        var lines = template.split(','),
        template = new Array(lines.length);

        for(var i = 0; i < lines.length;i++) {
            template[i] = "";
            var lineControls = lines[i].split('-');

            for(var j = 0; j < lineControls.length;j++) {
                if(this.isDefinedControl(lineControls[j]))
                    template[i] = template[i] + this.getPresetControl(lineControls[j])
                else
                    template[i] = template[i] + lineControls[j];
            }
        }

        return template;
    }

};