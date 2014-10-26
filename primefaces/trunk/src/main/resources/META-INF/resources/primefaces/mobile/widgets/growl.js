/**
 * PrimeFaces Mobile Growl Widget
 */
PrimeFaces.widget.Growl = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.initOptions(cfg);
        
        this.jq.popup({
            positionTo: 'window',
            theme: 'b',
            overlayTheme: 'b'
        });
        
        this.container = $(this.jqId + '-popup');
        this.popupContainer = this.container.find('> div.ui-popup');
        this.popupContainer.append('<p></p>');
        this.messageContainer = this.popupContainer.children('p');
        this.placeholder = $(this.jqId + '-placeholder');
        
        this.popupContainer.removeAttr('id');
        this.placeholder.attr('id', this.id);
        
        this.show(this.cfg.msgs);
    },
    
    initOptions: function(cfg) {
        this.cfg = cfg;
        this.cfg.sticky = this.cfg.sticky||false;
        this.cfg.life = this.cfg.life||6000;
        this.cfg.escape = (this.cfg.escape === false) ? false : true;
    },
    
    refresh: function(cfg) {
    	this.initOptions(cfg);
        this.show(cfg.msgs);
    },
    
    show: function(msgs) {
        var $this = this;

        this.removeAll();

        if(msgs.length) {
            $.each(msgs, function(index, msg) {
                $this.renderMessage(msg);
            });

            this.jq.popup('open', {transition:'pop'});
            
            if(!this.cfg.sticky) {
                this.setRemovalTimeout();
            }
        }
    },
    
    removeAll: function() {
        this.messageContainer.children().remove();
    },
    
    renderMessage: function(msg) {
        var markup = '<div class="ui-growl-item ui-grid-a">';
        markup += '<div class="ui-growl-severity ui-block-a"><a class="ui-btn ui-shadow ui-corner-all ui-btn-icon-notext ui-btn-b ui-btn-inline" href="#"></a></div>';
        markup += '<div class="ui-growl-message ui-block-b">';
        markup += '<div class="ui-growl-summary"></div>';
        markup += '<div class="ui-growl-detail"></div>';
        markup += '</div></div>';
        
        var item = $(markup),
        severityEL = item.children('.ui-growl-severity'),
        summaryEL = item.find('> .ui-growl-message > .ui-growl-summary'),
        detailEL = item.find('> .ui-growl-message > .ui-growl-detail');

        severityEL.children('a').addClass(this.getSeverityIcon(msg.severity));
        
        if(this.cfg.escape) {
            summaryEL.text(msg.summary);
            detailEL.text(msg.detail);
        }
        else {
            summaryEL.html(msg.summary);
            detailEL.html(msg.detail);
        }
                
        this.messageContainer.append(item);
    },
    
    getSeverityIcon: function(severity) {
        var icon;
        
        switch(severity) {
            case 'info':
                icon = 'ui-icon-info';
                break;
            break;
    
            case 'warn':
                icon = 'ui-icon-alert';
                break;
            break;
    
            case 'error':
                icon = 'ui-icon-delete';
                break;
            break;
    
            case 'fatal':
                icon = 'ui-icon-delete';
                break;
            break;
        }
        
        return icon;
    },
        
    setRemovalTimeout: function() {
        var $this = this;
        
        if(this.timeout) {
            clearTimeout(this.timeout);
        }
        
        this.timeout = setTimeout(function() {
            $this.jq.popup('close');
        }, this.cfg.life);
    }
});
