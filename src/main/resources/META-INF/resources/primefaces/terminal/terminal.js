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
        this.promptContainerParent = this.promptContainer.parent();
        
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
        
        this.jq.on('click', function() {
            $this.focus();
        });
    },
            
    processCommand: function() {
        this.commands.push(this.input.val());
        this.commandIndex++;

        // print the previous command, the response will be appenend async when the ajax response is received
        var item = $('<div></div>');
        item.append('<span>' + this.cfg.prompt + '</span><span class="ui-terminal-command"></span>').appendTo(this.content);
        item.find('.ui-terminal-command').text(this.input.val());

        // hide the prompt until the command finishes
        this.promptContainerParent.hide();

        var $this = this,
        options = {
            source : this.id,
            update: this.id,
            process: this.id,
            params: [
                {name: this.id + '_command', value: true}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            // clear input
                            $this.input.val('');
                            
                            // show promt again and focus input
                            $this.promptContainerParent.show();
                            
                            if(PrimeFaces.isIE()) {
                            	window.setTimeout(function(){
                            		$this.focus();
                             	}, 50);
                            } else {
                            	$this.focus();
                            }

                            // add response
                            $this.processResponse(content);
                        }
                    });

                return true;
            }
        };
        
        PrimeFaces.ajax.Request.handle(options);
    },
            
    focus: function() {
        this.input.trigger('focus');
    },
            
    clear: function() {
        this.content.html('');
        this.input.val('');
    },

    /**
     * Internally used to add the content from the ajax response to the terminal.
     * Can also be used e.g. by a websocket.
     * 
     * @param {string} content
     */
    processResponse: function(content) {
        $('<div></div>').text(content).appendTo(this.content.children().last());

        // always scroll down to the last item
        this.jq.scrollTop(this.jq[0].scrollHeight);
    }
});
