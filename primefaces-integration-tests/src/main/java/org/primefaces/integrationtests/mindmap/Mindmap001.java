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
package org.primefaces.integrationtests.mindmap;

import org.primefaces.event.SelectEvent;
import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.model.mindmap.DefaultMindmapNode;
import org.primefaces.model.mindmap.MindmapNode;

import java.io.Serial;
import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Mindmap001 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private MindmapNode root;

    @PostConstruct
    public void init() {
        root = new DefaultMindmapNode("Root", "Root node", "FFCC00", false);
        root.addNode(new DefaultMindmapNode("A", "Node A", "6e9ebf", true));
        root.addNode(new DefaultMindmapNode("B", "Node B", "6e9ebf", true));
        root.addNode(new DefaultMindmapNode("C", "Node C", "6e9ebf", true));
        // label must be escaped in the widget script and in the select response
        root.addNode(new DefaultMindmapNode("\"\\</script>", "Node D", "6e9ebf", true));
    }

    /**
     * Loads the child nodes of "A" lazily on the first selection.
     *
     * @param event the select event
     */
    public void onSelect(SelectEvent<MindmapNode> event) {
        MindmapNode node = event.getObject();
        if ("A".equals(node.getLabel()) && node.getChildren().isEmpty()) {
            node.addNode(new DefaultMindmapNode("A1", "Node A1", "82c542", false));
            node.addNode(new DefaultMindmapNode("A2", "Node A2", "82c542", false));
        }
        TestUtils.addMessage("Selected", node.getLabel());
    }

    public void onDblselect(SelectEvent<MindmapNode> event) {
        TestUtils.addMessage("Double selected", event.getObject().getLabel());
    }
}
