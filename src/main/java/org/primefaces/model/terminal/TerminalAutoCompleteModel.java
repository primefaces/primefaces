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

public class TerminalAutoCompleteModel implements Serializable {

    private List<TerminalCommand> commands;

    public TerminalAutoCompleteModel() {
        this.commands = new ArrayList<TerminalCommand>();
    }

    public List<TerminalCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<TerminalCommand> commands) {
        this.commands = commands;
    }

    public TerminalCommand addCommand(String commandText) {
        TerminalCommand command = new TerminalCommand(commandText);

        this.commands.add(command);

        return command;
    }

}
