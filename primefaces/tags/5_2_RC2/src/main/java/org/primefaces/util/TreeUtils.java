/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.util;

import java.util.Arrays;
import java.util.Comparator;
import org.primefaces.model.TreeNode;
import org.primefaces.model.TreeNodeList;

/**
 * Utilities for Tree components.
 */
public class TreeUtils {
    
    /**
     * Sorts children of a node using a comparator
     * 
     * @param node Node instance whose children to be sorted
     * @param comparator Comparator to use in sorting
     */
    public static void sortNode(TreeNode node, Comparator comparator) {
        TreeNodeList children = (TreeNodeList) node.getChildren();
        
        if(children != null && !children.isEmpty()) {
            Object[] childrenArray = children.toArray();
            Arrays.sort(childrenArray, comparator);
            for(int i = 0; i < childrenArray.length; i++) {
                children.setSibling(i, (TreeNode) childrenArray[i]);
            }
            
            for(int i = 0; i < children.size(); i++) {
                sortNode(children.get(i), comparator);
            }
        }
    }
}
