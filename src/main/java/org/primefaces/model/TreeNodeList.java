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
package org.primefaces.model;

import java.util.ArrayList;

public class TreeNodeList extends ArrayList<TreeNode> {

    /**
     * Optimized set implementation to be used in sorting
     * @param index index of the element to replace
     * @param node node to be stored at the specified position
     * @return the node previously at the specified position
     */
    public TreeNode setSibling(int index, TreeNode node) {
        throw new UnsupportedOperationException();
    }
}

