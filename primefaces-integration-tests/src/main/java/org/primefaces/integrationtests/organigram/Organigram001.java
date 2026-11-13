/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.integrationtests.organigram;

import org.primefaces.event.organigram.OrganigramNodeCollapseEvent;
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.model.DefaultOrganigramNode;
import org.primefaces.model.OrganigramNode;

import java.io.Serial;
import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Organigram001 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private OrganigramNode rootNode;
    private OrganigramNode selection;

    @PostConstruct
    public void init() {
        rootNode = new DefaultOrganigramNode("root", "Company", null);
        rootNode.setCollapsible(false);

        OrganigramNode development = addDivision(rootNode, "Development", "Alice", "Bob");
        addDivision(development, "QA", "Carol");
        addDivision(rootNode, "Marketing", "Dave");
    }

    private OrganigramNode addDivision(OrganigramNode parent, String name, String... employees) {
        OrganigramNode division = new DefaultOrganigramNode("division", name, parent);
        division.setSelectable(true);

        for (String employee : employees) {
            OrganigramNode employeeNode = new DefaultOrganigramNode("employee", employee, division);
            employeeNode.setSelectable(true);
        }

        return division;
    }

    public void onSelect(OrganigramNodeSelectEvent event) {
        TestUtils.addMessage("Selected", (String) event.getOrganigramNode().getData());
    }

    public void onExpand(OrganigramNodeExpandEvent event) {
        TestUtils.addMessage("Expanded", (String) event.getOrganigramNode().getData());
    }

    public void onCollapse(OrganigramNodeCollapseEvent event) {
        TestUtils.addMessage("Collapsed", (String) event.getOrganigramNode().getData());
    }
}
