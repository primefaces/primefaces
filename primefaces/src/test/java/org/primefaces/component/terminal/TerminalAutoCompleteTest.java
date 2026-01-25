/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.component.terminal;

import org.primefaces.model.terminal.TerminalAutoCompleteMatches;
import org.primefaces.model.terminal.TerminalAutoCompleteModel;
import org.primefaces.model.terminal.TerminalCommand;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerminalAutoCompleteTest {

    private Terminal terminal;
    private TerminalAutoCompleteModel model;

    @BeforeEach
    void setup() {
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

    @AfterEach
    void teardown() {
        terminal = null;
        model = null;
    }

    @Test
    void givenGThenReturnsGit() {
        // given
        String input = "g";
        String[] args = {};

        // when
        TerminalAutoCompleteMatches matches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(matches, "");
        assertMatches(matches, "git");
    }

    @Test
    void givenGitThenReturnsAllSecondLevelArguments() {
        // given
        String input = "git";
        String[] args = {};

        // when
        TerminalAutoCompleteMatches matches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(matches, "git");
        assertMatches(matches, "pull", "push", "checkout", "commit", "rebase", "squash", "status");
    }

    @Test
    void givenGitCThenReturnsCommitAndCheckout() {
        // given
        String input = "git";
        String[] args = {"c"};

        // when
        TerminalAutoCompleteMatches matches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(matches, "git");
        assertMatches(matches, "commit", "checkout");
    }

    @Test
    void givenGitCThenReturnsCommit() {
        // given
        String input = "git";
        String[] args = {"co"};

        // when
        TerminalAutoCompleteMatches matches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(matches, "git");
        assertMatches(matches, "commit");
    }

    @Test
    void givenGitPThenReturnsPullAndPush() {
        // given
        String input = "git";
        String[] args = {"p"};

        // when
        TerminalAutoCompleteMatches matches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(matches, "git");
        assertMatches(matches, "pull", "push");
    }

    @Test
    void givenGitRThenReturnsRebase() {
        // given
        String input = "git";
        String[] args = {"r"};

        // when
        TerminalAutoCompleteMatches matches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(matches, "git");
        assertMatches(matches, "rebase");
    }

    @Test
    void givenGitPushOThenReturnsGitPushOrigin() {
        // given
        String input = "git";
        String[] args = {"push", "o"};

        // when
        TerminalAutoCompleteMatches matches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(matches, "git push");
        assertMatches(matches, "origin");
    }

    @Test
    void givenGitPushOriginThenReturnsGitPushOriginMaster() {
        // given
        String input = "git";
        String[] args = {"push", "origin"};

        // when
        TerminalAutoCompleteMatches matches = terminal.traverseAutoCompleteModel(model, input, args);

        // then
        assertBaseCommand(matches, "git push origin");
        assertMatches(matches, "master");
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
