/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.model;

import static org.junit.Assert.*;
import org.junit.Before;

import org.junit.Test;

public class CheckboxTreeNodeTest {
    
    private TreeNode root;
    
    @Before
    public void initModel() {
        root = new CheckboxTreeNode("Files", null);
		
		TreeNode documents = new CheckboxTreeNode("Documents", root);
		TreeNode pictures = new CheckboxTreeNode("Pictures", root);
		TreeNode movies = new CheckboxTreeNode("Movies", root);
		
		TreeNode work = new CheckboxTreeNode("Work", documents);
		TreeNode primefaces = new CheckboxTreeNode("PrimeFaces", documents);
		
		//Documents
		TreeNode expenses = new CheckboxTreeNode("document", "Expenses", work);
		TreeNode resume = new CheckboxTreeNode("document", "Resume", work);
		TreeNode refdoc = new CheckboxTreeNode("document", "Refdoc", primefaces);
		
		//Pictures
		TreeNode barca = new CheckboxTreeNode("picture", "Barcelona.jpg", pictures);
		TreeNode primelogo = new CheckboxTreeNode("picture", "Logo.jpg", pictures);
		TreeNode optimus = new CheckboxTreeNode("picture", "OptimusPrime.jpg", pictures);
		
		//Movies
		TreeNode pacino = new CheckboxTreeNode("Al Pacino", movies);
		TreeNode deniro = new CheckboxTreeNode("Robert De Niro", movies);
		
		TreeNode scarface = new CheckboxTreeNode("mp3", "Scarface", pacino);
		TreeNode carlitosWay = new CheckboxTreeNode("mp3", "Carlito's Way", pacino);
		
		TreeNode goodfellas = new CheckboxTreeNode("mp3", "Goodfellas", deniro);
		TreeNode untouchables = new CheckboxTreeNode("mp3", "Untouchables", deniro);
    }
    
    @Test
    public void shouldPropagateDown() {
        TreeNode work = root.getChildren().get(0).getChildren().get(0);
        assertEquals("Work", work.getData());
        
        work.setSelected(true);
        
        TreeNode expenses = work.getChildren().get(0);
        TreeNode resume = work.getChildren().get(1);
        
        assertTrue(expenses.isSelected());
        assertTrue(resume.isSelected());
    }
    
    @Test
    public void shouldPropagateUpWhenOneChildOfANodeIsSelected() {
        TreeNode document = root.getChildren().get(0);
        TreeNode work = document.getChildren().get(0);
        TreeNode expenses = work.getChildren().get(0);
        TreeNode resume = work.getChildren().get(1);
        
        expenses.setSelected(true);
        
        assertTrue(work.isPartialSelected());
        assertTrue(document.isPartialSelected());
        assertFalse(work.isSelected());
        assertFalse(document.isSelected());
    }
    
    @Test
    public void shouldPropagateUpWhenAllChildrenOfANodeIsSelected() {
        TreeNode document = root.getChildren().get(0);
        TreeNode work = document.getChildren().get(0);
        TreeNode expenses = work.getChildren().get(0);
        TreeNode resume = work.getChildren().get(1);
        
        expenses.setSelected(true);
        resume.setSelected(true);
        
        assertTrue(work.isSelected());
        assertTrue(document.isPartialSelected());
        assertFalse(document.isSelected());
    }
    
    @Test
    public void shouldDeleteNode() {
        TreeNode work = root.getChildren().get(0).getChildren().get(0);
        TreeNode expenses = root.getChildren().get(0).getChildren().get(0).getChildren().get(0);
        
        assertEquals("Expenses", expenses.getData());
        
        expenses.getChildren().clear();
        expenses.getParent().getChildren().remove(expenses);
        
        assertNull(expenses.getParent());
        assertEquals(1, work.getChildCount());
    }
    
    @Test
    public void shouldUpdateSelectionStateWhenNodesAreAdded() {
        TreeNode work = root.getChildren().get(0).getChildren().get(0);
        assertEquals("Work", work.getData());
        assertEquals(2, work.getChildCount());
        
        TreeNode unselectedNode = new CheckboxTreeNode("Unselected");
        work.getChildren().add(unselectedNode);
        
        assertFalse(work.isSelected());
        assertFalse(work.isPartialSelected());
        
        TreeNode selectedNode1 = new CheckboxTreeNode("Selected1");
        selectedNode1.setSelected(true);
        work.getChildren().add(selectedNode1);
        
        assertFalse(work.isSelected());
        assertTrue(work.isPartialSelected());
        
        TreeNode unselectedNode2 = new CheckboxTreeNode("Unselected2");
        work.getChildren().add(unselectedNode2);
        
        assertFalse(work.isSelected());
        assertTrue(work.isPartialSelected());
    }
    
    @Test
    public void shouldUpdateSelectionStateWhenNodesAreRemoved() {
        TreeNode work = root.getChildren().get(0).getChildren().get(0);
        assertEquals("Work", work.getData());
        assertEquals(2, work.getChildCount());
        
        TreeNode resume = work.getChildren().get(1);
        assertEquals("Resume", resume.getData());
        
        work.getChildren().get(0).setSelected(true);

        assertFalse(work.isSelected());
        assertTrue(work.isPartialSelected()); 
        
        work.getChildren().remove(resume);
        
        assertTrue(work.isSelected());
        assertFalse(work.isPartialSelected()); 
    }
    
    
}
