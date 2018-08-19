/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.terminal;

import java.util.ArrayDeque;
import java.util.Arrays;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;

import org.primefaces.model.terminal.TerminalAutoCompleteMatches;
import org.primefaces.model.terminal.TerminalAutoCompleteModel;
import org.primefaces.model.terminal.TerminalCommand;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "terminal/terminal.css"),
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "terminal/terminal.js")
})
public class Terminal extends TerminalBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Terminal";

    public static final String CONTAINER_CLASS = "ui-terminal ui-widget ui-widget-content ui-corner-all";
    public static final String WELCOME_MESSAGE_CLASS = "ui-terminal-welcome";
    public static final String CONTENT_CLASS = "ui-terminal-content";
    public static final String PROMPT_CLASS = "ui-terminal-prompt";
    public static final String INPUT_CLASS = "ui-terminal-input";

    public boolean isCommandRequest() {
        FacesContext context = getFacesContext();
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_command");
    }

    public boolean isAutoCompleteRequest() {
        FacesContext context = getFacesContext();
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_autocomplete");
    }

    TerminalAutoCompleteMatches traverseAutoCompleteModel(TerminalAutoCompleteModel commandModel, String input, String[] args) {
        ArrayDeque argumentQueue = new ArrayDeque(Arrays.asList(args));
        return traverseAutoCompleteModel(commandModel, input, argumentQueue);
    }

    private TerminalAutoCompleteMatches traverseAutoCompleteModel(TerminalAutoCompleteModel commandModel, String input, ArrayDeque<String> inputArguments) {
        TerminalAutoCompleteMatches matches = new TerminalAutoCompleteMatches();

        for (TerminalCommand command : commandModel.getCommands()) {
            if (isPartialMatch(command, input)) {
                if (isExactMatch(command, input) && command.hasArguments()) {
                    matches.extendBaseCommand(input);
                    return traverseArguments(command, matches, inputArguments);
                }

                matches.addMatch(command);
            }
        }

        return matches;
    }

    private TerminalAutoCompleteMatches traverseArguments(TerminalCommand command, TerminalAutoCompleteMatches matches, ArrayDeque<String> inputArguments) {
        if (command.getArguments() != null) {
            for (TerminalCommand argument : command.getArguments()) {
                if (!inputArguments.isEmpty()) {
                    String inputArgument = inputArguments.peek();

                    if (isPartialMatch(argument, inputArgument)) {
                        if (isExactMatch(argument, inputArgument) && argument.hasArguments()) {
                            matches.extendBaseCommand(argument);
                            inputArguments.removeFirst();
                            return traverseArguments(argument, matches, inputArguments);
                        }

                        matches.addMatch(argument);
                    }
                }
                else {
                    matches.addMatch(argument);
                }
            }
        }

        return matches;
    }

    private boolean isPartialMatch(TerminalCommand command, String input) {
        return command.getText().startsWith(input);
    }

    private boolean isExactMatch(TerminalCommand command, String input) {
        return command.getText().equalsIgnoreCase(input);
    }
}