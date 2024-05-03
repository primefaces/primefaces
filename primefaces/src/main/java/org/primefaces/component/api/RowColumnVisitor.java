/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.component.api;

import java.io.IOException;

import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.row.Row;

public interface RowColumnVisitor {

    void visitColumn(int index, UIColumn column) throws IOException;

    void visitRow(int index, Row row) throws IOException;

    void visitRowEnd(int index, Row row) throws IOException;

    void visitColumnGroup(int index, ColumnGroup colGroup) throws IOException;

    class Adapter implements RowColumnVisitor {

        @Override
        public void visitColumn(int index, UIColumn column) throws IOException {
            // NOOP
        }

        @Override
        public void visitRow(int index, Row row) throws IOException {
            // NOOP
        }

        @Override
        public void visitRowEnd(int index, Row row) throws IOException {
            // NOOP
        }

        @Override
        public void visitColumnGroup(int index, ColumnGroup colGroup) throws IOException {
            // NOOP
        }
    }

    class ColumnCounter extends Adapter {

        private int count = 0;

        @Override
        public void visitColumn(int index, UIColumn column) throws IOException {
            count++;
        }

        public int getCount() {
            return count;
        }
    }
}
