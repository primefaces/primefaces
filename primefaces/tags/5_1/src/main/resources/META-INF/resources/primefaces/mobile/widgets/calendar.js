/**
 * PrimeFaces Calendar Widget
 */
PrimeFaces.widget.Calendar = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.inline = !this.cfg.popup;
        this.input = $(this.jqId + '_input');
        var $this = this;

        this.configureLocale();

        //disabled dates
        this.cfg.beforeShowDay = function(date) { 
            if($this.cfg.preShowDay)
                return $this.cfg.preShowDay(date);
            else if($this.cfg.disabledWeekends)
                return $.datepicker.noWeekends(date);
            else
                return [true,''];
        }
        
        this.bindEvents();

        if(!this.cfg.disabled) {
            this.input.date(this.cfg);
        }
                        
        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },
        
    refresh: function(cfg) {
        this.init(cfg);
    },
    
    configureLocale: function() {
        var localeSettings = PrimeFaces.locales[this.cfg.locale];

        if(localeSettings) {
            for(var setting in localeSettings) {
                if(localeSettings.hasOwnProperty(setting)) {
                    this.cfg[setting] = localeSettings[setting];
                }
            }
        }
    },
    
    bindEvents: function() {
        var $this = this;

        this.cfg.onSelect = function() {
            $this.fireDateSelectEvent();
            
            setTimeout( function(){
                $this.input.date( "addMobileStyle" );
            },0);
        };
    },
    
    fireDateSelectEvent: function() {
        if(this.cfg.behaviors) {
            var dateSelectBehavior = this.cfg.behaviors['dateSelect'];

            if(dateSelectBehavior) {
                dateSelectBehavior.call(this);
            }
        }
    },
    
    setDate: function(date) {
        this.input.date('setDate', date);
    },
    
    getDate: function() {
        return this.input.date('getDate');
    },
    
    enable: function() {
        this.input.date('enable');
    },
    
    disable: function() {
        this.input.date('disable');
    }
    
});