/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.model.terminal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TerminalCommand implements Serializable {

    private static final long serialVersionUID = 1L;

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
            this.arguments = new ArrayList<>();
        }

        this.arguments.add(argument);

        return argument;
    }

}
