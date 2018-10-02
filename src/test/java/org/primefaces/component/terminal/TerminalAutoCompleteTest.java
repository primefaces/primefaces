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

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.model.terminal.TerminalCommand;
import org.primefaces.model.terminal.TerminalAutoCompleteMatches;
import org.primefaces.model.terminal.TerminalAutoCompleteModel;

public class TerminalAutoCompleteTest {

    private Terminal terminal;
    private TerminalAutoCompleteModel model;

    @Before
    public void setup() {
        terminal = new Terminal();

        model = new TerminalAutoCompleteModel();

        TerminalCommand git = model.addCommand("git");

        git.addArgument("checkout");
        git.addArgument("commit");
        git.addArgument("rebase");
        git.addArgument("squash");
        git.addArgument("status");
        git.addArgument("pull");
        git.addArgument("push").addArgument("origin").addArgument("master");
    }

    @After
    public void teardown() {
        terminal = null;
        model = null;
    }

    @Test
    public void givenGThenReturnsGit() {
        // given
        final String input = "g";
        final String[] args = {};

        // when
        TerminalAutoCompleteMatches TerminalAutoCompleteMatches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(TerminalAutoCompleteMatches, "");
        assertMatches(TerminalAutoCompleteMatches, "git");
    }

    @Test
    public void givenGitThenReturnsAllSecondLevelArguments() {
        // given
        final String input = "git";
        final String[] args = {};

        // when
        TerminalAutoCompleteMatches TerminalAutoCompleteMatches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(TerminalAutoCompleteMatches, "git");
        assertMatches(TerminalAutoCompleteMatches, "pull", "push","checkout", "commit", "rebase", "squash", "status");
    }

    @Test
    public void givenGitCThenReturnsCommitAndCheckout() {
        // given
        final String input = "git";
        final String[] args = { "c" };

        // when
        TerminalAutoCompleteMatches TerminalAutoCompleteMatches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(TerminalAutoCompleteMatches, "git");
        assertMatches(TerminalAutoCompleteMatches, "commit", "checkout");
    }

    @Test
    public void givenGitCThenReturnsCommit() {
        // given
        final String input = "git";
        final String[] args = { "co" };

        // when
        TerminalAutoCompleteMatches TerminalAutoCompleteMatches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(TerminalAutoCompleteMatches, "git");
        assertMatches(TerminalAutoCompleteMatches, "commit");
    }

    @Test
    public void givenGitPThenReturnsPullAndPush() {
        // given
        final String input = "git";
        final String[] args = { "p" };

        // when
        TerminalAutoCompleteMatches TerminalAutoCompleteMatches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(TerminalAutoCompleteMatches, "git");
        assertMatches(TerminalAutoCompleteMatches, "pull", "push");
    }

    @Test
    public void givenGitRThenReturnsRebase() {
        // given
        final String input = "git";
        final String[] args = { "r" };

        // when
        TerminalAutoCompleteMatches TerminalAutoCompleteMatches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(TerminalAutoCompleteMatches, "git");
        assertMatches(TerminalAutoCompleteMatches, "rebase");
    }

    @Test
    public void givenGitPushOThenReturnsGitPushOrigin() {
        // given
        final String input = "git";
        final String[] args = { "push", "o" };

        // when
        TerminalAutoCompleteMatches TerminalAutoCompleteMatches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(TerminalAutoCompleteMatches, "git push");
        assertMatches(TerminalAutoCompleteMatches, "origin");
    }

    @Test
    public void givenGitPushOriginThenReturnsGitPushOriginMaster() {
        // given
        final String input = "git";
        final String[] args = { "push", "origin" };

        // when
        TerminalAutoCompleteMatches TerminalAutoCompleteMatches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(TerminalAutoCompleteMatches, "git push origin");
        assertMatches(TerminalAutoCompleteMatches, "master");
    }

    private void assertBaseCommand(TerminalAutoCompleteMatches autoCompleteMatches, String expectedBaseCommand) {
        assertEquals(expectedBaseCommand, autoCompleteMatches.getBaseCommand());
    }

    private void assertMatches(TerminalAutoCompleteMatches autoCompleteMatches, String... elements) {
        Collection<String> matches = autoCompleteMatches.getMatches();

        assertEquals(elements.length, matches.size());
        for (String match : elements) {
            assertTrue(matches.contains(match));
        }

    }

}