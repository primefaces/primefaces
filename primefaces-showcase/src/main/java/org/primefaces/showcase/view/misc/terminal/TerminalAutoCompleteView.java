/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.showcase.view.misc.terminal;

import org.primefaces.model.terminal.TerminalAutoCompleteModel;
import org.primefaces.model.terminal.TerminalCommand;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class TerminalAutoCompleteView implements Serializable {

    private TerminalAutoCompleteModel autoCompleteModel;

    public TerminalAutoCompleteView() {
        this.autoCompleteModel = buildAutoCompleteModel();
    }

    private TerminalAutoCompleteModel buildAutoCompleteModel() {
        TerminalAutoCompleteModel model = new TerminalAutoCompleteModel();

        TerminalCommand git = model.addCommand("git");

        git.addArgument("checkout");
        git.addArgument("commit");
        git.addArgument("status");
        git.addArgument("pull");
        git.addArgument("push").addArgument("origin").addArgument("master");

        TerminalCommand svn = model.addCommand("svn");

        svn.addArgument("commit");
        svn.addArgument("checkout");
        svn.addArgument("status");
        svn.addArgument("update");

        return model;
    }

    public TerminalAutoCompleteModel getAutoCompleteModel() {
        return autoCompleteModel;
    }

    public String handleCommand(String command, String[] params) {
        StringBuilder response = new StringBuilder("The command you entered was: '").append(command);

        for (String param : params) {
            response.append(" ").append(param);
        }

        return response.append("'").toString();
    }

}
