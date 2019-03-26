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

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class TerminalAutoCompleteMatches extends JSONObject {

    private static final String BASECOMMAND = "baseCommand";
    private static final String MATCHES = "matches";

    public TerminalAutoCompleteMatches() {
        this("");
    }

    public TerminalAutoCompleteMatches(String baseCommand) {
        super();
        setBaseCommand(baseCommand);
        put(MATCHES, new JSONArray());
    }

    public String getBaseCommand() {
        return (String) get(BASECOMMAND);
    }

    public void setBaseCommand(String baseCommand) {
        put(BASECOMMAND, baseCommand);
    }

    public void extendBaseCommand(TerminalCommand argument) {
        extendBaseCommand(argument.getText());
    }

    public void extendBaseCommand(String argument) {
        String baseCommand = getBaseCommand();

        if (baseCommand.isEmpty()) {
            baseCommand = argument;
        }
        else {
            baseCommand = baseCommand + " " + argument;
        }

        setBaseCommand(baseCommand);
    }

    public Collection<String> getMatches() {
        JSONArray arr = (JSONArray) get(MATCHES);

        ArrayList<String> matches = new ArrayList<String>(arr.length());
        Iterator<Object> i = arr.iterator();

        while (i.hasNext()) {
            String match = (String) i.next();
            matches.add(match);
        }

        return matches;
    }

    public void setMatches(Collection<String> matches) {
        JSONArray arr = (JSONArray) get(MATCHES);

        if (matches != null) {
            for (String match : matches) {
                arr.put(match);
            }
        }

        put(MATCHES, arr);
    }

    public void addMatch(TerminalCommand match) {
        this.addMatch(match.getText());
    }

    public void addMatch(String match) {
        JSONArray arr = (JSONArray) get(MATCHES);

        arr.put(match);

        put(MATCHES, arr);
    }

}
