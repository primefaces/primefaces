/**
 * PrimeFaces Password
 */
PrimeFaces.widget.Password = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

    if(!this.jq.is(':disabled')) {
        if(this.cfg.feedback) {
            this.setupFeedback();
        }

        //Client Behaviors
        if(this.cfg.behaviors) {
            PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
        }

        //Visuals
        PrimeFaces.skinInput(this.jq);
    }

    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Password, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Password.prototype.setupFeedback = function() {
    var _self = this;
    
    //remove previous panel if any
    var oldPanel = $(this.jqId + '_panel');
    if(oldPanel.length == 1) {
        oldPanel.remove();
    }
    
    //config
    this.cfg.promptLabel = this.cfg.promptLabel||'Please enter a password';
    this.cfg.weakLabel = this.cfg.weakLabel||'Weak';
    this.cfg.mediumLabel = this.cfg.mediumLabel||'Medium';
    this.cfg.strongLabel = this.cfg.strongLabel||'Strong';
    
    var panelStyle = this.cfg.inline ? 'ui-password-panel-inline' : 'ui-password-panel-overlay';
    
    //create panel element
    var panelMarkup = '<div id="' + this.id + '_panel" class="ui-password-panel ui-widget ui-state-highlight ui-corner-all ui-helper-hidden ' + panelStyle + '">';
    panelMarkup += '<div class="ui-password-meter" style="background-position:0pt 0pt">&nbsp;</div>';
    panelMarkup += '<div class="ui-password-info">' + this.cfg.promptLabel + '</div>';
    panelMarkup += '</div>';
    
    this.panel = $(panelMarkup).insertAfter(this.jq);
    this.meter = this.panel.children('div.ui-password-meter');
    this.infoText = this.panel.children('div.ui-password-info');
    
    //events
    this.jq.focus(function() {
        _self.show();
    })
    .blur(function() {
        _self.hide();
    })
    .keyup(function() {
        var value = _self.jq.val(),
        label = null,
        meterPos = null;
        
        if(value.length == 0) {
            label = _self.cfg.promptLabel;
            meterPos = '0px 0px';
        }
        else {
            var score = _self.testStrength(_self.jq.val());

            if(score < 30) {
                label = _self.cfg.weakLabel;
                meterPos = '0px -10px';
            }
            else if(score >= 30 && score < 80) {
                label = _self.cfg.mediumLabel;
                meterPos = '0px -20px';
            } 
            else if(score >= 80) {
                label = _self.cfg.strongLabel;
                meterPos = '0px -30px';
            }
        }
        
        //update meter and info text
        _self.meter.css('background-position', meterPos);
        _self.infoText.text(label);
    });
    
    //overlay setting
    if(!this.cfg.inline) {
        this.panel.appendTo('body');
        
        //Hide overlay on resize
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if(_self.panel.is(':visible')) {
                _self.panel.hide();
            }
        });
    }
}

PrimeFaces.widget.Password.prototype.testStrength = function(str) {
    var grade = 0, 
    val = 0, 
    _self = this;

    val = str.match('[0-9]');
    grade += _self.normalize(val ? val.length : 1/4, 1) * 25;

    val = str.match('[a-zA-Z]');
    grade += _self.normalize(val ? val.length : 1/2, 3) * 10;

    val = str.match('[!@#$%^&*?_~.,;=]');
    grade += _self.normalize(val ? val.length : 1/6, 1) * 35;

    val = str.match('[A-Z]');
    grade += _self.normalize(val ? val.length : 1/6, 1) * 30;

    grade *= str.length / 8;

    return grade > 100 ? 100 : grade;
}

PrimeFaces.widget.Password.prototype.normalize = function(x, y) {
    var diff = x - y;

    if(diff <= 0) {
        return x / y;
    }
    else {
        return 1 + 0.5 * (x / (x + y/4));
    }
}

PrimeFaces.widget.Password.prototype.show = function() {
    //align panel before showing
    if(!this.cfg.inline) {
        this.panel.css({
            left:'', 
            top:'',
            'z-index': ++PrimeFaces.zindex
        })
        .position({
            my: 'left top',
            at: 'right top',
            of: this.jq
        });
        
        this.panel.fadeIn();
    }
    else {
       this.panel.slideDown(); 
    }        
}

PrimeFaces.widget.Password.prototype.hide = function() {
    if(this.cfg.inline)
        this.panel.slideUp();
    else
        this.panel.fadeOut();
}