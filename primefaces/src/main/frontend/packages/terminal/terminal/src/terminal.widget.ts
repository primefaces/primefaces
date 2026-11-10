/**
 * The configuration for the {@link Terminal} widget.
 */
export interface TerminalCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** The current prompt text, i.e. the prefix at the beginning of the line. */
    prompt: string;
}

/**
 * __PrimeFaces Terminal Widget__
 *
 * Terminal is an AJAX-powered web-based terminal that brings desktop terminals to Faces.
 */
export class Terminal<Cfg extends TerminalCfg = TerminalCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The DOM element for the content of this terminal. */
    content: JQuery = $();
    /** List with all commands the user entered up until now. Used for a command history. */
    commands: string[] = [];
    /**
     * 0-based index of the current command, used when scrolling back to previous commands via the arrow keys.
     */
    commandIndex: number = 0;
    /** The DOM element for the prompt input field. */
    input: JQuery = $();
    /** The DOM element for the container with the prompt input. */
    promptContainer: JQuery = $();
    /** The DOM element for the parent of the container with the prompt input. */
    promptContainerParent: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.promptContainer = this.jq.find('> div:last-child > span.p-terminal-prompt');
        this.cfg.prompt = this.promptContainer.text();
        this.content = this.jq.children('.p-terminal-content');
        this.input = this.promptContainer.next('');
        this.commands = [];
        this.commandIndex = 0;
        this.promptContainerParent = this.promptContainer.parent();

        this.bindEvents();
    }

    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindEvents(): void {
        this.input.on('keydown.terminal', (e) => {
            switch (e.key) {
                case 'ArrowUp':
                    if (this.commandIndex > 0) {
                        this.input.val(this.commands[--this.commandIndex]!);
                    }

                    e.preventDefault();
                    break;

                case 'ArrowDown':
                    if (this.commandIndex < (this.commands.length - 1)) {
                        this.input.val(this.commands[++this.commandIndex]!);
                    }
                    else {
                        this.commandIndex = this.commands.length;
                        this.input.val('');
                    }

                    e.preventDefault();
                    break;

                case 'Enter':
                case 'NumpadEnter':
                    this.processCommand();

                    e.preventDefault();
                    break;

                case 'Tab':
                    this.autoCompleteCommand();

                    e.preventDefault();
                    break;
            }
        });

        this.jq.on('click', () => {
            this.focus();
        });
    }

    /**
     * Callback that is invoked when the enter key is pressed. Takes the entered command and executes it.
     */
    private processCommand(): void {
        if (this.input.val() != '') {
            this.commands.push(this.input.val() as string);
            this.commandIndex = this.commands.length;
        }

        // print the previous command, the response will be appended async when the ajax response is received
        const item = $('<div></div>');
        item.append('<span>' + PrimeFaces.escapeHTML(this.cfg.prompt!) + '</span><span class="p-terminal-command"></span>').appendTo(this.content);
        item.find('.p-terminal-command').text(this.input.val() as string);

        // hide the prompt until the command finishes
        this.promptContainerParent.hide();

        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            params: [
                { name: this.id + '_command', value: true }
            ],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        // clear input
                        this.input.val('');

                        // show prompt again and focus input
                        this.promptContainerParent.show();
                        this.focus();

                        // add response
                        this.processResponse(content);
                    }
                });

                return true;
            }
        };

        if (this.hasBehavior('command')) {
            this.callBehavior('command', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Callback that is invoked when the tab key is pressed. Computes the available commands and offers autocompletion.
     */
    private autoCompleteCommand(): void {

        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            params: [
                { name: this.id + '_autocomplete', value: true }
            ],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        // parse the JSON response of the matches
                        const responseObj: PrimeFacesTypes.widget.Terminal.AutoCompleteResponse | null = JSON.parse(PrimeFaces.utils.decodeXml(content) as string);

                        if (responseObj == null) {
                            // if the response is null (no command model), do nothing
                        } else if (responseObj == null || responseObj.matches.length === 1) { // if there is one match, replace the current command with the match
                            if (responseObj.baseCommand == '') {
                                this.input.val(responseObj.matches[0]!);
                            } else {
                                this.input.val(responseObj.baseCommand + ' ' + responseObj.matches[0]);
                            }
                        } else if (responseObj.matches.length > 1) { // if there are more matches,
                            // create clickable anchors for the them.
                            // the click event will insert the selected command into the input.

                            const matchesOutput: JQuery[] = new Array();
                            const entryChar = '<span>&#8735;</span>';
                            const lineBreak = '<br />';

                            // store the baseCommand in a hidden span, the anchors will use it
                            if (responseObj.baseCommand != '') {
                                matchesOutput.push($('<span style="display:none;" class="p-terminal-basecommand">' + PrimeFaces.escapeHTML(responseObj.baseCommand) + '</span>'));
                            }

                            // create the anchors
                            for (let i = 0; i < responseObj.matches.length; i++) {
                                const anchor = $('<a href="javascript:void(0);">' + PrimeFaces.escapeHTML(responseObj.matches[i]!) + '</a>')
                                    .on("click", (e) => {
                                        e.preventDefault();

                                        // prepend the baseCommand to the selected command on click
                                        const baseSpan = $(e.currentTarget).siblings(".p-terminal-basecommand");
                                        let command = $(e.currentTarget).text();
                                        if (baseSpan.length) {
                                            command = baseSpan.html().trim() + ' ' + command;
                                        }

                                        // update the terminal input
                                        this.input.val(command);
                                    });

                                matchesOutput.push($(entryChar), anchor, $(lineBreak));
                            }

                            // print the previous command
                            const item = $('<div></div>');
                            item.append('<span>' + PrimeFaces.escapeHTML(this.cfg.prompt!) + '</span><span class="p-terminal-command"></span>')
                                .appendTo(this.content);
                            item.find('.p-terminal-command').text(this.input.val() as string);

                            // print the list of matches as response
                            $('<div class="p-terminal-autocomplete"></div>').append(matchesOutput).appendTo(this.content.children().last());

                            // always scroll down to the last item
                            this.jq.scrollTop(this.jq[0]!.scrollHeight);
                        }

                        this.focus();
                    }
                });

                return true;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }

    /**
     * Puts focus on this terminal input.
     */
    focus(): void {
        this.input.trigger('focus');
    }

    /**
     * Clears the console output.
     */
    clear(): void {
        this.content.html('');
        this.input.val('');
    }

    /**
     * Internally used to add the content from the AJAX response to the terminal. Can also be used for example by a
     * websocket.
     * @param content HTML escaped content to display.
     */
    processResponse(content: string): void {
        $('<div>' + content + '</div>').appendTo(this.content.children().last());

        // always scroll down to the last item
        this.jq.scrollTop(this.jq[0]!.scrollHeight);
    }
}
