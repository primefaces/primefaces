/* 
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