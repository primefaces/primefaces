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
package org.primefaces.integrationtests.datatable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable handler for tracking LazyDataModel method calls (load, count, etc.).
 * Useful for testing to verify that data is loaded efficiently (e.g., avoiding loading all data).
 */
public class LazyDataModelCallTracker implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<String> calls = new ArrayList<>();

    /**
     * Records a load() call with its parameters.
     *
     * @param first the offset of first data
     * @param pageSize the number of data to load
     */
    public void recordLoadCall(int first, int pageSize) {
        calls.add(String.format("load(first=%d, size=%d)", first, pageSize));
    }

    /**
     * Clears all tracking history.
     */
    public void reset() {
        calls.clear();
    }

    /**
     * Gets the total number of load() calls.
     */
    public int getLoadCallCount() {
        return calls.size();
    }

    /**
     * No-op setter for JSF compatibility with h:inputHidden.
     */
    public void setLoadCallCount(int loadCallCount) {
        // No-op - this is a computed property
    }

    /**
     * Gets the complete tracking history as a string with pipe separators.
     */
    public String getFullHistoryString() {
        return String.join(" | ", calls);
    }

    /**
     * No-op setter for JSF compatibility with h:inputHidden.
     */
    public void setFullHistoryString(String fullHistoryString) {
        // No-op - this is a computed property
    }

    /**
     * Gets the list of load calls for programmatic access.
     */
    public List<String> getCalls() {
        return new ArrayList<>(calls);
    }
}
