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
package org.primefaces.model.terminal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TerminalCommand implements Serializable {

    private String text;
    private List<TerminalCommand> arguments;

    public TerminalCommand(String commandText) {
        this.text = commandText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TerminalCommand> getArguments() {
        return arguments;
    }

    public void setArguments(List<TerminalCommand> arguments) {
        this.arguments = arguments;
    }

    public boolean hasArguments() {
        return ((this.arguments == null) || !this.arguments.isEmpty());
    }

    public TerminalCommand addArgument(String argumentText) {
        TerminalCommand argument = new TerminalCommand(argumentText);

        if (this.arguments == null) {
            this.arguments = new ArrayList<TerminalCommand>();
        }

        this.arguments.add(argument);

        return argument;
    }

}
