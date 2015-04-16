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
            params: [
                {name: this.id + '_command', value: true}
            ],
            onsuccess: function (responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function (content) {
                            var commandResponseContainer = $('<div></div>');
                            commandResponseContainer.append('<div class="ui-terminal-command">' + content+ '</div>').appendTo(this.content);
                            this.input.val('');
                            $('#ui-prompt-id').show();
                            //scroll the terminal down on long output
                            this.content.prevObject[0].scrollTop = this.content.prevObject[0].scrollHeight;
                            this.focus();
                        }
                    });

                return true;
            }
        };
        var commandResponseContainer = $('<div></div>');
        var outputid = Date.now();
        //print the previous command out, so we can append asynchronously
        commandResponseContainer.append('<span class="ui-terminal-command">' + this.cfg.prompt + '</span><span class="ui-terminal-command" id=command' + outputid + '></span>').appendTo(this.content);
        //sanitize the command text
        $('#command' + outputid).text(this.input.val());
        //hide the prompt until the command finishes
        $('#ui-prompt-id').hide();
        PrimeFaces.ajax.Request.handle(options);
    },
            
    focus: function() {
        this.input.trigger('focus');
    },
            
    clear: function() {
        this.content.html('');
        this.input.val('');
    },
    //to be used with the socket component, allowing the command to output asynchronously to the terminal before finishing
    handle: function (content) {
        var commandResponseContainer = $("<div></div>");
        commandResponseContainer.append('<div class="ui-terminal-command">' + content + "</div>").appendTo(this.content);
        this.content.prevObject[0].scrollTop = this.content.prevObject[0].scrollHeight;
    }
});
