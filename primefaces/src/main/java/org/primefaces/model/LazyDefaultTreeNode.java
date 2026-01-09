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
package org.primefaces.model;

import org.primefaces.util.Callbacks;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Lazy version of the {@link DefaultTreeNode}.
 * A lazy version of the {@link CheckboxTreeNode} is still missing.
 *
 * @param <T> T the data type
 */
public class LazyDefaultTreeNode<T> extends DefaultTreeNode<T> implements LazyTreeNode {

    private static final long serialVersionUID = 1L;

    private Callbacks.SerializableFunction<T, List<T>> loadFunction;
    private Callbacks.SerializableFunction<T, Boolean> isLeafFunction;
    private boolean loaded;

    // serialization
    public LazyDefaultTreeNode() {
    }

    public LazyDefaultTreeNode(T data,
            Callbacks.SerializableFunction<T, List<T>> loadFunction,
            Callbacks.SerializableFunction<T, Boolean> isLeafFunction) {
        super(data);
        this.loadFunction = loadFunction;
        this.isLeafFunction = isLeafFunction;
    }

    @Override
    public TreeNodeChildren<T> getChildren() {
        if (isLeaf()) {
            return null;
        }

        lazyLoad();

        return super.getChildren();
    }

    @Override
    public int getChildCount() {
        if (isLeaf()) {
            return 0;
        }

        lazyLoad();

        return super.getChildCount();
    }

    @Override
    public boolean isLeaf() {
        return isLeafFunction.apply(getData());
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    protected void lazyLoad() {
        if (!loaded) {
            loaded = true;

            List<T> childData = loadFunction.apply(getData());
            List<LazyDefaultTreeNode<T>> childNodes = childData
                    .stream()
                    .map(f -> new LazyDefaultTreeNode<>(f, loadFunction, isLeafFunction))
                    .collect(Collectors.toList());
            super.getChildren().addAll(childNodes);
        }
    }

    @Override
    protected LazyDefaultTreeNodeChildren<T> initChildren() {
        return new LazyDefaultTreeNodeChildren<>(this);
    }

}
