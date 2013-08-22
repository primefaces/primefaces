/**
 * PrimeFaces Terminal Widget
 */
PrimeFaces.widget.Terminal = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.promptContainer = this.jq.find('> div:last-child > span.ui-terminal-prompt');
        this.cfg.prompt = this.promptContainer.text();
        this.content = this.jq.children('.ui-terminal-content');
        this.input = this.promptContainer.next('');
        this.commands = [];
        this.commandIndex = 0;
        
        this.bindEvents();
    },
            
    bindEvents: function() {
        var $this = this;
        
        this.input.on('keydown.terminal', function(e) {
            var keyCode = $.ui.keyCode;
            
            switch(e.which) {
                case keyCode.UP:
                    if($this.commandIndex > 0) {
                        $this.input.val($this.commands[--$this.commandIndex]);
                    }
                    
                    e.preventDefault();
                break;

                case keyCode.DOWN:
                    if($this.commandIndex < ($this.commands.length - 1)) {
                        $this.input.val($this.commands[++$this.commandIndex]);
                    }
                    else {
                        $this.commandIndex = $this.commands.length;
                        $this.input.val('');
                    }
                    
                    e.preventDefault();
                break;

                case keyCode.ENTER:
                case keyCode.NUMPAD_ENTER:
                    $this.processCommand();

                    e.preventDefault();
                break;
            }
        });
    },
            
    processCommand: function() {
        this.commands.push(this.input.val());
        this.commandIndex++;
        
        var $this = this,
        options = {
            source : this.id,
            update: this.id,
            process: this.id,
            onsuccess: function(responseXML) {
                var xmlDoc = $(responseXML.documentElement),
                updates = xmlDoc.find("update");
                
                for(var i=0; i < updates.length; i++) {
                    var update = updates.eq(i),
                    id = update.attr('id'),
                    content = update.get(0).childNodes[0].nodeValue;            

                    if(id === $this.id) {
                       var commandResponseContainer = $('<div></div>');
                       commandResponseContainer.append('<span>' + $this.cfg.prompt + '</span><span class="ui-terminal-command">' +  $this.input.val() + '</span>')
                                                .append('<div>' + content + '</div>').appendTo($this.content);
                                        
                       $this.input.val('');
                    } 
                    else {
                        PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                    }
                }

                PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);
                
                $this.jq.scrollTop($this.content.height());

                return true;
            }
        };
        
        options.params = [
          {name: this.id + '_command', value: true}
        ];
        
        PrimeFaces.ajax.AjaxRequest(options);
    },
            
    clear: function() {
        this.content.html('');
        this.input.val('');
    }
});