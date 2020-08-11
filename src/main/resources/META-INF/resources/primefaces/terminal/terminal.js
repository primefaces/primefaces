/**
 * __PrimeFaces Terminal Widget__
 * 
 * Terminal is an AJAX-powered web-based terminal that brings desktop terminals to JSF.
 * 
 * @prop {JQuery} content The DOM element for the content of this terminal.
 * @prop {string[]} commands List with all commands the user entered up until now. Used for a command history.
 * @prop {number} commandIndex 0-based index of the current command, used when scrolling back to previous commands via
 * the arrow keys.
 * @prop {JQuery} input The DOM element for the prompt input field.
 * @prop {JQuery} promptContainerThe DOM element for the container with the prompt input.
 * @prop {JQuery} promptContainerParent The DOM element for the parent of the container with the prompt input.
 * 
 * @interface {PrimeFaces.widget.TerminalCfg} cfg The configuration for the {@link  Terminal| Terminal widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.prompt The current prompt text, i.e. the prefix at the beginning of the line.
 */
PrimeFaces.widget.Terminal = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
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
                    $this.processCommand();

                    e.preventDefault();
                break;
                
                case keyCode.TAB:
                    $this.autoCompleteCommand();
                    
                    e.preventDefault();
                    break;
            }
        });

        this.jq.on('click', function() {
            $this.focus();
        });
    },

    /**
     * Callback that is invoked when the enter key is pressed. Takes the entered command and executes it.
     * @private
     */
    processCommand: function() {
        if (this.input.val() != '') {
            this.commands.push(this.input.val());
            this.commandIndex = this.commands.length;
        }

        // print the previous command, the response will be appended async when the ajax response is received
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

                            // show prompt again and focus input
                            $this.promptContainerParent.show();
                            $this.focus();

                            // add response
                            $this.processResponse(content);
                        }
                    });

                return true;
            }
        };

        if(this.hasBehavior('command')) {
            this.callBehavior('command', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Callback that is invoked when the tab key is pressed. Computes the available commands and offers autocompletion.
     * @private
     */
    autoCompleteCommand: function() {

        var $this = this,
        options = {
            source : this.id,
            update: this.id,
            process: this.id,
            params: [
                {name: this.id + '_autocomplete', value: true}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            // parse the JSON response of the matches
                            var responseObj = JSON.parse(content);

                            if (responseObj == null) {
                                // if the response is null (no command model), do nothing
                            } else if (responseObj == null || responseObj.matches.length === 1) { // if there is one match, replace the current command with the match
                                if (responseObj.baseCommand == '') {
                                    $this.input.val(responseObj.matches[0]);
                                } else {
                                    $this.input.val(responseObj.baseCommand + ' ' + responseObj.matches[0]);
                                }
                            } else if (responseObj.matches.length > 1) { // if there are more matches,
                                // create clickable anchors for the them.
                                // the click event will insert the selected command into the input.

                                var matchesOutput = new Array();
                                var entryChar = '<span>&#8735;</span>';
                                var lineBreak = '<br />';

                                // store the baseCommand in a hidden span, the anchors will use it
                                if (responseObj.baseCommand != '') {
                                    matchesOutput.push($('<span style="display:none;" class="ui-terminal-basecommand">' + PrimeFaces.escapeHTML(responseObj.baseCommand) + '</span>'));
                                }

                                // create the anchors
                                for (i = 0; i < responseObj.matches.length; i++) { 
                                    var anchor = $('<a href="javascript:void(0);">' + PrimeFaces.escapeHTML(responseObj.matches[i]) + '</a>')
                                    .on("click", function(e) {
                                        e.preventDefault();

                                        // prepend the baseCommand to the selected command on click
                                        var baseSpan = $(this).siblings(".ui-terminal-basecommand");
                                        var command = $(this).text();
                                        if (baseSpan.length) {
                                            command = baseSpan.html().trim() + ' ' + command;
                                        }

                                        // update the terminal input
                                        $('input.ui-terminal-input').val(command);
                                    });

                                    matchesOutput.push($(entryChar), anchor, $(lineBreak));
                                }

                                // print the previous command
                                var item = $('<div></div>');
                                item.append('<span>' + PrimeFaces.escapeHTML(this.cfg.prompt) + '</span><span class="ui-terminal-command"></span>')
                                .appendTo(this.content);
                                item.find('.ui-terminal-command').text(this.input.val());

                                // print the list of matches as response
                                $('<div class="ui-terminal-autocomplete"></div>').append(matchesOutput).appendTo(this.content.children().last());

                                // always scroll down to the last item
                                this.jq.scrollTop(this.jq[0].scrollHeight);
                            }

                            $this.focus();
                        }
                    });

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },

    /**
     * Puts focus on this terminal input.
     */
    focus: function() {
        if (PrimeFaces.env.isIE()) {
            window.setTimeout(function(terminal){
                terminal.input.trigger('focus');
            }, 50);
        }
        else {
            this.input.trigger('focus');
        }
    },

    /**
     * Clears the console output.
     */
    clear: function() {
        this.content.html('');
        this.input.val('');
    },

    /**
     * Internally used to add the content from the AJAX response to the terminal. Can also be used for example by a
     * websocket.
     * @private
     * @param {string} content HTML escaped content to display.
     */
    processResponse: function(content) {
        $('<div>' + content + '</div>').appendTo(this.content.children().last());

        // always scroll down to the last item
        this.jq.scrollTop(this.jq[0].scrollHeight);
    }
});
