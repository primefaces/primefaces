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

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

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
