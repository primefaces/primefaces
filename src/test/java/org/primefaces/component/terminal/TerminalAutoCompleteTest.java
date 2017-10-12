/**
 * Copyright 2009-2017 PrimeTek.
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

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.terminal.AutoCompleteMatches;

public class TerminalAutoCompleteTest {

    private Terminal terminal;

    private DefaultTreeNode rootNode;

    @Before
    public void setup() {
        terminal = new Terminal();

        rootNode = new DefaultTreeNode("Root");

        DefaultTreeNode show = new DefaultTreeNode("show");

        DefaultTreeNode servers = new DefaultTreeNode("servers");
        DefaultTreeNode status = new DefaultTreeNode("status");
        DefaultTreeNode info = new DefaultTreeNode("info");

        DefaultTreeNode version = new DefaultTreeNode("version");
        DefaultTreeNode date = new DefaultTreeNode("date");

        info.getChildren().add(version);
        info.getChildren().add(date);

        show.getChildren().addAll(Arrays.asList(servers, status, info));
        rootNode.getChildren().add(show);
    }

    @After
    public void teardown() {
        terminal = null;
        rootNode = null;
    }

    @Test
    public void givenShoThenReturnsShow() {
        // given
        final String command = "sho";
        final String[] args = new String[] {};

        // when
        AutoCompleteMatches autoCompleteMatches = terminal.traverseCommandModel(rootNode, command, args);

        // then
        assertBaseCommand(autoCompleteMatches, "");
        assertMatches(autoCompleteMatches, "show");
    }

    @Test
    public void givenShowThenReturnsStatusAndInfo() {
        // given
        final String command = "show";
        final String[] args = new String[] {};

        // when
        AutoCompleteMatches autoCompleteMatches = terminal.traverseCommandModel(rootNode, command, args);

        // then
        assertBaseCommand(autoCompleteMatches, "show");
        assertMatches(autoCompleteMatches, "servers", "status", "info");
    }

    @Test
    public void givenShowStaThenReturnsStatus() {
        // given
        final String command = "show";
        final String[] args = new String[] { "sta" };

        // when
        AutoCompleteMatches autoCompleteMatches = terminal.traverseCommandModel(rootNode, command, args);

        // then
        assertBaseCommand(autoCompleteMatches, "show");
        assertMatches(autoCompleteMatches, "status");
    }

    @Test
    public void givenShowThenReturnsServersAndStatus() {
        // given
        final String command = "show";
        final String[] args = new String[] { "s" };

        // when
        AutoCompleteMatches autoCompleteMatches = terminal.traverseCommandModel(rootNode, command, args);

        // then
        assertBaseCommand(autoCompleteMatches, "show");
        assertMatches(autoCompleteMatches, "servers", "status");
    }

    @Test
    public void givenShowInfoThenReturnsInfoAndDate() {
        // given
        final String command = "show";
        final String[] args = new String[] { "info" };

        // when
        AutoCompleteMatches autoCompleteMatches = terminal.traverseCommandModel(rootNode, command, args);

        // then,
        assertBaseCommand(autoCompleteMatches, "show info");
        assertMatches(autoCompleteMatches, "date", "version");
    }

    private void assertBaseCommand(AutoCompleteMatches autoCompleteMatches, String expectedBaseCommand) {
        assertEquals(autoCompleteMatches.getBaseCommand(), expectedBaseCommand);
    }

    private void assertMatches(AutoCompleteMatches autoCompleteMatches, String... elements) {
        Collection<String> matches = autoCompleteMatches.getMatches();

        assertEquals(elements.length, matches.size());
        for (String match : elements) {
            assertTrue(matches.contains(match));
        }

    }

}